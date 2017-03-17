package cn.finalteam.toolsfinal.coder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;

public class MD5Coder {
    private static final String[] strDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", EntityCapsManager.ELEMENT, "d", "e", "f"};

    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < (byte) 0) {
            iRet += 256;
        }
        return strDigits[iRet / 16] + strDigits[iRet % 16];
    }

    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        if (iRet < (byte) 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (byte byteToArrayString : bByte) {
            sBuffer.append(byteToArrayString(byteToArrayString));
        }
        return sBuffer.toString();
    }

    public static String getMD5Code(String source) {
        NoSuchAlgorithmException ex;
        String resultString = null;
        try {
            String resultString2 = new String(source);
            try {
                resultString = byteToString(MessageDigest.getInstance("MD5").digest(source.getBytes()));
            } catch (NoSuchAlgorithmException e) {
                ex = e;
                resultString = resultString2;
                ex.printStackTrace();
                return resultString;
            }
        } catch (NoSuchAlgorithmException e2) {
            ex = e2;
            ex.printStackTrace();
            return resultString;
        }
        return resultString;
    }
}
