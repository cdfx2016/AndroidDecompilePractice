package org.jivesoftware.smack.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class Java7ZlibInputOutputStream extends XMPPInputOutputStream {
    private static final int compressionLevel = -1;
    private static final Method method;
    private static final boolean supported = (method != null);

    static {
        Method method = null;
        try {
            method = Deflater.class.getMethod("deflate", new Class[]{byte[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e2) {
        }
        method = method;
    }

    public Java7ZlibInputOutputStream() {
        this.compressionMethod = "zlib";
    }

    public InputStream getInputStream(InputStream inputStream) {
        return new InflaterInputStream(inputStream, new Inflater(), 512) {
            public int available() throws IOException {
                return this.inf.needsInput() ? 0 : super.available();
            }
        };
    }

    public OutputStream getOutputStream(OutputStream outputStream) {
        return new DeflaterOutputStream(outputStream, new Deflater(-1)) {
            public void flush() throws IOException {
                if (Java7ZlibInputOutputStream.supported) {
                    int deflate;
                    if (!this.def.needsInput()) {
                        do {
                            deflate = this.def.deflate(this.buf, 0, this.buf.length);
                            this.out.write(this.buf, 0, deflate);
                        } while (deflate > 0);
                        this.out.flush();
                    }
                    do {
                        try {
                            deflate = ((Integer) Java7ZlibInputOutputStream.method.invoke(this.def, new Object[]{this.buf, Integer.valueOf(0), Integer.valueOf(this.buf.length), Integer.valueOf(2)})).intValue();
                            this.out.write(this.buf, 0, deflate);
                        } catch (IllegalArgumentException e) {
                            throw new IOException("Can't flush");
                        } catch (IllegalAccessException e2) {
                            throw new IOException("Can't flush");
                        } catch (InvocationTargetException e3) {
                            throw new IOException("Can't flush");
                        }
                    } while (deflate > 0);
                    super.flush();
                    return;
                }
                super.flush();
            }
        };
    }

    public boolean isSupported() {
        return supported;
    }
}
