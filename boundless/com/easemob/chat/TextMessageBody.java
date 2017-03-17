package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TextMessageBody extends MessageBody implements Parcelable {
    public static final Creator<TextMessageBody> CREATOR = new Creator<TextMessageBody>() {
        public TextMessageBody createFromParcel(Parcel parcel) {
            return new TextMessageBody(parcel);
        }

        public TextMessageBody[] newArray(int i) {
            return new TextMessageBody[i];
        }
    };
    String message;

    private TextMessageBody(Parcel parcel) {
        this.message = parcel.readString();
    }

    public TextMessageBody(String str) {
        this.message = str;
    }

    public int describeContents() {
        return 0;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "txt:\"" + this.message + "\"";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.message);
    }
}
