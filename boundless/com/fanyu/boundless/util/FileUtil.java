package com.fanyu.boundless.util;

import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileUtil {
    public static final String FILE_EXTENSION_SEPARATOR = ".";
    public static File updateDir = null;
    public static File updateFile = null;

    public static void createFile(String name) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory() + "/wuya");
            updateFile = new File(updateDir + "/" + name + ".apk");
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean hasSdcard() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static void delete(String dir, FilenameFilter filter) {
        if (!TextUtils.isEmpty(dir)) {
            File file = new File(dir);
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                if (file.isDirectory()) {
                    File[] lists;
                    if (filter != null) {
                        lists = file.listFiles(filter);
                    } else {
                        lists = file.listFiles();
                    }
                    if (lists != null) {
                        for (File f : lists) {
                            if (f.isFile()) {
                                f.delete();
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            if (extenPosi != -1) {
                return filePath.substring(0, extenPosi);
            }
            return filePath;
        } else if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        } else {
            String substring;
            if (filePosi < extenPosi) {
                substring = filePath.substring(filePosi + 1, extenPosi);
            } else {
                substring = filePath.substring(filePosi + 1);
            }
            return substring;
        }
    }
}
