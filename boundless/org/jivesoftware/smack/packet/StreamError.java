package org.jivesoftware.smack.packet;

public class StreamError {
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-streams";
    private String code;
    private String text;

    public StreamError(String str) {
        this.code = str;
    }

    public StreamError(String str, String str2) {
        this(str);
        this.text = str2;
    }

    public String getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("stream:error (").append(this.code).append(")");
        if (this.text != null) {
            stringBuilder.append(" text: ").append(this.text);
        }
        return stringBuilder.toString();
    }
}
