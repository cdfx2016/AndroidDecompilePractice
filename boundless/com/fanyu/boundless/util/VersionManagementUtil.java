package com.fanyu.boundless.util;

import android.content.Context;
import cn.finalteam.toolsfinal.io.FilenameUtils;

public class VersionManagementUtil {
    private static final VersionManagementUtil INSTANCE = new VersionManagementUtil();
    private static final String TAG = "VersionManagementUtil";
    private static Context mContext;

    public static VersionManagementUtil getInstance(Context mContext) {
        mContext = mContext;
        return INSTANCE;
    }

    public static String getVersion(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static int getVersionCode(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int VersionComparison(int versionServer, int versionLocal) {
        int version1 = versionServer;
        int version2 = versionLocal;
        if (version1 > version2) {
            return 1;
        }
        if (version1 < version2) {
            return 0;
        }
        return -1;
    }

    public static int VersionComparison(String versionServer, String versionLocal) {
        String version1 = versionServer;
        String version2 = versionLocal;
        if (version1 == null || version1.length() == 0 || version2 == null || version2.length() == 0) {
            throw new IllegalArgumentException("Invalid parameter!");
        }
        int index1 = 0;
        int index2 = 0;
        while (index1 < version1.length() && index2 < version2.length()) {
            int[] number1 = getValue(version1, index1);
            int[] number2 = getValue(version2, index2);
            if (number1[0] < number2[0]) {
                return -1;
            }
            if (number1[0] > number2[0]) {
                return 1;
            }
            index1 = number1[1] + 1;
            index2 = number2[1] + 1;
        }
        if (index1 == version1.length() && index2 == version2.length()) {
            return 0;
        }
        if (index1 < version1.length()) {
            return 1;
        }
        return -1;
    }

    public static int[] getValue(String version, int index) {
        int[] value_index = new int[2];
        StringBuilder sb = new StringBuilder();
        while (index < version.length() && version.charAt(index) != FilenameUtils.EXTENSION_SEPARATOR) {
            sb.append(version.charAt(index));
            index++;
        }
        value_index[0] = Integer.parseInt(sb.toString());
        value_index[1] = index;
        return value_index;
    }
}
