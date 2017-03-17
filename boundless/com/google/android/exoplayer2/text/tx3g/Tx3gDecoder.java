package com.google.android.exoplayer2.text.tx3g;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class Tx3gDecoder extends SimpleSubtitleDecoder {
    private final ParsableByteArray parsableByteArray = new ParsableByteArray();

    public Tx3gDecoder() {
        super("Tx3gDecoder");
    }

    protected Subtitle decode(byte[] bytes, int length) {
        this.parsableByteArray.reset(bytes, length);
        int textLength = this.parsableByteArray.readUnsignedShort();
        if (textLength == 0) {
            return Tx3gSubtitle.EMPTY;
        }
        return new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(textLength)));
    }
}
