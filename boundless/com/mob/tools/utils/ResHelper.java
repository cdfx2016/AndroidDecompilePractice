package com.mob.tools.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.fanyu.boundless.util.StringUtils;
import com.mob.tools.MobLog;
import com.mob.tools.network.KVPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResHelper {
    private static float density;
    private static int deviceWidth;
    private static Uri mediaUri;
    private static Object rp;

    public static int dipToPx(Context context, int dip) {
        if (density <= 0.0f) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) ((((float) dip) * density) + 0.5f);
    }

    public static int pxToDip(Context context, int px) {
        if (density <= 0.0f) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) ((((float) px) / density) + 0.5f);
    }

    public static int designToDevice(Context context, int designScreenWidth, int designPx) {
        if (deviceWidth == 0) {
            int[] scrSize = getScreenSize(context);
            deviceWidth = scrSize[0] < scrSize[1] ? scrSize[0] : scrSize[1];
        }
        return (int) (((((float) designPx) * ((float) deviceWidth)) / ((float) designScreenWidth)) + 0.5f);
    }

    public static int designToDevice(Context context, float designScreenDensity, int designPx) {
        if (density <= 0.0f) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) (((((float) designPx) * density) / designScreenDensity) + 0.5f);
    }

    public static int[] getScreenSize(Context context) {
        WindowManager windowManager;
        try {
            windowManager = (WindowManager) context.getSystemService("window");
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            windowManager = null;
        }
        if (windowManager == null) {
            return new int[]{0, 0};
        }
        Display display = windowManager.getDefaultDisplay();
        if (VERSION.SDK_INT < 13) {
            display.getMetrics(new DisplayMetrics());
            return new int[]{dm.widthPixels, dm.heightPixels};
        }
        try {
            Point size = new Point();
            Method method = display.getClass().getMethod("getRealSize", new Class[]{Point.class});
            method.setAccessible(true);
            method.invoke(display, new Object[]{size});
            return new int[]{size.x, size.y};
        } catch (Throwable t2) {
            MobLog.getInstance().w(t2);
            return new int[]{0, 0};
        }
    }

    public static int getScreenWidth(Context context) {
        return getScreenSize(context)[0];
    }

    public static int getScreenHeight(Context context) {
        return getScreenSize(context)[1];
    }

    public static void setResourceProvider(Object rp) {
        try {
            if (rp.getClass().getMethod("getResId", new Class[]{Context.class, String.class, String.class}) != null) {
                rp = rp;
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
    }

    public static int getResId(Context context, String resType, String resName) {
        int resId = 0;
        if (context == null || TextUtils.isEmpty(resType) || TextUtils.isEmpty(resName)) {
            return resId;
        }
        if (rp != null) {
            try {
                Method mth = rp.getClass().getMethod("getResId", new Class[]{Context.class, String.class, String.class});
                mth.setAccessible(true);
                resId = ((Integer) mth.invoke(rp, new Object[]{context, resType, resName})).intValue();
            } catch (Throwable t) {
                MobLog.getInstance().d(t);
            }
        }
        if (resId <= 0) {
            String pck = context.getPackageName();
            if (TextUtils.isEmpty(pck)) {
                return resId;
            }
            if (resId <= 0) {
                resId = context.getResources().getIdentifier(resName, resType, pck);
                if (resId <= 0) {
                    resId = context.getResources().getIdentifier(resName.toLowerCase(), resType, pck);
                }
            }
            if (resId <= 0) {
                System.err.println("failed to parse " + resType + " resource \"" + resName + "\"");
            }
        }
        return resId;
    }

    public static int getBitmapRes(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    public static int getStringRes(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    public static int getStringArrayRes(Context context, String resName) {
        return getResId(context, "array", resName);
    }

    public static int getLayoutRes(Context context, String resName) {
        return getResId(context, TtmlNode.TAG_LAYOUT, resName);
    }

    public static int getStyleRes(Context context, String resName) {
        return getResId(context, TtmlNode.TAG_STYLE, resName);
    }

    public static int getIdRes(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    public static int getColorRes(Context context, String resName) {
        return getResId(context, TtmlNode.ATTR_TTS_COLOR, resName);
    }

    public static int getRawRes(Context context, String resName) {
        return getResId(context, "raw", resName);
    }

    public static int getPluralsRes(Context context, String resName) {
        return getResId(context, "plurals", resName);
    }

    public static int getAnimRes(Context context, String resName) {
        return getResId(context, "anim", resName);
    }

    public static String getCacheRoot(Context context) {
        String appDir = context.getFilesDir().getAbsolutePath() + "/Mob/";
        DeviceHelper helper = DeviceHelper.getInstance(context);
        if (helper.getSdcardState()) {
            appDir = helper.getSdcardPath() + "/Mob/";
        }
        File file = new File(appDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appDir;
    }

    public static String getCachePath(Context context, String category) {
        String appDir = context.getFilesDir().getAbsolutePath() + "/Mob/cache/";
        DeviceHelper helper = DeviceHelper.getInstance(context);
        try {
            if (helper.getSdcardState()) {
                appDir = helper.getSdcardPath() + "/Mob/" + helper.getPackageName() + "/cache/";
            }
        } catch (Throwable t) {
            MobLog.getInstance().d(t);
        }
        if (!TextUtils.isEmpty(category)) {
            appDir = appDir + category + "/";
        }
        File file = new File(appDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appDir;
    }

    public static String getImageCachePath(Context context) {
        return getCachePath(context, "images");
    }

    public static void clearCache(Context context) throws Throwable {
        deleteFileAndFolder(new File(getCachePath(context, null)));
    }

    public static void deleteFilesInFolder(File folder) throws Throwable {
        if (folder != null && folder.exists()) {
            if (folder.isFile()) {
                folder.delete();
                return;
            }
            String[] names = folder.list();
            if (names != null && names.length > 0) {
                for (String name : names) {
                    File f = new File(folder, name);
                    if (f.isDirectory()) {
                        deleteFilesInFolder(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
    }

    public static void deleteFileAndFolder(File folder) throws Throwable {
        if (folder != null && folder.exists()) {
            if (folder.isFile()) {
                folder.delete();
                return;
            }
            String[] names = folder.list();
            if (names == null || names.length <= 0) {
                folder.delete();
                return;
            }
            for (String name : names) {
                File f = new File(folder, name);
                if (f.isDirectory()) {
                    deleteFileAndFolder(f);
                } else {
                    f.delete();
                }
            }
            folder.delete();
        }
    }

    public static String toWordText(String text, int lenInWord) {
        char[] cText = text.toCharArray();
        int count = lenInWord * 2;
        StringBuilder sb = new StringBuilder();
        for (char ch : cText) {
            count -= ch < 'Ā' ? 1 : 2;
            if (count < 0) {
                return sb.toString();
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static int getTextLengthInWord(String text) {
        int count = 0;
        for (char c : text == null ? new char[0] : text.toCharArray()) {
            count += c < 'Ā' ? 1 : 2;
        }
        return count;
    }

    public static long strToDate(String strDate) {
        return new SimpleDateFormat(StringUtils.DEFAULT_DATE_TIME_FORMAT).parse(strDate, new ParsePosition(0)).getTime();
    }

    public static long dateStrToLong(String strDate) {
        return new SimpleDateFormat(StringUtils.DEFAULT_FORMAT_DATE).parse(strDate, new ParsePosition(0)).getTime();
    }

    public static Date longToDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.getTime();
    }

    public static String longToTime(long time, int level) {
        String format = "yyyy-MM-dd kk:mm:ss";
        switch (level) {
            case 1:
                format = "yyyy";
                break;
            case 2:
                format = "yyyy-MM";
                break;
            case 5:
                format = StringUtils.DEFAULT_FORMAT_DATE;
                break;
            case 10:
                format = "yyyy-MM-dd kk";
                break;
            case 12:
                format = "yyyy-MM-dd kk:mm";
                break;
        }
        return new SimpleDateFormat(format).format(Long.valueOf(time));
    }

    public static long dateToLong(String date) {
        try {
            Date d = new Date(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal.getTimeInMillis();
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return 0;
        }
    }

    public static int[] covertTimeInYears(long time) {
        long delta = System.currentTimeMillis() - time;
        if (delta <= 0) {
            return new int[]{0, 0};
        }
        delta /= 1000;
        if (delta < 60) {
            return new int[]{(int) delta, 0};
        }
        delta /= 60;
        if (delta < 60) {
            return new int[]{(int) delta, 1};
        }
        delta /= 60;
        if (delta < 24) {
            return new int[]{(int) delta, 2};
        }
        delta /= 24;
        if (delta < 30) {
            return new int[]{(int) delta, 3};
        }
        if (delta / 30 < 12) {
            return new int[]{(int) (delta / 30), 4};
        }
        return new int[]{(int) ((delta / 30) / 12), 5};
    }

    public static Uri pathToContentUri(Context context, String imagePath) {
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{imagePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            return Uri.withAppendedPath(Uri.parse("content://media/external/images/media"), "" + cursor.getInt(cursor.getColumnIndex("_id")));
        } else if (!new File(imagePath).exists()) {
            return null;
        } else {
            ContentValues values = new ContentValues();
            values.put("_data", imagePath);
            return context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static String contentUriToPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        if (new File(uri.getPath()).exists()) {
            return uri.getPath();
        }
        String path = null;
        Cursor c = null;
        try {
            if (VERSION.SDK_INT >= 19) {
                Class<?> DocumentsContract = Class.forName("android.provider.DocumentsContract");
                Method isDocumentUri = DocumentsContract.getMethod("isDocumentUri", new Class[]{Context.class, Uri.class});
                isDocumentUri.setAccessible(true);
                if (Boolean.TRUE.equals(isDocumentUri.invoke(null, new Object[]{context, uri}))) {
                    Method getDocumentId = DocumentsContract.getMethod("getDocumentId", new Class[]{Uri.class});
                    getDocumentId.setAccessible(true);
                    String id = String.valueOf(getDocumentId.invoke(null, new Object[]{uri})).split(":")[1];
                    String[] column = new String[]{"_data"};
                    String[] args = new String[]{id};
                    c = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, column, "_id=?", args, null);
                }
            }
            if (c == null) {
                c = context.getContentResolver().query(uri, null, null, null, null);
            }
            if (c == null) {
                return null;
            }
            if (c.moveToFirst()) {
                path = c.getString(c.getColumnIndex("_data"));
            }
            c.close();
            return path;
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
            return null;
        }
    }

    public static Uri videoPathToContentUri(Context context, String videoPath) {
        Cursor cursor = context.getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{videoPath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            return Uri.withAppendedPath(Uri.parse("content://media/external/video/media"), "" + cursor.getInt(cursor.getColumnIndex("_id")));
        } else if (!new File(videoPath).exists()) {
            return null;
        } else {
            ContentValues values = new ContentValues();
            values.put("_data", videoPath);
            return context.getContentResolver().insert(Video.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static synchronized Uri getMediaUri(Context context, String filePath, String mimeType) {
        Uri result;
        synchronized (ResHelper.class) {
            final Object object = new Object();
            mediaUri = null;
            MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{mimeType}, new OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    ResHelper.mediaUri = uri;
                    synchronized (object) {
                        object.notifyAll();
                    }
                }
            });
            try {
                if (mediaUri == null) {
                    synchronized (object) {
                        object.wait(10000);
                    }
                }
            } catch (InterruptedException e) {
            }
            result = mediaUri;
            mediaUri = null;
        }
        return result;
    }

    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            if (value == null) {
                value = "";
            }
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(Data.urlEncode(key) + "=" + Data.urlEncode(String.valueOf(value)));
        }
        return sb.toString();
    }

    public static String encodeUrl(ArrayList<KVPair<String>> values) {
        if (values == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Iterator i$ = values.iterator();
        while (i$.hasNext()) {
            KVPair<String> pair = (KVPair) i$.next();
            if (i > 0) {
                sb.append('&');
            }
            String key = pair.name;
            String value = pair.value;
            if (key != null) {
                if (value == null) {
                    value = "";
                }
                sb.append(Data.urlEncode(key) + "=" + Data.urlEncode(value));
                i++;
            }
        }
        return sb.toString();
    }

    public static Bundle urlToBundle(String url) {
        int index = url.indexOf("://");
        if (index >= 0) {
            url = "http://" + url.substring(index + 1);
        } else {
            url = "http://" + url;
        }
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return new Bundle();
        }
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            for (String parameter : s.split("&")) {
                String[] v = parameter.split("=");
                if (v.length < 2 || v[1] == null) {
                    params.putString(URLDecoder.decode(v[0]), "");
                } else {
                    params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }

    public static int parseInt(String string) throws Throwable {
        return parseInt(string, 10);
    }

    public static int parseInt(String string, int radix) throws Throwable {
        if (radix < 2 || radix > 36) {
            throw new Throwable("Invalid radix: " + radix);
        } else if (string == null) {
            throw invalidInt(string);
        } else {
            int length = string.length();
            int i = 0;
            if (length == 0) {
                throw invalidInt(string);
            }
            boolean negative = string.charAt(0) == '-';
            if (negative) {
                i = 0 + 1;
                if (i == length) {
                    throw invalidInt(string);
                }
            }
            return parseInt(string, i, radix, negative);
        }
    }

    private static int parseInt(String string, int offset, int radix, boolean negative) throws Throwable {
        int max = Integer.MIN_VALUE / radix;
        int result = 0;
        int length = string.length();
        int offset2 = offset;
        while (offset2 < length) {
            offset = offset2 + 1;
            int digit = digit(string.charAt(offset2), radix);
            if (digit == -1) {
                throw invalidInt(string);
            } else if (max > result) {
                throw invalidInt(string);
            } else {
                int next = (result * radix) - digit;
                if (next > result) {
                    throw invalidInt(string);
                }
                result = next;
                offset2 = offset;
            }
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw invalidInt(string);
            }
        }
        return result;
    }

    private static int digit(int codePoint, int radix) {
        if (radix < 2 || radix > 36) {
            return -1;
        }
        int result = -1;
        if (48 <= codePoint && codePoint <= 57) {
            result = codePoint - 48;
        } else if (97 <= codePoint && codePoint <= 122) {
            result = (codePoint - 97) + 10;
        } else if (65 <= codePoint && codePoint <= 90) {
            result = (codePoint - 65) + 10;
        }
        if (result >= radix) {
            return -1;
        }
        return result;
    }

    private static Throwable invalidInt(String s) throws Throwable {
        throw new Throwable("Invalid int: \"" + s + "\"");
    }

    public static long parseLong(String string) throws Throwable {
        return parseLong(string, 10);
    }

    public static long parseLong(String string, int radix) throws Throwable {
        if (radix < 2 || radix > 36) {
            throw new Throwable("Invalid radix: " + radix);
        } else if (string == null) {
            throw new Throwable("Invalid long: \"" + string + "\"");
        } else {
            int length = string.length();
            int i = 0;
            if (length == 0) {
                throw new Throwable("Invalid long: \"" + string + "\"");
            }
            boolean negative = string.charAt(0) == '-';
            if (negative) {
                i = 0 + 1;
                if (i == length) {
                    throw new Throwable("Invalid long: \"" + string + "\"");
                }
            }
            return parseLong(string, i, radix, negative);
        }
    }

    private static long parseLong(String string, int offset, int radix, boolean negative) throws Throwable {
        long max = Long.MIN_VALUE / ((long) radix);
        long result = 0;
        long length = (long) string.length();
        int i = offset;
        while (((long) i) < length) {
            offset = i + 1;
            int digit = digit(string.charAt(i), radix);
            if (digit == -1) {
                throw new Throwable("Invalid long: \"" + string + "\"");
            } else if (max > result) {
                throw new Throwable("Invalid long: \"" + string + "\"");
            } else {
                long next = (((long) radix) * result) - ((long) digit);
                if (next > result) {
                    throw new Throwable("Invalid long: \"" + string + "\"");
                }
                result = next;
                i = offset;
            }
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw new Throwable("Invalid long: \"" + string + "\"");
            }
        }
        return result;
    }

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static <T> T forceCast(Object obj) {
        return forceCast(obj, null);
    }

    public static <T> T forceCast(Object obj, T defValue) {
        boolean z = true;
        if (obj == null) {
            return defValue;
        }
        if (obj instanceof Byte) {
            byte value = ((Byte) obj).byteValue();
            if (defValue instanceof Boolean) {
                return Boolean.valueOf(value != (byte) 0);
            } else if (defValue instanceof Short) {
                return Short.valueOf((short) value);
            } else {
                if (defValue instanceof Character) {
                    return Character.valueOf((char) value);
                }
                if (defValue instanceof Integer) {
                    return Integer.valueOf(value);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value);
                }
            }
        } else if (obj instanceof Character) {
            char value2 = ((Character) obj).charValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) value2);
            }
            if (defValue instanceof Boolean) {
                if (value2 == '\u0000') {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Short) {
                return Short.valueOf((short) value2);
            } else {
                if (defValue instanceof Integer) {
                    return Integer.valueOf(value2);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value2);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value2);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value2);
                }
            }
        } else if (obj instanceof Short) {
            short value3 = ((Short) obj).shortValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) value3);
            }
            if (defValue instanceof Boolean) {
                if (value3 == (short) 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Character) {
                return Character.valueOf((char) value3);
            } else {
                if (defValue instanceof Integer) {
                    return Integer.valueOf(value3);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value3);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value3);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value3);
                }
            }
        } else if (obj instanceof Integer) {
            int value4 = ((Integer) obj).intValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) value4);
            }
            if (defValue instanceof Boolean) {
                if (value4 == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Character) {
                return Character.valueOf((char) value4);
            } else {
                if (defValue instanceof Short) {
                    return Short.valueOf((short) value4);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value4);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value4);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value4);
                }
            }
        } else if (obj instanceof Float) {
            float value5 = ((Float) obj).floatValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) ((int) value5));
            }
            if (defValue instanceof Boolean) {
                if (value5 == 0.0f) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Character) {
                return Character.valueOf((char) ((int) value5));
            } else {
                if (defValue instanceof Short) {
                    return Short.valueOf((short) ((int) value5));
                }
                if (defValue instanceof Integer) {
                    return Integer.valueOf((int) value5);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value5);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value5);
                }
            }
        } else if (obj instanceof Long) {
            long value6 = ((Long) obj).longValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) ((int) value6));
            }
            if (defValue instanceof Boolean) {
                if (value6 == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Character) {
                return Character.valueOf((char) ((int) value6));
            } else {
                if (defValue instanceof Short) {
                    return Short.valueOf((short) ((int) value6));
                }
                if (defValue instanceof Integer) {
                    return Integer.valueOf((int) value6);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value6);
                }
                if (defValue instanceof Double) {
                    return Double.valueOf((double) value6);
                }
            }
        } else if (obj instanceof Double) {
            double value7 = ((Double) obj).doubleValue();
            if (defValue instanceof Byte) {
                return Byte.valueOf((byte) ((int) value7));
            }
            if (defValue instanceof Boolean) {
                if (value7 == 0.0d) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } else if (defValue instanceof Character) {
                return Character.valueOf((char) ((int) value7));
            } else {
                if (defValue instanceof Short) {
                    return Short.valueOf((short) ((int) value7));
                }
                if (defValue instanceof Integer) {
                    return Integer.valueOf((int) value7);
                }
                if (defValue instanceof Float) {
                    return Float.valueOf((float) value7);
                }
                if (defValue instanceof Long) {
                    return Long.valueOf((long) value7);
                }
            }
        }
        return obj;
    }

    public static boolean copyFile(String fromFilePath, String toFilePath) {
        if (TextUtils.isEmpty(fromFilePath) || TextUtils.isEmpty(toFilePath) || !new File(fromFilePath).exists()) {
            return false;
        }
        try {
            copyFile(new FileInputStream(fromFilePath), new FileOutputStream(toFilePath));
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static void copyFile(FileInputStream src, FileOutputStream dst) throws Throwable {
        byte[] buf = new byte[65536];
        int len = src.read(buf);
        while (len > 0) {
            dst.write(buf, 0, len);
            len = src.read(buf);
        }
        src.close();
        dst.close();
    }

    public static long getFileSize(String path) throws Throwable {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        return getFileSize(new File(path));
    }

    public static long getFileSize(File file) throws Throwable {
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        int size = 0;
        for (String file2 : file.list()) {
            size = (int) (((long) size) + getFileSize(new File(file, file2)));
        }
        return (long) size;
    }

    public static boolean saveObjectToFile(String filePath, Object object) {
        File cacheFile;
        Throwable t;
        ObjectOutputStream oos;
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File cacheFile2 = new File(filePath);
                try {
                    if (cacheFile2.exists()) {
                        cacheFile2.delete();
                    }
                    if (!cacheFile2.getParentFile().exists()) {
                        cacheFile2.getParentFile().mkdirs();
                    }
                    cacheFile2.createNewFile();
                    cacheFile = cacheFile2;
                } catch (Throwable th) {
                    t = th;
                    cacheFile = cacheFile2;
                    t.printStackTrace();
                    cacheFile = null;
                    if (cacheFile != null) {
                        try {
                            oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(cacheFile)));
                            oos.writeObject(object);
                            oos.flush();
                            oos.close();
                            return true;
                        } catch (Throwable t2) {
                            t2.printStackTrace();
                        }
                    }
                    return false;
                }
            } catch (Throwable th2) {
                t2 = th2;
                t2.printStackTrace();
                cacheFile = null;
                if (cacheFile != null) {
                    oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(cacheFile)));
                    oos.writeObject(object);
                    oos.flush();
                    oos.close();
                    return true;
                }
                return false;
            }
            if (cacheFile != null) {
                oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(cacheFile)));
                oos.writeObject(object);
                oos.flush();
                oos.close();
                return true;
            }
        }
        return false;
    }

    public static Object readObjectFromFile(String filePath) {
        File cacheFile;
        Throwable t;
        ObjectInputStream ois;
        Object object;
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File cacheFile2 = new File(filePath);
                try {
                    if (cacheFile2.exists()) {
                        cacheFile = cacheFile2;
                    } else {
                        cacheFile = null;
                    }
                } catch (Throwable th) {
                    t = th;
                    cacheFile = cacheFile2;
                    t.printStackTrace();
                    cacheFile = null;
                    if (cacheFile != null) {
                        try {
                            ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFile)));
                            object = ois.readObject();
                            ois.close();
                            return object;
                        } catch (Throwable t2) {
                            t2.printStackTrace();
                        }
                    }
                    return null;
                }
            } catch (Throwable th2) {
                t2 = th2;
                t2.printStackTrace();
                cacheFile = null;
                if (cacheFile != null) {
                    ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFile)));
                    object = ois.readObject();
                    ois.close();
                    return object;
                }
                return null;
            }
            if (cacheFile != null) {
                ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFile)));
                object = ois.readObject();
                ois.close();
                return object;
            }
        }
        return null;
    }
}
