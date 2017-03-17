package com.easemob.chat;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.easemob.util.EMLog;

public class EMChatConfig {
    static String CHATSERVER = (PRODUCTION ? "im1.easemob.com" : "chat.easemob.com");
    private static final String CONFIG_EASEMOB_API_URL = "EASEMOB_API_URL";
    private static final String CONFIG_EASEMOB_APPKEY = "EASEMOB_APPKEY";
    private static final String CONFIG_EASEMOB_CHAT_ADDRESS = "EASEMOB_CHAT_ADDRESS";
    private static final String CONFIG_EASEMOB_CHAT_DOMAIN = "EASEMOB_CHAT_DOMAIN";
    static String DOMAIN = "easemob.com";
    static String DOMAIN_SUFFIX = "@easemob.com";
    static String MUC_DOMAIN = "conferene.easemob.com";
    static String MUC_DOMAIN_SUFFIX = "@conference.easemob.com";
    private static boolean PRODUCTION = true;
    static final String SHARE_SERCRET = "share-secret";
    private static final String TAG = "conf";
    static final String TOKEN_ENTITY = "entities";
    static String USER_SERVER = (PRODUCTION ? "a1.easemob.com" : "api.easemob.com");
    static final String UUID = "uuid";
    public static boolean debugMode = false;
    private static EMChatConfig instance = null;
    public String APPKEY = null;
    public String AccessToken = null;
    Context applicationContext = null;

    private EMChatConfig() {
    }

    public static EMChatConfig getInstance() {
        if (instance == null) {
            instance = new EMChatConfig();
        }
        return instance;
    }

    private void printConfig() {
        EMLog.d(TAG, " APPKEY:" + this.APPKEY + " CHATSERVER:" + CHATSERVER + " domain:" + DOMAIN);
        EMLog.d(TAG, "STORAGE_URL:" + USER_SERVER);
    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public String getDomain() {
        return DOMAIN;
    }

    public String getStorageUrl() {
        return USER_SERVER;
    }

    boolean loadConfig(Context context) {
        this.applicationContext = context;
        try {
            Bundle bundle = this.applicationContext.getPackageManager().getApplicationInfo(this.applicationContext.getPackageName(), 128).metaData;
            String string = bundle.getString(CONFIG_EASEMOB_APPKEY);
            if (string == null && this.APPKEY == null) {
                Log.e(TAG, "EASEMOB_APPKEY is not set in AndroidManifest file");
                throw new RuntimeException("必须在清单文件里填写正确的EASEMOB_APPKEY");
            }
            this.APPKEY = string;
            EMLog.i(TAG, "EASEMOB_APPKEY is set to:" + this.APPKEY);
            string = bundle.getString(CONFIG_EASEMOB_CHAT_ADDRESS);
            if (string != null) {
                CHATSERVER = string;
            }
            string = bundle.getString(CONFIG_EASEMOB_API_URL);
            if (string != null) {
                USER_SERVER = string;
            }
            String string2 = bundle.getString(CONFIG_EASEMOB_CHAT_DOMAIN);
            if (string2 != null) {
                DOMAIN = string2;
            }
            DOMAIN_SUFFIX = "@" + DOMAIN;
            printConfig();
            return true;
        } catch (NameNotFoundException e) {
            EMLog.e(TAG, e.getMessage());
            return false;
        }
    }
}
