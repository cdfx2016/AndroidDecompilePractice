package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.easemob.util.EMLog;
import java.io.File;

public class VoiceMessageBody extends FileMessageBody implements Parcelable {
    public static final Creator<VoiceMessageBody> CREATOR = new Creator<VoiceMessageBody>() {
        public VoiceMessageBody createFromParcel(Parcel parcel) {
            return new VoiceMessageBody(parcel);
        }

        public VoiceMessageBody[] newArray(int i) {
            return new VoiceMessageBody[i];
        }
    };
    int length;

    private VoiceMessageBody(Parcel parcel) {
        this.length = 0;
        this.fileName = parcel.readString();
        this.localUrl = parcel.readString();
        this.remoteUrl = parcel.readString();
        this.length = parcel.readInt();
    }

    public VoiceMessageBody(File file, int i) {
        this.length = 0;
        if (file.exists()) {
            this.localUrl = file.getAbsolutePath();
            this.fileName = file.getName();
            this.length = i;
            EMLog.d("voicemsg", "create voice, message body for:" + file.getAbsolutePath());
            return;
        }
        throw new IllegalArgumentException("voice file doesn't exists:" + file.getAbsolutePath());
    }

    VoiceMessageBody(String str, String str2, int i) {
        this.length = 0;
        this.fileName = str;
        this.remoteUrl = str2;
        this.length = i;
    }

    public int describeContents() {
        return 0;
    }

    public int getLength() {
        return this.length;
    }

    public String toString() {
        return "voice:" + this.fileName + ",localurl:" + this.localUrl + ",remoteurl:" + this.remoteUrl + ",length:" + this.length;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.fileName);
        parcel.writeString(this.localUrl);
        parcel.writeString(this.remoteUrl);
        parcel.writeInt(this.length);
    }
}
