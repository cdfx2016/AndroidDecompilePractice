package com.mob.commons;

import android.content.Context;
import com.mob.tools.MobLog;
import com.mob.tools.utils.ResHelper;
import java.io.File;

/* compiled from: DeviceLevelTags */
public class d {
    public static synchronized boolean a(Context context, String str) {
        boolean exists;
        synchronized (d.class) {
            try {
                exists = new File(ResHelper.getCacheRoot(context), str).exists();
            } catch (Throwable th) {
                MobLog.getInstance().w(th);
                exists = true;
            }
        }
        return exists;
    }

    public static synchronized void b(Context context, String str) {
        synchronized (d.class) {
            try {
                File file = new File(ResHelper.getCacheRoot(context), str);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (Throwable th) {
                MobLog.getInstance().w(th);
            }
        }
    }

    public static synchronized void c(Context context, String str) {
        synchronized (d.class) {
            try {
                File file = new File(ResHelper.getCacheRoot(context), str);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Throwable th) {
                MobLog.getInstance().w(th);
            }
        }
    }
}
