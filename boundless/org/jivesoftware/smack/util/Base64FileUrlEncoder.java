package org.jivesoftware.smack.util;

public class Base64FileUrlEncoder implements StringEncoder {
    private static Base64FileUrlEncoder instance = new Base64FileUrlEncoder();

    private Base64FileUrlEncoder() {
    }

    public static Base64FileUrlEncoder getInstance() {
        return instance;
    }

    public String decode(String str) {
        return new String(Base64.decode(str, 16));
    }

    public String encode(String str) {
        return Base64.encodeBytes(str.getBytes(), 16);
    }
}
