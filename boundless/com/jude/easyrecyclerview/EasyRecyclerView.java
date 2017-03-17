package com.jude.easyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.swipe.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.Iterator;

public class EasyRecyclerView extends FrameLayout {
    public static boolean DEBUG = false;
    public static final String TAG = "EasyRecyclerView";
    protected boolean mClipToPadding;
    private int mEmptyId;
    protected ViewGroup mEmptyView;
    private int mErrorId;
    protected ViewGroup mErrorView;
    protected OnScrollListener mExternalOnScrollListener;
    protected ArrayList<OnScrollListener> mExternalOnScrollListenerList = new ArrayList();
    protected OnScrollListener mInternalOnScrollListener;
    protected int mPadding;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mPaddingTop;
    private int mProgressId;
    protected ViewGroup mProgressView;
    protected SwipeRefreshLayout mPtrLayout;
    protected RecyclerView mRecycler;
    protected OnRefreshListener mRefreshListener;
    protected int mScrollbar;
    protected int mScrollbarStyle;

    public SwipeRefreshLayout getSwipeToRefresh() {
        return this.mPtrLayout;
    }

    public RecyclerView getRecyclerView() {
        return this.mRecycler;
    }

    public EasyRecyclerView(Context context) {
        super(context);
        initView();
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView);
        try {
            this.mClipToPadding = a.getBoolean(R.styleable.EasyRecyclerView_recyclerClipToPadding, false);
            this.mPadding = (int) a.getDimension(R.styleable.EasyRecyclerView_recyclerPadding, -1.0f);
            this.mPaddingTop = (int) a.getDimension(R.styleable.EasyRecyclerView_recyclerPaddingTop, 0.0f);
            this.mPaddingBottom = (int) a.getDimension(R.styleable.EasyRecyclerView_recyclerPaddingBottom, 0.0f);
            this.mPaddingLeft = (int) a.getDimension(R.styleable.EasyRecyclerView_recyclerPaddingLeft, 0.0f);
            this.mPaddingRight = (int) a.getDimension(R.styleable.EasyRecyclerView_recyclerPaddingRight, 0.0f);
            this.mScrollbarStyle = a.getInteger(R.styleable.EasyRecyclerView_scrollbarStyle, -1);
            this.mScrollbar = a.getInteger(R.styleable.EasyRecyclerView_scrollbars, -1);
            this.mEmptyId = a.getResourceId(R.styleable.EasyRecyclerView_layout_empty, 0);
            this.mProgressId = a.getResourceId(R.styleable.EasyRecyclerView_layout_progress, 0);
            this.mErrorId = a.getResourceId(R.styleable.EasyRecyclerView_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (!isInEditMode()) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_recyclerview, this);
            this.mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
            this.mPtrLayout.setEnabled(false);
            this.mProgressView = (ViewGroup) v.findViewById(R.id.progress);
            if (this.mProgressId != 0) {
                LayoutInflater.from(getContext()).inflate(this.mProgressId, this.mProgressView);
            }
            this.mEmptyView = (ViewGroup) v.findViewById(R.id.empty);
            if (this.mEmptyId != 0) {
                LayoutInflater.from(getContext()).inflate(this.mEmptyId, this.mEmptyView);
            }
            this.mErrorView = (ViewGroup) v.findViewById(R.id.error);
            if (this.mErrorId != 0) {
                LayoutInflater.from(getContext()).inflate(this.mErrorId, this.mErrorView);
            }
            initRecyclerView(v);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.mPtrLayout.dispatchTouchEvent(ev);
    }

    public void setRecyclerPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        this.mRecycler.setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
    }

    public void setClipToPadding(boolean isClip) {
        this.mRecycler.setClipToPadding(isClip);
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView.removeAllViews();
        this.mEmptyView.addView(emptyView);
    }

    public void setProgressView(View progressView) {
        this.mProgressView.removeAllViews();
        this.mProgressView.addView(progressView);
    }

    public void setErrorView(View errorView) {
        this.mErrorView.removeAllViews();
        this.mErrorView.addView(errorView);
    }

    public void setEmptyView(int emptyView) {
        this.mEmptyView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(emptyView, this.mEmptyView);
    }

    public void setProgressView(int progressView) {
        this.mProgressView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(progressView, this.mProgressView);
    }

    public void setErrorView(int errorView) {
        this.mErrorView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(errorView, this.mErrorView);
    }

    public void scrollToPosition(int position) {
        getRecyclerView().scrollToPosition(position);
    }

    protected void initRecyclerView(View view) {
        this.mRecycler = (RecyclerView) view.findViewById(16908298);
        setItemAnimator(null);
        if (this.mRecycler != null) {
            this.mRecycler.setHasFixedSize(true);
            this.mRecycler.setClipToPadding(this.mClipToPadding);
            this.mInternalOnScrollListener = new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (EasyRecyclerView.this.mExternalOnScrollListener != null) {
                        EasyRecyclerView.this.mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);
                    }
                    Iterator it = EasyRecyclerView.this.mExternalOnScrollListenerList.iterator();
                    while (it.hasNext()) {
                        ((OnScrollListener) it.next()).onScrolled(recyclerView, dx, dy);
                    }
                }

                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (EasyRecyclerView.this.mExternalOnScrollListener != null) {
                        EasyRecyclerView.this.mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                    }
                    Iterator it = EasyRecyclerView.this.mExternalOnScrollListenerList.iterator();
                    while (it.hasNext()) {
                        ((OnScrollListener) it.next()).onScrollStateChanged(recyclerView, newState);
                    }
                }
            };
            this.mRecycler.addOnScrollListener(this.mInternalOnScrollListener);
            if (((float) this.mPadding) != -1.0f) {
                this.mRecycler.setPadding(this.mPadding, this.mPadding, this.mPadding, this.mPadding);
            } else {
                this.mRecycler.setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
            }
            if (this.mScrollbarStyle != -1) {
                this.mRecycler.setScrollBarStyle(this.mScrollbarStyle);
            }
            switch (this.mScrollbar) {
                case 0:
                    setVerticalScrollBarEnabled(false);
                    return;
                case 1:
                    setHorizontalScrollBarEnabled(false);
                    return;
                case 2:
                    setVerticalScrollBarEnabled(false);
                    setHorizontalScrollBarEnabled(false);
                    return;
                default:
                    return;
            }
        }
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        this.mRecycler.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        this.mRecycler.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    public void setLayoutManager(LayoutManager manager) {
        this.mRecycler.setLayoutManager(manager);
    }

    public void setAdapter(Adapter adapter) {
        this.mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        showRecycler();
    }

    public void setAdapterWithProgress(Adapter adapter) {
        this.mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        if (adapter instanceof RecyclerArrayAdapter) {
            if (((RecyclerArrayAdapter) adapter).getCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        } else if (adapter.getItemCount() == 0) {
            showProgress();
        } else {
            showRecycler();
        }
    }

    public void clear() {
        this.mRecycler.setAdapter(null);
    }

    private void hideAll() {
        this.mEmptyView.setVisibility(8);
        this.mProgressView.setVisibility(8);
        this.mErrorView.setVisibility(8);
        this.mPtrLayout.setRefreshing(false);
        this.mRecycler.setVisibility(4);
    }

    public void showError() {
        log("showError");
        if (this.mErrorView.getChildCount() > 0) {
            hideAll();
            this.mErrorView.setVisibility(0);
            return;
        }
        showRecycler();
    }

    public void showEmpty() {
        log("showEmpty");
        if (this.mEmptyView.getChildCount() > 0) {
            hideAll();
            this.mEmptyView.setVisibility(0);
            return;
        }
        showRecycler();
    }

    public void showProgress() {
        log("showProgress");
        if (this.mProgressView.getChildCount() > 0) {
            hideAll();
            this.mProgressView.setVisibility(0);
            return;
        }
        showRecycler();
    }

    public void showRecycler() {
        log("showRecycler");
        hideAll();
        this.mRecycler.setVisibility(0);
    }

    public void setRefreshListener(OnRefreshListener listener) {
        this.mPtrLayout.setEnabled(true);
        this.mPtrLayout.setOnRefreshListener(listener);
        this.mRefreshListener = listener;
    }

    public void setRefreshing(final boolean isRefreshing) {
        this.mPtrLayout.post(new Runnable() {
            public void run() {
                EasyRecyclerView.this.mPtrLayout.setRefreshing(isRefreshing);
            }
        });
    }

    public void setRefreshing(final boolean isRefreshing, final boolean isCallbackListener) {
        this.mPtrLayout.post(new Runnable() {
            public void run() {
                EasyRecyclerView.this.mPtrLayout.setRefreshing(isRefreshing);
                if (isRefreshing && isCallbackListener && EasyRecyclerView.this.mRefreshListener != null) {
                    EasyRecyclerView.this.mRefreshListener.onRefresh();
                }
            }
        });
    }

    public void setRefreshingColorResources(@ColorRes int... colRes) {
        this.mPtrLayout.setColorSchemeResources(colRes);
    }

    public void setRefreshingColor(int... col) {
        this.mPtrLayout.setColorSchemeColors(col);
    }

    @Deprecated
    public void setOnScrollListener(OnScrollListener listener) {
        this.mExternalOnScrollListener = listener;
    }

    public void addOnScrollListener(OnScrollListener listener) {
        this.mExternalOnScrollListenerList.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        this.mExternalOnScrollListenerList.remove(listener);
    }

    public void removeAllOnScrollListeners() {
        this.mExternalOnScrollListenerList.clear();
    }

    public void addOnItemTouchListener(OnItemTouchListener listener) {
        this.mRecycler.addOnItemTouchListener(listener);
    }

    public void removeOnItemTouchListener(OnItemTouchListener listener) {
        this.mRecycler.removeOnItemTouchListener(listener);
    }

    public Adapter getAdapter() {
        return this.mRecycler.getAdapter();
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(ItemAnimator animator) {
        this.mRecycler.setItemAnimator(animator);
    }

    public void addItemDecoration(ItemDecoration itemDecoration) {
        this.mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(ItemDecoration itemDecoration, int index) {
        this.mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(ItemDecoration itemDecoration) {
        this.mRecycler.removeItemDecoration(itemDecoration);
    }

    public View getErrorView() {
        if (this.mErrorView.getChildCount() > 0) {
            return this.mErrorView.getChildAt(0);
        }
        return null;
    }

    public View getProgressView() {
        if (this.mProgressView.getChildCount() > 0) {
            return this.mProgressView.getChildAt(0);
        }
        return null;
    }

    public View getEmptyView() {
        if (this.mEmptyView.getChildCount() > 0) {
            return this.mEmptyView.getChildAt(0);
        }
        return null;
    }

    private static void log(String content) {
        if (DEBUG) {
            Log.i(TAG, content);
        }
    }
}
