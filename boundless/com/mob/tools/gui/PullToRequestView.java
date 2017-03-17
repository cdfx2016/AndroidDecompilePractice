package com.mob.tools.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PullToRequestView extends RelativeLayout {
    private static final long MIN_REF_TIME = 1000;
    private PullToRequestAdatper adapter;
    private View bodyView;
    private float downY;
    private int footerHeight;
    private View footerView;
    private int headerHeight;
    private View headerView;
    private long pullTime;
    private boolean pullingDownLock;
    private boolean pullingUpLock;
    private int state;
    private Runnable stopAct;
    private int top;

    public PullToRequestView(Context context) {
        super(context);
        init();
    }

    public PullToRequestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRequestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.stopAct = new Runnable() {
            public void run() {
                PullToRequestView.this.reversePulling();
            }
        };
    }

    public void setAdapter(PullToRequestAdatper adapter) {
        this.adapter = adapter;
        removeAllViews();
        this.bodyView = (View) adapter.getBodyView();
        LayoutParams lpBody = new LayoutParams(-1, -1);
        lpBody.addRule(9);
        lpBody.addRule(11);
        lpBody.addRule(10);
        addView(this.bodyView, lpBody);
        this.headerView = adapter.getHeaderView();
        this.headerView.setLayoutParams(new LayoutParams(-2, -2));
        this.headerView.measure(0, 0);
        this.headerHeight = this.headerView.getMeasuredHeight();
        LayoutParams lpHead = new LayoutParams(-2, this.headerHeight);
        lpHead.addRule(9);
        lpHead.addRule(11);
        lpHead.addRule(10);
        lpHead.topMargin = -this.headerHeight;
        addView(this.headerView, lpHead);
        this.footerView = adapter.getFooterView();
        this.footerView.setLayoutParams(new LayoutParams(-2, -2));
        this.footerView.measure(0, 0);
        this.footerHeight = this.footerView.getMeasuredHeight();
        LayoutParams lpFooter = new LayoutParams(-2, this.headerHeight);
        lpFooter.addRule(9);
        lpFooter.addRule(11);
        lpFooter.addRule(12);
        lpFooter.bottomMargin = -this.headerHeight;
        addView(this.footerView, lpFooter);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.downY = ev.getY();
                break;
            case 1:
            case 3:
                switch (this.state) {
                    case -1:
                        this.top = -this.footerHeight;
                        scrollTo(0, -this.top);
                        break;
                    case 0:
                        if (this.top <= this.headerHeight) {
                            if (this.top >= (-this.footerHeight)) {
                                if (this.top != 0) {
                                    scrollTo(0, 0);
                                    if (this.adapter != null) {
                                        if (this.top > 0) {
                                            this.adapter.onPullDown(0);
                                        } else {
                                            this.adapter.onPullUp(0);
                                        }
                                    }
                                    this.top = 0;
                                    break;
                                }
                            }
                            this.top = -this.footerHeight;
                            scrollTo(0, -this.top);
                            if (this.adapter != null) {
                                this.adapter.onPullUp(100);
                            }
                            performRequestNext();
                            ev = getCancelEvent(ev);
                            break;
                        }
                        this.top = this.headerHeight;
                        scrollTo(0, -this.top);
                        if (this.adapter != null) {
                            this.adapter.onPullDown(100);
                        }
                        performFresh();
                        ev = getCancelEvent(ev);
                        break;
                        break;
                    case 1:
                        this.top = this.headerHeight;
                        scrollTo(0, -this.top);
                        break;
                    default:
                        break;
                }
            case 2:
                float curY = ev.getY();
                switch (this.state) {
                    case -1:
                        this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                        if (this.top > 0) {
                            this.top = 0;
                        }
                        scrollTo(0, -this.top);
                        ev = getCancelEvent(ev);
                        break;
                    case 1:
                        this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                        if (this.top < 0) {
                            this.top = 0;
                        }
                        scrollTo(0, -this.top);
                        ev = getCancelEvent(ev);
                        break;
                    default:
                        if (this.top <= 0) {
                            if (this.top >= 0) {
                                if (curY <= this.downY) {
                                    if (curY < this.downY && canPullUp()) {
                                        this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                                        scrollTo(0, -this.top);
                                        if (!(this.adapter == null || this.footerHeight == 0)) {
                                            this.adapter.onPullUp(((-this.top) * 100) / this.footerHeight);
                                        }
                                        ev = getCancelEvent(ev);
                                        break;
                                    }
                                } else if (canPullDown()) {
                                    this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                                    scrollTo(0, -this.top);
                                    if (!(this.adapter == null || this.headerHeight == 0)) {
                                        this.adapter.onPullUp(((-this.top) * 100) / this.headerHeight);
                                    }
                                    ev = getCancelEvent(ev);
                                    break;
                                }
                            }
                            this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                            if (this.top > 0) {
                                this.top = 0;
                            }
                            scrollTo(0, -this.top);
                            if (!(this.adapter == null || this.footerHeight == 0)) {
                                this.adapter.onPullUp(((-this.top) * 100) / this.footerHeight);
                            }
                            ev = getCancelEvent(ev);
                            break;
                        }
                        this.top = (int) (((float) this.top) + ((curY - this.downY) / 2.0f));
                        if (this.top < 0) {
                            this.top = 0;
                        }
                        scrollTo(0, -this.top);
                        if (!(this.adapter == null || this.headerHeight == 0)) {
                            this.adapter.onPullDown((this.top * 100) / this.headerHeight);
                        }
                        ev = getCancelEvent(ev);
                        break;
                        break;
                }
                this.downY = curY;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private MotionEvent getCancelEvent(MotionEvent ev) {
        return MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), 3, ev.getX(), ev.getY(), ev.getMetaState());
    }

    public void performPullingDown(boolean request) {
        this.top = this.headerHeight;
        scrollTo(0, -this.top);
        if (request) {
            performFresh();
        }
    }

    private void performFresh() {
        this.pullTime = System.currentTimeMillis();
        this.state = 1;
        if (this.adapter != null) {
            this.adapter.onRefresh();
        }
    }

    public void performPullingUp(boolean request) {
        this.top = -this.footerHeight;
        scrollTo(0, -this.top);
        if (request) {
            performRequestNext();
        }
    }

    private void performRequestNext() {
        this.pullTime = System.currentTimeMillis();
        this.state = -1;
        if (this.adapter != null) {
            this.adapter.onRequestNext();
        }
    }

    private void reversePulling() {
        this.top = 0;
        scrollTo(0, 0);
        this.state = 0;
        if (this.adapter != null) {
            this.adapter.onReversed();
        }
    }

    public void stopPulling() {
        long delta = System.currentTimeMillis() - this.pullTime;
        if (delta < MIN_REF_TIME) {
            postDelayed(this.stopAct, MIN_REF_TIME - delta);
        } else {
            post(this.stopAct);
        }
    }

    public void lockPullingDown() {
        this.pullingDownLock = true;
    }

    public void lockPullingUp() {
        this.pullingUpLock = true;
    }

    public void releasePullingDownLock() {
        this.pullingDownLock = false;
    }

    public void releasePullingUpLock() {
        this.pullingUpLock = false;
    }

    private boolean canPullDown() {
        return !this.pullingDownLock && this.adapter.isPullDownReady() && this.state == 0;
    }

    private boolean canPullUp() {
        return !this.pullingUpLock && this.adapter.isPullUpReady() && this.state == 0;
    }
}
