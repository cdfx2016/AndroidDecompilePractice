package com.mob.tools.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferedByteArrayOutputStream extends ByteArrayOutputStream {
    public BufferedByteArrayOutputStream(int size) {
        super(size);
    }

    public boolean switchBuffer(byte[] newBuf) {
        if (newBuf == null || newBuf.length != this.buf.length) {
            return false;
        }
        byte[] o = this.buf;
        this.buf = newBuf;
        newBuf = o;
        return true;
    }

    public byte[] getBuffer() {
        return this.buf;
    }

    public int getBufferSize() {
        return this.buf.length;
    }

    public void write(ByteBuffer buffer) throws IOException {
        write(buffer, buffer.limit());
    }

    public void write(ByteBuffer buffer, int size) throws IOException {
        if (this.buf.length - this.count >= size) {
            buffer.get(this.buf, this.count, size);
            this.count += size;
            return;
        }
        byte[] outData = new byte[size];
        buffer.get(outData);
        write(outData);
    }
}
