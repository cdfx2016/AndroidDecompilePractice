package org.jivesoftware.smack.parsing;

public class UnparsablePacket {
    private final String content;
    private final Exception e;

    public UnparsablePacket(String str, Exception exception) {
        this.content = str;
        this.e = exception;
    }

    public String getContent() {
        return this.content;
    }

    public Exception getParsingException() {
        return this.e;
    }
}
