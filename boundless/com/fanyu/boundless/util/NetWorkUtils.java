package com.fanyu.boundless.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
    private static NetWorkUtils mInstance;

    public static NetWorkUtils getInstance() {
        if (mInstance == null) {
            mInstance = new NetWorkUtils();
        }
        return mInstance;
    }

    public String getCurrentNetType(Context context) {
        String type = "";
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null) {
            return "null";
        }
        if (info.getType() == 1) {
            return "wifi";
        }
        if (info.getType() != 0) {
            return type;
        }
        int subType = info.getSubtype();
        if (subType == 4 || subType == 1 || subType == 2) {
            return "2g";
        }
        if (subType == 3 || subType == 8 || subType == 6 || subType == 5 || subType == 12) {
            return "3g";
        }
        if (subType == 13) {
            return "4g";
        }
        return type;
    }
}
