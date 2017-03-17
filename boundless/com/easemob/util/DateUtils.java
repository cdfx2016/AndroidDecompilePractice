package com.easemob.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static final long INTERVAL_IN_MILLISECONDS = 30000;
    private static final long MILLIS_PER_DAY = 86400000;

    public static Date StringToDate(String str, String str2) {
        Date date = null;
        try {
            date = new SimpleDateFormat(str2).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(5, -2);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        long time = instance.getTime().getTime();
        Calendar instance2 = Calendar.getInstance();
        instance2.add(5, -2);
        instance2.set(11, 23);
        instance2.set(12, 59);
        instance2.set(13, 59);
        instance2.set(14, 999);
        long time2 = instance2.getTime().getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(time);
        timeInfo.setEndTime(time2);
        return timeInfo;
    }

    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar instance = Calendar.getInstance();
        instance.set(5, 1);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        long time = instance.getTime().getTime();
        long time2 = Calendar.getInstance().getTime().getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(time);
        timeInfo.setEndTime(time2);
        return timeInfo;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(2, -1);
        instance.set(5, 1);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        long time = instance.getTime().getTime();
        Calendar instance2 = Calendar.getInstance();
        instance2.add(2, -1);
        instance2.set(5, 1);
        instance2.set(11, 23);
        instance2.set(12, 59);
        instance2.set(13, 59);
        instance2.set(14, 999);
        instance2.roll(5, -1);
        long time2 = instance2.getTime().getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(time);
        timeInfo.setEndTime(time2);
        return timeInfo;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

    public static String getTimestampString(Date date) {
        String str;
        long time = date.getTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (isSameDay(time, currentTimeMillis)) {
            Calendar instance = GregorianCalendar.getInstance();
            instance.setTime(date);
            int i = instance.get(11);
            str = i > 17 ? "晚上 hh:mm" : (i < 0 || i > 6) ? (i <= 11 || i > 17) ? "上午 hh:mm" : "下午 hh:mm" : "凌晨 hh:mm";
        } else {
            str = isYesterday(time, currentTimeMillis) ? "昨天 HH:mm" : "M月d日 HH:mm";
        }
        return new SimpleDateFormat(str, Locale.CHINA).format(date);
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar instance = Calendar.getInstance();
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        long time = instance.getTime().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar instance2 = Calendar.getInstance();
        instance2.set(11, 23);
        instance2.set(12, 59);
        instance2.set(13, 59);
        instance2.set(14, 999);
        long time2 = instance2.getTime().getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(time);
        timeInfo.setEndTime(time2);
        return timeInfo;
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(5, -1);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        long time = instance.getTime().getTime();
        Calendar instance2 = Calendar.getInstance();
        instance2.add(5, -1);
        instance2.set(11, 23);
        instance2.set(12, 59);
        instance2.set(13, 59);
        instance2.set(14, 999);
        long time2 = instance2.getTime().getTime();
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setStartTime(time);
        timeInfo.setEndTime(time2);
        return timeInfo;
    }

    public static boolean isCloseEnough(long j, long j2) {
        long j3 = j - j2;
        if (j3 < 0) {
            j3 = -j3;
        }
        return j3 < 30000;
    }

    private static boolean isSameDay(long j, long j2) {
        return j / MILLIS_PER_DAY == j2 / MILLIS_PER_DAY;
    }

    private static boolean isYesterday(long j, long j2) {
        return (j / MILLIS_PER_DAY) + 1 == j2 / MILLIS_PER_DAY;
    }

    public static String toTime(int i) {
        int i2 = i / 1000;
        int i3 = i2 / 60;
        if (i3 >= 60) {
            int i4 = i3 / 60;
            i3 %= 60;
        }
        i2 %= 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i2)});
    }
}
