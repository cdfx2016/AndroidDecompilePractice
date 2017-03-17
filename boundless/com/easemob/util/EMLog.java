package com.easemob.util;

import android.util.Log;

public class EMLog {
    public static boolean debugMode = false;

    public static void d(String str, String str2) {
        if (debugMode) {
            Log.d(str, str2);
        }
    }

    public static void d(String str, String str2, Throwable th) {
        if (debugMode) {
            Log.d(str, str2, th);
        }
    }

    public static void e(String str, String str2) {
        Log.e(str, str2);
    }

    public static void e(String str, String str2, Throwable th) {
        Log.e(str, str2, th);
    }

    public static void i(String str, String str2) {
        Log.i(str, str2);
    }

    public static void v(String str, String str2) {
        if (debugMode) {
            Log.v(str, str2);
        }
    }

    public static void w(String str, String str2) {
        Log.w(str, str2);
    }

    public static void w(String str, String str2, Throwable th) {
        if (debugMode) {
            Log.w(str, str2, th);
        }
    }
}
