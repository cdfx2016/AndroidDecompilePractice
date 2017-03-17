package cn.finalteam.toolsfinal.coder;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DES3Coder {
    private static final String Algorithm = "DESede";

    public static byte[] encryptMode(byte[] src, String password) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(password), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(1, deskey);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
            return null;
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptMode(byte[] src, String password) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(password), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(2, deskey);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
            return null;
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }

    private static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes("UTF-8");
        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
}
