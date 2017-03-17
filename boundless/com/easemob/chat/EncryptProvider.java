package com.easemob.chat;

public interface EncryptProvider {
    byte[] decrypt(byte[] bArr, String str);

    byte[] encrypt(byte[] bArr, String str);
}
