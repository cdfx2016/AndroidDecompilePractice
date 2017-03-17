package cn.finalteam.toolsfinal.coder;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

public class RSACoder {
    public static final String CHIPER_ALGORITHM = "RSA/ECB/";
    public static final String KEY_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 1024;
    public static final byte[] PUBLIC_EXPONENT = new byte[]{(byte) 1, (byte) 0, (byte) 1};

    public enum PADDING {
        NoPadding,
        PKCS1Padding
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024, new SecureRandom());
            return keyPairGen.genKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error when init key pair, errmsg: " + e.getMessage(), e);
        }
    }

    private static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, publicExponent)));
        } catch (Exception e) {
            throw new RuntimeException("Error when generate rsaPubblicKey, errmsg: " + e.getMessage(), e);
        }
    }

    private static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new RSAPrivateKeySpec(new BigInteger(1, modulus), new BigInteger(1, privateExponent)));
        } catch (Exception e) {
            throw new RuntimeException("Error when generate rsaPrivateKey, errmsg: " + e.getMessage(), e);
        }
    }

    private static byte[] encrypt(Key key, byte[] data, PADDING padding) {
        try {
            StringBuilder append = new StringBuilder().append(CHIPER_ALGORITHM);
            if (padding == null) {
                padding = PADDING.NoPadding;
            }
            Cipher cipher = Cipher.getInstance(append.append(padding).toString());
            cipher.init(1, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Error when encrypt data, errmsg: " + e.getMessage(), e);
        }
    }

    public static byte[] encryptByPublicKey(byte[] publicKey, byte[] data, PADDING padding) {
        return encrypt(generateRSAPublicKey(publicKey, PUBLIC_EXPONENT), data, padding);
    }

    public static byte[] encryptByPrivateKey(byte[] publicKey, byte[] privateKey, byte[] data, PADDING padding) {
        return encrypt(generateRSAPrivateKey(publicKey, privateKey), data, padding);
    }

    private static byte[] decrypt(Key key, byte[] data, PADDING padding) {
        try {
            StringBuilder append = new StringBuilder().append(CHIPER_ALGORITHM);
            if (padding == null) {
                padding = PADDING.NoPadding;
            }
            Cipher cipher = Cipher.getInstance(append.append(padding).toString());
            cipher.init(2, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Error when decrypt data, errmsg: " + e.getMessage(), e);
        }
    }

    public static byte[] decryptByPublicKey(byte[] publicKey, byte[] data, PADDING padding) {
        return decrypt(generateRSAPublicKey(publicKey, PUBLIC_EXPONENT), data, padding);
    }

    public static byte[] decryptByPrivateKey(byte[] publicKey, byte[] privateKey, byte[] data, PADDING padding) {
        return decrypt(generateRSAPrivateKey(publicKey, privateKey), data, padding);
    }
}
