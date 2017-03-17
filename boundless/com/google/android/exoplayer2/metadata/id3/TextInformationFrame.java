package com.google.android.exoplayer2.metadata.id3;

public final class TextInformationFrame extends Id3Frame {
    public final String description;

    public TextInformationFrame(String id, String description) {
        super(id);
        this.description = description;
    }
}
