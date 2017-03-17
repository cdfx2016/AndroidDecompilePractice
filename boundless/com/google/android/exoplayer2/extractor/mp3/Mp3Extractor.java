package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;

public final class Mp3Extractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new Mp3Extractor()};
        }
    };
    private static final int HEADER_MASK = -128000;
    private static final int INFO_HEADER = Util.getIntegerCodeForString("Info");
    private static final int MAX_SNIFF_BYTES = 4096;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
    private static final int XING_HEADER = Util.getIntegerCodeForString("Xing");
    private long basisTimeUs;
    private ExtractorOutput extractorOutput;
    private final long forcedFirstSampleTimestampUs;
    private final GaplessInfoHolder gaplessInfoHolder;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;

    interface Seeker extends SeekMap {
        long getTimeUs(long j);
    }

    public Mp3Extractor() {
        this(C.TIME_UNSET);
    }

    public Mp3Extractor(long forcedFirstSampleTimestampUs) {
        this.forcedFirstSampleTimestampUs = forcedFirstSampleTimestampUs;
        this.scratch = new ParsableByteArray(4);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = C.TIME_UNSET;
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return synchronize(input, true);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = this.extractorOutput.track(0);
        this.extractorOutput.endTracks();
    }

    public void seek(long position) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C.TIME_UNSET;
        this.samplesRead = 0;
        this.sampleBytesRemaining = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0) {
            try {
                synchronize(input, false);
            } catch (EOFException e) {
                return -1;
            }
        }
        if (this.seeker == null) {
            this.seeker = setupSeeker(input);
            this.extractorOutput.seekMap(this.seeker);
            this.trackOutput.format(Format.createAudioSampleFormat(null, this.synchronizedHeader.mimeType, null, -1, 4096, this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, -1, this.gaplessInfoHolder.encoderDelay, this.gaplessInfoHolder.encoderPadding, null, null, 0, null));
        }
        return readSample(input);
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (!extractorInput.peekFully(this.scratch.data, 0, 4, true)) {
                return -1;
            }
            this.scratch.setPosition(0);
            int sampleHeaderData = this.scratch.readInt();
            if ((HEADER_MASK & sampleHeaderData) != (this.synchronizedHeaderData & HEADER_MASK) || MpegAudioHeader.getFrameSize(sampleHeaderData) == -1) {
                extractorInput.skipFully(1);
                this.synchronizedHeaderData = 0;
                return 0;
            }
            MpegAudioHeader.populateHeader(sampleHeaderData, this.synchronizedHeader);
            if (this.basisTimeUs == C.TIME_UNSET) {
                this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                if (this.forcedFirstSampleTimestampUs != C.TIME_UNSET) {
                    this.basisTimeUs += this.forcedFirstSampleTimestampUs - this.seeker.getTimeUs(0);
                }
            }
            this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        }
        int bytesAppended = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (bytesAppended == -1) {
            return -1;
        }
        this.sampleBytesRemaining -= bytesAppended;
        if (this.sampleBytesRemaining > 0) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.basisTimeUs + ((this.samplesRead * C.MICROS_PER_SECOND) / ((long) this.synchronizedHeader.sampleRate)), 1, this.synchronizedHeader.frameSize, 0, null);
        this.samplesRead += (long) this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = 0;
        return 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean synchronize(com.google.android.exoplayer2.extractor.ExtractorInput r13, boolean r14) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r12 = this;
        r7 = 0;
        r0 = 0;
        r3 = 0;
        r5 = 0;
        if (r14 == 0) goto L_0x003e;
    L_0x0006:
        r4 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
    L_0x0008:
        r13.resetPeekPosition();
        r8 = r13.getPosition();
        r10 = 0;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 != 0) goto L_0x0024;
    L_0x0015:
        r8 = r12.gaplessInfoHolder;
        com.google.android.exoplayer2.extractor.mp3.Id3Util.parseId3(r13, r8);
        r8 = r13.getPeekPosition();
        r3 = (int) r8;
        if (r14 != 0) goto L_0x0024;
    L_0x0021:
        r13.skipFully(r3);
    L_0x0024:
        r8 = r12.scratch;
        r9 = r8.data;
        r10 = 0;
        r11 = 4;
        if (r7 <= 0) goto L_0x0041;
    L_0x002c:
        r8 = 1;
    L_0x002d:
        r8 = r13.peekFully(r9, r10, r11, r8);
        if (r8 != 0) goto L_0x0043;
    L_0x0033:
        if (r14 == 0) goto L_0x009c;
    L_0x0035:
        r8 = r3 + r5;
        r13.skipFully(r8);
    L_0x003a:
        r12.synchronizedHeaderData = r0;
        r8 = 1;
    L_0x003d:
        return r8;
    L_0x003e:
        r4 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        goto L_0x0008;
    L_0x0041:
        r8 = 0;
        goto L_0x002d;
    L_0x0043:
        r8 = r12.scratch;
        r9 = 0;
        r8.setPosition(r9);
        r8 = r12.scratch;
        r2 = r8.readInt();
        if (r0 == 0) goto L_0x005b;
    L_0x0051:
        r8 = -128000; // 0xfffffffffffe0c00 float:NaN double:NaN;
        r8 = r8 & r2;
        r9 = -128000; // 0xfffffffffffe0c00 float:NaN double:NaN;
        r9 = r9 & r0;
        if (r8 != r9) goto L_0x0062;
    L_0x005b:
        r1 = com.google.android.exoplayer2.extractor.MpegAudioHeader.getFrameSize(r2);
        r8 = -1;
        if (r1 != r8) goto L_0x0087;
    L_0x0062:
        r6 = r5 + 1;
        if (r5 != r4) goto L_0x0073;
    L_0x0066:
        if (r14 != 0) goto L_0x0070;
    L_0x0068:
        r8 = new com.google.android.exoplayer2.ParserException;
        r9 = "Searched too many bytes.";
        r8.<init>(r9);
        throw r8;
    L_0x0070:
        r8 = 0;
        r5 = r6;
        goto L_0x003d;
    L_0x0073:
        r7 = 0;
        r0 = 0;
        if (r14 == 0) goto L_0x0081;
    L_0x0077:
        r13.resetPeekPosition();
        r8 = r3 + r6;
        r13.advancePeekPosition(r8);
        r5 = r6;
        goto L_0x0024;
    L_0x0081:
        r8 = 1;
        r13.skipFully(r8);
        r5 = r6;
        goto L_0x0024;
    L_0x0087:
        r7 = r7 + 1;
        r8 = 1;
        if (r7 != r8) goto L_0x0098;
    L_0x008c:
        r8 = r12.synchronizedHeader;
        com.google.android.exoplayer2.extractor.MpegAudioHeader.populateHeader(r2, r8);
        r0 = r2;
    L_0x0092:
        r8 = r1 + -4;
        r13.advancePeekPosition(r8);
        goto L_0x0024;
    L_0x0098:
        r8 = 4;
        if (r7 != r8) goto L_0x0092;
    L_0x009b:
        goto L_0x0033;
    L_0x009c:
        r13.resetPeekPosition();
        goto L_0x003a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.synchronize(com.google.android.exoplayer2.extractor.ExtractorInput, boolean):boolean");
    }

    private Seeker setupSeeker(ExtractorInput input) throws IOException, InterruptedException {
        int xingBase = 21;
        ParsableByteArray frame = new ParsableByteArray(this.synchronizedHeader.frameSize);
        input.peekFully(frame.data, 0, this.synchronizedHeader.frameSize);
        long position = input.getPosition();
        long length = input.getLength();
        int headerData = 0;
        Seeker seeker = null;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                xingBase = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            xingBase = 13;
        }
        if (frame.limit() >= xingBase + 4) {
            frame.setPosition(xingBase);
            headerData = frame.readInt();
        }
        if (headerData == XING_HEADER || headerData == INFO_HEADER) {
            seeker = XingSeeker.create(this.synchronizedHeader, frame, position, length);
            if (!(seeker == null || this.gaplessInfoHolder.hasGaplessInfo())) {
                input.resetPeekPosition();
                input.advancePeekPosition(xingBase + 141);
                input.peekFully(this.scratch.data, 0, 3);
                this.scratch.setPosition(0);
                this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
            }
            input.skipFully(this.synchronizedHeader.frameSize);
        } else if (frame.limit() >= 40) {
            frame.setPosition(36);
            if (frame.readInt() == VBRI_HEADER) {
                seeker = VbriSeeker.create(this.synchronizedHeader, frame, position, length);
                input.skipFully(this.synchronizedHeader.frameSize);
            }
        }
        if (seeker != null) {
            return seeker;
        }
        input.resetPeekPosition();
        input.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(input.getPosition(), this.synchronizedHeader.bitrate, length);
    }
}
