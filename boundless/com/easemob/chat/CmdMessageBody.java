package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CmdMessageBody extends MessageBody implements Parcelable {
    public static final Creator<CmdMessageBody> CREATOR = new Creator<CmdMessageBody>() {
        public CmdMessageBody createFromParcel(Parcel parcel) {
            return new CmdMessageBody(parcel);
        }

        public CmdMessageBody[] newArray(int i) {
            return new CmdMessageBody[i];
        }
    };
    public String action;
    public String[] params;

    private CmdMessageBody(Parcel parcel) {
        this.action = parcel.readString();
        this.params = (String[]) parcel.readArray(null);
    }

    public CmdMessageBody(String str, String[] strArr) {
        this.action = str;
        this.params = strArr;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "cmd:\"" + this.action + "\"";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.action);
        parcel.writeStringArray(this.params);
    }
}
