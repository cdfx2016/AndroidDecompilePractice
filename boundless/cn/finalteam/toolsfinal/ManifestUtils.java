package cn.finalteam.toolsfinal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class ManifestUtils {
    public static String getMetaData(Context context, String metaKey) {
        String msg = "";
        try {
            msg = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString(metaKey);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(msg)) {
            return "";
        }
        return msg;
    }

    public static String getChannelNo(Context context, String channelKey) {
        return getMetaData(context, channelKey);
    }

    public static String getVersionName(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(version)) {
            return "";
        }
        return version;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return versionCode;
        }
    }
}
