package org.jivesoftware.smackx.bytestreams.socks5;

import java.io.DataInputStream;
import java.io.IOException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;

class Socks5Utils {
    Socks5Utils() {
    }

    public static String createDigest(String str, String str2, String str3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str).append(str2).append(str3);
        return StringUtils.hash(stringBuilder.toString());
    }

    public static byte[] receiveSocks5Message(DataInputStream dataInputStream) throws IOException, XMPPException {
        Object obj = new byte[5];
        dataInputStream.readFully(obj, 0, 5);
        if (obj[3] != (byte) 3) {
            throw new XMPPException("Unsupported SOCKS5 address type");
        }
        byte b = obj[4];
        Object obj2 = new byte[(b + 7)];
        System.arraycopy(obj, 0, obj2, 0, obj.length);
        dataInputStream.readFully(obj2, obj.length, b + 2);
        return obj2;
    }
}
