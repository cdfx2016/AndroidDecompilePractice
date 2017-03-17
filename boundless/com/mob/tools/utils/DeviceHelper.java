package com.mob.tools.utils;

import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.easemob.util.HanziToPinyin.Token;
import com.fanyu.boundless.config.Preferences;
import com.mob.tools.MobLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.json.JSONArray;
import org.json.JSONException;

public class DeviceHelper {
    private static DeviceHelper deviceHelper;
    private Context context;

    private class GSConnection implements ServiceConnection {
        boolean got;
        private final BlockingQueue<IBinder> iBinders;

        private GSConnection() {
            this.got = false;
            this.iBinders = new LinkedBlockingQueue();
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.iBinders.put(service);
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder takeBinder() throws InterruptedException {
            if (this.got) {
                throw new IllegalStateException();
            }
            this.got = true;
            return (IBinder) this.iBinders.poll(1500, TimeUnit.MILLISECONDS);
        }
    }

    public static synchronized DeviceHelper getInstance(Context c) {
        DeviceHelper deviceHelper;
        synchronized (DeviceHelper.class) {
            if (deviceHelper == null && c != null) {
                deviceHelper = new DeviceHelper(c);
            }
            deviceHelper = deviceHelper;
        }
        return deviceHelper;
    }

    private DeviceHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isRooted() {
        return false;
    }

    public String getSSID() {
        String str = null;
        try {
            if (checkPermission("android.permission.ACCESS_WIFI_STATE")) {
                Object wifi = getSystemService("wifi");
                if (wifi != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ge");
                    sb.append("tC");
                    sb.append("on");
                    sb.append("ne");
                    sb.append("ct");
                    sb.append("io");
                    sb.append("nI");
                    sb.append("nf");
                    sb.append("o");
                    Object info = ReflectHelper.invokeInstanceMethod(wifi, sb.toString(), new Object[0]);
                    if (info != null) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append("ge");
                        sb1.append("tS");
                        sb1.append("SI");
                        sb1.append("D");
                        String ssid = (String) ReflectHelper.invokeInstanceMethod(info, sb1.toString(), new Object[0]);
                        if (ssid != null) {
                            str = ssid.replace("\"", "");
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
        return str;
    }

    public String getBssid() {
        try {
            if (!checkPermission("android.permission.ACCESS_WIFI_STATE")) {
                return null;
            }
            Object wifi = getSystemService("wifi");
            if (wifi == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tC");
            sb.append("on");
            sb.append("ne");
            sb.append("ct");
            sb.append("io");
            sb.append("nI");
            sb.append("nf");
            sb.append("o");
            Object info = ReflectHelper.invokeInstanceMethod(wifi, sb.toString(), new Object[0]);
            if (info == null) {
                return null;
            }
            StringBuilder sb1 = new StringBuilder();
            sb1.append("ge");
            sb1.append("tB");
            sb1.append("SS");
            sb1.append("ID");
            String bssid = (String) ReflectHelper.invokeInstanceMethod(info, sb1.toString(), new Object[0]);
            if (bssid == null) {
                bssid = null;
            }
            return bssid;
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
            return null;
        }
    }

    public String getMacAddress() {
        if (VERSION.SDK_INT >= 23) {
            String hd;
            try {
                hd = getHardwareAddressFromShell("wlan0");
            } catch (Throwable t) {
                MobLog.getInstance().d(t);
                hd = null;
            }
            if (hd == null) {
                try {
                    hd = getCurrentNetworkHardwareAddress();
                } catch (Throwable t2) {
                    MobLog.getInstance().d(t2);
                    hd = null;
                }
            }
            if (hd == null) {
                try {
                    String[] hds = listNetworkHardwareAddress();
                    if (hds.length > 0) {
                        hd = hds[0];
                    }
                } catch (Throwable t22) {
                    MobLog.getInstance().d(t22);
                    hd = null;
                }
            }
            if (hd != null) {
                return hd;
            }
        }
        try {
            Object wifi = getSystemService("wifi");
            if (wifi == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tC");
            sb.append("on");
            sb.append("ne");
            sb.append("ct");
            sb.append("io");
            sb.append("nI");
            sb.append("nf");
            sb.append("o");
            Object info = ReflectHelper.invokeInstanceMethod(wifi, sb.toString(), new Object[0]);
            if (info == null) {
                return null;
            }
            StringBuilder sb1 = new StringBuilder();
            sb1.append("ge");
            sb1.append("tM");
            sb1.append("ac");
            sb1.append("Ad");
            sb1.append("dr");
            sb1.append("es");
            sb1.append("s");
            String mac = (String) ReflectHelper.invokeInstanceMethod(info, sb1.toString(), new Object[0]);
            if (mac == null) {
                mac = null;
            }
            return mac;
        } catch (Throwable t222) {
            MobLog.getInstance().w(t222);
            return null;
        }
    }

    private String getCurrentNetworkHardwareAddress() throws Throwable {
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        if (nis == null) {
            return null;
        }
        for (NetworkInterface intf : Collections.list(nis)) {
            Enumeration<InetAddress> ias = intf.getInetAddresses();
            if (ias != null) {
                for (InetAddress add : Collections.list(ias)) {
                    if (!add.isLoopbackAddress() && (add instanceof Inet4Address)) {
                        byte[] mac = intf.getHardwareAddress();
                        if (mac != null) {
                            StringBuilder buf = new StringBuilder();
                            int len$ = mac.length;
                            for (int i$ = 0; i$ < len$; i$++) {
                                buf.append(String.format("%02x:", new Object[]{Byte.valueOf(arr$[i$])}));
                            }
                            if (buf.length() > 0) {
                                buf.deleteCharAt(buf.length() - 1);
                            }
                            return buf.toString();
                        }
                    }
                }
                continue;
            }
        }
        return null;
    }

    private String[] listNetworkHardwareAddress() throws Throwable {
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        if (nis == null) {
            return null;
        }
        List<NetworkInterface> interfaces = Collections.list(nis);
        HashMap<String, String> macs = new HashMap();
        for (NetworkInterface intf : interfaces) {
            byte[] mac = intf.getHardwareAddress();
            if (mac != null) {
                StringBuilder buf = new StringBuilder();
                int len$ = mac.length;
                for (int i$ = 0; i$ < len$; i$++) {
                    buf.append(String.format("%02x:", new Object[]{Byte.valueOf(arr$[i$])}));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macs.put(intf.getName(), buf.toString());
            }
        }
        ArrayList<String> arrayList = new ArrayList(macs.keySet());
        ArrayList<String> wlans = new ArrayList();
        ArrayList<String> eths = new ArrayList();
        ArrayList<String> rmnets = new ArrayList();
        ArrayList<String> dummys = new ArrayList();
        ArrayList<String> usbs = new ArrayList();
        ArrayList<String> rmnetUsbs = new ArrayList();
        ArrayList<String> others = new ArrayList();
        while (arrayList.size() > 0) {
            String name = (String) arrayList.remove(0);
            if (name.startsWith("wlan")) {
                wlans.add(name);
            } else if (name.startsWith("eth")) {
                eths.add(name);
            } else if (name.startsWith("rev_rmnet")) {
                rmnets.add(name);
            } else if (name.startsWith("dummy")) {
                dummys.add(name);
            } else if (name.startsWith("usbnet")) {
                usbs.add(name);
            } else if (name.startsWith("rmnet_usb")) {
                rmnetUsbs.add(name);
            } else {
                others.add(name);
            }
        }
        Collections.sort(wlans);
        Collections.sort(eths);
        Collections.sort(rmnets);
        Collections.sort(dummys);
        Collections.sort(usbs);
        Collections.sort(rmnetUsbs);
        Collections.sort(others);
        arrayList.addAll(wlans);
        arrayList.addAll(eths);
        arrayList.addAll(rmnets);
        arrayList.addAll(dummys);
        arrayList.addAll(usbs);
        arrayList.addAll(rmnetUsbs);
        arrayList.addAll(others);
        String[] macArr = new String[arrayList.size()];
        for (int i = 0; i < macArr.length; i++) {
            macArr[i] = (String) macs.get(arrayList.get(i));
        }
        return macArr;
    }

    private String getHardwareAddressFromShell(String networkCard) {
        Throwable t;
        Throwable th;
        String line = null;
        BufferedReader br = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ca");
            sb.append("t ");
            sb.append("/s");
            sb.append("ys");
            sb.append("/c");
            sb.append("la");
            sb.append("ss");
            sb.append("/n");
            sb.append("et");
            sb.append("/");
            sb.append(networkCard);
            sb.append("/a");
            sb.append("dd");
            sb.append("re");
            sb.append("ss");
            BufferedReader br2 = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(sb.toString()).getInputStream()));
            try {
                line = br2.readLine();
                if (br2 != null) {
                    try {
                        br2.close();
                        br = br2;
                    } catch (Throwable th2) {
                        br = br2;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                br = br2;
                if (br != null) {
                    br.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            t = th4;
            MobLog.getInstance().d(t);
            if (br != null) {
                br.close();
            }
            if (TextUtils.isEmpty(line)) {
            }
        }
        if (TextUtils.isEmpty(line)) {
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getDeviceId() {
        String deviceId = getIMEI();
        if (!TextUtils.isEmpty(deviceId) || VERSION.SDK_INT < 9) {
            return deviceId;
        }
        return getSerialno();
    }

    public String getMime() {
        return getIMEI();
    }

    public String getIMEI() {
        Object phone = getSystemService("phone");
        if (phone == null) {
            return null;
        }
        String deviceId = null;
        try {
            if (checkPermission("android.permission.READ_PHONE_STATE")) {
                StringBuilder sb = new StringBuilder();
                sb.append("ge");
                sb.append("tD");
                sb.append("ev");
                sb.append("ic");
                sb.append("eI");
                sb.append("d");
                deviceId = (String) ReflectHelper.invokeInstanceMethod(phone, sb.toString(), new Object[0]);
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        return deviceId;
    }

    public String getSerialno() {
        if (VERSION.SDK_INT < 9) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("an");
            sb.append("dr");
            sb.append("oi");
            sb.append("d.");
            sb.append("os");
            sb.append(".S");
            sb.append("ys");
            sb.append("te");
            sb.append("mP");
            sb.append("ro");
            sb.append("pe");
            sb.append("rt");
            sb.append("ie");
            sb.append("s");
            ReflectHelper.importClass(sb.toString());
            StringBuilder sb1 = new StringBuilder();
            sb1.append("ge");
            sb1.append("t");
            return (String) ReflectHelper.invokeStaticMethod("SystemProperties", sb1.toString(), "ro.serialno", EnvironmentCompat.MEDIA_UNKNOWN);
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
            return null;
        }
    }

    public String getDeviceData() {
        return Base64AES(getModel() + "|" + getOSVersionInt() + "|" + getManufacturer() + "|" + getCarrier() + "|" + getScreenSize(), getDeviceKey().substring(0, 16));
    }

    public String getDeviceDataNotAES() {
        return getModel() + "|" + getOSVersion() + "|" + getManufacturer() + "|" + getCarrier() + "|" + getScreenSize();
    }

    public String Base64AES(String msg, String key) {
        String result = null;
        try {
            result = Base64.encodeToString(Data.AES128Encode(key, msg), 0);
            if (result.contains("\n")) {
                result = result.replace("\n", "");
            }
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
        }
        return result;
    }

    public String getOSVersion() {
        return String.valueOf(getOSVersionInt());
    }

    public int getOSVersionInt() {
        return VERSION.SDK_INT;
    }

    public String getOSVersionName() {
        return VERSION.RELEASE;
    }

    public String getOSLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getAppLanguage() {
        return this.context.getResources().getConfiguration().locale.getLanguage();
    }

    public String getOSCountry() {
        return Locale.getDefault().getCountry();
    }

    public String getScreenSize() {
        int[] size = ResHelper.getScreenSize(this.context);
        if (this.context.getResources().getConfiguration().orientation == 1) {
            return size[0] + "x" + size[1];
        }
        return size[1] + "x" + size[0];
    }

    public String getCarrier() {
        try {
            Object tm = getSystemService("phone");
            if (tm == null) {
                return "-1";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tS");
            sb.append("im");
            sb.append("Op");
            sb.append("er");
            sb.append("at");
            sb.append("or");
            String operator = (String) ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
            if (TextUtils.isEmpty(operator)) {
                return "-1";
            }
            return operator;
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return "-1";
        }
    }

    public String getCarrierName() {
        Object tm = getSystemService("phone");
        if (tm == null) {
            return null;
        }
        try {
            if (checkPermission("android.permission.READ_PHONE_STATE")) {
                StringBuilder sb = new StringBuilder();
                sb.append("ge");
                sb.append("tS");
                sb.append("im");
                sb.append("Op");
                sb.append("er");
                sb.append("at");
                sb.append("or");
                sb.append("Na");
                sb.append("me");
                String operator = (String) ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
                if (TextUtils.isEmpty(operator)) {
                    return null;
                }
                return operator;
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
        return null;
    }

    public String getMCC() {
        String imsi = getIMSI();
        if (imsi == null || imsi.length() < 3) {
            return null;
        }
        return imsi.substring(0, 3);
    }

    public String getMNC() {
        String imsi = getIMSI();
        if (imsi == null || imsi.length() < 5) {
            return null;
        }
        return imsi.substring(3, 5);
    }

    public String getSimSerialNumber() {
        try {
            Object tm = getSystemService("phone");
            if (tm == null) {
                return "-1";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tS");
            sb.append("im");
            sb.append("Se");
            sb.append("ri");
            sb.append("al");
            sb.append("Nu");
            sb.append("mb");
            sb.append("er");
            return (String) ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return "-1";
        }
    }

    public String getLine1Number() {
        try {
            Object tm = getSystemService("phone");
            if (tm == null) {
                return "-1";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tL");
            sb.append("in");
            sb.append("e1");
            sb.append("Nu");
            sb.append("mb");
            sb.append("er");
            return (String) ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return "-1";
        }
    }

    public String getBluetoothName() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("an");
            sb.append("dr");
            sb.append("oi");
            sb.append("d.");
            sb.append("bl");
            sb.append("ue");
            sb.append("to");
            sb.append("ot");
            sb.append("h.");
            sb.append("Bl");
            sb.append("ue");
            sb.append("to");
            sb.append("ot");
            sb.append("hA");
            sb.append("da");
            sb.append("pt");
            sb.append("er");
            ReflectHelper.importClass(sb.toString());
            if (checkPermission("android.permission.BLUETOOTH")) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append("ge");
                sb1.append("tD");
                sb1.append("ef");
                sb1.append("au");
                sb1.append("lt");
                sb1.append("Ad");
                sb1.append("ap");
                sb1.append("te");
                sb1.append("r");
                Object myDevice = ReflectHelper.invokeStaticMethod("BluetoothAdapter", sb1.toString(), new Object[0]);
                if (myDevice != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("ge");
                    sb2.append("tN");
                    sb2.append("am");
                    sb2.append("e");
                    return (String) ReflectHelper.invokeInstanceMethod(myDevice, sb2.toString(), new Object[0]);
                }
            }
        } catch (Throwable e) {
            MobLog.getInstance().d(e);
        }
        return null;
    }

    public String getSignMD5() {
        try {
            return Data.MD5(this.context.getPackageManager().getPackageInfo(getPackageName(), 64).signatures[0].toByteArray());
        } catch (Exception e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    private Object getSystemService(String name) {
        try {
            return this.context.getSystemService(name);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public String getNetworkType() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService("connectivity");
        if (conn == null) {
            return PrivacyRule.SUBSCRIPTION_NONE;
        }
        try {
            if (!checkPermission("android.permission.ACCESS_NETWORK_STATE")) {
                return PrivacyRule.SUBSCRIPTION_NONE;
            }
            NetworkInfo network = conn.getActiveNetworkInfo();
            if (network == null || !network.isAvailable()) {
                return PrivacyRule.SUBSCRIPTION_NONE;
            }
            int type = network.getType();
            switch (type) {
                case 0:
                    if (is4GMobileNetwork()) {
                        return "4G";
                    }
                    return isFastMobileNetwork() ? "3G" : "2G";
                case 1:
                    return "wifi";
                case 6:
                    return "wimax";
                case 7:
                    return "bluetooth";
                case 8:
                    return "dummy";
                case 9:
                    return "ethernet";
                default:
                    return String.valueOf(type);
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return PrivacyRule.SUBSCRIPTION_NONE;
        }
    }

    public String getNetworkTypeForStatic() {
        String networkType = getNetworkType().toLowerCase();
        if (TextUtils.isEmpty(networkType) || PrivacyRule.SUBSCRIPTION_NONE.equals(networkType)) {
            return PrivacyRule.SUBSCRIPTION_NONE;
        }
        if (networkType.startsWith("4g") || networkType.startsWith("3g") || networkType.startsWith("2g")) {
            return "cell";
        }
        if (networkType.startsWith("wifi")) {
            return "wifi";
        }
        return "other";
    }

    public String getDetailNetworkTypeForStatic() {
        String networkType = getNetworkType().toLowerCase();
        if (TextUtils.isEmpty(networkType) || PrivacyRule.SUBSCRIPTION_NONE.equals(networkType)) {
            return PrivacyRule.SUBSCRIPTION_NONE;
        }
        if (networkType.startsWith("wifi")) {
            return "wifi";
        }
        if (networkType.startsWith("4g")) {
            return "4g";
        }
        if (networkType.startsWith("3g")) {
            return "3g";
        }
        if (networkType.startsWith("2g")) {
            return "2g";
        }
        if (networkType.startsWith("bluetooth")) {
            return "bluetooth";
        }
        return networkType;
    }

    public int getPlatformCode() {
        return 1;
    }

    private boolean is4GMobileNetwork() {
        Object phone = getSystemService("phone");
        if (phone == null) {
            return false;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tN");
            sb.append("et");
            sb.append("wo");
            sb.append("rk");
            sb.append("Ty");
            sb.append("pe");
            return ((Integer) ReflectHelper.invokeInstanceMethod(phone, sb.toString(), new Object[0])).intValue() == 13;
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return false;
        }
    }

    private boolean isFastMobileNetwork() {
        Object phone = getSystemService("phone");
        if (phone == null) {
            return false;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tN");
            sb.append("et");
            sb.append("wo");
            sb.append("rk");
            sb.append("Ty");
            sb.append("pe");
            switch (((Integer) ReflectHelper.invokeInstanceMethod(phone, sb.toString(), new Object[0])).intValue()) {
                case 0:
                    return false;
                case 1:
                    return false;
                case 2:
                    return false;
                case 3:
                    return true;
                case 4:
                    return false;
                case 5:
                    return true;
                case 6:
                    return true;
                case 7:
                    return false;
                case 8:
                    return true;
                case 9:
                    return true;
                case 10:
                    return true;
                case 11:
                    return false;
                case 12:
                    return true;
                case 13:
                    return true;
                case 14:
                    return true;
                case 15:
                    return true;
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
        return false;
    }

    public JSONArray getRunningApp() {
        JSONArray appNmes = new JSONArray();
        Object am = getSystemService("activity");
        if (am != null) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("ge");
                sb.append("tR");
                sb.append("un");
                sb.append("ni");
                sb.append("ng");
                sb.append("Ap");
                sb.append("pP");
                sb.append("ro");
                sb.append("ce");
                sb.append("ss");
                sb.append("es");
                List<?> apps = (List) ReflectHelper.invokeInstanceMethod(am, sb.toString(), new Object[0]);
                if (apps != null) {
                    for (Object app : apps) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append("pr");
                        sb1.append("oc");
                        sb1.append("es");
                        sb1.append("sN");
                        sb1.append("am");
                        sb1.append("e");
                        appNmes.put(ReflectHelper.getInstanceField(app, sb1.toString()));
                    }
                }
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
            }
        }
        return appNmes;
    }

    public String getRunningAppStr() throws JSONException {
        JSONArray apps = getRunningApp();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < apps.length(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(String.valueOf(apps.get(i)));
        }
        return sb.toString();
    }

    public String getDeviceKey() {
        String localKey;
        try {
            localKey = getLocalDeviceKey();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            localKey = null;
        }
        if (!TextUtils.isEmpty(localKey) && localKey.length() >= 40) {
            return localKey;
        }
        String newKey;
        try {
            String mac = getMacAddress();
            String udid = getDeviceId();
            newKey = Data.byteToHex(Data.SHA1(mac + ":" + udid + ":" + getModel()));
        } catch (Throwable t2) {
            MobLog.getInstance().d(t2);
            newKey = null;
        }
        if (TextUtils.isEmpty(newKey) || newKey.length() < 40) {
            newKey = getCharAndNumr(40);
        }
        if (newKey != null) {
            try {
                saveLocalDeviceKey(newKey);
            } catch (Throwable t22) {
                MobLog.getInstance().w(t22);
            }
        }
        return newKey;
    }

    public String getCharAndNumr(int length) {
        long realTime = System.currentTimeMillis() ^ SystemClock.elapsedRealtime();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(realTime);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            if ("char".equalsIgnoreCase(random.nextInt(2) % 2 == 0 ? "char" : "num")) {
                stringBuffer.insert(i + 1, (char) (random.nextInt(26) + 97));
            } else {
                stringBuffer.insert(stringBuffer.length(), random.nextInt(10));
            }
        }
        return stringBuffer.toString().substring(0, 40);
    }

    private String getLocalDeviceKey() throws Throwable {
        String str = null;
        if (getSdcardState()) {
            File keyFile;
            File cacheRoot = new File(getSdcardPath(), "ShareSDK");
            if (cacheRoot.exists()) {
                keyFile = new File(cacheRoot, ".dk");
                if (keyFile.exists() && keyFile.renameTo(new File(ResHelper.getCacheRoot(this.context), ".dk"))) {
                    keyFile.delete();
                }
            }
            keyFile = new File(ResHelper.getCacheRoot(this.context), ".dk");
            if (keyFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFile));
                Object key = ois.readObject();
                str = null;
                if (key != null && (key instanceof char[])) {
                    str = String.valueOf((char[]) key);
                }
                ois.close();
            }
        }
        return str;
    }

    private void saveLocalDeviceKey(String key) throws Throwable {
        if (getSdcardState()) {
            File keyFile = new File(ResHelper.getCacheRoot(this.context), ".dk");
            if (keyFile.exists()) {
                keyFile.delete();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(keyFile));
            oos.writeObject(key.toCharArray());
            oos.flush();
            oos.close();
        }
    }

    public String getPackageName() {
        return this.context.getPackageName();
    }

    public String getAppName() {
        String appName = this.context.getApplicationInfo().name;
        if (appName != null) {
            return appName;
        }
        int appLbl = this.context.getApplicationInfo().labelRes;
        if (appLbl > 0) {
            appName = this.context.getString(appLbl);
        } else {
            appName = String.valueOf(this.context.getApplicationInfo().nonLocalizedLabel);
        }
        return appName;
    }

    public int getAppVersion() {
        int i = 0;
        try {
            return this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode;
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
            return i;
        }
    }

    public String getAppVersionName() {
        try {
            return this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName;
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
            return "1.0";
        }
    }

    public ArrayList<HashMap<String, String>> getInstalledApp(boolean includeSystemApp) {
        ArrayList<String> packages;
        try {
            packages = new ArrayList();
            StringBuilder sb = new StringBuilder();
            sb.append("pm");
            sb.append(" l");
            sb.append("is");
            sb.append("t ");
            sb.append("pa");
            sb.append("ck");
            sb.append("ag");
            sb.append("es");
            Process p = Runtime.getRuntime().exec(sb.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "utf-8"));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.toLowerCase().trim();
                if (line.startsWith("package:")) {
                    packages.add(line.substring("package:".length()).trim());
                }
            }
            br.close();
            p.destroy();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return new ArrayList();
        }
        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        PackageManager pm = this.context.getPackageManager();
        Iterator i$ = packages.iterator();
        while (i$.hasNext()) {
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo((String) i$.next(), 0);
            } catch (Throwable t2) {
                MobLog.getInstance().d(t2);
            }
            if (pi != null) {
                if (!includeSystemApp) {
                    if (isSystemApp(pi)) {
                    }
                }
                HashMap<String, String> app = new HashMap();
                app.put("pkg", pi.packageName);
                String appName = pi.applicationInfo.name;
                if (appName == null) {
                    int appLbl = pi.applicationInfo.labelRes;
                    if (appLbl > 0) {
                        CharSequence label = pm.getText(pi.packageName, appLbl, pi.applicationInfo);
                        if (label != null) {
                            appName = label.toString().trim();
                        }
                    }
                    if (appName == null) {
                        appName = String.valueOf(pi.applicationInfo.nonLocalizedLabel);
                    }
                }
                app.put(Preferences.sbry, appName);
                app.put("version", pi.versionName);
                arrayList.add(app);
            }
        }
        return arrayList;
    }

    private boolean isSystemApp(PackageInfo pi) {
        boolean isSysApp;
        if ((pi.applicationInfo.flags & 1) == 1) {
            isSysApp = true;
        } else {
            isSysApp = false;
        }
        boolean isSysUpd;
        if ((pi.applicationInfo.flags & 128) == 1) {
            isSysUpd = true;
        } else {
            isSysUpd = false;
        }
        if (isSysApp || isSysUpd) {
            return true;
        }
        return false;
    }

    public String getNetworkOperator() {
        Object tm = getSystemService("phone");
        if (tm == null) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tN");
            sb.append("et");
            sb.append("wo");
            sb.append("rk");
            sb.append("Op");
            sb.append("er");
            sb.append("at");
            sb.append("or");
            return (String) ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public boolean checkPermission(String permission) throws Throwable {
        int res;
        if (VERSION.SDK_INT >= 23) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("an");
                sb.append("dr");
                sb.append("oi");
                sb.append("d.");
                sb.append("co");
                sb.append("nt");
                sb.append("en");
                sb.append("t.");
                sb.append("Co");
                sb.append("nt");
                sb.append("ex");
                sb.append("t");
                ReflectHelper.importClass(sb.toString());
                StringBuilder sb1 = new StringBuilder();
                sb1.append("ch");
                sb1.append("ec");
                sb1.append("kS");
                sb1.append("el");
                sb1.append("fP");
                sb1.append("er");
                sb1.append("mi");
                sb1.append("ss");
                sb1.append("io");
                sb1.append("n");
                Integer ret = (Integer) ReflectHelper.invokeInstanceMethod(this.context, sb1.toString(), permission);
                res = ret == null ? -1 : ret.intValue();
            } catch (Throwable t) {
                MobLog.getInstance().d(t);
                res = -1;
            }
        } else {
            this.context.checkPermission(permission, Process.myPid(), Process.myUid());
            res = this.context.getPackageManager().checkPermission(permission, getPackageName());
        }
        if (res == 0) {
            return true;
        }
        return false;
    }

    public String getTopTaskPackageName() {
        boolean hasPer;
        try {
            hasPer = checkPermission("android.permission.GET_TASKS");
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            hasPer = false;
        }
        if (hasPer) {
            try {
                Object am = getSystemService("activity");
                if (am == null) {
                    return null;
                }
                StringBuilder sb;
                StringBuilder sb1;
                if (VERSION.SDK_INT <= 20) {
                    sb = new StringBuilder();
                    sb.append("ge");
                    sb.append("tR");
                    sb.append("un");
                    sb.append("ni");
                    sb.append("ng");
                    sb.append("Ta");
                    sb.append("sk");
                    sb.append("s");
                    List<?> tasks = (List) ReflectHelper.invokeInstanceMethod(am, sb.toString(), Integer.valueOf(1));
                    sb1 = new StringBuilder();
                    sb1.append("to");
                    sb1.append("pA");
                    sb1.append("ct");
                    sb1.append("iv");
                    sb1.append("it");
                    sb1.append("y");
                    Object topActivity = ReflectHelper.getInstanceField(tasks.get(0), sb1.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("ge");
                    sb2.append("tP");
                    sb2.append("ac");
                    sb2.append("ka");
                    sb2.append("ge");
                    sb2.append("Na");
                    sb2.append("me");
                    return (String) ReflectHelper.invokeInstanceMethod(topActivity, sb2.toString(), new Object[0]);
                }
                sb = new StringBuilder();
                sb.append("ge");
                sb.append("tR");
                sb.append("un");
                sb.append("ni");
                sb.append("ng");
                sb.append("Ap");
                sb.append("pP");
                sb.append("ro");
                sb.append("ce");
                sb.append("ss");
                sb.append("es");
                List<?> processInfos = (List) ReflectHelper.invokeInstanceMethod(am, sb.toString(), new Object[0]);
                sb1 = new StringBuilder();
                sb1.append("pr");
                sb1.append("oc");
                sb1.append("es");
                sb1.append("sN");
                sb1.append("am");
                sb1.append("e");
                return ((String) ReflectHelper.getInstanceField(processInfos.get(0), sb1.toString())).split(":")[0];
            } catch (Throwable t2) {
                MobLog.getInstance().w(t2);
            }
        }
        return null;
    }

    public boolean getSdcardState() {
        try {
            return checkPermission("android.permission.WRITE_EXTERNAL_STORAGE") && "mounted".equals(Environment.getExternalStorageState());
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return false;
        }
    }

    public String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getAndroidID() {
        String androidId = Secure.getString(this.context.getContentResolver(), "android_id");
        MobLog.getInstance().i("getAndroidID === " + androidId, new Object[0]);
        return androidId;
    }

    public String getAdvertisingID() throws Throwable {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new Throwable("Do not call this function from the main thread !");
        }
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        GSConnection gsc = new GSConnection();
        String str = null;
        try {
            this.context.bindService(intent, gsc, 1);
            IBinder binder = gsc.takeBinder();
            Parcel input = Parcel.obtain();
            Parcel output = Parcel.obtain();
            input.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
            binder.transact(1, input, output, 0);
            output.readException();
            str = output.readString();
            output.recycle();
            input.recycle();
            return str;
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
            return str;
        } finally {
            this.context.unbindService(gsc);
        }
    }

    public void hideSoftInput(View view) {
        InputMethodManager service = getSystemService("input_method");
        if (service != null) {
            service.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftInput(View view) {
        InputMethodManager service = getSystemService("input_method");
        if (service != null) {
            service.toggleSoftInputFromWindow(view.getWindowToken(), 2, 0);
        }
    }

    public boolean isMainProcess(int pid) {
        try {
            Object am = getSystemService("activity");
            StringBuilder sb = new StringBuilder();
            sb.append("ge");
            sb.append("tR");
            sb.append("un");
            sb.append("ni");
            sb.append("ng");
            sb.append("Ap");
            sb.append("pP");
            sb.append("ro");
            sb.append("ce");
            sb.append("ss");
            sb.append("es");
            List<?> rps = (List) ReflectHelper.invokeInstanceMethod(am, sb.toString(), new Object[0]);
            if (rps == null) {
                return pid <= 0;
            } else {
                int mPid;
                String application = null;
                if (pid <= 0) {
                    mPid = Process.myPid();
                } else {
                    mPid = pid;
                }
                for (Object appProcess : rps) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append("pi");
                    sb1.append("d");
                    if (((Integer) ReflectHelper.getInstanceField(appProcess, sb1.toString())).intValue() == mPid) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("pr");
                        sb2.append("oc");
                        sb2.append("es");
                        sb2.append("sN");
                        sb2.append("am");
                        sb2.append("e");
                        application = (String) ReflectHelper.getInstanceField(appProcess, sb2.toString());
                        break;
                    }
                }
                return getPackageName().equals(application);
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return false;
        }
    }

    public String getIMSI() {
        Object phone = getSystemService("phone");
        if (phone == null) {
            return null;
        }
        String imsi = null;
        try {
            if (checkPermission("android.permission.READ_PHONE_STATE")) {
                StringBuilder sb = new StringBuilder();
                sb.append("ge");
                sb.append("tS");
                sb.append("ub");
                sb.append("sc");
                sb.append("ri");
                sb.append("be");
                sb.append("rI");
                sb.append("d");
                imsi = (String) ReflectHelper.invokeInstanceMethod(phone, sb.toString(), new Object[0]);
            }
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
        if (TextUtils.isEmpty(imsi)) {
            return null;
        }
        return imsi;
    }

    public String getIPAddress() {
        try {
            if (checkPermission("android.permission.INTERNET")) {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while (en.hasMoreElements()) {
                    Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
        }
        return "0.0.0.0";
    }

    public float[] getLocation(int GPSTimeout, int networkTimeout) {
        if (getLocation(GPSTimeout, networkTimeout, true) == null) {
            return null;
        }
        return new float[]{(float) getLocation(GPSTimeout, networkTimeout, true).getLatitude(), (float) getLocation(GPSTimeout, networkTimeout, true).getLongitude()};
    }

    public Location getLocation(int GPSTimeout, int networkTimeout, boolean useLastKnown) {
        try {
            if (checkPermission("android.permission.ACCESS_FINE_LOCATION")) {
                return new LocationHelper().getLocation(this.context, GPSTimeout, networkTimeout, useLastKnown);
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
        return null;
    }

    public HashMap<String, String> ping(String address, int count, int packetsize) {
        ArrayList<Float> sucRes = new ArrayList();
        int bytes = packetsize + 8;
        Process p = Runtime.getRuntime().exec("ping -c " + count + " -s " + packetsize + Token.SEPARATOR + address);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        while (line != null) {
            int i;
            if (line.startsWith(bytes + " bytes from")) {
                if (line.endsWith("ms")) {
                    line = line.substring(0, line.length() - 2).trim();
                } else if (line.endsWith("s")) {
                    line = line.substring(0, line.length() - 1).trim() + "000";
                }
                i = line.indexOf("time=");
                if (i > 0) {
                    try {
                        sucRes.add(Float.valueOf(Float.parseFloat(line.substring(i + 5).trim())));
                    } catch (Throwable t) {
                        MobLog.getInstance().d(t);
                    }
                }
            }
            line = br.readLine();
        }
        p.waitFor();
        int sucCount = sucRes.size();
        int fldCount = count - sucRes.size();
        float min = 0.0f;
        float max = 0.0f;
        float average = 0.0f;
        if (sucCount > 0) {
            min = AutoScrollHelper.NO_MAX;
            for (i = 0; i < sucCount; i++) {
                float item = ((Float) sucRes.get(i)).floatValue();
                if (item < min) {
                    min = item;
                }
                if (item > max) {
                    max = item;
                }
                average += item;
            }
            average /= (float) sucCount;
        }
        HashMap<String, String> map = new HashMap();
        map.put("address", address);
        map.put("transmitted", String.valueOf(count));
        map.put("received", String.valueOf(sucCount));
        map.put("loss", String.valueOf(fldCount));
        map.put("min", String.valueOf(min));
        map.put("max", String.valueOf(max));
        map.put("avg", String.valueOf(average));
        return map;
    }

    public int getCellId() {
        try {
            if (checkPermission("android.permission.ACCESS_COARSE_LOCATION")) {
                Object tm = getSystemService("phone");
                if (tm != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ge");
                    sb.append("tC");
                    sb.append("el");
                    sb.append("lL");
                    sb.append("oc");
                    sb.append("at");
                    sb.append("io");
                    sb.append("n");
                    Object loc = ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
                    if (loc != null) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append("ge");
                        sb1.append("tC");
                        sb1.append("id");
                        return ((Integer) ResHelper.forceCast(ReflectHelper.invokeInstanceMethod(loc, sb1.toString(), new Object[0]), Integer.valueOf(-1))).intValue();
                    }
                }
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
        return -1;
    }

    public int getCellLac() {
        try {
            if (checkPermission("android.permission.ACCESS_COARSE_LOCATION")) {
                Object tm = getSystemService("phone");
                if (tm != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ge");
                    sb.append("tC");
                    sb.append("el");
                    sb.append("lL");
                    sb.append("oc");
                    sb.append("at");
                    sb.append("io");
                    sb.append("n");
                    Object loc = ReflectHelper.invokeInstanceMethod(tm, sb.toString(), new Object[0]);
                    if (loc != null) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append("ge");
                        sb1.append("tL");
                        sb1.append("ac");
                        return ((Integer) ResHelper.forceCast(ReflectHelper.invokeInstanceMethod(loc, sb1.toString(), new Object[0]), Integer.valueOf(-1))).intValue();
                    }
                }
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
        return -1;
    }

    public String getDeviceType() {
        UiModeManager um = (UiModeManager) getSystemService("uimode");
        if (um != null) {
            switch (um.getCurrentModeType()) {
                case 1:
                    return "NO_UI";
                case 2:
                    return "DESK";
                case 3:
                    return "CAR";
                case 4:
                    return "TELEVISION";
                case 5:
                    return "APPLIANCE";
                case 6:
                    return "WATCH";
            }
        }
        return "UNDEFINED";
    }
}
