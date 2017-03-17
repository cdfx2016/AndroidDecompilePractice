package com.easemob.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.util.EMLog;

public class StartServiceReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        EMLog.d("boot", "start easemob service on boot");
        Intent intent2 = new Intent(context, EMChatService.class);
        intent2.putExtra("reason", "boot");
        context.startService(intent2);
    }
}
