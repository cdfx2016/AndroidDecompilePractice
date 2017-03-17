package com.jude.rollviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Scroller;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class RollPagerView extends RelativeLayout implements OnPageChangeListener {
    private int alpha;
    private int color;
    private int delay;
    private int gravity;
    private PagerAdapter mAdapter;
    private GestureDetector mGestureDetector;
    private TimeTaskHandler mHandler;
    private View mHintView;
    private HintViewDelegate mHintViewDelegate;
    private OnItemClickListener mOnItemClickListener;
    private long mRecentTouchTime;
    private ViewPager mViewPager;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private Timer timer;

    public interface HintViewDelegate {
        void initView(int i, int i2, HintView hintView);

        void setCurrentPosition(int i, HintView hintView);
    }

    private class JPagerObserver extends DataSetObserver {
        private JPagerObserver() {
        }

        public void onChanged() {
            RollPagerView.this.dataSetChanged();
        }

        public void onInvalidated() {
            RollPagerView.this.dataSetChanged();
        }
    }

    private static final class TimeTaskHandler extends Handler {
        private WeakReference<RollPagerView> mRollPagerViewWeakReference;

        public TimeTaskHandler(RollPagerView rollPagerView) {
            this.mRollPagerViewWeakReference = new WeakReference(rollPagerView);
        }

        public void handleMessage(Message msg) {
            RollPagerView rollPagerView = (RollPagerView) this.mRollPagerViewWeakReference.get();
            int cur = rollPagerView.getViewPager().getCurrentItem() + 1;
            if (cur >= rollPagerView.mAdapter.getCount()) {
                cur = 0;
            }
            rollPagerView.getViewPager().setCurrentItem(cur);
            rollPagerView.mHintViewDelegate.setCurrentPosition(cur, (HintView) rollPagerView.mHintView);
            if (rollPagerView.mAdapter.getCount() <= 1) {
                rollPagerView.stopPlay();
            }
        }
    }

    private static class WeakTimerTask extends TimerTask {
        private WeakReference<RollPagerView> mRollPagerViewWeakReference;

        public WeakTimerTask(RollPagerView mRollPagerView) {
            this.mRollPagerViewWeakReference = new WeakReference(mRollPagerView);
        }

        public void run() {
            RollPagerView rollPagerView = (RollPagerView) this.mRollPagerViewWeakReference.get();
            if (rollPagerView == null) {
                cancel();
            } else if (rollPagerView.isShown() && System.currentTimeMillis() - rollPagerView.mRecentTouchTime > ((long) rollPagerView.delay)) {
                rollPagerView.mHandler.sendEmptyMessage(0);
            }
        }
    }

    public RollPagerView(Context context) {
        this(context, null);
    }

    public RollPagerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHintViewDelegate = new HintViewDelegate() {
            public void setCurrentPosition(int position, HintView hintView) {
                if (hintView != null) {
                    hintView.setCurrent(position);
                }
            }

            public void initView(int length, int gravity, HintView hintView) {
                if (hintView != null) {
                    hintView.initView(length, gravity);
                }
            }
        };
        this.mHandler = new TimeTaskHandler(this);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (this.mViewPager != null) {
            removeView(this.mViewPager);
        }
        TypedArray type = getContext().obtainStyledAttributes(attrs, R.styleable.RollViewPager);
        this.gravity = type.getInteger(R.styleable.RollViewPager_rollviewpager_hint_gravity, 1);
        this.delay = type.getInt(R.styleable.RollViewPager_rollviewpager_play_delay, 0);
        this.color = type.getColor(R.styleable.RollViewPager_rollviewpager_hint_color, ViewCompat.MEASURED_STATE_MASK);
        this.alpha = type.getInt(R.styleable.RollViewPager_rollviewpager_hint_alpha, 0);
        this.paddingLeft = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingLeft, 0.0f);
        this.paddingRight = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingRight, 0.0f);
        this.paddingTop = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingTop, 0.0f);
        this.paddingBottom = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingBottom, (float) Util.dip2px(getContext(), 4.0f));
        this.mViewPager = new ViewPager(getContext());
        this.mViewPager.setId(R.id.viewpager_inner);
        this.mViewPager.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.mViewPager);
        type.recycle();
        initHint(new ColorPointHintView(getContext(), Color.parseColor("#E3AC42"), Color.parseColor("#88ffffff")));
        this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                if (RollPagerView.this.mOnItemClickListener != null) {
                    if (RollPagerView.this.mAdapter instanceof LoopPagerAdapter) {
                        RollPagerView.this.mOnItemClickListener.onItemClick(RollPagerView.this.mViewPager.getCurrentItem() % ((LoopPagerAdapter) RollPagerView.this.mAdapter).getRealCount());
                    } else {
                        RollPagerView.this.mOnItemClickListener.onItemClick(RollPagerView.this.mViewPager.getCurrentItem());
                    }
                }
                return super.onSingleTapUp(e);
            }
        });
    }

    private void startPlay() {
        if (this.delay > 0 && this.mAdapter != null && this.mAdapter.getCount() > 1) {
            if (this.timer != null) {
                this.timer.cancel();
            }
            this.timer = new Timer();
            this.timer.schedule(new WeakTimerTask(this), (long) this.delay, (long) this.delay);
        }
    }

    private void stopPlay() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void setHintViewDelegate(HintViewDelegate delegate) {
        this.mHintViewDelegate = delegate;
    }

    private void initHint(HintView hintview) {
        if (this.mHintView != null) {
            removeView(this.mHintView);
        }
        if (hintview != null && (hintview instanceof HintView)) {
            this.mHintView = (View) hintview;
            loadHintView();
        }
    }

    private void loadHintView() {
        addView(this.mHintView);
        this.mHintView.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
        LayoutParams lp = new LayoutParams(-1, -2);
        lp.addRule(12);
        this.mHintView.setLayoutParams(lp);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(this.color);
        gd.setAlpha(this.alpha);
        this.mHintView.setBackgroundDrawable(gd);
        this.mHintViewDelegate.initView(this.mAdapter == null ? 0 : this.mAdapter.getCount(), this.gravity, (HintView) this.mHintView);
    }

    public void setAnimationDurtion(final int during) {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mField.set(this.mViewPager, new Scroller(getContext(), new Interpolator() {
                public float getInterpolation(float t) {
                    t -= 1.0f;
                    return ((((t * t) * t) * t) * t) + 1.0f;
                }
            }) {
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    if (System.currentTimeMillis() - RollPagerView.this.mRecentTouchTime > ((long) RollPagerView.this.delay)) {
                        duration = during;
                    } else {
                        duration /= 2;
                    }
                    super.startScroll(startX, startY, dx, dy, duration);
                }

                public void startScroll(int startX, int startY, int dx, int dy) {
                    super.startScroll(startX, startY, dx, dy, during);
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    public void setPlayDelay(int delay) {
        this.delay = delay;
        startPlay();
    }

    public void pause() {
        stopPlay();
    }

    public void resume() {
        startPlay();
    }

    public boolean isPlaying() {
        return this.timer != null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setHintPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.mHintView.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
    }

    public void setHintAlpha(int alpha) {
        this.alpha = alpha;
        initHint((HintView) this.mHintView);
    }

    public void setHintView(HintView hintview) {
        if (this.mHintView != null) {
            removeView(this.mHintView);
        }
        this.mHintView = (View) hintview;
        if (hintview != null && (hintview instanceof View)) {
            initHint(hintview);
        }
    }

    public ViewPager getViewPager() {
        return this.mViewPager;
    }

    public void setAdapter(PagerAdapter adapter) {
        adapter.registerDataSetObserver(new JPagerObserver());
        this.mViewPager.setAdapter(adapter);
        this.mViewPager.addOnPageChangeListener(this);
        this.mAdapter = adapter;
        dataSetChanged();
    }

    private void dataSetChanged() {
        if (this.mHintView != null) {
            this.mHintViewDelegate.initView(this.mAdapter.getCount(), this.gravity, (HintView) this.mHintView);
            this.mHintViewDelegate.setCurrentPosition(this.mViewPager.getCurrentItem(), (HintView) this.mHintView);
        }
        startPlay();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mRecentTouchTime = System.currentTimeMillis();
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int arg0) {
        this.mHintViewDelegate.setCurrentPosition(arg0, (HintView) this.mHintView);
    }
}
