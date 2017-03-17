package org.apache.harmony.javax.security.auth.callback;

import java.io.Serializable;

public class TextOutputCallback implements Callback, Serializable {
    public static final int ERROR = 2;
    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    private static final long serialVersionUID = 1689502495511663102L;
    private String message;
    private int messageType;

    public TextOutputCallback(int i, String str) {
        if (i > 2 || i < 0) {
            throw new IllegalArgumentException("auth.16");
        } else if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("auth.1F");
        } else {
            this.messageType = i;
            this.message = str;
        }
    }

    public String getMessage() {
        return this.message;
    }

    public int getMessageType() {
        return this.messageType;
    }
}
