package com.easemob.cloud;

import android.text.format.Time;
import com.fanyu.boundless.util.FileUtil;
import java.util.Map;
import java.util.Properties;

public abstract class CloudFileManager {
    protected static final String TAG = "CloudFileManager";
    public static CloudFileManager instance = null;
    protected Properties sessionContext;

    public abstract boolean authorization();

    public abstract void deleteFileInBackground(String str, String str2, String str3, CloudOperationCallback cloudOperationCallback);

    public abstract void downloadFile(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback);

    public String getRemoteFileName(String str, String str2) {
        Time time = new Time();
        time.setToNow();
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str)).append(time.toString().substring(0, 15)).toString())).append(str2.substring(str2.lastIndexOf(FileUtil.FILE_EXTENSION_SEPARATOR), str2.length())).toString();
    }

    public abstract void uploadFileInBackground(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback);
}
