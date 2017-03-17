package com.mob.tools.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class MobViewPager extends ViewGroup {
    private static final int SNAP_VELOCITY = 500;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private ViewPagerAdapter adapter;
    private View currentPage;
    private int currentScreen;
    private float lastMotionX;
    private float lastMotionY;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private View nextPage;
    private View previousPage;
    private int screenCount;
    private Scroller scroller;
    private int touchSlop;
    private int touchState;

    public MobViewPager(Context context) {
        this(context, null);
    }

    public MobViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MobViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.touchState = 0;
        init(context);
    }

    private void init(Context context) {
        this.scroller = new Scroller(getContext(), new Interpolator() {
            float[] values = new float[]{0.0f, 0.0157073f, 0.0314108f, 0.0471065f, 0.0627905f, 0.0784591f, 0.0941083f, 0.109734f, 0.125333f, 0.140901f, 0.156434f, 0.171929f, 0.187381f, 0.202787f, 0.218143f, 0.233445f, 0.24869f, 0.263873f, 0.278991f, 0.29404f, 0.309017f, 0.323917f, 0.338738f, 0.353475f, 0.368125f, 0.382683f, 0.397148f, 0.411514f, 0.425779f, 0.439939f, 0.45399f, 0.46793f, 0.481754f, 0.495459f, 0.509041f, 0.522499f, 0.535827f, 0.549023f, 0.562083f, 0.575005f, 0.587785f, 0.60042f, 0.612907f, 0.625243f, 0.637424f, 0.649448f, 0.661312f, 0.673013f, 0.684547f, 0.695913f, 0.707107f, 0.718126f, 0.728969f, 0.739631f, 0.750111f, 0.760406f, 0.770513f, 0.78043f, 0.790155f, 0.799685f, 0.809017f, 0.81815f, 0.827081f, 0.835807f, 0.844328f, 0.85264f, 0.860742f, 0.868632f, 0.876307f, 0.883766f, 0.891007f, 0.898028f, 0.904827f, 0.911403f, 0.917755f, 0.92388f, 0.929776f, 0.935444f, 0.940881f, 0.946085f, 0.951057f, 0.955793f, 0.960294f, 0.964557f, 0.968583f, 0.97237f, 0.975917f, 0.979223f, 0.982287f, 0.985109f, 0.987688f, 0.990024f, 0.992115f, 0.993961f, 0.995562f, 0.996917f, 0.998027f, 0.99889f, 0.999507f, 0.999877f, 1.0f};

            public float getInterpolation(float t) {
                return this.values[(int) (100.0f * t)];
            }
        });
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.touchSlop = configuration.getScaledTouchSlop();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void setAdapter(ViewPagerAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.setMobViewPager(null);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.setMobViewPager(this);
        }
        if (adapter == null) {
            this.currentScreen = 0;
            removeAllViews();
            return;
        }
        this.screenCount = adapter.getCount();
        if (this.screenCount <= 0) {
            this.currentScreen = 0;
            removeAllViews();
        } else if (this.screenCount <= this.currentScreen) {
            scrollToScreen(this.screenCount - 1, true);
        } else {
            removeAllViews();
            if (this.currentScreen > 0) {
                this.previousPage = adapter.getView(this.currentScreen - 1, this.previousPage, this);
                addView(this.previousPage);
            }
            this.currentPage = adapter.getView(this.currentScreen, this.currentPage, this);
            addView(this.currentPage);
            if (this.currentScreen < this.screenCount - 1) {
                this.nextPage = adapter.getView(this.currentScreen + 1, this.nextPage, this);
                addView(this.nextPage);
            }
        }
    }

    public int getCurrentScreen() {
        return this.currentScreen;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.adapter != null && this.screenCount > 0) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int adjustedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, 1073741824);
            int adjustedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, 1073741824);
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).measure(adjustedWidthMeasureSpec, adjustedHeightMeasureSpec);
            }
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.adapter != null && this.screenCount > 0) {
            int width = r - l;
            int height = b - t;
            int cLeft = this.currentScreen * width;
            if (this.currentScreen > 0) {
                this.previousPage.layout(cLeft - width, 0, cLeft, height);
            }
            this.currentPage.layout(cLeft, 0, cLeft + width, height);
            if (this.currentScreen < this.screenCount - 1) {
                this.nextPage.layout(cLeft + width, 0, (cLeft + width) + width, height);
            }
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.adapter != null && this.screenCount > 0) {
            long drawingTime = getDrawingTime();
            if (this.currentScreen > 0) {
                drawChild(canvas, this.previousPage, drawingTime);
            }
            drawChild(canvas, this.currentPage, drawingTime);
            if (this.currentScreen < this.screenCount - 1) {
                drawChild(canvas, this.nextPage, drawingTime);
            }
        }
    }

    public void computeScroll() {
        if (this.adapter != null && this.screenCount > 0) {
            if (this.scroller.computeScrollOffset()) {
                scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
                postInvalidate();
                return;
            }
            int lastScreen = this.currentScreen;
            int scrX = this.scroller.getCurrX();
            int w = getWidth();
            int index = scrX / w;
            if (scrX % w > w / 2) {
                index++;
            }
            this.currentScreen = Math.max(0, Math.min(index, this.screenCount - 1));
            if (lastScreen != this.currentScreen) {
                onScreenChange(lastScreen);
            }
        }
    }

    private void onScreenChange(int lastScreen) {
        if (this.adapter != null) {
            int i;
            int screen;
            View tmp;
            if (this.currentScreen > lastScreen) {
                for (i = 0; i < this.currentScreen - lastScreen; i++) {
                    screen = (lastScreen + i) + 1;
                    tmp = this.previousPage;
                    this.previousPage = this.currentPage;
                    this.currentPage = this.nextPage;
                    if (getChildCount() >= 3) {
                        removeViewAt(0);
                    }
                    if (screen < this.screenCount - 1) {
                        this.nextPage = this.adapter.getView(screen + 1, tmp, this);
                        addView(this.nextPage);
                    } else {
                        this.nextPage = tmp;
                    }
                }
            } else {
                for (i = 0; i < lastScreen - this.currentScreen; i++) {
                    screen = (lastScreen - i) - 1;
                    tmp = this.nextPage;
                    this.nextPage = this.currentPage;
                    this.currentPage = this.previousPage;
                    if (getChildCount() >= 3) {
                        removeViewAt(2);
                    }
                    if (screen > 0) {
                        this.previousPage = this.adapter.getView(screen - 1, tmp, this);
                        addView(this.previousPage, 0);
                    } else {
                        this.previousPage = tmp;
                    }
                }
            }
            this.adapter.onScreenChange(this.currentScreen, lastScreen);
        }
    }

    public void scrollLeft(boolean immediate) {
        if (this.currentScreen > 0) {
            scrollToScreen(this.currentScreen - 1, immediate);
        }
    }

    public void scrollRight(boolean immediate) {
        if (this.currentScreen < this.screenCount - 1) {
            scrollToScreen(this.currentScreen + 1, immediate);
        }
    }

    public void scrollToScreen(int whichScreen, boolean immediate) {
        if (this.currentPage != null && getFocusedChild() == this.currentPage) {
            this.currentPage.clearFocus();
        }
        int delta = (whichScreen * getWidth()) - getScrollX();
        this.scroller.abortAnimation();
        this.scroller.startScroll(getScrollX(), 0, delta, 0, immediate ? 0 : Math.abs(getWidth()) / 2);
        invalidate();
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (this.adapter == null) {
            return super.dispatchUnhandledMove(focused, direction);
        }
        if (direction == 17) {
            if (this.currentScreen > 0) {
                scrollToScreen(this.currentScreen - 1, false);
                return true;
            }
        } else if (direction == 66 && this.currentScreen < this.screenCount - 1) {
            scrollToScreen(this.currentScreen + 1, false);
            return true;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 2 && this.touchState != 0) {
            return true;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action) {
            case 0:
                float x1 = ev.getX();
                float y1 = ev.getY();
                this.lastMotionX = x1;
                this.lastMotionY = y1;
                this.touchState = this.scroller.isFinished() ? 0 : 1;
                break;
            case 1:
            case 3:
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.touchState = 0;
                break;
            case 2:
                handleInterceptMove(ev);
                break;
        }
        if (this.touchState == 0) {
            return false;
        }
        return true;
    }

    private void handleInterceptMove(MotionEvent ev) {
        float x = ev.getX();
        int xDiff = (int) Math.abs(x - this.lastMotionX);
        if (((int) Math.abs(ev.getY() - this.lastMotionY)) < xDiff && xDiff > this.touchSlop) {
            this.touchState = 1;
            this.lastMotionX = x;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.adapter == null) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        int action = ev.getAction();
        float x = ev.getX();
        switch (action) {
            case 0:
                if (this.touchState != 0) {
                    if (!this.scroller.isFinished()) {
                        this.scroller.abortAnimation();
                    }
                    this.lastMotionX = x;
                    break;
                }
                break;
            case 1:
                if (this.touchState == 1) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    if (velocityX > SNAP_VELOCITY && this.currentScreen > 0) {
                        scrollToScreen(this.currentScreen - 1, false);
                    } else if (velocityX >= -500 || this.currentScreen >= this.screenCount - 1) {
                        int screenWidth = getWidth();
                        scrollToScreen((getScrollX() + (screenWidth / 2)) / screenWidth, false);
                    } else {
                        scrollToScreen(this.currentScreen + 1, false);
                    }
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                }
                this.touchState = 0;
                break;
            case 2:
                if (this.touchState != 1) {
                    if (onInterceptTouchEvent(ev) && this.touchState == 1) {
                        handleScrollMove(ev);
                        break;
                    }
                }
                handleScrollMove(ev);
                break;
            case 3:
                this.touchState = 0;
                break;
        }
        return true;
    }

    private void handleScrollMove(MotionEvent ev) {
        if (this.adapter != null) {
            float x1 = ev.getX();
            int deltaX = (int) (this.lastMotionX - x1);
            this.lastMotionX = x1;
            if (deltaX < 0) {
                if (getScrollX() > 0) {
                    scrollBy(Math.max(-getScrollX(), deltaX), 0);
                }
            } else if (deltaX > 0 && getChildCount() != 0) {
                int availableToScroll = (getChildAt(getChildCount() - 1).getRight() - getScrollX()) - getWidth();
                if (availableToScroll > 0) {
                    scrollBy(Math.min(availableToScroll, deltaX), 0);
                }
            }
        }
    }
}
