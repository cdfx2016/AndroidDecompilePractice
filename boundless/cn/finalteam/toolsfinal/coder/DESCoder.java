package cn.finalteam.toolsfinal.coder;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESCoder {
    public static byte[] encrypt(byte[] data, String password) {
        try {
            SecureRandom random = new SecureRandom();
            SecretKey securekey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(password.getBytes()));
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(1, securekey, random);
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] src, String password) {
        try {
            SecureRandom random = new SecureRandom();
            SecretKey securekey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(password.getBytes()));
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(2, securekey, random);
            return cipher.doFinal(src);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
