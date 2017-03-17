package com.mob.commons;

import com.mob.tools.MobLog;
import com.mob.tools.utils.FileLocker;
import java.io.File;

/* compiled from: Locks */
public class e {
    public static final void a(File file, Runnable runnable) {
        a(file, true, runnable);
    }

    public static final void a(File file, boolean z, Runnable runnable) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileLocker fileLocker = new FileLocker();
            fileLocker.setLockFile(file.getAbsolutePath());
            if (fileLocker.lock(z)) {
                runnable.run();
                fileLocker.release();
            }
        } catch (Throwable th) {
            MobLog.getInstance().w(th);
        }
    }
}
