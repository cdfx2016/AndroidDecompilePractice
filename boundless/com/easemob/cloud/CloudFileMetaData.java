package com.easemob.cloud;

public class CloudFileMetaData {
    String lastModified;
    String name;
    String path;
    String size;

    public String getLastModified() {
        return this.lastModified;
    }

    public String getName() {
        return this.name;
    }

    public String getSize() {
        return this.size;
    }

    public void setLastModified(String str) {
        this.lastModified = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setSize(String str) {
        this.size = str;
    }

    public String toString() {
        return this.name;
    }
}
