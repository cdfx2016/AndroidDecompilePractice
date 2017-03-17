package org.jivesoftware.smack.util;

public interface StringEncoder {
    String decode(String str);

    String encode(String str);
}
