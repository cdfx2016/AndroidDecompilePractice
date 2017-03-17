package cn.finalteam.toolsfinal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private boolean mCrashSave;
    private String mCrashSaveTargetFolder;
    private UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> mDeviceInfoMap = new HashMap();
    private OnCrashListener mOnCrashListener;

    public interface OnCrashListener {
        void onCrash(Context context, String str);
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public CrashHandler init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        return this;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (handleException(ex) || this.mDefaultHandler == null) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        this.mDefaultHandler.uncaughtException(thread, ex);
    }

    public boolean handleException(Throwable ex) {
        Thread.setDefaultUncaughtExceptionHandler(null);
        if (ex == null) {
            return false;
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(pw);
        }
        pw.close();
        final String crashMsg = writer.toString();
        new Thread() {
            public void run() {
                Looper.prepare();
                if (CrashHandler.this.mOnCrashListener != null) {
                    CrashHandler.this.mOnCrashListener.onCrash(CrashHandler.this.mContext, crashMsg);
                }
                Looper.loop();
            }
        }.start();
        if (this.mCrashSave) {
            collectDeviceInfo(this.mContext);
            saveCrashInfo2File(crashMsg);
        }
        return true;
    }

    public CrashHandler setOnCrashListener(OnCrashListener listener) {
        this.mOnCrashListener = listener;
        return this;
    }

    public CrashHandler setCrashSave(boolean isSave) {
        this.mCrashSave = isSave;
        return this;
    }

    public CrashHandler setCrashSaveTargetFolder(String targetFolder) {
        this.mCrashSaveTargetFolder = targetFolder;
        return this;
    }

    private void collectDeviceInfo(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
            if (pi != null) {
                String versionCode = pi.versionCode + "";
                this.mDeviceInfoMap.put("versionName", pi.versionName == null ? "null" : pi.versionName);
                this.mDeviceInfoMap.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.mDeviceInfoMap.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(String crashMsg) {
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> entry : this.mDeviceInfoMap.entrySet()) {
            String value = (String) entry.getValue();
            sb.append(((String) entry.getKey()) + "=" + value + IOUtils.LINE_SEPARATOR_WINDOWS);
        }
        sb.append(crashMsg);
        String fileName = "crash-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + Constants.ACCEPT_TIME_SEPARATOR_SERVER + System.currentTimeMillis() + ".log";
        if (Environment.getExternalStorageState().equals("mounted")) {
            try {
                File dir;
                if (StringUtils.isEmpty(this.mCrashSaveTargetFolder)) {
                    dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
                } else {
                    dir = new File(this.mCrashSaveTargetFolder);
                }
                if (!dir.exists()) {
                    dir.mkdir();
                }
                FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
}
