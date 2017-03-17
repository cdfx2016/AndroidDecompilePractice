package com.easemob.chat;

import android.content.Context;
import android.util.Log;
import com.easemob.analytics.ActiveCollector;
import com.easemob.util.EMLog;

public class EMChat {
    private static final String TAG = "EaseMob";
    private static EMChat instance = null;
    private static final String version = "2.0.2";
    boolean appInited = false;
    private boolean autoLogin = true;
    private EMChatManager chatManager = null;
    private EMContactManager contactManager = null;
    private EMSessionManager sessionManager = null;

    private EMChat() {
    }

    public static EMChat getInstance() {
        if (instance == null) {
            instance = new EMChat();
        }
        return instance;
    }

    public String getVersion() {
        return version;
    }

    public void init(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (EMChatConfig.getInstance().loadConfig(applicationContext)) {
            InitSmackStaticCode.initStaticCode(context);
            this.chatManager = EMChatManager.getInstance();
            this.sessionManager = EMSessionManager.getInstance(applicationContext);
            this.contactManager = EMContactManager.getInstance();
            String lastLoginUser = this.sessionManager.getLastLoginUser();
            if (!(lastLoginUser == null || lastLoginUser.equals(""))) {
                EMChatDB.initDB(context, lastLoginUser);
            }
            ActiveCollector.sendActivePacket(applicationContext);
            if (this.autoLogin) {
                String lastLoginUser2 = this.sessionManager.getLastLoginUser();
                if (lastLoginUser2 != null && !lastLoginUser2.equals("")) {
                    this.sessionManager.login(lastLoginUser2, this.sessionManager.getLastLoginPwd(), null);
                    return;
                }
                return;
            }
            return;
        }
        Log.e(TAG, "wrong configuration");
    }

    public void setAppInited() {
        this.appInited = true;
        try {
            EMGroupManager.getInstance().processOfflineMessages();
            EMChatManager.getInstance().processOfflinePresenceMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoLogin(boolean z) {
        this.autoLogin = z;
    }

    public void setDebugMode(boolean z) {
        EMChatConfig.debugMode = z;
        EMLog.debugMode = z;
    }
}
