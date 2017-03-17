package com.easemob.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;

public class NetUtils {
    private static final int HIGH_SPEED_DOWNLOAD_BUF_SIZE = 30720;
    private static final int HIGH_SPEED_UPLOAD_BUF_SIZE = 10240;
    private static final int LOW_SPEED_DOWNLOAD_BUF_SIZE = 2024;
    private static final int LOW_SPEED_UPLOAD_BUF_SIZE = 1024;
    private static final int MAX_SPEED_DOWNLOAD_BUF_SIZE = 102400;
    private static final int MAX_SPEED_UPLOAD_BUF_SIZE = 102400;
    private static final String TAG = "net";

    public static int getDownloadBufSize(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return (activeNetworkInfo == null || activeNetworkInfo.getType() != 1) ? (activeNetworkInfo == null && isConnectionFast(activeNetworkInfo.getType(), activeNetworkInfo.getSubtype())) ? HIGH_SPEED_DOWNLOAD_BUF_SIZE : LOW_SPEED_DOWNLOAD_BUF_SIZE : 102400;
    }

    public static int getUploadBufSize(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return (activeNetworkInfo == null || activeNetworkInfo.getType() != 1) ? (activeNetworkInfo == null && isConnectionFast(activeNetworkInfo.getType(), activeNetworkInfo.getSubtype())) ? HIGH_SPEED_UPLOAD_BUF_SIZE : 1024 : 102400;
    }

    public static boolean hasDataConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
            if (networkInfo2 == null || !networkInfo2.isConnectedOrConnecting()) {
                EMLog.d(TAG, "no data connection");
                return false;
            }
            EMLog.d(TAG, "has mobile connection");
            return true;
        }
        EMLog.d(TAG, "has wifi connection");
        return true;
    }

    public static boolean hasNetwork(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static boolean isConnectionFast(int i, int i2) {
        if (i == 1) {
            return true;
        }
        if (i == 0) {
            switch (i2) {
                case 1:
                    return false;
                case 2:
                    return false;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                    return true;
                case 4:
                    return false;
                case 7:
                    return false;
                default:
                    if (VERSION.SDK_INT >= 11 && (i2 == 14 || i2 == 13)) {
                        return true;
                    }
                    if (VERSION.SDK_INT >= 9 && i2 == 12) {
                        return true;
                    }
                    if (VERSION.SDK_INT >= 8 && i2 == 11) {
                        return false;
                    }
                    break;
            }
        }
        return false;
    }
}
