package com.fanyu.boundless.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
    private static final String DB_BASE_NAME = "fanyu_file";
    private static SharedPreferencesUtil sInstances;
    private SharedPreferences mSharedPreferences;

    public static SharedPreferencesUtil getsInstances(Context context) {
        if (sInstances == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sInstances == null) {
                    sInstances = new SharedPreferencesUtil(context);
                }
            }
        }
        return sInstances;
    }

    private SharedPreferencesUtil(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(DB_BASE_NAME, 0);
    }

    public void putString(String key, String value) {
        Editor editor = this.mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        Editor editor = this.mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        Editor editor = this.mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return this.mSharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return this.mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.mSharedPreferences.getBoolean(key, defaultValue);
    }
}
