package com.fanyu.boundless.widget.recyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fanyu.boundless.R;

public class PullLoadMoreRecyclerView extends LinearLayout {
    private Adapter adapter;
    private boolean hasMore = true;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private ProgressBar loadMoreProgressBar;
    private TextView loadMoreText;
    protected Context mContext;
    private View mFooterView;
    private int mIndex = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private PullLoadMoreListener mPullLoadMoreListener;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean move = false;
    private boolean pullRefreshEnable = true;
    private boolean pushRefreshEnable = true;

    public interface PullLoadMoreListener {
        void onLoadMore();

        void onRefresh();
    }

    public class onTouchRecyclerView implements OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            if (PullLoadMoreRecyclerView.this.isRefresh || PullLoadMoreRecyclerView.this.isLoadMore) {
                return true;
            }
            return false;
        }
    }

    public RecyclerView getmRecyclerView() {
        return this.mRecyclerView;
    }

    public PullLoadMoreRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public PullLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout, null);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        this.mSwipeRefreshLayout.setColorSchemeResources(17170444, 17170444, 17170444);
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh(this));
        this.loadMoreProgressBar = (ProgressBar) view.findViewById(R.id.loadMoreProgressBar);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setLinearLayout();
        this.mRecyclerView.setVerticalScrollBarEnabled(true);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mRecyclerView.setOnScrollListener(new RecyclerViewOnScroll(this, true, true, this.move, this.mIndex, this.mLinearLayoutManager, this.mRecyclerView));
        this.mRecyclerView.setOnTouchListener(new onTouchRecyclerView());
        this.mFooterView = view.findViewById(R.id.footerView);
        this.loadMoreText = (TextView) view.findViewById(R.id.loadMoreText);
        this.mFooterView.setVisibility(8);
        addView(view);
    }

    public void setmFooterViewbool(boolean view) {
        if (view) {
            this.mFooterView.setVisibility(0);
        } else {
            this.mFooterView.setVisibility(8);
        }
    }

    public void setOnPauseListenerParams(boolean pauseOnScroll, boolean pauseOnFling) {
        this.mRecyclerView.setOnScrollListener(new RecyclerViewOnScroll(this, pauseOnScroll, pauseOnFling, this.move, this.mIndex, this.mLinearLayoutManager, this.mRecyclerView));
    }

    public void setLinearLayout() {
        this.mLinearLayoutManager = new LinearLayoutManager(this.mContext);
        this.mLinearLayoutManager.setOrientation(1);
        this.mRecyclerView.setLayoutManager(this.mLinearLayoutManager);
    }

    public void setGridLayout(int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, spanCount);
        gridLayoutManager.setOrientation(1);
        this.mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public void setStaggeredGridLayout(int spanCount) {
        this.mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, 1));
    }

    public void setDividerGroidItem() {
        this.mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this.mContext));
    }

    public LayoutManager getLayoutManager() {
        return this.mRecyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public void scrollToTop() {
        this.mRecyclerView.scrollToPosition(0);
    }

    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            this.mRecyclerView.setAdapter(adapter);
        }
    }

    public void setPullRefreshEnable(boolean enable) {
        this.pullRefreshEnable = enable;
        setSwipeRefreshEnable(enable);
    }

    public boolean getPullRefreshEnable() {
        return this.pullRefreshEnable;
    }

    public void setSwipeRefreshEnable(boolean enable) {
        this.mSwipeRefreshLayout.setEnabled(enable);
    }

    public boolean getSwipeRefreshEnable() {
        return this.mSwipeRefreshLayout.isEnabled();
    }

    public void setColorSchemeResources(int... colorResIds) {
        this.mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return this.mSwipeRefreshLayout;
    }

    public void setRefreshing(final boolean isRefreshing) {
        this.mSwipeRefreshLayout.post(new Runnable() {
            public void run() {
                if (PullLoadMoreRecyclerView.this.pullRefreshEnable) {
                    PullLoadMoreRecyclerView.this.mSwipeRefreshLayout.setRefreshing(isRefreshing);
                }
            }
        });
    }

    public boolean getPushRefreshEnable() {
        return this.pushRefreshEnable;
    }

    public void setPushRefreshEnable(boolean pushRefreshEnable) {
        this.pushRefreshEnable = pushRefreshEnable;
    }

    public void setFooterViewText(CharSequence text) {
        this.loadMoreText.setText(text);
    }

    public void setFooterViewText(int resid) {
        this.loadMoreText.setText(resid);
    }

    public void refresh() {
        if (this.mPullLoadMoreListener != null) {
            this.mPullLoadMoreListener.onRefresh();
        }
    }

    public void loadMore() {
        if (this.mPullLoadMoreListener != null && this.hasMore) {
            this.mFooterView.setVisibility(0);
            invalidate();
            this.mPullLoadMoreListener.onLoadMore();
        }
    }

    public void setPullLoadMoreCompleted() {
        this.isRefresh = false;
        this.mSwipeRefreshLayout.setRefreshing(false);
        this.isLoadMore = false;
        this.mFooterView.setVisibility(8);
    }

    public void setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        this.mPullLoadMoreListener = listener;
    }

    public boolean isLoadMore() {
        return this.isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh() {
        return this.isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isHasMore() {
        return this.hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setscrollToPosition(int i) {
        this.mRecyclerView.scrollToPosition(i);
    }

    public void smoothMoveToPosition(int n) {
        int firstItem = this.mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = this.mLinearLayoutManager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            this.mRecyclerView.smoothScrollToPosition(n);
        } else if (n <= lastItem) {
            this.mRecyclerView.smoothScrollBy(0, this.mRecyclerView.getChildAt(n - firstItem).getTop());
        } else {
            this.mRecyclerView.smoothScrollToPosition(n);
            this.move = true;
        }
    }
}
