package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.MediaPeriod.Callback;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;

final class MergingMediaPeriod implements MediaPeriod, Callback {
    private Callback callback;
    private MediaPeriod[] enabledPeriods;
    private int pendingChildPrepareCount;
    public final MediaPeriod[] periods;
    private SequenceableLoader sequenceableLoader;
    private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices = new IdentityHashMap();
    private TrackGroupArray trackGroups;

    public MergingMediaPeriod(MediaPeriod... periods) {
        this.periods = periods;
    }

    public void prepare(Callback callback) {
        this.callback = callback;
        this.pendingChildPrepareCount = this.periods.length;
        for (MediaPeriod period : this.periods) {
            period.prepare(this);
        }
    }

    public void maybeThrowPrepareError() throws IOException {
        for (MediaPeriod period : this.periods) {
            period.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (i = 0; i < selections.length; i++) {
            int i2;
            int j;
            if (streams[i] == null) {
                i2 = -1;
            } else {
                i2 = ((Integer) this.streamPeriodIndices.get(streams[i])).intValue();
            }
            streamChildIndices[i] = i2;
            selectionChildIndices[i] = -1;
            if (selections[i] != null) {
                TrackGroup trackGroup = selections[i].getTrackGroup();
                for (j = 0; j < this.periods.length; j++) {
                    if (this.periods[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i] = j;
                        break;
                    }
                }
            }
        }
        this.streamPeriodIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        ArrayList<MediaPeriod> enabledPeriodsList = new ArrayList(this.periods.length);
        i = 0;
        while (i < this.periods.length) {
            j = 0;
            while (j < selections.length) {
                childStreams[j] = streamChildIndices[j] == i ? streams[j] : null;
                childSelections[j] = selectionChildIndices[j] == i ? selections[j] : null;
                j++;
            }
            long selectPositionUs = this.periods[i].selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs);
            if (i == 0) {
                positionUs = selectPositionUs;
            } else if (selectPositionUs != positionUs) {
                throw new IllegalStateException("Children enabled at different positions");
            }
            boolean periodEnabled = false;
            for (j = 0; j < selections.length; j++) {
                if (selectionChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] != null);
                    newStreams[j] = childStreams[j];
                    periodEnabled = true;
                    this.streamPeriodIndices.put(childStreams[j], Integer.valueOf(i));
                } else if (streamChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] == null);
                }
            }
            if (periodEnabled) {
                enabledPeriodsList.add(this.periods[i]);
            }
            i++;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        this.enabledPeriods = new MediaPeriod[enabledPeriodsList.size()];
        enabledPeriodsList.toArray(this.enabledPeriods);
        this.sequenceableLoader = new CompositeSequenceableLoader(this.enabledPeriods);
        return positionUs;
    }

    public boolean continueLoading(long positionUs) {
        return this.sequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.sequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        int i;
        long positionUs = this.periods[0].readDiscontinuity();
        for (i = 1; i < this.periods.length; i++) {
            if (this.periods[i].readDiscontinuity() != C.TIME_UNSET) {
                throw new IllegalStateException("Child reported discontinuity");
            }
        }
        if (positionUs != C.TIME_UNSET) {
            i = 0;
            while (i < this.enabledPeriods.length) {
                if (this.enabledPeriods[i] == this.periods[0] || this.enabledPeriods[i].seekToUs(positionUs) == positionUs) {
                    i++;
                } else {
                    throw new IllegalStateException("Children seeked to different positions");
                }
            }
        }
        return positionUs;
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = Long.MAX_VALUE;
        for (MediaPeriod period : this.enabledPeriods) {
            long rendererBufferedPositionUs = period.getBufferedPositionUs();
            if (rendererBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, rendererBufferedPositionUs);
            }
        }
        return bufferedPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : bufferedPositionUs;
    }

    public long seekToUs(long positionUs) {
        positionUs = this.enabledPeriods[0].seekToUs(positionUs);
        for (int i = 1; i < this.enabledPeriods.length; i++) {
            if (this.enabledPeriods[i].seekToUs(positionUs) != positionUs) {
                throw new IllegalStateException("Children seeked to different positions");
            }
        }
        return positionUs;
    }

    public void onPrepared(MediaPeriod ignored) {
        int i = 0;
        int i2 = this.pendingChildPrepareCount - 1;
        this.pendingChildPrepareCount = i2;
        if (i2 <= 0) {
            int totalTrackGroupCount = 0;
            for (MediaPeriod period : this.periods) {
                totalTrackGroupCount += period.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            MediaPeriod[] mediaPeriodArr = this.periods;
            int length = mediaPeriodArr.length;
            while (i < length) {
                TrackGroupArray periodTrackGroups = mediaPeriodArr[i].getTrackGroups();
                int periodTrackGroupCount = periodTrackGroups.length;
                int j = 0;
                int trackGroupIndex2 = trackGroupIndex;
                while (j < periodTrackGroupCount) {
                    trackGroupIndex = trackGroupIndex2 + 1;
                    trackGroupArray[trackGroupIndex2] = periodTrackGroups.get(j);
                    j++;
                    trackGroupIndex2 = trackGroupIndex;
                }
                i++;
                trackGroupIndex = trackGroupIndex2;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onContinueLoadingRequested(MediaPeriod ignored) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }
}
