package com.fanyu.boundless.service;

import android.annotation.SuppressLint;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.fanyu.boundless.R;
import com.fanyu.boundless.app.MyApplication;
import com.fanyu.boundless.util.FileUtil;
import com.fanyu.boundless.view.login.LoginActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateService extends Service {
    private static final int DOWN_ERROR = 0;
    private static final int DOWN_OK = 1;
    private static final int TIMEOUT = 20000;
    private static String down_url = "FunsApplication.mFuns.getBaseUrl()/apk/infopool_1.1.apk";
    private String TAG = "UpdateService";
    private String app_name;
    private Builder builder;
    private RemoteViews contentView;
    private NotificationManager notificationManager;
    private int notification_id = 0;
    private PendingIntent pendingIntent;
    private Intent updateIntent;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("---------------------------");
        this.app_name = "dingxing_" + MyApplication.serverVersion;
        FileUtil.createFile(this.app_name);
        System.out.println(this.app_name);
        createNotification();
        down_url = "http://dx.gensaint.com/apk/dingxing_" + MyApplication.serverVersionCode + ".apk";
        System.out.println(down_url + "==================");
        createThread();
        return super.onStartCommand(intent, flags, startId);
    }

    public void createThread() {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Log.e(UpdateService.this.TAG, "升级发生异常.");
                        Toast.makeText(UpdateService.this.getBaseContext(), "升级发生异常,下次启动程序将再次升级...", 1).show();
                        UpdateService.this.builder.setContentTitle(UpdateService.this.app_name);
                        UpdateService.this.builder.setContentText("下载失败");
                        UpdateService.this.builder.setContentIntent(UpdateService.this.pendingIntent);
                        UpdateService.this.notificationManager.notify(UpdateService.this.notification_id, UpdateService.this.builder.build());
                        UpdateService.this.stopService(UpdateService.this.updateIntent);
                        return;
                    case 1:
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setFlags(1);
                        intent.setFlags(2);
                        intent.setDataAndType(UpdateService.getUriForFile(UpdateService.this.getApplicationContext(), FileUtil.updateFile), "application/vnd.android.package-archive");
                        intent.addFlags(268435456);
                        UpdateService.this.pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
                        UpdateService.this.builder.setContentTitle(UpdateService.this.app_name);
                        UpdateService.this.builder.setContentText("下载成功，点击安装");
                        UpdateService.this.builder.setContentIntent(UpdateService.this.pendingIntent);
                        UpdateService.this.notificationManager.notify(UpdateService.this.notification_id, UpdateService.this.builder.build());
                        UpdateService.this.startActivity(intent);
                        UpdateService.this.stopService(UpdateService.this.updateIntent);
                        return;
                    default:
                        UpdateService.this.stopService(UpdateService.this.updateIntent);
                        return;
                }
            }
        };
        final Message message = new Message();
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (UpdateService.this.downloadUpdateFile(UpdateService.down_url, FileUtil.updateFile.toString()) > 0) {
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.e(UpdateService.this.TAG, e.getMessage());
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    @SuppressLint({"NewApi"})
    public void createNotification() {
        Log.d(this.TAG, "createNotification");
        this.notificationManager = (NotificationManager) getSystemService("notification");
        this.builder = new Builder(this);
        this.builder.setSmallIcon(R.mipmap.xiazai);
        this.builder.setTicker("开始下载");
        this.builder.setWhen(System.currentTimeMillis());
        this.builder.setAutoCancel(true);
        this.contentView = new RemoteViews(getPackageName(), R.layout.notification);
        this.contentView.setTextViewText(R.id.notificationTitle, "正在下载");
        this.contentView.setTextViewText(R.id.notificationPercent, "0%");
        this.contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
        this.builder.setContent(this.contentView);
        this.updateIntent = new Intent(this, LoginActivity.class);
        this.updateIntent.addFlags(536870912);
        this.pendingIntent = PendingIntent.getActivity(this, 0, this.updateIntent, 0);
        this.builder.setContentIntent(this.pendingIntent);
        this.builder.setDefaults(1);
        this.notificationManager.notify(this.notification_id, this.builder.build());
    }

    public long downloadUpdateFile(String down_url, String file) throws Exception {
        int downloadCount = 0;
        int updateCount = 0;
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(down_url).openConnection();
        httpURLConnection.setConnectTimeout(20000);
        httpURLConnection.setReadTimeout(20000);
        int totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("404");
        }
        InputStream inputStream = httpURLConnection.getInputStream();
        OutputStream outputStream = new FileOutputStream(file, false);
        byte[] buffer = new byte[1024];
        while (true) {
            int readsize = inputStream.read(buffer);
            if (readsize == -1) {
                break;
            }
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;
            if (updateCount == 0 || ((downloadCount * 100) / totalSize) - 5 >= updateCount) {
                updateCount += 5;
                this.contentView.setTextViewText(R.id.notificationPercent, updateCount + "%");
                this.contentView.setProgressBar(R.id.notificationProgress, 100, updateCount, false);
                this.builder.setDefaults(4);
                this.notificationManager.notify(this.notification_id, this.builder.build());
            }
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();
        return (long) downloadCount;
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        } else if (VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(context.getApplicationContext(), "com.fanyu.boundless.fileprovider", file);
        }
    }
}
