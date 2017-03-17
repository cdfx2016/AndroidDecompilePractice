package cn.finalteam.galleryfinal.model;

import android.text.TextUtils;
import java.io.Serializable;

public class PhotoInfo implements Serializable {
    private int height;
    private int photoId;
    private String photoPath;
    private int width;

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PhotoInfo)) {
            return false;
        }
        PhotoInfo info = (PhotoInfo) o;
        if (info != null) {
            return TextUtils.equals(info.getPhotoPath(), getPhotoPath());
        }
        return false;
    }
}
