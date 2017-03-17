package com.easemob.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import java.util.Map;
import java.util.UUID;

public class DeviceUuidFactory {
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static final String PREFS_FILE = "device_id.xml";
    protected static UUID uuid;

    public DeviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, 0);
                    String string = sharedPreferences.getString(PREFS_DEVICE_ID, null);
                    if (string != null) {
                        uuid = UUID.fromString(string);
                    } else {
                        string = Secure.getString(context.getContentResolver(), "android_id");
                        try {
                            if ("9774d56d682e549c".equals(string)) {
                                string = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
                                uuid = string != null ? UUID.nameUUIDFromBytes(string.getBytes("utf8")) : generateDeviceUuid(context);
                            } else {
                                uuid = UUID.nameUUIDFromBytes(string.getBytes("utf8"));
                            }
                            sharedPreferences.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private UUID generateDeviceUuid(Context context) {
        String str = Build.BOARD + Build.BRAND + Build.CPU_ABI + Build.DEVICE + Build.DISPLAY + Build.FINGERPRINT + Build.HOST + Build.ID + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER;
        String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        String string = Secure.getString(context.getContentResolver(), "android_id");
        String macAddress = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
        return (isEmpty(deviceId) && isEmpty(string) && isEmpty(macAddress)) ? UUID.randomUUID() : UUID.nameUUIDFromBytes((str.toString() + deviceId + string + macAddress).getBytes());
    }

    private static boolean isEmpty(Object obj) {
        return obj == null ? true : ((obj instanceof String) && ((String) obj).trim().length() == 0) ? true : obj instanceof Map ? ((Map) obj).isEmpty() : false;
    }

    public UUID getDeviceUuid() {
        return uuid;
    }
}
