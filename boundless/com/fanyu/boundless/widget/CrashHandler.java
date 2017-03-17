package com.fanyu.boundless.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.fanyu.boundless.bean.home.UploadLogApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.UploadLogPresenter;
import com.fanyu.boundless.util.FileUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.home.UploadLogView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressLint({"SimpleDateFormat"})
public class CrashHandler implements UncaughtExceptionHandler, UploadLogView {
    public static String TAG = "MyCrash";
    private static CrashHandler instance = new CrashHandler();
    private DateFormat formatter = new SimpleDateFormat(StringUtils.DEFAULT_FORMAT_DATE);
    private Map<String, String> infos = new HashMap();
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;
    private UploadLogPresenter mPresenter;
    private String pathname;
    Runnable runnable = new Runnable() {
        public void run() {
            File file = null;
            try {
                CrashHandler.makeRootDirectory(CrashHandler.getGlobalpath());
                try {
                    file = new File(CrashHandler.this.pathname);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("filefile```", file.getName());
                UploadLogApi uploadLogApi = new UploadLogApi();
                uploadLogApi.setFile(file);
                uploadLogApi.setFilename(file.getName());
                CrashHandler.this.mPresenter.startPost((RxAppCompatActivity) CrashHandler.this.mContext, uploadLogApi);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    };
    private SharedPreferences sharedPreferences;
    private String userphoneString;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.userphoneString = this.sharedPreferences.getString(Preferences.USER_ID, "");
        Thread.setDefaultUncaughtExceptionHandler(this);
        autoClear(5);
        this.mPresenter = new UploadLogPresenter(this.mContext, this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (handleException(ex) || this.mDefaultHandler == null) {
            SystemClock.sleep(1000);
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        this.mDefaultHandler.uncaughtException(thread, ex);
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        try {
            new Thread() {
                public void run() {
                    Looper.prepare();
                    Toast.makeText(CrashHandler.this.mContext, "很抱歉,程序出现异常,即将退出.", 1).show();
                    Looper.loop();
                }
            }.start();
            collectDeviceInfo(this.mContext);
            this.pathname = saveCrashInfoFile(ex);
            new Thread(this.runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void makeRootDirectory(String filePath) {
        try {
            File file = new File(filePath);
            File file2;
            try {
                if (!file.exists()) {
                    file.mkdir();
                }
                file2 = file;
            } catch (Exception e) {
                file2 = file;
            }
        } catch (Exception e2) {
        }
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 1);
            if (pi != null) {
                String versionCode = pi.versionCode + "";
                this.infos.put("versionName", pi.versionName + "");
                this.infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e2) {
                Log.e(TAG, "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(IOUtils.LINE_SEPARATOR_WINDOWS + new SimpleDateFormat(StringUtils.DEFAULT_DATE_TIME_FORMAT).format(new Date()) + "\n");
            for (Entry<String, String> entry : this.infos.entrySet()) {
                String value = (String) entry.getValue();
                sb.append(((String) entry.getKey()) + "=" + value + "\n");
            }
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
                cause.printStackTrace(printWriter);
            }
            printWriter.flush();
            printWriter.close();
            sb.append(writer.toString());
            return writeFile(sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
            return null;
        }
    }

    private String writeFile(String sb) throws Exception {
        String fileName = "crash-" + this.formatter.format(new Date()) + Constants.ACCEPT_TIME_SEPARATOR_SERVER + this.userphoneString + ".log";
        String path = "";
        if (FileUtil.hasSdcard()) {
            path = getGlobalpath();
            Log.d("loaclpath```", path);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        }
        Log.d("loaclpath---------```", path + fileName);
        return path + fileName;
    }

    public static String getGlobalpath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash" + File.separator;
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    public void autoClear(final int autoClearDay) {
        FileUtil.delete(getGlobalpath(), new FilenameFilter() {
            public boolean accept(File file, String filename) {
                return new StringBuilder().append("crash-").append(StringUtils.getOtherDay(autoClearDay < 0 ? autoClearDay : autoClearDay * -1)).toString().compareTo(FileUtil.getFileNameWithoutExtension(filename)) >= 0;
            }
        });
    }

    public void uploadlog(String result) {
    }

    public void showTip(String msg) {
    }

    public void loadSuccess(Object object) {
    }

    public void loadFailure(String errorMsg) {
    }

    public void showLoadingDialog() {
    }

    public void closeLoadingDialog() {
    }
}
