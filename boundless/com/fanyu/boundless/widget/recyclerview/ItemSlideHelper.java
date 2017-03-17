package com.fanyu.boundless.widget.recyclerview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ItemSlideHelper implements OnItemTouchListener, OnGestureListener {
    private static final String TAG = "ItemSwipeHelper";
    private final int DEFAULT_DURATION = 200;
    private int mActivePointerId;
    private Callback mCallback;
    private Animator mExpandAndCollapseAnim;
    private GestureDetectorCompat mGestureDetector;
    private boolean mIsDragging;
    private int mLastX;
    private int mLastY;
    private int mMaxVelocity;
    private int mMinVelocity;
    private View mTargetView;
    private int mTouchSlop;

    public interface Callback {
        View findTargetView(float f, float f2);

        ViewHolder getChildViewHolder(View view);

        int getHorizontalRange(ViewHolder viewHolder);
    }

    public ItemSlideHelper(Context context, Callback callback) {
        this.mCallback = callback;
        this.mGestureDetector = new GestureDetectorCompat(context, this);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        boolean z = true;
        Log.d(TAG, "onInterceptTouchEvent: " + e.getAction());
        int action = MotionEventCompat.getActionMasked(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        if (rv.getScrollState() != 0) {
            if (this.mTargetView == null) {
                return false;
            }
            smoothHorizontalExpandOrCollapse(100.0f);
            this.mTargetView = null;
            return false;
        } else if (this.mExpandAndCollapseAnim != null && this.mExpandAndCollapseAnim.isRunning()) {
            return true;
        } else {
            boolean needIntercept = false;
            switch (action) {
                case 0:
                    this.mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                    this.mLastX = (int) e.getX();
                    this.mLastY = (int) e.getY();
                    if (this.mTargetView == null) {
                        this.mTargetView = this.mCallback.findTargetView((float) x, (float) y);
                        break;
                    }
                    if (inView(x, y)) {
                        z = false;
                    }
                    return z;
                case 1:
                case 3:
                    if (isExpanded()) {
                        if (inView(x, y)) {
                            Log.d(TAG, "click item");
                        } else {
                            needIntercept = true;
                        }
                        smoothHorizontalExpandOrCollapse(100.0f);
                    }
                    this.mTargetView = null;
                    break;
                case 2:
                    int deltaX = x - this.mLastX;
                    if (Math.abs(y - this.mLastY) <= Math.abs(deltaX)) {
                        if (this.mTargetView == null || Math.abs(deltaX) < this.mTouchSlop) {
                            needIntercept = false;
                        } else {
                            needIntercept = true;
                        }
                        this.mIsDragging = needIntercept;
                        break;
                    }
                    return false;
                    break;
            }
            return needIntercept;
        }
    }

    private boolean isExpanded() {
        return this.mTargetView != null && this.mTargetView.getScrollX() == getHorizontalRange();
    }

    private boolean isCollapsed() {
        return this.mTargetView != null && this.mTargetView.getScrollX() == 0;
    }

    private boolean inView(int x, int y) {
        if (this.mTargetView == null) {
            return false;
        }
        int left = this.mTargetView.getWidth() - this.mTargetView.getScrollX();
        return new Rect(left, this.mTargetView.getTop(), left + getHorizontalRange(), this.mTargetView.getBottom()).contains(x, y);
    }

    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onTouchEvent: " + e.getAction());
        if ((this.mExpandAndCollapseAnim != null && this.mExpandAndCollapseAnim.isRunning()) || this.mTargetView == null) {
            return;
        }
        if (this.mGestureDetector.onTouchEvent(e)) {
            this.mIsDragging = false;
            return;
        }
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (MotionEventCompat.getActionMasked(e)) {
            case 0:
                return;
            case 1:
            case 3:
                if (this.mIsDragging) {
                    if (!smoothHorizontalExpandOrCollapse(0.0f) && isCollapsed()) {
                        this.mTargetView = null;
                    }
                    this.mIsDragging = false;
                    return;
                }
                return;
            case 2:
                int deltaX = (int) (((float) this.mLastX) - e.getX());
                if (this.mIsDragging) {
                    horizontalDrag(deltaX);
                }
                this.mLastX = x;
                return;
            default:
                return;
        }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void horizontalDrag(int delta) {
        int scrollX = this.mTargetView.getScrollX();
        int scrollY = this.mTargetView.getScrollY();
        if (scrollX + delta <= 0) {
            this.mTargetView.scrollTo(0, scrollY);
            return;
        }
        int horRange = getHorizontalRange();
        scrollX += delta;
        if (Math.abs(scrollX) < horRange) {
            this.mTargetView.scrollTo(scrollX, scrollY);
        } else {
            this.mTargetView.scrollTo(horRange, scrollY);
        }
    }

    private boolean smoothHorizontalExpandOrCollapse(float velocityX) {
        int scrollX = this.mTargetView.getScrollX();
        int scrollRange = getHorizontalRange();
        if (this.mExpandAndCollapseAnim != null) {
            return false;
        }
        int to = 0;
        int duration = 200;
        if (velocityX != 0.0f) {
            if (velocityX > 0.0f) {
                to = 0;
            } else {
                to = scrollRange;
            }
            duration = (int) ((1.0f - (Math.abs(velocityX) / ((float) this.mMaxVelocity))) * 200.0f);
        } else if (scrollX > scrollRange / 2) {
            to = scrollRange;
        }
        if (to == scrollX) {
            return false;
        }
        this.mExpandAndCollapseAnim = ObjectAnimator.ofInt(this.mTargetView, "scrollX", new int[]{to});
        this.mExpandAndCollapseAnim.setDuration((long) duration);
        this.mExpandAndCollapseAnim.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                ItemSlideHelper.this.mExpandAndCollapseAnim = null;
                if (ItemSlideHelper.this.isCollapsed()) {
                    ItemSlideHelper.this.mTargetView = null;
                }
                Log.d(ItemSlideHelper.TAG, "onAnimationEnd");
            }

            public void onAnimationCancel(Animator animation) {
                ItemSlideHelper.this.mExpandAndCollapseAnim = null;
                Log.d(ItemSlideHelper.TAG, "onAnimationCancel");
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        this.mExpandAndCollapseAnim.start();
        return true;
    }

    public int getHorizontalRange() {
        return this.mCallback.getHorizontalRange(this.mCallback.getChildViewHolder(this.mTargetView));
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) <= ((float) this.mMinVelocity) || Math.abs(velocityX) >= ((float) this.mMaxVelocity) || smoothHorizontalExpandOrCollapse(velocityX)) {
            return false;
        }
        if (isCollapsed()) {
            this.mTargetView = null;
        }
        return true;
    }
}
