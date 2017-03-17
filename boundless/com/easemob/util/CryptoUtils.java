package com.easemob.util;

import android.util.Base64;
import com.easemob.chat.core.b;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    public static final int ALGORIGHM_AES = 1;
    public static final int ALGORIGHM_DES = 0;
    static final String HEXES = "0123456789ABCDEF";
    Cipher cipher = null;
    Cipher decipher = null;
    String key = "TongliforniaJohnson";
    byte[] keyBytes = new byte[]{(byte) 74, (byte) 111, (byte) 104, (byte) 110, (byte) 115, (byte) 111, (byte) 110, (byte) 77, (byte) 97, (byte) 74, (byte) 105, (byte) 70, (byte) 97, (byte) 110, (byte) 103, (byte) 74, (byte) 101, (byte) 114, (byte) 118, (byte) 105, (byte) 115, (byte) 76, (byte) 105, (byte) 117, (byte) 76, (byte) 105, (byte) 117, (byte) 83, (byte) 104, (byte) 97, (byte) 111, (byte) 90};

    public static String getHex(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            stringBuilder.append(HEXES.charAt((b & PsExtractor.VIDEO_STREAM_MASK) >> 4)).append(HEXES.charAt(b & 15));
        }
        return stringBuilder.toString();
    }

    public byte[] decrypt(byte[] bArr) throws Exception {
        return this.decipher.doFinal(bArr);
    }

    public String decryptBase64String(String str) throws Exception {
        return new String(decrypt(Base64.decode(str, 0)), "UTF-8");
    }

    public byte[] encrypt(String str) throws Exception {
        return this.cipher.doFinal(str.getBytes("UTF-8"));
    }

    public byte[] encrypt(byte[] bArr) throws Exception {
        return this.cipher.doFinal(bArr);
    }

    public String encryptBase64String(String str) throws Exception {
        return new String(Base64.encode(encrypt(str), 0));
    }

    public void init(int i) {
        if (i == 0) {
            initDES();
        } else {
            initAES();
        }
    }

    public void initAES() {
        try {
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key secretKeySpec = new SecretKeySpec(this.keyBytes, "AES");
            this.cipher.init(1, secretKeySpec);
            this.decipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.decipher.init(2, secretKeySpec);
            EMLog.d(b.a, "initital for AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initDES() {
        try {
            this.keyBytes = Arrays.copyOf(MessageDigest.getInstance("md5").digest(this.key.getBytes("utf-8")), 24);
            int i = 16;
            int i2 = 0;
            while (i2 < 8) {
                int i3 = i + 1;
                int i4 = i2 + 1;
                this.keyBytes[i] = this.keyBytes[i2];
                i = i3;
                i2 = i4;
            }
            Key secretKeySpec = new SecretKeySpec(this.keyBytes, "DESede");
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
            this.cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            this.cipher.init(1, secretKeySpec, ivParameterSpec);
            this.decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            this.decipher.init(2, secretKeySpec, ivParameterSpec);
            EMLog.d(b.a, "initital for DES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
