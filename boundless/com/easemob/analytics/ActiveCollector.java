package com.easemob.analytics;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.util.ApacheHttpClient;
import com.easemob.util.DeviceUuidFactory;
import com.easemob.util.EMLog;
import com.xiaomi.mipush.sdk.Constants;
import org.json.JSONObject;

public class ActiveCollector {
    private static final String perf_actived = "actived";

    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ Context val$context;

        AnonymousClass1(Context context) {
            this.val$context = context;
        }

        public void run() {
            String str = "http://";
            Object storageUrl = EMChatConfig.getInstance().getStorageUrl();
            if (!storageUrl.startsWith("http")) {
                storageUrl = new StringBuilder(String.valueOf(str)).append(storageUrl).toString();
            }
            try {
                String httpPost = ApacheHttpClient.httpPost(new StringBuilder(String.valueOf(storageUrl)).append("/").append(EMChatConfig.getInstance().APPKEY.replaceFirst("#", "/")).append("/devices").toString(), ActiveCollector.collectActiveInfo(this.val$context), null);
                EMLog.d("ana", "send activing msg result :" + httpPost);
                if (httpPost.contains("uuid")) {
                    Editor edit = PreferenceManager.getDefaultSharedPreferences(this.val$context).edit();
                    edit.putBoolean(ActiveCollector.perf_actived, true);
                    edit.commit();
                }
            } catch (Exception e) {
                EMLog.e("ana", "exception :" + e.getMessage());
            }
        }
    }

    public static String collectActiveInfo(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("version", VERSION.RELEASE);
            jSONObject.put("manufacturer", Build.MANUFACTURER);
            jSONObject.put("model", Build.MODEL);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            jSONObject.put("imei", telephonyManager.getDeviceId());
            jSONObject.put("operator", telephonyManager.getNetworkOperatorName());
            jSONObject.put("easemob.version", EMChat.getInstance().getVersion());
            LocationManager locationManager = (LocationManager) context.getSystemService("location");
            Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
            Location lastKnownLocation2 = lastKnownLocation == null ? locationManager.getLastKnownLocation("network") : lastKnownLocation;
            if (lastKnownLocation2 != null) {
                jSONObject.put("loc.lat", lastKnownLocation2.getLatitude());
                jSONObject.put("loc.lng", lastKnownLocation2.getLongitude());
            } else {
                EMLog.d("ana", "no last location info to use");
            }
            jSONObject.put(Constants.EXTRA_KEY_TOKEN, new DeviceUuidFactory(context).getDeviceUuid().toString());
            jSONObject.put("model", "android");
            EMLog.d("ana", jSONObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject.toString();
    }

    public static void sendActivePacket(Context context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(perf_actived, false)) {
            EMLog.d("ana", "sending activing msg");
            try {
                new Thread(new AnonymousClass1(context)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUninstallPacket() {
    }
}
