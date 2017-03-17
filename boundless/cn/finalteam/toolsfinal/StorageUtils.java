package cn.finalteam.toolsfinal;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;

public final class StorageUtils {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "cache";

    private StorageUtils() {
    }

    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true, INDIVIDUAL_DIR_NAME);
    }

    public static File getCacheDirectory(Context context, boolean isCache, String customDir) {
        return getCacheDirectory(context, true, isCache, customDir);
    }

    public static File getCacheDirectory(Context context, boolean preferExternal, boolean cache, String customDir) {
        String externalStorageState;
        File appCacheDir = null;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e2) {
            externalStorageState = "";
        }
        if (preferExternal && "mounted".equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, cache, customDir);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            return new File("/data/data/" + context.getPackageName() + "/" + customDir + "/");
        }
        return appCacheDir;
    }

    public static File getIndividualCacheDirectory(Context context) {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
    }

    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (individualCacheDir.exists() || individualCacheDir.mkdir()) {
            return individualCacheDir;
        }
        return appCacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if ("mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            return context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && "mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            return context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context, boolean cache, String customDir) {
        File appCacheDir = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), "Android"), DataPacketExtension.ELEMENT_NAME), context.getPackageName()), customDir);
        if (appCacheDir.exists()) {
            return appCacheDir;
        }
        if (!appCacheDir.mkdirs()) {
            return null;
        }
        try {
            new File(appCacheDir, ".nomedia").createNewFile();
            return appCacheDir;
        } catch (IOException e) {
            return appCacheDir;
        }
    }

    private static boolean hasExternalStoragePermission(Context context) {
        return context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION) == 0;
    }
}
