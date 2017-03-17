package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class EMContact implements Parcelable {
    public static final Creator<EMContact> CREATOR = new Creator<EMContact>() {
        public EMContact createFromParcel(Parcel parcel) {
            return new EMContact(parcel);
        }

        public EMContact[] newArray(int i) {
            return new EMContact[i];
        }
    };
    protected String eid;
    protected String nick;
    protected String username;

    protected EMContact() {
    }

    private EMContact(Parcel parcel) {
        this.eid = parcel.readString();
        this.username = parcel.readString();
    }

    public EMContact(String str) {
        if (str.contains("@")) {
            this.eid = str;
            this.username = EMContactManager.getUserNameFromEid(str);
            return;
        }
        this.username = str;
        this.eid = EMContactManager.getEidFromUserName(str);
    }

    EMContact(String str, String str2) {
        this.eid = str;
        if (str2.contains("@")) {
            this.username = EMContactManager.getUserNameFromEid(str2);
        } else {
            this.username = str2;
        }
    }

    public int compare(EMContact eMContact) {
        return getNick().compareTo(eMContact.getNick());
    }

    public int describeContents() {
        return 0;
    }

    public String getNick() {
        return this.nick == null ? this.username : this.nick;
    }

    public String getUsername() {
        return this.username;
    }

    public void setNick(String str) {
        this.nick = str;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public String toString() {
        return "<contact jid:" + this.eid + ", username:" + this.username + ", nick:" + this.nick + ">";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.eid);
        parcel.writeString(this.username);
    }
}
