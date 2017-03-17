package com.mob.tools.network;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringPart extends HTTPPart {
    private StringBuilder sb = new StringBuilder();

    public StringPart append(String text) {
        this.sb.append(text);
        return this;
    }

    protected InputStream getInputStream() throws Throwable {
        return new ByteArrayInputStream(this.sb.toString().getBytes("utf-8"));
    }

    public String toString() {
        return this.sb.toString();
    }

    protected long length() throws Throwable {
        return (long) this.sb.toString().getBytes("utf-8").length;
    }
}
