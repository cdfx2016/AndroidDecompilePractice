package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

public class OggExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new OggExtractor()};
        }
    };
    private static final int MAX_VERIFICATION_BYTES = 8;
    private StreamReader streamReader;

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        try {
            OggPageHeader header = new OggPageHeader();
            if (!header.populate(input, true) || (header.type & 2) != 2) {
                return false;
            }
            int length = Math.min(header.bodySize, 8);
            ParsableByteArray scratch = new ParsableByteArray(length);
            input.peekFully(scratch.data, 0, length);
            if (FlacReader.verifyBitstreamType(resetPosition(scratch))) {
                this.streamReader = new FlacReader();
            } else if (VorbisReader.verifyBitstreamType(resetPosition(scratch))) {
                this.streamReader = new VorbisReader();
            } else if (!OpusReader.verifyBitstreamType(resetPosition(scratch))) {
                return false;
            } else {
                this.streamReader = new OpusReader();
            }
            return true;
        } catch (ParserException e) {
            return false;
        }
    }

    public void init(ExtractorOutput output) {
        TrackOutput trackOutput = output.track(0);
        output.endTracks();
        this.streamReader.init(output, trackOutput);
    }

    public void seek(long position) {
        this.streamReader.seek(position);
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        return this.streamReader.read(input, seekPosition);
    }

    StreamReader getStreamReader() {
        return this.streamReader;
    }

    private static ParsableByteArray resetPosition(ParsableByteArray scratch) {
        scratch.setPosition(0);
        return scratch;
    }
}
