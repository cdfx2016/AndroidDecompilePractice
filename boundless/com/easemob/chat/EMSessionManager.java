package com.easemob.chat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.easemob.EMCallBack;
import com.easemob.chat.core.HeartBeatReceiver;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.ApacheHttpClient;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import org.json.JSONException;
import org.json.JSONObject;

class EMSessionManager {
    private static final String PREF_KEY_LOGIN_PWD = "easemob.chat.loginpwd";
    private static final String PREF_KEY_LOGIN_USER = "easemob.chat.loginuser";
    private static final String TAG = "Session";
    private static EMSessionManager instance = null;
    private Context appContext = null;
    private long currentElapsedReadtime = 0;
    public EMContact currentUser = null;
    private long firstElapsedRealtime = 0;
    int heartbeatInterval = 90;
    private HeartBeatReceiver heartbeatReceiver = null;
    private String lastLoginPwd = null;
    private String lastLoginUser = null;
    private String password;
    private String username;
    private XmppConnectionManager xmppConnectionManager = null;

    private EMSessionManager(Context context) {
        this.appContext = context;
    }

    private String getAccessToken(String str, String str2, String str3) {
        Exception e;
        String replaceFirst = str.replaceFirst("#", "/");
        String str4 = "";
        replaceFirst = EMChatConfig.USER_SERVER.contains("http://") ? EMChatConfig.USER_SERVER + "/" + replaceFirst + "/token" : "http://" + EMChatConfig.USER_SERVER + "/" + replaceFirst + "/token";
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("grant_type", "password");
            jSONObject.put("username", str2);
            jSONObject.put("password", str3);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            replaceFirst = ApacheHttpClient.httpPost(replaceFirst, jSONObject.toString(), null);
            try {
                EMLog.d(TAG, "accesstoken:" + replaceFirst);
            } catch (Exception e3) {
                e = e3;
                EMLog.e(TAG, "getAccessToken  exception:" + e.getMessage());
                return replaceFirst;
            }
        } catch (Exception e4) {
            e = e4;
            replaceFirst = null;
            EMLog.e(TAG, "getAccessToken  exception:" + e.getMessage());
            return replaceFirst;
        }
        return replaceFirst;
    }

    public static EMSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new EMSessionManager(context.getApplicationContext());
        }
        return instance;
    }

    private void initXmppConnection(String str, String str2) {
        if (this.xmppConnectionManager != null) {
            try {
                EMLog.d(TAG, "try to disconnect previous connection");
                this.xmppConnectionManager.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            XmppConnectionManager.resetInstance();
            this.xmppConnectionManager = null;
        }
        EMChatConfig.getInstance();
        this.xmppConnectionManager = new XmppConnectionManager(str, str2, EMChatConfig.CHATSERVER, this.appContext);
        EMContactManager instance = EMContactManager.getInstance();
        if (EMContactManager.getInstance().enableRosterVersion) {
            this.xmppConnectionManager.getConnection().setRosterStorage(instance.getRosterStorage(this.appContext));
        }
        EMChatManager.getInstance().init(this.appContext, this.xmppConnectionManager);
        EMChatManager.getInstance().addPacketListeners(this.xmppConnectionManager.getConnection());
        EMGroupManager.getInstance().init(this.appContext, this.xmppConnectionManager);
    }

    private void startHeartbeatTimer() {
        if (this.heartbeatReceiver == null) {
            this.heartbeatReceiver = new HeartBeatReceiver();
            this.appContext.registerReceiver(this.heartbeatReceiver, new IntentFilter("easemob.chat.heatbeat." + EMChatConfig.getInstance().APPKEY));
            ((AlarmManager) this.appContext.getSystemService("alarm")).setRepeating(2, SystemClock.elapsedRealtime(), (long) (this.heartbeatInterval * 1000), PendingIntent.getBroadcast(this.appContext, 0, new Intent("easemob.chat.heatbeat." + EMChatConfig.getInstance().APPKEY), 0));
            EMLog.d(TAG, "start heatbeat timer for interval:" + this.heartbeatInterval);
        }
    }

    private void stopHeartbeatTimer() {
        EMLog.d(TAG, "stop heart beat timer");
        try {
            this.appContext.unregisterReceiver(this.heartbeatReceiver);
            this.heartbeatReceiver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changePasswordXMPP(String str) throws EaseMobException {
        if (this.xmppConnectionManager != null && this.xmppConnectionManager.isConnected() && this.xmppConnectionManager.isAuthentificated()) {
            try {
                this.xmppConnectionManager.getConnection().getAccountManager().changePassword(str);
                return;
            } catch (Exception e) {
                EMLog.e(TAG, "changePasswordInBackground XMPP failed: usr:" + getLoginUserName() + ", newPassword:" + str + ", " + e.toString());
                throw new EaseMobException(e.getMessage());
            }
        }
        EMLog.e(TAG, "changePasswordInBackground failed. xmppConnectionManager is null. ");
        throw new EMNetworkUnconnectedException();
    }

    public void createAccountXMPP(String str, String str2) throws EaseMobException {
        try {
            EMChatConfig.getInstance();
            XmppConnectionManager xmppConnectionManager = new XmppConnectionManager(str, str2, EMChatConfig.CHATSERVER, this.appContext);
            xmppConnectionManager.connect();
            xmppConnectionManager.getConnection().getAccountManager().createAccount(str, str2);
            xmppConnectionManager.disconnect();
            EMLog.d(TAG, "created xmpp user:" + str);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    String getLastLoginPwd() {
        if (this.lastLoginPwd == null) {
            try {
                this.lastLoginPwd = EMChatManager.getInstance().getCryptoUtils().decryptBase64String(PreferenceManager.getDefaultSharedPreferences(this.appContext).getString(PREF_KEY_LOGIN_PWD, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.lastLoginPwd;
    }

    String getLastLoginUser() {
        if (this.lastLoginUser == null) {
            this.lastLoginUser = PreferenceManager.getDefaultSharedPreferences(this.appContext).getString(PREF_KEY_LOGIN_USER, "");
        }
        return this.lastLoginUser;
    }

    public String getLoginUserName() {
        return this.currentUser.username;
    }

    public XmppConnectionManager getXmppConnectionManager() {
        return this.xmppConnectionManager;
    }

    public boolean isConnected() {
        return this.xmppConnectionManager == null ? false : this.xmppConnectionManager.isConnected() & this.xmppConnectionManager.isAuthentificated();
    }

    public void login(final String str, final String str2, final EMCallBack eMCallBack) {
        String bareEidFromUserName = EMContactManager.getBareEidFromUserName(str);
        EMLog.d(TAG, "login with eid:" + bareEidFromUserName);
        if (isConnected()) {
            EMLog.d(TAG, "already loggedin and conected. skip login");
            if (eMCallBack != null) {
                eMCallBack.onSuccess();
                return;
            }
            return;
        }
        this.currentUser = new EMContact(bareEidFromUserName, str);
        this.username = str;
        this.password = str2;
        try {
            initXmppConnection(bareEidFromUserName, str2);
        } catch (Exception e) {
            e.printStackTrace();
            if (eMCallBack != null) {
                eMCallBack.onError(-1, e.toString());
            }
        }
        new Thread(new Runnable() {
            public void run() {
                EMSessionManager.this.currentElapsedReadtime = SystemClock.elapsedRealtime();
                if (EMChatConfig.getInstance().AccessToken == null || EMSessionManager.this.firstElapsedRealtime == 0 || EMSessionManager.this.currentElapsedReadtime - EMSessionManager.this.firstElapsedRealtime > 86400000) {
                    Object access$3 = EMSessionManager.this.getAccessToken(EMChatConfig.getInstance().APPKEY, str, str2);
                    if (!TextUtils.isEmpty(access$3)) {
                        try {
                            String string = new JSONObject(access$3).getString("access_token");
                            EMSessionManager.this.firstElapsedRealtime = SystemClock.elapsedRealtime();
                            EMChatConfig.getInstance().AccessToken = string;
                        } catch (JSONException e) {
                            if (eMCallBack != null) {
                                eMCallBack.onError(-1, "用户名或密码错误！");
                                return;
                            }
                            return;
                        }
                    } else if (eMCallBack != null) {
                        eMCallBack.onError(-1, "用户名或密码错误！");
                        return;
                    } else {
                        return;
                    }
                }
                try {
                    EMChatDB.initDB(EMSessionManager.this.appContext, str);
                    EMSessionManager.this.xmppConnectionManager.connectSync();
                    EMSessionManager.this.startHeartbeatTimer();
                    if (!str.equals(EMSessionManager.this.getLastLoginUser())) {
                        EMSessionManager.this.setLastLoginUser(str);
                        EMSessionManager.this.setLastLoginPwd(str2);
                    }
                    PathUtil.getInstance().initDirs(EMChatConfig.getInstance().APPKEY, str, EMSessionManager.this.appContext);
                    EMContactManager.getInstance().init(EMSessionManager.this.appContext, EMSessionManager.this.xmppConnectionManager);
                    EMGroupManager.getInstance().joinGroupsAfterLogin();
                    EMLog.i(EMSessionManager.TAG, "EaseMob Server connected.");
                    if (eMCallBack != null) {
                        eMCallBack.onSuccess();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    EMSessionManager.this.xmppConnectionManager = null;
                    EMLog.e(EMSessionManager.TAG, "xmppConnectionManager.connectSync() failed: " + e2.getMessage());
                    if (eMCallBack != null) {
                        eMCallBack.onError(-1, e2.getMessage());
                    }
                }
            }
        }).start();
    }

    public void logout() {
        new Thread() {
            public void run() {
                EMSessionManager.this.stopHeartbeatTimer();
                try {
                    if (EMSessionManager.this.isConnected()) {
                        EMSessionManager.this.xmppConnectionManager.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EMSessionManager.this.xmppConnectionManager = null;
                EMLog.i(EMSessionManager.TAG, "EaseMobChatService disconnected.");
                EMChatDB.getInstance().closeDatabase();
                EMSessionManager.this.setLastLoginUser("");
                EMSessionManager.this.setLastLoginPwd("");
            }
        }.start();
    }

    void setLastLoginPwd(String str) {
        this.lastLoginPwd = str;
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.appContext).edit();
        try {
            edit.putString(PREF_KEY_LOGIN_PWD, EMChatManager.getInstance().getCryptoUtils().encryptBase64String(str));
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setLastLoginUser(String str) {
        this.lastLoginUser = str;
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.appContext).edit();
        edit.putString(PREF_KEY_LOGIN_USER, str);
        edit.commit();
    }
}
