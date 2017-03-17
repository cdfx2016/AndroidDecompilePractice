package cn.finalteam.toolsfinal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtils {
    public static void install(Context context, File uriFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
        intent.setFlags(268435456);
        context.startActivity(intent);
    }

    public static void uninstall(Context context, String packageName) {
        context.startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + packageName)));
    }

    public static boolean isAvilible(Context context, String packageName) {
        List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
        List<String> packageNames = new ArrayList();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                packageNames.add(((PackageInfo) packageInfos.get(i)).packageName);
            }
        }
        return packageNames.contains(packageName);
    }

    public static String getChannelFromApk(Context context, String channelPrefix) {
        IOException e;
        String[] split;
        String channel;
        Throwable th;
        String sourceDir = context.getApplicationInfo().sourceDir;
        String key = "META-INF/" + channelPrefix;
        String ret = "";
        ZipFile zipfile = null;
        try {
            ZipFile zipfile2 = new ZipFile(sourceDir);
            try {
                Enumeration<?> entries = zipfile2.entries();
                while (entries.hasMoreElements()) {
                    String entryName = ((ZipEntry) entries.nextElement()).getName();
                    if (entryName.startsWith(key)) {
                        ret = entryName;
                        break;
                    }
                }
                if (zipfile2 != null) {
                    try {
                        zipfile2.close();
                        zipfile = zipfile2;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        zipfile = zipfile2;
                    }
                }
            } catch (IOException e3) {
                e2 = e3;
                zipfile = zipfile2;
                try {
                    e2.printStackTrace();
                    if (zipfile != null) {
                        try {
                            zipfile.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    split = ret.split(channelPrefix);
                    channel = "";
                    return split != null ? channel : channel;
                } catch (Throwable th2) {
                    th = th2;
                    if (zipfile != null) {
                        try {
                            zipfile.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                zipfile = zipfile2;
                if (zipfile != null) {
                    zipfile.close();
                }
                throw th;
            }
        } catch (IOException e4) {
            e222 = e4;
            e222.printStackTrace();
            if (zipfile != null) {
                zipfile.close();
            }
            split = ret.split(channelPrefix);
            channel = "";
            if (split != null) {
            }
        }
        split = ret.split(channelPrefix);
        channel = "";
        if (split != null && split.length >= 2) {
            return ret.substring(key.length());
        }
    }
}
