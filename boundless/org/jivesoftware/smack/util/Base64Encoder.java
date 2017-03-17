package org.jivesoftware.smack.util;

public class Base64Encoder implements StringEncoder {
    private static Base64Encoder instance = new Base64Encoder();

    private Base64Encoder() {
    }

    public static Base64Encoder getInstance() {
        return instance;
    }

    public String decode(String str) {
        return new String(Base64.decode(str));
    }

    public String encode(String str) {
        return Base64.encodeBytes(str.getBytes());
    }
}
