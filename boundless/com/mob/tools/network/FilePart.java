package com.mob.tools.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FilePart extends HTTPPart {
    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    public void setFile(String path) {
        this.file = new File(path);
    }

    protected InputStream getInputStream() throws Throwable {
        return new FileInputStream(this.file);
    }

    public String toString() {
        return this.file.toString();
    }

    protected long length() throws Throwable {
        return this.file.length();
    }
}
