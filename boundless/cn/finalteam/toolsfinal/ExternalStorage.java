package cn.finalteam.toolsfinal;

import android.os.Environment;
import com.easemob.util.HanziToPinyin.Token;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExternalStorage {
    public static final String EXTERNAL_SD_CARD = "externalSdCard";
    public static final String SD_CARD = "sdCard";

    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state) || "mounted_ro".equals(state)) {
            return true;
        }
        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    public static boolean isWritable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static Map<String, File> getAllStorageLocations() {
        File file;
        String line;
        String element;
        Map<String, File> hashMap = new HashMap(10);
        List<String> mMounts = new ArrayList(10);
        List<String> mVold = new ArrayList(10);
        mMounts.add("/mnt/sdcard");
        mVold.add("/mnt/sdcard");
        try {
            file = new File("/proc/mounts");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        element = line.split(Token.SEPARATOR)[1];
                        if (!element.equals("/mnt/sdcard")) {
                            mMounts.add(element);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File voldFile = new File("/system/etc/vold.fstab");
            if (voldFile.exists()) {
                Scanner scanner2 = new Scanner(voldFile);
                while (scanner2.hasNext()) {
                    line = scanner2.nextLine();
                    if (line.startsWith("dev_mount")) {
                        element = line.split(Token.SEPARATOR)[2];
                        if (element.contains(":")) {
                            element = element.substring(0, element.indexOf(":"));
                        }
                        if (!element.equals("/mnt/sdcard")) {
                            mVold.add(element);
                        }
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        int i = 0;
        while (i < mMounts.size()) {
            if (!mVold.contains((String) mMounts.get(i))) {
                int i2 = i - 1;
                mMounts.remove(i);
                i = i2;
            }
            i++;
        }
        mVold.clear();
        List<String> arrayList = new ArrayList(10);
        for (String mount : mMounts) {
            file = new File(mount);
            if (file.exists() && file.isDirectory() && file.canWrite()) {
                File[] list = file.listFiles();
                String hash = "[";
                if (list != null) {
                    for (File f : list) {
                        hash = hash + f.getName().hashCode() + ":" + f.length() + ", ";
                    }
                }
                hash = hash + "]";
                if (!arrayList.contains(hash)) {
                    String key = "sdCard_" + hashMap.size();
                    if (hashMap.size() == 0) {
                        key = SD_CARD;
                    } else if (hashMap.size() == 1) {
                        key = EXTERNAL_SD_CARD;
                    }
                    arrayList.add(hash);
                    hashMap.put(key, file);
                }
            }
        }
        mMounts.clear();
        if (hashMap.isEmpty()) {
            hashMap.put(SD_CARD, Environment.getExternalStorageDirectory());
        }
        return hashMap;
    }
}
