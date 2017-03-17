package cn.smssdk;

import android.content.Context;
import android.telephony.SmsMessage;
import java.util.ArrayList;
import java.util.HashMap;

public class SMSSDK {
    public static final int EVENT_GET_CONTACTS = 4;
    public static final int EVENT_GET_FRIENDS_IN_APP = 6;
    public static final int EVENT_GET_NEW_FRIENDS_COUNT = 7;
    public static final int EVENT_GET_SUPPORTED_COUNTRIES = 1;
    public static final int EVENT_GET_VERIFICATION_CODE = 2;
    public static final int EVENT_GET_VOICE_VERIFICATION_CODE = 8;
    public static final int EVENT_SUBMIT_USER_INFO = 5;
    public static final int EVENT_SUBMIT_VERIFICATION_CODE = 3;
    public static final int RESULT_COMPLETE = -1;
    public static final int RESULT_ERROR = 0;
    private static a a;

    public interface VerifyCodeReadListener {
        void onReadVerifyCode(String str);
    }

    public static void initSDK(Context context, String str, String str2) {
        initSDK(context, str, str2, false);
    }

    public static void initSDK(Context context, String str, String str2, boolean z) {
        if (a == null) {
            a = new a(context, str, str2);
            if (z) {
                a.d();
            }
            a.a();
        }
    }

    public static String getVersion() {
        return "2.1.3";
    }

    public static void registerEventHandler(EventHandler eventHandler) {
        a();
        a.a(eventHandler);
    }

    public static void unregisterEventHandler(EventHandler eventHandler) {
        a();
        a.b(eventHandler);
    }

    public static void unregisterAllEventHandler() {
        a();
        a.b();
    }

    private static void a() {
        if (a == null) {
            throw new NullPointerException("Please call SMSSDK.initSDK(Context, String, String) before any action.");
        }
    }

    public static void getSupportedCountries() {
        a();
        a.a(1, null);
    }

    public static void getVerificationCode(String str, String str2) {
        getVerificationCode(str, str2, null);
    }

    public static void getVerificationCode(String str, String str2, OnSendMessageHandler onSendMessageHandler) {
        getVerificationCode(str, str2, null, onSendMessageHandler);
    }

    public static void getVerificationCode(String str, String str2, String str3, OnSendMessageHandler onSendMessageHandler) {
        getVerificationCode(str, str2, null, str3, onSendMessageHandler);
    }

    public static void getVerificationCode(String str, String str2, String str3, String str4, OnSendMessageHandler onSendMessageHandler) {
        a();
        a.a(2, new Object[]{str, str2, str3, str4, onSendMessageHandler});
    }

    public static void submitVerificationCode(String str, String str2, String str3) {
        a();
        a.a(3, new String[]{str, str2, str3});
    }

    public static void getContacts(boolean z) {
        a();
        a.a(4, Boolean.valueOf(z));
    }

    public static void submitUserInfo(String str, String str2, String str3, String str4, String str5) {
        a();
        a.a(5, new String[]{str, str2, str3, str4, str5});
    }

    public static void getFriendsInApp() {
        a();
        a.a(6, null);
    }

    public static void getNewFriendsCount() {
        a();
        a.a(7, null);
    }

    public static HashMap<Character, ArrayList<String[]>> getGroupedCountryList() {
        a();
        return a.c();
    }

    public static String[] getCountry(String str) {
        a();
        return a.a(str);
    }

    public static String[] getCountryByMCC(String str) {
        a();
        return a.b(str);
    }

    public static void readVerificationCode(SmsMessage smsMessage, VerifyCodeReadListener verifyCodeReadListener) {
        a();
        a.a(smsMessage, verifyCodeReadListener);
    }

    public static void getVoiceVerifyCode(String str, String str2) {
        getVoiceVerifyCode(str, str2, null);
    }

    public static void getVoiceVerifyCode(String str, String str2, String str3) {
        a();
        a.a(8, new String[]{str2, str, str3});
    }
}
