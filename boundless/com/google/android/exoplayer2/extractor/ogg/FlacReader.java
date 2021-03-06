package com.google.android.exoplayer2.extractor.ogg;

import android.support.v4.media.TransportMediator;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

final class FlacReader extends StreamReader {
    private static final byte AUDIO_PACKET_TYPE = (byte) -1;
    private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
    private static final byte SEEKTABLE_PACKET_TYPE = (byte) 3;
    private FlacOggSeeker flacOggSeeker;
    private FlacStreamInfo streamInfo;

    private class FlacOggSeeker implements OggSeeker, SeekMap {
        private static final int METADATA_LENGTH_OFFSET = 1;
        private static final int SEEK_POINT_SIZE = 18;
        private long currentGranule;
        private long firstFrameOffset;
        private long[] offsets;
        private volatile long queriedGranule;
        private long[] sampleNumbers;
        private volatile long seekedGranule;

        private FlacOggSeeker() {
            this.firstFrameOffset = -1;
            this.currentGranule = -1;
        }

        public void setFirstFrameOffset(long firstFrameOffset) {
            this.firstFrameOffset = firstFrameOffset;
        }

        public void parseSeekTable(ParsableByteArray data) {
            data.skipBytes(1);
            int numberOfSeekPoints = data.readUnsignedInt24() / 18;
            this.sampleNumbers = new long[numberOfSeekPoints];
            this.offsets = new long[numberOfSeekPoints];
            for (int i = 0; i < numberOfSeekPoints; i++) {
                this.sampleNumbers[i] = data.readLong();
                this.offsets[i] = data.readLong();
                data.skipBytes(2);
            }
        }

        public long read(ExtractorInput input) throws IOException, InterruptedException {
            if (this.currentGranule < 0) {
                return -1;
            }
            this.currentGranule = (-this.currentGranule) - 2;
            return this.currentGranule;
        }

        public synchronized long startSeek() {
            this.currentGranule = this.seekedGranule;
            return this.queriedGranule;
        }

        public SeekMap createSeekMap() {
            return this;
        }

        public boolean isSeekable() {
            return true;
        }

        public synchronized long getPosition(long timeUs) {
            int index;
            this.queriedGranule = FlacReader.this.convertTimeToGranule(timeUs);
            index = Util.binarySearchFloor(this.sampleNumbers, this.queriedGranule, true, true);
            this.seekedGranule = this.sampleNumbers[index];
            return this.firstFrameOffset + this.offsets[index];
        }

        public long getDurationUs() {
            return FlacReader.this.streamInfo.durationUs();
        }
    }

    FlacReader() {
    }

    public static boolean verifyBitstreamType(ParsableByteArray data) {
        return data.bytesLeft() >= 5 && data.readUnsignedByte() == TransportMediator.KEYCODE_MEDIA_PAUSE && data.readUnsignedInt() == 1179402563;
    }

    protected void reset(boolean headerData) {
        super.reset(headerData);
        if (headerData) {
            this.streamInfo = null;
            this.flacOggSeeker = null;
        }
    }

    private static boolean isAudioPacket(byte[] data) {
        return data[0] == AUDIO_PACKET_TYPE;
    }

    protected long preparePayload(ParsableByteArray packet) {
        if (isAudioPacket(packet.data)) {
            return (long) getFlacFrameBlockSize(packet);
        }
        return -1;
    }

    protected boolean readHeaders(ParsableByteArray packet, long position, SetupData setupData) throws IOException, InterruptedException {
        byte[] data = packet.data;
        if (this.streamInfo == null) {
            this.streamInfo = new FlacStreamInfo(data, 17);
            byte[] metadata = Arrays.copyOfRange(data, 9, packet.limit());
            metadata[4] = Byte.MIN_VALUE;
            setupData.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_FLAC, null, -1, this.streamInfo.bitRate(), this.streamInfo.channels, this.streamInfo.sampleRate, Collections.singletonList(metadata), null, 0, null);
        } else if ((data[0] & TransportMediator.KEYCODE_MEDIA_PAUSE) == 3) {
            FlacReader flacReader = this;
            this.flacOggSeeker = new FlacOggSeeker();
            this.flacOggSeeker.parseSeekTable(packet);
        } else if (isAudioPacket(data)) {
            if (this.flacOggSeeker != null) {
                this.flacOggSeeker.setFirstFrameOffset(position);
                setupData.oggSeeker = this.flacOggSeeker;
            }
            return false;
        }
        return true;
    }

    private int getFlacFrameBlockSize(ParsableByteArray packet) {
        int blockSizeCode = (packet.data[2] & 255) >> 4;
        switch (blockSizeCode) {
            case 1:
                return 192;
            case 2:
            case 3:
            case 4:
            case 5:
                return 576 << (blockSizeCode - 2);
            case 6:
            case 7:
                packet.skipBytes(4);
                packet.readUtf8EncodedLong();
                int value = blockSizeCode == 6 ? packet.readUnsignedByte() : packet.readUnsignedShort();
                packet.setPosition(0);
                return value + 1;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 256 << (blockSizeCode - 8);
            default:
                return -1;
        }
    }
}
