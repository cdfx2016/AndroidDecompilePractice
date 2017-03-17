package cn.finalteam.galleryfinal.model;

import java.io.Serializable;

public class PhotoTempModel implements Serializable {
    private int orientation;
    private String sourcePath;

    public PhotoTempModel(String path) {
        this.sourcePath = path;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
