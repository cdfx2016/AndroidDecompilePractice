package com.fanyu.boundless.widget;

import android.content.Context;
import android.util.TypedValue;

public class Utils {
    public static int dipToPx(Context c, float dipValue) {
        return (int) TypedValue.applyDimension(1, dipValue, c.getResources().getDisplayMetrics());
    }

    public static int spToPx(Context context, float spValue) {
        return (int) TypedValue.applyDimension(2, spValue, context.getResources().getDisplayMetrics());
    }
}
