package fm.jiecao.jcvideoplayer_lib;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import java.util.Formatter;
import java.util.Locale;

public class JCUtils {
    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 86400000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        Formatter mFormatter = new Formatter(new StringBuilder(), Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
        }
        return mFormatter.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo == null || networkInfo.getType() != 1) {
            return false;
        }
        return true;
    }

    public static Activity scanForActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null;
    }

    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        }
        return context instanceof ContextThemeWrapper ? getAppCompActivity(((ContextThemeWrapper) context).getBaseContext()) : null;
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int getUrlType(String url) {
        if (url == null) {
            return 3;
        }
        if (url.contains(".mpd")) {
            return 0;
        }
        if (url.contains(".ism") || url.contains(".isml")) {
            return 1;
        }
        if (url.contains(".m3u8")) {
            return 2;
        }
        return 3;
    }
}
