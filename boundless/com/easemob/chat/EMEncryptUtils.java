package com.easemob.chat;

import android.util.Base64;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class EMEncryptUtils {
    private static final String TAG = "encrypt";

    public static void decryptFile(String str, String str2) {
        try {
            EMLog.d("encrypt", "decrypt file:" + str);
            RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
            int length = (int) randomAccessFile.length();
            byte[] bArr = new byte[length];
            int read = randomAccessFile.read(bArr);
            if (read != length) {
                EMLog.e("encrypt", "error read file, file len:" + length + " readLen:" + read);
                return;
            }
            randomAccessFile.close();
            byte[] decrypt = EMChatManager.getInstance().getEncryptProvider().decrypt(bArr, str2);
            FileOutputStream fileOutputStream = new FileOutputStream(str, false);
            fileOutputStream.write(decrypt);
            fileOutputStream.close();
            EMLog.d("encrypt", "decrypted file:" + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String decryptMessage(String str, String str2) {
        try {
            EMLog.d("encrypt", "encrypted str:" + str);
            byte[] decode = Base64.decode(str, 0);
            EMLog.d("encrypt", "base64 decode bytes:" + EasyUtils.convertByteArrayToString(decode));
            byte[] decrypt = EMChatManager.getInstance().getEncryptProvider().decrypt(decode, str2);
            EMLog.d("encrypt", "decrypt bytes:" + EasyUtils.convertByteArrayToString(decrypt));
            String str3 = new String(decrypt, "UTF-8");
            EMLog.d("encrypt", "descripted str:" + str3);
            return str3;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String encryptFile(String str, String str2) {
        try {
            EMLog.d("encrypt", "try to encrypt file:" + str);
            RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
            int length = (int) randomAccessFile.length();
            EMLog.d("encrypt", "try to encrypt file:" + str + " original len:" + length);
            byte[] bArr = new byte[length];
            int read = randomAccessFile.read(bArr);
            if (read != length) {
                EMLog.e("encrypt", "error read file, file len:" + length + " readLen:" + read);
                return str;
            }
            randomAccessFile.close();
            byte[] encrypt = EMChatManager.getInstance().getEncryptProvider().encrypt(bArr, str2);
            String str3 = null;
            int lastIndexOf = str.lastIndexOf(46);
            if (lastIndexOf >= 0) {
                str3 = str.substring(lastIndexOf);
            }
            File createTempFile = File.createTempFile("encrypted", str3);
            FileOutputStream fileOutputStream = new FileOutputStream(createTempFile);
            fileOutputStream.write(encrypt);
            fileOutputStream.close();
            str3 = createTempFile.getAbsolutePath();
            EMLog.d("encrypt", "generated encrypted file:" + str3);
            return str3;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    static String encryptMessage(String str, String str2) {
        try {
            EncryptProvider encryptProvider = EMChatManager.getInstance().getEncryptProvider();
            byte[] bytes = str.getBytes("UTF-8");
            EMLog.d("encrypt", "utf-8 bytes:" + EasyUtils.convertByteArrayToString(bytes));
            byte[] encrypt = encryptProvider.encrypt(bytes, str2);
            EMLog.d("encrypt", "encrypted bytes:" + EasyUtils.convertByteArrayToString(encrypt));
            bytes = Base64.encode(encrypt, 0);
            EMLog.d("encrypt", "base64 bytes:" + EasyUtils.convertByteArrayToString(bytes));
            String str3 = new String(bytes);
            EMLog.d("encrypt", "encrypted str:" + str3);
            return str3;
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e("encrypt", "encryption error, send plain msg");
            return str;
        }
    }
}
