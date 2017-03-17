package com.google.android.exoplayer2.text;

final class SimpleSubtitleOutputBuffer extends SubtitleOutputBuffer {
    private SimpleSubtitleDecoder owner;

    public SimpleSubtitleOutputBuffer(SimpleSubtitleDecoder owner) {
        this.owner = owner;
    }

    public final void release() {
        this.owner.releaseOutputBuffer((SubtitleOutputBuffer) this);
    }
}
