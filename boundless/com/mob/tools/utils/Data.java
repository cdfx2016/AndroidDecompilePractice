package com.mob.tools.utils;

import android.text.TextUtils;
import android.util.Base64;
import cn.finalteam.toolsfinal.coder.RSACoder;
import com.mob.tools.MobLog;
import com.mob.tools.network.BufferedByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.CRC32;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Data {
    private static final String CHAT_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static byte[] SHA1(String text) throws Throwable {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        return SHA1(text.getBytes("utf-8"));
    }

    public static byte[] SHA1(byte[] data) throws Throwable {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(data);
        return md.digest();
    }

    public static byte[] SHA1(InputStream data) {
        if (data == null) {
            return null;
        }
        try {
            byte[] buf = new byte[1024];
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            int len = data.read(buf);
            while (len != -1) {
                md.update(buf, 0, len);
                len = data.read(buf);
            }
            return md.digest();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public static byte[] SHA1(File data) {
        if (data == null || !data.exists()) {
            return null;
        }
        byte[] sha = null;
        try {
            InputStream fis = new FileInputStream(data);
            sha = SHA1(fis);
            fis.close();
            return sha;
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return sha;
        }
    }

    public static byte[] AES128Encode(String key, String text) throws Throwable {
        if (key == null || text == null) {
            return null;
        }
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] keyBytes16 = new byte[16];
        System.arraycopy(keyBytes, 0, keyBytes16, 0, Math.min(keyBytes.length, 16));
        byte[] data = text.getBytes("UTF-8");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes16, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(1, keySpec);
        byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
        cipher.doFinal(cipherText, cipher.update(data, 0, data.length, cipherText, 0));
        return cipherText;
    }

    public static byte[] AES128Encode(byte[] key, String text) throws Throwable {
        if (key == null || text == null) {
            return null;
        }
        return AES128Encode(key, text.getBytes("UTF-8"));
    }

    public static byte[] AES128Encode(byte[] key, byte[] data) throws Throwable {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(1, keySpec);
        byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
        cipher.doFinal(cipherText, cipher.update(data, 0, data.length, cipherText, 0));
        return cipherText;
    }

    public static String AES128Decode(String key, byte[] cipherText) throws Throwable {
        if (key == null || cipherText == null) {
            return null;
        }
        return new String(AES128Decode(key.getBytes("UTF-8"), cipherText), "UTF-8");
    }

    public static byte[] AES128Decode(byte[] keyBytes, byte[] cipherText) throws Throwable {
        if (keyBytes == null || cipherText == null) {
            return null;
        }
        byte[] keyBytes16 = new byte[16];
        System.arraycopy(keyBytes, 0, keyBytes16, 0, Math.min(keyBytes.length, 16));
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes16, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
        cipher.init(2, keySpec);
        byte[] plainText = new byte[cipher.getOutputSize(cipherText.length)];
        int ptLength = cipher.update(cipherText, 0, cipherText.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);
        return plainText;
    }

    public static String byteToHex(byte[] data) {
        return byteToHex(data, 0, data.length);
    }

    public static String byteToHex(byte[] data, int offset, int len) {
        StringBuffer buffer = new StringBuffer();
        if (data == null) {
            return buffer.toString();
        }
        for (int i = offset; i < len; i++) {
            buffer.append(String.format("%02x", new Object[]{Byte.valueOf(data[i])}));
        }
        return buffer.toString();
    }

    public static String base62(long value) {
        String result = value == 0 ? "0" : "";
        while (value > 0) {
            int v = (int) (value % 62);
            value /= 62;
            result = CHAT_SET.charAt(v) + result;
        }
        return result;
    }

    public static String MD5(String data) {
        if (data == null) {
            return null;
        }
        byte[] tmp = rawMD5(data);
        if (tmp != null) {
            return toHex(tmp);
        }
        return null;
    }

    public static String MD5(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] tmp = rawMD5(data);
        if (tmp != null) {
            return toHex(tmp);
        }
        return null;
    }

    public static String MD5(File data) {
        if (data == null || !data.exists()) {
            return null;
        }
        try {
            InputStream fis = new FileInputStream(data);
            byte[] md5 = rawMD5(fis);
            fis.close();
            if (md5 != null) {
                return toHex(md5);
            }
            return null;
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    public static byte[] rawMD5(String data) {
        if (data == null) {
            return null;
        }
        try {
            return rawMD5(data.getBytes("utf-8"));
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    public static byte[] rawMD5(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            InputStream bais = new ByteArrayInputStream(data);
            byte[] md5 = rawMD5(bais);
            bais.close();
            return md5;
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    public static byte[] rawMD5(InputStream data) {
        if (data == null) {
            return null;
        }
        try {
            byte[] buf = new byte[1024];
            MessageDigest md = MessageDigest.getInstance("MD5");
            int len = data.read(buf);
            while (len != -1) {
                md.update(buf, 0, len);
                len = data.read(buf);
            }
            return md.digest();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public static String Base64AES(String msg, String key) {
        if (msg == null || key == null) {
            return null;
        }
        try {
            String result = Base64.encodeToString(AES128Encode(key, msg), 0);
            if (TextUtils.isEmpty(result) || !result.contains("\n")) {
                return result;
            }
            return result.replace("\n", "");
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    public static String urlEncode(String s, String enc) throws Throwable {
        String text = URLEncoder.encode(s, enc);
        return TextUtils.isEmpty(text) ? text : text.replace("+", "%20");
    }

    public static String urlEncode(String s) {
        try {
            return urlEncode(s, "utf-8");
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public static String CRC32(byte[] data) throws Throwable {
        CRC32 crc = new CRC32();
        crc.update(data);
        long value = crc.getValue();
        StringBuilder sb = new StringBuilder();
        byte b = (byte) ((int) (value >>> 56));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 48));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 40));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 32));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 24));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 16));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) (value >>> 8));
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        b = (byte) ((int) value);
        sb.append(String.format("%02x", new Object[]{Integer.valueOf(b & 255)}));
        while (sb.charAt(0) == '0') {
            sb = sb.deleteCharAt(0);
        }
        return sb.toString().toLowerCase();
    }

    public static byte[] rawRSAEncode(byte[] data, byte[] publicKey, int keySize) throws Throwable {
        int blockSize = (keySize / 8) - 11;
        RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance(RSACoder.KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(1, key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int offSet = 0; data.length - offSet > 0; offSet += blockSize) {
            byte[] cache = cipher.doFinal(data, offSet, Math.min(data.length - offSet, blockSize));
            baos.write(cache, 0, cache.length);
        }
        baos.close();
        return baos.toByteArray();
    }

    public static byte[] rawRSADecode(byte[] data, byte[] privateKey, int keySize) throws Throwable {
        KeyFactory factory = KeyFactory.getInstance(RSACoder.KEY_ALGORITHM);
        RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance(RSACoder.KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(2, key);
        int blockSize = keySize / 8;
        ByteArrayOutputStream baos = new BufferedByteArrayOutputStream();
        for (int offSet = 0; data.length - offSet > 0; offSet += blockSize) {
            byte[] cache = cipher.doFinal(data, offSet, Math.min(data.length - offSet, blockSize));
            baos.write(cache, 0, cache.length);
        }
        baos.close();
        return baos.toByteArray();
    }

    private static String toHex(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buffer.append(String.format("%02x", new Object[]{Byte.valueOf(data[i])}));
        }
        return buffer.toString();
    }
}
