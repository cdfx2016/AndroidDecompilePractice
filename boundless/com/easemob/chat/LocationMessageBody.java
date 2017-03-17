package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LocationMessageBody extends MessageBody implements Parcelable {
    public static final Creator<LocationMessageBody> CREATOR = new Creator<LocationMessageBody>() {
        public LocationMessageBody createFromParcel(Parcel parcel) {
            return new LocationMessageBody(parcel);
        }

        public LocationMessageBody[] newArray(int i) {
            return new LocationMessageBody[i];
        }
    };
    String address;
    double latitude;
    double longitude;

    private LocationMessageBody(Parcel parcel) {
        this.address = parcel.readString();
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
    }

    public LocationMessageBody(String str, double d, double d2) {
        this.address = str;
        this.latitude = d;
        this.longitude = d2;
    }

    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return this.address;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String toString() {
        return "location:" + this.address + ",lat:" + this.latitude + ",lng:" + this.longitude;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.address);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
    }
}
