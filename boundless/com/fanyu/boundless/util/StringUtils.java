package com.fanyu.boundless.util;

import com.easemob.util.HanziToPinyin.Token;
import com.xiaomi.mipush.sdk.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtils {
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
    public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(StringUtils.DEFAULT_FORMAT_DATE);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(StringUtils.DEFAULT_DATE_TIME_FORMAT);
        }
    };
    private static StringUtils mInstance;

    public static StringUtils getsInstances() {
        if (mInstance == null) {
            mInstance = new StringUtils();
        }
        return mInstance;
    }

    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    public static String getOtherDay(int diff) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(5, diff);
        return getDateFormat(mCalendar.getTime());
    }

    public static String getDateFormat(Date date) {
        return dateSimpleFormat(date, (SimpleDateFormat) defaultDateFormat.get());
    }

    public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
        if (format == null) {
            format = (SimpleDateFormat) defaultDateTimeFormat.get();
        }
        return date == null ? "" : format.format(date);
    }

    public static String datestring(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(Long.parseLong(time));
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return sdf.format(gc.getTime());
    }

    public static String formatDateTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (time == null || "".equals(time)) {
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(1, current.get(1));
        today.set(2, current.get(2));
        today.set(5, current.get(5));
        today.set(11, 0);
        today.set(12, 0);
        today.set(13, 0);
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(1, current.get(1));
        yesterday.set(2, current.get(2));
        yesterday.set(5, current.get(5) - 1);
        yesterday.set(11, 0);
        yesterday.set(12, 0);
        yesterday.set(13, 0);
        current.setTime(date);
        if (current.after(today)) {
            return time.split(Token.SEPARATOR)[1];
        }
        if (current.before(today) && current.after(yesterday)) {
            return "昨天 " + time.split(Token.SEPARATOR)[1];
        }
        return time.substring(time.indexOf(Constants.ACCEPT_TIME_SEPARATOR_SERVER) + 1, time.length());
    }

    public static boolean isEmpty(String value) {
        if (value == null || value.equals("") || value.equals("null")) {
            return false;
        }
        return true;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    public static String covertDateToString2(Date date) {
        return dateToString(date, DEFAULT_FORMAT_DATE);
    }

    public static String dateToString(Date date, String formatStr) {
        try {
            return new SimpleDateFormat(formatStr).format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        return Pattern.compile("[\\s]").matcher(str).replaceAll("");
    }
}
