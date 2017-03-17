package com.easemob.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.easemob.util.EMLog;

public class EMChatService extends Service {
    private static final String TAG = "chatservice";
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        EMChatService getService() {
            return EMChatService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        EMLog.d(TAG, "onBind");
        return this.mBinder;
    }

    public void onCreate() {
        super.onCreate();
        EMLog.i(TAG, "chat service created");
    }

    public void onDestroy() {
        EMLog.d(TAG, "onDestroy");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String stringExtra = intent.getStringExtra("reason");
            if (stringExtra != null && stringExtra.equals("boot")) {
                EMLog.d(TAG, "start service from boot ,need to login");
                EMChat.getInstance().init(getApplicationContext());
            }
        }
        return 2;
    }

    public boolean onUnbind(Intent intent) {
        return true;
    }
}
