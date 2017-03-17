package com.google.android.exoplayer2.metadata;

public interface MetadataDecoder<T> {
    boolean canDecode(String str);

    T decode(byte[] bArr, int i) throws MetadataDecoderException;
}
