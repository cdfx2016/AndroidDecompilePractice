package com.easemob.util;

import android.content.Context;
import cn.finalteam.toolsfinal.io.FileUtils;
import java.text.DecimalFormat;

public class TextFormater {
    private static final int GB_SP_DIFF = 160;
    private static final char[] firstLetter = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z'};
    private static final int[] secPosvalueList = new int[]{1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600};

    private static char convert(byte[] bArr) {
        int i;
        int i2 = 0;
        for (i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) (bArr[i] - 160);
        }
        i = (bArr[0] * 100) + bArr[1];
        while (i2 < 23) {
            if (i >= secPosvalueList[i2] && i < secPosvalueList[i2 + 1]) {
                return firstLetter[i2];
            }
            i2++;
        }
        return '-';
    }

    public static String formatStr(Context context, int i, String str) {
        return String.format(context.getText(i).toString(), new Object[]{str});
    }

    public static String getDataSize(long j) {
        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        return j < 1024 ? new StringBuilder(String.valueOf(j)).append("bytes").toString() : j < FileUtils.ONE_MB ? decimalFormat.format((double) (((float) j) / 1024.0f)) + "KB" : j < FileUtils.ONE_GB ? decimalFormat.format((double) ((((float) j) / 1024.0f) / 1024.0f)) + "MB" : j < 0 ? decimalFormat.format((double) (((((float) j) / 1024.0f) / 1024.0f) / 1024.0f)) + "GB" : "error";
    }

    public static String getFirstLetter(String str) {
        String toLowerCase = str.toLowerCase();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < toLowerCase.length(); i++) {
            char[] cArr = new char[]{toLowerCase.charAt(i)};
            byte[] bytes = new String(cArr).getBytes();
            if (bytes[0] >= Byte.MIN_VALUE || bytes[0] <= (byte) 0) {
                stringBuffer.append(convert(bytes));
            } else {
                stringBuffer.append(cArr);
            }
        }
        return stringBuffer.toString().substring(0, 1);
    }

    public static String getKBDataSize(long j) {
        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        return j < 1024 ? new StringBuilder(String.valueOf(j)).append("KB").toString() : j < FileUtils.ONE_MB ? decimalFormat.format((double) (((float) j) / 1024.0f)) + "MB" : j < FileUtils.ONE_GB ? decimalFormat.format((double) ((((float) j) / 1024.0f) / 1024.0f)) + "GB" : "error";
    }
}
