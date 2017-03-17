package cn.finalteam.toolsfinal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class DeviceUtils {
    public static final int NETWORK_CLASS_2_G = 2;
    public static final int NETWORK_CLASS_3_G = 3;
    public static final int NETWORK_CLASS_4_G = 4;
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1;

    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
            return "0.0.0.0";
        } catch (SocketException e) {
            return "0.0.0.0";
        }
    }

    public static String getExternalStorageDirectory() {
        Map<String, String> map = System.getenv();
        String[] values = new String[map.values().size()];
        map.values().toArray(values);
        String path = values[values.length - 1];
        return (!path.startsWith("/mnt/") || Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) ? null : path;
    }

    public static long getAvailaleSize() {
        if (!existSDCard()) {
            return 0;
        }
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
    }

    public static long getAllSize() {
        if (!existSDCard()) {
            return 0;
        }
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
    }

    public static boolean isOnline(Context context) {
        boolean z = false;
        try {
            NetworkInfo ni = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (ni != null) {
                z = ni.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        List<RunningServiceInfo> serviceList = ((ActivityManager) mContext.getSystemService("activity")).getRunningServices(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        if (serviceList.size() == 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (((RunningServiceInfo) serviceList.get(i)).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isProessRunning(Context context, String proessName) {
        for (RunningAppProcessInfo info : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
            if (info.processName.equals(proessName)) {
                return true;
            }
        }
        return false;
    }

    public static String getIMEI(Context context) {
        String imei = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        if (StringUtils.isEmpty(imei)) {
            return "";
        }
        return imei;
    }

    public static String getMac(Context context) {
        String mac = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
        if (StringUtils.isEmpty(mac)) {
            return "";
        }
        return mac;
    }

    public static String getUDID(Context context) {
        String udid = Secure.getString(context.getContentResolver(), "android_id");
        if (StringUtils.isEmpty(udid) || udid.equals("9774d56d682e549c") || udid.length() < 15) {
            udid = new BigInteger(64, new SecureRandom()).toString(16);
        }
        if (StringUtils.isEmpty(udid)) {
            return "";
        }
        return udid;
    }

    public static void vibrate(Context context, long duration) {
        ((Vibrator) context.getSystemService("vibrator")).vibrate(new long[]{0, duration}, -1);
    }

    public static String getLatestCameraPicture(Context context) {
        if (!existSDCard()) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "bucket_display_name", "datetaken", "mime_type"}, null, null, "datetaken DESC");
        if (cursor.moveToFirst()) {
            return cursor.getString(1);
        }
        return null;
    }

    public static DisplayMetrics getScreenPix(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    @TargetApi(11)
    public static void coptyToClipBoard(Context context, String content) {
        if (VERSION.SDK_INT >= 11) {
            ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", content));
        } else {
            ((android.text.ClipboardManager) context.getSystemService("clipboard")).setText(content);
        }
    }

    public static List<String> getAppPackageNamelist(Context context) {
        List<String> packList = new ArrayList();
        for (PackageInfo packinfo : context.getPackageManager().getInstalledPackages(0)) {
            packList.add(packinfo.packageName);
        }
        return packList;
    }

    public static boolean isAppInstall(Context context, String packageName) {
        List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
        List<String> packageNames = new ArrayList();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                packageNames.add(((PackageInfo) packageInfos.get(i)).packageName);
            }
        }
        return packageNames.contains(packageName);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) ((dipValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public boolean isSoftKeyAvail(Activity activity) {
        final boolean[] isSoftkey = new boolean[]{false};
        final View activityRootView = activity.getWindow().getDecorView().findViewById(16908290);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (activityRootView.getRootView().getHeight() - activityRootView.getHeight() > 100) {
                    isSoftkey[0] = true;
                }
            }
        });
        return isSoftkey[0];
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(16908290).getTop();
    }

    @SuppressLint({"NewApi"})
    public static boolean startActivityForPackage(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent("android.intent.action.MAIN", null);
            resolveIntent.addCategory("android.intent.category.LAUNCHER");
            resolveIntent.setFlags(131072);
            resolveIntent.setPackage(pi.packageName);
            ResolveInfo ri = (ResolveInfo) context.getPackageManager().queryIntentActivities(resolveIntent, 0).iterator().next();
            if (ri == null) {
                return false;
            }
            String packageName1 = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setFlags(270532608);
            intent.setComponent(new ComponentName(packageName1, className));
            context.startActivity(intent);
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void hideInputSoftFromWindowMethod(Context context, View view) {
        try {
            ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInputSoftFromWindowMethod(Context context, View view) {
        try {
            ((InputMethodManager) context.getSystemService("input_method")).showSoftInput(view, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isActiveSoftInput(Context context) {
        return ((InputMethodManager) context.getSystemService("input_method")).isActive();
    }

    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent("android.intent.action.MAIN");
        mHomeIntent.addCategory("android.intent.category.HOME");
        mHomeIntent.addFlags(270532608);
        context.startActivity(mHomeIntent);
    }

    public static int getPhoneType(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getPhoneType();
    }

    public static int getNetType(Context context) {
        int netWorkType = 0;
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == 1) {
                netWorkType = 1;
            } else if (type == 0) {
                switch (((TelephonyManager) context.getSystemService("phone")).getNetworkType()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return 2;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return 3;
                    case 13:
                        return 4;
                    default:
                        return 0;
                }
            }
        }
        return netWorkType;
    }

    public static void callPhone(Context context, String phoneNumber) {
        context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
    }

    public static void callDial(Context context, String phoneNumber) {
        context.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNumber)));
    }

    public static void sendSms(Context context, String phoneNumber, String content) {
        StringBuilder append = new StringBuilder().append("smsto:");
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = "";
        }
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(append.append(phoneNumber).toString()));
        String str = "sms_body";
        if (TextUtils.isEmpty(content)) {
            content = "";
        }
        intent.putExtra(str, content);
        context.startActivity(intent);
    }

    public static boolean isPhone(Context context) {
        if (((TelephonyManager) context.getSystemService("phone")).getPhoneType() == 0) {
            return false;
        }
        return true;
    }
}
