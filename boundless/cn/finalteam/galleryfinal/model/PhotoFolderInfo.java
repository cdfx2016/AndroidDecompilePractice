package cn.finalteam.galleryfinal.model;

import java.io.Serializable;
import java.util.List;

public class PhotoFolderInfo implements Serializable {
    private PhotoInfo coverPhoto;
    private int folderId;
    private String folderName;
    private List<PhotoInfo> photoList;

    public int getFolderId() {
        return this.folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public PhotoInfo getCoverPhoto() {
        return this.coverPhoto;
    }

    public void setCoverPhoto(PhotoInfo coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<PhotoInfo> getPhotoList() {
        return this.photoList;
    }

    public void setPhotoList(List<PhotoInfo> photoList) {
        this.photoList = photoList;
    }
}
