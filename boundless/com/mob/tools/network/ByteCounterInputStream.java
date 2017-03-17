package com.mob.tools.network;

import java.io.IOException;
import java.io.InputStream;

public class ByteCounterInputStream extends InputStream {
    private InputStream is;
    private OnReadListener listener;
    private long readBytes;

    public ByteCounterInputStream(InputStream is) {
        this.is = is;
    }

    public int read() throws IOException {
        int data = this.is.read();
        if (data >= 0) {
            this.readBytes++;
            if (this.listener != null) {
                this.listener.onRead(this.readBytes);
            }
        }
        return data;
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int len = this.is.read(buffer, byteOffset, byteCount);
        if (len > 0) {
            this.readBytes += (long) len;
            if (this.listener != null) {
                this.listener.onRead(this.readBytes);
            }
        }
        return len;
    }

    public void mark(int readlimit) {
        this.is.mark(readlimit);
    }

    public boolean markSupported() {
        return this.is.markSupported();
    }

    public synchronized void reset() throws IOException {
        this.is.reset();
        this.readBytes = 0;
    }

    public long skip(long byteCount) throws IOException {
        return this.is.skip(byteCount);
    }

    public int available() throws IOException {
        return this.is.available();
    }

    public void close() throws IOException {
        this.is.close();
    }

    public void setOnInputStreamReadListener(OnReadListener l) {
        this.listener = l;
    }
}
