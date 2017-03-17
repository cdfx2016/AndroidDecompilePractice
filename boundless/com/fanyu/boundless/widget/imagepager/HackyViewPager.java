package com.fanyu.boundless.widget.imagepager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class HackyViewPager extends ViewPager {
    private static final String TAG = "HackyViewPager";

    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean z = false;
        try {
            z = super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "hacky viewpager error1");
        } catch (ArrayIndexOutOfBoundsException e2) {
            Log.e(TAG, "hacky viewpager error2");
        }
        return z;
    }
}
