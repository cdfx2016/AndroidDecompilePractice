package com.google.android.exoplayer2.source.dash;

import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.rawcc.RawCcExtractor;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.List;

public class DefaultDashChunkSource implements DashChunkSource {
    private final int adaptationSetIndex;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private boolean missingLastSegment;
    private int periodIndex;
    private final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;

    public static final class Factory implements com.google.android.exoplayer2.source.dash.DashChunkSource.Factory {
        private final com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
        }

        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int adaptationSetIndex, TrackSelection trackSelection, long elapsedRealtimeOffsetMs) {
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndex, trackSelection, this.dataSourceFactory.createDataSource(), elapsedRealtimeOffsetMs);
        }
    }

    protected static final class RepresentationHolder {
        public final ChunkExtractorWrapper extractorWrapper;
        private long periodDurationUs;
        public Representation representation;
        public Format sampleFormat;
        public DashSegmentIndex segmentIndex;
        private int segmentNumShift;

        public RepresentationHolder(long periodDurationUs, Representation representation) {
            this.periodDurationUs = periodDurationUs;
            this.representation = representation;
            String containerMimeType = representation.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                this.extractorWrapper = null;
            } else {
                Extractor extractor;
                boolean resendFormatOnInit = false;
                if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                    extractor = new RawCcExtractor();
                    resendFormatOnInit = true;
                } else if (mimeTypeIsWebm(containerMimeType)) {
                    extractor = new MatroskaExtractor();
                } else {
                    extractor = new FragmentedMp4Extractor();
                }
                this.extractorWrapper = new ChunkExtractorWrapper(extractor, representation.format, true, resendFormatOnInit);
            }
            this.segmentIndex = representation.getIndex();
        }

        public void setSampleFormat(Format sampleFormat) {
            this.sampleFormat = sampleFormat;
        }

        public void updateRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            this.periodDurationUs = newPeriodDurationUs;
            this.representation = newRepresentation;
            if (oldIndex != null) {
                this.segmentIndex = newIndex;
                if (oldIndex.isExplicit()) {
                    int oldIndexLastSegmentNum = oldIndex.getLastSegmentNum(this.periodDurationUs);
                    long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, this.periodDurationUs);
                    int newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
                    long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
                    if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                        this.segmentNumShift += (oldIndex.getLastSegmentNum(this.periodDurationUs) + 1) - newIndexFirstSegmentNum;
                    } else if (oldIndexEndTimeUs < newIndexStartTimeUs) {
                        throw new BehindLiveWindowException();
                    } else {
                        this.segmentNumShift += oldIndex.getSegmentNum(newIndexStartTimeUs, this.periodDurationUs) - newIndexFirstSegmentNum;
                    }
                }
            }
        }

        public int getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getLastSegmentNum() {
            int lastSegmentNum = this.segmentIndex.getLastSegmentNum(this.periodDurationUs);
            if (lastSegmentNum == -1) {
                return -1;
            }
            return this.segmentNumShift + lastSegmentNum;
        }

        public long getSegmentStartTimeUs(int segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(int segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public int getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(int segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }

        private static boolean mimeTypeIsWebm(String mimeType) {
            return mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM) || mimeType.startsWith(MimeTypes.APPLICATION_WEBM);
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int adaptationSetIndex, TrackSelection trackSelection, DataSource dataSource, long elapsedRealtimeOffsetMs) {
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = manifest;
        this.adaptationSetIndex = adaptationSetIndex;
        this.trackSelection = trackSelection;
        this.dataSource = dataSource;
        this.periodIndex = periodIndex;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        long periodDurationUs = manifest.getPeriodDurationUs(periodIndex);
        List<Representation> representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        for (int i = 0; i < this.representationHolders.length; i++) {
            this.representationHolders[i] = new RepresentationHolder(periodDurationUs, (Representation) representations.get(trackSelection.getIndexInTrackGroup(i)));
        }
    }

    public void updateManifest(DashManifest newManifest, int newPeriodIndex) {
        try {
            this.manifest = newManifest;
            this.periodIndex = newPeriodIndex;
            long periodDurationUs = this.manifest.getPeriodDurationUs(this.periodIndex);
            List<Representation> representations = getRepresentations();
            for (int i = 0; i < this.representationHolders.length; i++) {
                this.representationHolders[i].updateRepresentation(periodDurationUs, (Representation) representations.get(this.trackSelection.getIndexInTrackGroup(i)));
            }
        } catch (BehindLiveWindowException e) {
            this.fatalError = e;
        }
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public int getPreferredQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (this.fatalError != null || this.trackSelection.length() < 2) {
            return queue.size();
        }
        return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
    }

    public final void getNextChunk(MediaChunk previous, long playbackPositionUs, ChunkHolder out) {
        if (this.fatalError == null) {
            this.trackSelection.updateSelectedTrack(previous != null ? previous.endTimeUs - playbackPositionUs : 0);
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.getSelectedIndex()];
            Representation selectedRepresentation = representationHolder.representation;
            DashSegmentIndex segmentIndex = representationHolder.segmentIndex;
            RangedUri pendingInitializationUri = null;
            RangedUri pendingIndexUri = null;
            Format sampleFormat = representationHolder.sampleFormat;
            if (sampleFormat == null) {
                pendingInitializationUri = selectedRepresentation.getInitializationUri();
            }
            if (segmentIndex == null) {
                pendingIndexUri = selectedRepresentation.getIndexUri();
            }
            if (pendingInitializationUri == null && pendingIndexUri == null) {
                int segmentNum;
                long nowUs = getNowUnixTimeUs();
                int firstAvailableSegmentNum = representationHolder.getFirstSegmentNum();
                int lastAvailableSegmentNum = representationHolder.getLastSegmentNum();
                if (lastAvailableSegmentNum == -1) {
                    long liveEdgeTimeInPeriodUs = (nowUs - (this.manifest.availabilityStartTime * 1000)) - (this.manifest.getPeriod(this.periodIndex).startMs * 1000);
                    if (this.manifest.timeShiftBufferDepth != C.TIME_UNSET) {
                        firstAvailableSegmentNum = Math.max(firstAvailableSegmentNum, representationHolder.getSegmentNum(liveEdgeTimeInPeriodUs - (this.manifest.timeShiftBufferDepth * 1000)));
                    }
                    lastAvailableSegmentNum = representationHolder.getSegmentNum(liveEdgeTimeInPeriodUs) - 1;
                }
                if (previous == null) {
                    segmentNum = Util.constrainValue(representationHolder.getSegmentNum(playbackPositionUs), firstAvailableSegmentNum, lastAvailableSegmentNum);
                } else {
                    segmentNum = previous.getNextChunkIndex();
                    if (segmentNum < firstAvailableSegmentNum) {
                        this.fatalError = new BehindLiveWindowException();
                        return;
                    }
                }
                if (segmentNum > lastAvailableSegmentNum || (this.missingLastSegment && segmentNum >= lastAvailableSegmentNum)) {
                    boolean z;
                    if (!this.manifest.dynamic || this.periodIndex < this.manifest.getPeriodCount() - 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    out.endOfStream = z;
                    return;
                }
                out.chunk = newMediaChunk(representationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), sampleFormat, segmentNum);
                return;
            }
            out.chunk = newInitializationChunk(representationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            InitializationChunk initializationChunk = (InitializationChunk) chunk;
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(initializationChunk.trackFormat)];
            Format sampleFormat = initializationChunk.getSampleFormat();
            if (sampleFormat != null) {
                representationHolder.setSampleFormat(sampleFormat);
            }
            if (representationHolder.segmentIndex == null) {
                SeekMap seekMap = initializationChunk.getSeekMap();
                if (seekMap != null) {
                    representationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex) seekMap, initializationChunk.dataSpec.uri.toString());
                }
            }
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e) {
        if (!cancelable) {
            return false;
        }
        if (this.manifest.dynamic || !(chunk instanceof MediaChunk) || !(e instanceof InvalidResponseCodeException) || ((InvalidResponseCodeException) e).responseCode != 404 || ((MediaChunk) chunk).chunkIndex < this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)].getLastSegmentNum()) {
            return ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), e);
        }
        this.missingLastSegment = true;
        return true;
    }

    private List<Representation> getRepresentations() {
        return ((AdaptationSet) this.manifest.getPeriod(this.periodIndex).adaptationSets.get(this.adaptationSetIndex)).representations;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        if (initializationUri != null) {
            requestUri = initializationUri.attemptMerge(indexUri);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource, new DataSpec(requestUri.getUri(), requestUri.start, requestUri.length, representationHolder.representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, representationHolder.extractorWrapper);
    }

    private Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, Format sampleFormat, int segmentNum) {
        Representation representation = representationHolder.representation;
        long startTimeUs = representationHolder.getSegmentStartTimeUs(segmentNum);
        long endTimeUs = representationHolder.getSegmentEndTimeUs(segmentNum);
        RangedUri segmentUri = representationHolder.getSegmentUrl(segmentNum);
        DataSpec dataSpec = new DataSpec(segmentUri.getUri(), segmentUri.start, segmentUri.length, representation.getCacheKey());
        if (representationHolder.extractorWrapper == null) {
            return new SingleSampleMediaChunk(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, segmentNum, trackFormat);
        }
        return new ContainerMediaChunk(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, segmentNum, -representation.presentationTimeOffsetUs, representationHolder.extractorWrapper, sampleFormat);
    }
}
