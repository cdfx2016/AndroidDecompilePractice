package org.jivesoftware.smack.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ObservableWriter extends Writer {
    List<WriterListener> listeners = new ArrayList();
    Writer wrappedWriter = null;

    public ObservableWriter(Writer writer) {
        this.wrappedWriter = writer;
    }

    private void notifyListeners(String str) {
        synchronized (this.listeners) {
            WriterListener[] writerListenerArr = new WriterListener[this.listeners.size()];
            this.listeners.toArray(writerListenerArr);
        }
        for (WriterListener write : writerListenerArr) {
            write.write(str);
        }
    }

    public void addWriterListener(WriterListener writerListener) {
        if (writerListener != null) {
            synchronized (this.listeners) {
                if (!this.listeners.contains(writerListener)) {
                    this.listeners.add(writerListener);
                }
            }
        }
    }

    public void close() throws IOException {
        this.wrappedWriter.close();
    }

    public void flush() throws IOException {
        this.wrappedWriter.flush();
    }

    public void removeWriterListener(WriterListener writerListener) {
        synchronized (this.listeners) {
            this.listeners.remove(writerListener);
        }
    }

    public void write(int i) throws IOException {
        this.wrappedWriter.write(i);
    }

    public void write(String str) throws IOException {
        this.wrappedWriter.write(str);
        notifyListeners(str);
    }

    public void write(String str, int i, int i2) throws IOException {
        this.wrappedWriter.write(str, i, i2);
        notifyListeners(str.substring(i, i + i2));
    }

    public void write(char[] cArr) throws IOException {
        this.wrappedWriter.write(cArr);
        notifyListeners(new String(cArr));
    }

    public void write(char[] cArr, int i, int i2) throws IOException {
        this.wrappedWriter.write(cArr, i, i2);
        notifyListeners(new String(cArr, i, i2));
    }
}
