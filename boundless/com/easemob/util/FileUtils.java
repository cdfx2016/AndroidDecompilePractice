package com.easemob.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.fanyu.boundless.util.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileUtils {
    public static String[] fileTypes = new String[]{"apk", "avi", "bmp", "chm", "dll", "doc", "docx", "dos", "gif", "html", "jpeg", "jpg", "movie", "mp3", "dat", "mp4", "mpe", "mpeg", "mpg", "pdf", "png", "ppt", "pptx", "rar", "txt", "wav", "wma", "wmv", "xls", "xlsx", "xml", "zip"};

    public static class MyComparator implements Comparator<File> {
        public int compare(File file, File file2) {
            return file.getName().compareTo(file2.getName());
        }
    }

    public static String getMIMEType(File file) {
        String str = "";
        str = file.getName();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(str.lastIndexOf(FileUtil.FILE_EXTENSION_SEPARATOR) + 1, str.length()).toLowerCase());
    }

    public static String getMIMEType(String str) {
        String str2 = "";
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(str.lastIndexOf(FileUtil.FILE_EXTENSION_SEPARATOR) + 1, str.length()).toLowerCase());
    }

    public static File[] loadFiles(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            listFiles = new File[0];
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                arrayList.add(file2);
            } else if (file2.isFile()) {
                arrayList2.add(file2);
            }
        }
        Comparator myComparator = new MyComparator();
        Collections.sort(arrayList, myComparator);
        Collections.sort(arrayList2, myComparator);
        Object obj = new File[(arrayList.size() + arrayList2.size())];
        System.arraycopy(arrayList.toArray(), 0, obj, 0, arrayList.size());
        System.arraycopy(arrayList2.toArray(), 0, obj, arrayList.size(), arrayList2.size());
        return obj;
    }

    public static void openFile(Uri uri, String str, Activity activity) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(uri, str);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "没有找到打开此类文件的程序", 1).show();
        }
    }

    public static void openFile(File file, Activity activity) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "没有找到打开此类文件的程序", 1).show();
        }
    }

    public static synchronized Object readObjectFromFile(File file) throws Exception {
        Object readObject;
        synchronized (FileUtils.class) {
            readObject = new ObjectInputStream(new FileInputStream(file)).readObject();
        }
        return readObject;
    }

    public static synchronized void saveObjectToFile(Object obj, File file) throws Exception {
        synchronized (FileUtils.class) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
    }
}
