package com.mob.tools.utils;

import java.io.FileOutputStream;
import java.nio.channels.FileLock;

public class FileLocker {
    private FileOutputStream fos;
    private FileLock lock;

    public synchronized void setLockFile(String path) {
        try {
            this.fos = new FileOutputStream(path);
        } catch (Throwable th) {
        }
        this.fos = null;
    }

    public synchronized boolean lock(boolean block) {
        boolean z = false;
        synchronized (this) {
            if (this.fos != null) {
                if (block) {
                    try {
                        this.lock = this.fos.getChannel().lock();
                    } catch (Throwable th) {
                    }
                } else {
                    this.lock = this.fos.getChannel().tryLock();
                }
                if (this.lock != null) {
                    z = true;
                }
            }
        }
        return z;
        this.lock = null;
        if (this.lock != null) {
            z = true;
        }
        return z;
    }

    public synchronized void lock(Runnable onLock, boolean block) {
        if (lock(block) && onLock != null) {
            onLock.run();
        }
    }

    public synchronized void unlock() {
        if (this.lock != null) {
            try {
                this.lock.release();
                this.lock = null;
            } catch (Throwable th) {
            }
        }
    }

    public synchronized void release() {
        if (this.fos != null) {
            unlock();
            try {
                this.fos.close();
                this.fos = null;
            } catch (Throwable th) {
            }
        }
    }
}
