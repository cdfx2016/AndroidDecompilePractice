package com.easemob.chat.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.util.EMLog;

public class HeartBeatReceiver extends BroadcastReceiver {
    private static final String TAG = "ping";
    static c pingPacket = new c();

    public void onReceive(Context context, Intent intent) {
        XmppConnectionManager instance = XmppConnectionManager.getInstance();
        if (instance != null && instance.isConnected() && instance.getConnection() != null) {
            try {
                EMLog.d("ping", "send heartbeat");
                instance.getConnection().sendPacket(pingPacket);
            } catch (Exception e) {
                EMLog.e("ping", e.toString());
            }
        }
    }
}
