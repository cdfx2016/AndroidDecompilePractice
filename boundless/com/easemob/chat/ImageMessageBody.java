package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.easemob.util.EMLog;
import java.io.File;

public class ImageMessageBody extends FileMessageBody implements Parcelable {
    public static final Creator<ImageMessageBody> CREATOR = new Creator<ImageMessageBody>() {
        public ImageMessageBody createFromParcel(Parcel parcel) {
            return new ImageMessageBody(parcel);
        }

        public ImageMessageBody[] newArray(int i) {
            return new ImageMessageBody[i];
        }
    };
    private boolean sendOriginalImage;
    String thumbnailSecret;
    String thumbnailUrl;

    public ImageMessageBody() {
        this.thumbnailSecret = null;
    }

    private ImageMessageBody(Parcel parcel) {
        this.thumbnailSecret = null;
        this.fileName = parcel.readString();
        this.localUrl = parcel.readString();
        this.remoteUrl = parcel.readString();
        this.thumbnailUrl = parcel.readString();
    }

    public ImageMessageBody(File file) throws IllegalArgumentException {
        this.thumbnailSecret = null;
        if (file.exists()) {
            this.localUrl = file.getAbsolutePath();
            this.fileName = file.getName();
            EMLog.d("imagemsg", "create image message body for:" + file.getAbsolutePath());
            return;
        }
        throw new IllegalArgumentException("image doesn't exists:" + file.getAbsolutePath());
    }

    ImageMessageBody(String str, String str2, String str3) {
        this.thumbnailSecret = null;
        this.fileName = str;
        this.remoteUrl = str2;
        this.thumbnailUrl = str3;
    }

    public int describeContents() {
        return 0;
    }

    public String getThumbnailSecret() {
        return this.thumbnailSecret;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public boolean isSendOriginalImage() {
        return this.sendOriginalImage;
    }

    public void setSendOriginalImage(boolean z) {
        this.sendOriginalImage = z;
    }

    public void setThumbnailSecret(String str) {
        this.thumbnailSecret = str;
    }

    public void setThumbnailUrl(String str) {
        this.thumbnailUrl = str;
    }

    public String toString() {
        return "image:" + this.fileName + ",localurl:" + this.localUrl + ",remoteurl:" + this.remoteUrl + ",thumbnial:" + this.thumbnailUrl;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.fileName);
        parcel.writeString(this.localUrl);
        parcel.writeString(this.remoteUrl);
        parcel.writeString(this.thumbnailUrl);
    }
}
