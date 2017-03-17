package com.fanyu.boundless.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;
import java.util.List;

public class SystemUtils {
    public static boolean isAppAlive(Context context, String packageName) {
        List<RunningAppProcessInfo> processInfos = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (((RunningAppProcessInfo) processInfos.get(i)).processName.equals(packageName)) {
                Log.i("NotificationLaunch", String.format("the %s is running, isAppAlive return true", new Object[]{packageName}));
                return true;
            }
        }
        Log.i("NotificationLaunch", String.format("the %s is not running, isAppAlive return false", new Object[]{packageName}));
        return false;
    }

    public static void startDetailActivity(Context context, String name, String price, String detail) {
    }
}
