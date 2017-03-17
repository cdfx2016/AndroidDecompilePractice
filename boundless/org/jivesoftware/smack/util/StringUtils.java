package org.jivesoftware.smack.util;

import cn.finalteam.toolsfinal.io.IOUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class StringUtils {
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] APOS_ENCODE = "&apos;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    public static final DateFormat XEP_0082_UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final List<PatternCouplings> couplings = new ArrayList();
    private static final DateFormat dateFormatter = DateFormatType.XEP_0082_DATE_PROFILE.createFormatter();
    private static final Pattern datePattern = Pattern.compile("^\\d+-\\d+-\\d+$");
    private static final DateFormat dateTimeFormatter = DateFormatType.XEP_0082_DATETIME_MILLIS_PROFILE.createFormatter();
    private static final DateFormat dateTimeNoMillisFormatter = DateFormatType.XEP_0082_DATETIME_PROFILE.createFormatter();
    private static final Pattern dateTimeNoMillisPattern = Pattern.compile("^\\d+(-\\d+){2}+T(\\d+:){2}\\d+(Z|([+-](\\d+:\\d+)))?$");
    private static final Pattern dateTimePattern = Pattern.compile("^\\d+(-\\d+){2}+T(\\d+:){2}\\d+.\\d+(Z|([+-](\\d+:\\d+)))?$");
    private static MessageDigest digest = null;
    private static char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static Random randGen = new Random();
    private static final DateFormat timeFormatter = DateFormatType.XEP_0082_TIME_MILLIS_ZONE_PROFILE.createFormatter();
    private static final DateFormat timeNoMillisFormatter = DateFormatType.XEP_0082_TIME_ZONE_PROFILE.createFormatter();
    private static final DateFormat timeNoMillisNoZoneFormatter = DateFormatType.XEP_0082_TIME_PROFILE.createFormatter();
    private static final Pattern timeNoMillisNoZonePattern = Pattern.compile("^(\\d+:){2}\\d+$");
    private static final Pattern timeNoMillisPattern = Pattern.compile("^(\\d+:){2}\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final DateFormat timeNoZoneFormatter = DateFormatType.XEP_0082_TIME_MILLIS_PROFILE.createFormatter();
    private static final Pattern timeNoZonePattern = Pattern.compile("^(\\d+:){2}\\d+.\\d+$");
    private static final Pattern timePattern = Pattern.compile("^(\\d+:){2}\\d+.\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final DateFormat xep0091Date6DigitFormatter = new SimpleDateFormat("yyyyMd'T'HH:mm:ss");
    private static final DateFormat xep0091Date7Digit1MonthFormatter = new SimpleDateFormat("yyyyMdd'T'HH:mm:ss");
    private static final DateFormat xep0091Date7Digit2MonthFormatter = new SimpleDateFormat("yyyyMMd'T'HH:mm:ss");
    private static final DateFormat xep0091Formatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    private static final Pattern xep0091Pattern = Pattern.compile("^\\d+T\\d+:\\d+:\\d+$");

    private static class PatternCouplings {
        DateFormat formatter;
        boolean needToConvertTimeZone = false;
        Pattern pattern;

        public PatternCouplings(Pattern pattern, DateFormat dateFormat) {
            this.pattern = pattern;
            this.formatter = dateFormat;
        }

        public PatternCouplings(Pattern pattern, DateFormat dateFormat, boolean z) {
            this.pattern = pattern;
            this.formatter = dateFormat;
            this.needToConvertTimeZone = z;
        }

        public String convertTime(String str) {
            return str.charAt(str.length() + -1) == 'Z' ? str.replace("Z", "+0000") : str.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)", "$1$2");
        }
    }

    static {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        XEP_0082_UTC_FORMAT.setTimeZone(timeZone);
        dateFormatter.setTimeZone(timeZone);
        timeFormatter.setTimeZone(timeZone);
        timeNoZoneFormatter.setTimeZone(timeZone);
        timeNoMillisFormatter.setTimeZone(timeZone);
        timeNoMillisNoZoneFormatter.setTimeZone(timeZone);
        dateTimeFormatter.setTimeZone(timeZone);
        dateTimeNoMillisFormatter.setTimeZone(timeZone);
        xep0091Formatter.setTimeZone(timeZone);
        xep0091Date6DigitFormatter.setTimeZone(timeZone);
        xep0091Date7Digit1MonthFormatter.setTimeZone(timeZone);
        xep0091Date7Digit1MonthFormatter.setLenient(false);
        xep0091Date7Digit2MonthFormatter.setTimeZone(timeZone);
        xep0091Date7Digit2MonthFormatter.setLenient(false);
        couplings.add(new PatternCouplings(datePattern, dateFormatter));
        couplings.add(new PatternCouplings(dateTimePattern, dateTimeFormatter, true));
        couplings.add(new PatternCouplings(dateTimeNoMillisPattern, dateTimeNoMillisFormatter, true));
        couplings.add(new PatternCouplings(timePattern, timeFormatter, true));
        couplings.add(new PatternCouplings(timeNoZonePattern, timeNoZoneFormatter));
        couplings.add(new PatternCouplings(timeNoMillisPattern, timeNoMillisFormatter, true));
        couplings.add(new PatternCouplings(timeNoMillisNoZonePattern, timeNoMillisNoZoneFormatter));
    }

    private StringUtils() {
    }

    public static byte[] decodeBase64(String str) {
        byte[] bytes;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = str.getBytes();
        }
        return Base64.decode(bytes, 0, bytes.length, 0);
    }

    private static Calendar determineNearestDate(final Calendar calendar, List<Calendar> list) {
        Collections.sort(list, new Comparator<Calendar>() {
            public int compare(Calendar calendar, Calendar calendar2) {
                return new Long(calendar.getTimeInMillis() - calendar.getTimeInMillis()).compareTo(new Long(calendar.getTimeInMillis() - calendar2.getTimeInMillis()));
            }
        });
        return (Calendar) list.get(0);
    }

    public static String encodeBase64(String str) {
        byte[] bArr = null;
        try {
            bArr = str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeBase64(bArr);
    }

    public static String encodeBase64(byte[] bArr) {
        return encodeBase64(bArr, false);
    }

    public static String encodeBase64(byte[] bArr, int i, int i2, boolean z) {
        return Base64.encodeBytes(bArr, i, i2, z ? 0 : 8);
    }

    public static String encodeBase64(byte[] bArr, boolean z) {
        return encodeBase64(bArr, 0, bArr.length, z);
    }

    public static String encodeHex(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            if ((b & 255) < 16) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toString(b & 255, 16));
        }
        return stringBuilder.toString();
    }

    public static String escapeForXML(String str) {
        int i = 0;
        if (str == null) {
            return null;
        }
        char[] toCharArray = str.toCharArray();
        int length = toCharArray.length;
        StringBuilder stringBuilder = new StringBuilder((int) (((double) length) * 1.3d));
        int i2 = 0;
        while (i2 < length) {
            char c = toCharArray[i2];
            if (c <= '>') {
                if (c == '<') {
                    if (i2 > i) {
                        stringBuilder.append(toCharArray, i, i2 - i);
                    }
                    i = i2 + 1;
                    stringBuilder.append(LT_ENCODE);
                } else if (c == '>') {
                    if (i2 > i) {
                        stringBuilder.append(toCharArray, i, i2 - i);
                    }
                    i = i2 + 1;
                    stringBuilder.append(GT_ENCODE);
                } else if (c == '&') {
                    if (i2 > i) {
                        stringBuilder.append(toCharArray, i, i2 - i);
                    }
                    if (length <= i2 + 5 || toCharArray[i2 + 1] != '#' || !Character.isDigit(toCharArray[i2 + 2]) || !Character.isDigit(toCharArray[i2 + 3]) || !Character.isDigit(toCharArray[i2 + 4]) || toCharArray[i2 + 5] != ';') {
                        i = i2 + 1;
                        stringBuilder.append(AMP_ENCODE);
                    }
                } else if (c == '\"') {
                    if (i2 > i) {
                        stringBuilder.append(toCharArray, i, i2 - i);
                    }
                    i = i2 + 1;
                    stringBuilder.append(QUOTE_ENCODE);
                } else if (c == '\'') {
                    if (i2 > i) {
                        stringBuilder.append(toCharArray, i, i2 - i);
                    }
                    i = i2 + 1;
                    stringBuilder.append(APOS_ENCODE);
                }
            }
            i2++;
        }
        if (i == 0) {
            return str;
        }
        if (i2 > i) {
            stringBuilder.append(toCharArray, i, i2 - i);
        }
        return stringBuilder.toString();
    }

    public static String escapeNode(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(str.length() + 8);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\"':
                    stringBuilder.append("\\22");
                    break;
                case '&':
                    stringBuilder.append("\\26");
                    break;
                case '\'':
                    stringBuilder.append("\\27");
                    break;
                case '/':
                    stringBuilder.append("\\2f");
                    break;
                case ':':
                    stringBuilder.append("\\3a");
                    break;
                case '<':
                    stringBuilder.append("\\3c");
                    break;
                case '>':
                    stringBuilder.append("\\3e");
                    break;
                case '@':
                    stringBuilder.append("\\40");
                    break;
                case '\\':
                    stringBuilder.append("\\5c");
                    break;
                default:
                    if (!Character.isWhitespace(charAt)) {
                        stringBuilder.append(charAt);
                        break;
                    }
                    stringBuilder.append("\\20");
                    break;
            }
        }
        return stringBuilder.toString();
    }

    private static List<Calendar> filterDatesBefore(Calendar calendar, Calendar... calendarArr) {
        List<Calendar> arrayList = new ArrayList();
        for (Calendar calendar2 : calendarArr) {
            if (calendar2 != null && calendar2.before(calendar)) {
                arrayList.add(calendar2);
            }
        }
        return arrayList;
    }

    public static String formatDate(Date date, DateFormatType dateFormatType) {
        return null;
    }

    public static String formatXEP0082Date(Date date) {
        String format;
        synchronized (dateTimeFormatter) {
            format = dateTimeFormatter.format(date);
        }
        return format;
    }

    private static Date handleDateWithMissingLeadingZeros(String str, int i) throws ParseException {
        if (i == 6) {
            Date parse;
            synchronized (xep0091Date6DigitFormatter) {
                parse = xep0091Date6DigitFormatter.parse(str);
            }
            return parse;
        }
        Calendar instance = Calendar.getInstance();
        Calendar parseXEP91Date = parseXEP91Date(str, xep0091Date7Digit1MonthFormatter);
        Calendar parseXEP91Date2 = parseXEP91Date(str, xep0091Date7Digit2MonthFormatter);
        List filterDatesBefore = filterDatesBefore(instance, parseXEP91Date, parseXEP91Date2);
        return !filterDatesBefore.isEmpty() ? determineNearestDate(instance, filterDatesBefore).getTime() : null;
    }

    public static synchronized String hash(String str) {
        String encodeHex;
        synchronized (StringUtils.class) {
            if (digest == null) {
                try {
                    digest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Failed to load the SHA-1 MessageDigest. Jive will be unable to function normally.");
                }
            }
            try {
                digest.update(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e2) {
                System.err.println(e2);
            }
            encodeHex = encodeHex(digest.digest());
        }
        return encodeHex;
    }

    public static boolean isFullJID(String str) {
        return parseName(str).length() > 0 && parseServer(str).length() > 0 && parseResource(str).length() > 0;
    }

    public static String parseBareAddress(String str) {
        if (str == null) {
            return null;
        }
        int indexOf = str.indexOf("/");
        return indexOf >= 0 ? indexOf == 0 ? "" : str.substring(0, indexOf) : str;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Date parseDate(java.lang.String r3) throws java.text.ParseException {
        /*
        r0 = xep0091Pattern;
        r0 = r0.matcher(r3);
        r0 = r0.matches();
        if (r0 == 0) goto L_0x0032;
    L_0x000c:
        r0 = "T";
        r0 = r3.split(r0);
        r1 = 0;
        r0 = r0[r1];
        r0 = r0.length();
        r1 = 8;
        if (r0 >= r1) goto L_0x0024;
    L_0x001d:
        r0 = handleDateWithMissingLeadingZeros(r3, r0);
        if (r0 == 0) goto L_0x0066;
    L_0x0023:
        return r0;
    L_0x0024:
        r1 = xep0091Formatter;
        monitor-enter(r1);
        r0 = xep0091Formatter;	 Catch:{ all -> 0x002f }
        r0 = r0.parse(r3);	 Catch:{ all -> 0x002f }
        monitor-exit(r1);	 Catch:{ all -> 0x002f }
        goto L_0x0023;
    L_0x002f:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x002f }
        throw r0;
    L_0x0032:
        r0 = couplings;
        r1 = r0.iterator();
    L_0x0038:
        r0 = r1.hasNext();
        if (r0 == 0) goto L_0x0066;
    L_0x003e:
        r0 = r1.next();
        r0 = (org.jivesoftware.smack.util.StringUtils.PatternCouplings) r0;
        r2 = r0.pattern;
        r2 = r2.matcher(r3);
        r2 = r2.matches();
        if (r2 == 0) goto L_0x0038;
    L_0x0050:
        r1 = r0.needToConvertTimeZone;
        if (r1 == 0) goto L_0x0058;
    L_0x0054:
        r3 = r0.convertTime(r3);
    L_0x0058:
        r1 = r0.formatter;
        monitor-enter(r1);
        r0 = r0.formatter;	 Catch:{ all -> 0x0063 }
        r0 = r0.parse(r3);	 Catch:{ all -> 0x0063 }
        monitor-exit(r1);	 Catch:{ all -> 0x0063 }
        goto L_0x0023;
    L_0x0063:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0063 }
        throw r0;
    L_0x0066:
        r1 = dateTimeNoMillisFormatter;
        monitor-enter(r1);
        r0 = dateTimeNoMillisFormatter;	 Catch:{ all -> 0x0071 }
        r0 = r0.parse(r3);	 Catch:{ all -> 0x0071 }
        monitor-exit(r1);	 Catch:{ all -> 0x0071 }
        goto L_0x0023;
    L_0x0071:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0071 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.StringUtils.parseDate(java.lang.String):java.util.Date");
    }

    public static String parseName(String str) {
        if (str == null) {
            return null;
        }
        int lastIndexOf = str.lastIndexOf("@");
        return lastIndexOf <= 0 ? "" : str.substring(0, lastIndexOf);
    }

    public static String parseResource(String str) {
        if (str == null) {
            return null;
        }
        int indexOf = str.indexOf("/");
        return (indexOf + 1 > str.length() || indexOf < 0) ? "" : str.substring(indexOf + 1);
    }

    public static String parseServer(String str) {
        if (str == null) {
            return null;
        }
        int lastIndexOf = str.lastIndexOf("@");
        if (lastIndexOf + 1 > str.length()) {
            return "";
        }
        int indexOf = str.indexOf("/");
        return (indexOf <= 0 || indexOf <= lastIndexOf) ? str.substring(lastIndexOf + 1) : str.substring(lastIndexOf + 1, indexOf);
    }

    public static Date parseXEP0082Date(String str) throws ParseException {
        return parseDate(str);
    }

    private static Calendar parseXEP91Date(String str, DateFormat dateFormat) {
        try {
            Calendar calendar;
            synchronized (dateFormat) {
                dateFormat.parse(str);
                calendar = dateFormat.getCalendar();
            }
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String randomString(int i) {
        if (i < 1) {
            return null;
        }
        char[] cArr = new char[i];
        for (int i2 = 0; i2 < cArr.length; i2++) {
            cArr[i2] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(cArr);
    }

    public static String unescapeNode(String str) {
        if (str == null) {
            return null;
        }
        char[] toCharArray = str.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(toCharArray.length);
        int i = 0;
        int length = toCharArray.length;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == IOUtils.DIR_SEPARATOR_WINDOWS && i + 2 < length) {
                char c = toCharArray[i + 1];
                char c2 = toCharArray[i + 2];
                if (c == '2') {
                    switch (c2) {
                        case '0':
                            stringBuilder.append(' ');
                            i += 2;
                            continue;
                        case '2':
                            stringBuilder.append('\"');
                            i += 2;
                            continue;
                        case '6':
                            stringBuilder.append('&');
                            i += 2;
                            continue;
                        case '7':
                            stringBuilder.append('\'');
                            i += 2;
                            continue;
                        case 'f':
                            stringBuilder.append(IOUtils.DIR_SEPARATOR_UNIX);
                            i += 2;
                            continue;
                    }
                } else if (c == '3') {
                    switch (c2) {
                        case 'a':
                            stringBuilder.append(':');
                            i += 2;
                            continue;
                        case 'c':
                            stringBuilder.append('<');
                            i += 2;
                            continue;
                        case 'e':
                            stringBuilder.append('>');
                            i += 2;
                            continue;
                        default:
                            break;
                    }
                } else if (c == '4') {
                    if (c2 == '0') {
                        stringBuilder.append("@");
                        i += 2;
                        i++;
                    }
                } else if (c == '5' && c2 == 'c') {
                    stringBuilder.append("\\");
                    i += 2;
                    i++;
                }
            }
            stringBuilder.append(charAt);
            i++;
        }
        return stringBuilder.toString();
    }
}
