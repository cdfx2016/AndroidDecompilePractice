package com.fanyu.boundless.widget.horizontalscrollmenu;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    private boolean mSwiped;

    public MyViewPager(Context context) {
        this(context, null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSwiped = true;
    }

    public void setSwiped(boolean swiped) {
        this.mSwiped = swiped;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mSwiped) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mSwiped) {
            return super.onTouchEvent(event);
        }
        return true;
    }
}
