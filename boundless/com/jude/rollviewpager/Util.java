package com.jude.rollviewpager;

import android.content.Context;

public class Util {
    public static int dip2px(Context ctx, float dpValue) {
        return (int) ((dpValue * ctx.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context ctx, float pxValue) {
        return (int) ((pxValue / ctx.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
