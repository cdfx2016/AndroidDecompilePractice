package com.easemob.util;

import android.os.Parcel;
import android.os.Parcelable;

public class LatLng implements Parcelable {
    public double latitude;
    public double longitude;

    public LatLng(double d, double d2) {
        this.latitude = d;
        this.longitude = d2;
    }

    public LatLng(Parcel parcel) {
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
    }
}
