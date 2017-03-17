package org.jivesoftware.smack.compression;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class JzlibInputOutputStream extends XMPPInputOutputStream {
    private static Class<?> ziClass;
    private static Class<?> zoClass;

    static {
        zoClass = null;
        ziClass = null;
        try {
            zoClass = Class.forName("com.jcraft.jzlib.ZOutputStream");
            ziClass = Class.forName("com.jcraft.jzlib.ZInputStream");
        } catch (ClassNotFoundException e) {
        }
    }

    public JzlibInputOutputStream() {
        this.compressionMethod = "zlib";
    }

    public InputStream getInputStream(InputStream inputStream) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object newInstance = ziClass.getConstructor(new Class[]{InputStream.class}).newInstance(new Object[]{inputStream});
        ziClass.getMethod("setFlushMode", new Class[]{Integer.TYPE}).invoke(newInstance, new Object[]{Integer.valueOf(2)});
        return (InputStream) newInstance;
    }

    public OutputStream getOutputStream(OutputStream outputStream) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object newInstance = zoClass.getConstructor(new Class[]{OutputStream.class, Integer.TYPE}).newInstance(new Object[]{outputStream, Integer.valueOf(9)});
        zoClass.getMethod("setFlushMode", new Class[]{Integer.TYPE}).invoke(newInstance, new Object[]{Integer.valueOf(2)});
        return (OutputStream) newInstance;
    }

    public boolean isSupported() {
        return (zoClass == null || ziClass == null) ? false : true;
    }
}
