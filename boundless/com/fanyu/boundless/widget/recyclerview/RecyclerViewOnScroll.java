package com.fanyu.boundless.widget.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecyclerViewOnScroll extends OnScrollListener {
    private ImageLoader imageLoader;
    private int mIndex = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerView mRecyclerView;
    private boolean move = false;
    private boolean pauseOnFling;
    private boolean pauseOnScroll;

    public RecyclerViewOnScroll(PullLoadMoreRecyclerView pullLoadMoreRecyclerView, boolean pauseOnScroll, boolean pauseOnFling, boolean move, int mIndex, LinearLayoutManager mLinearLayoutManager, RecyclerView mRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.move = move;
        this.mIndex = mIndex;
        this.mLinearLayoutManager = mLinearLayoutManager;
        this.mRecyclerView = mRecyclerView;
        this.imageLoader = ImageLoader.getInstance();
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastCompletelyVisibleItem = 0;
        int firstVisibleItem = 0;
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            lastCompletelyVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            lastCompletelyVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
            lastCompletelyVisibleItem = findMax(lastPositions);
            firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
        }
        if (firstVisibleItem != 0 && firstVisibleItem != -1) {
            this.mPullLoadMoreRecyclerView.setSwipeRefreshEnable(false);
        } else if (this.mPullLoadMoreRecyclerView.getPullRefreshEnable()) {
            this.mPullLoadMoreRecyclerView.setSwipeRefreshEnable(true);
        }
        if (!this.mPullLoadMoreRecyclerView.getPushRefreshEnable() || this.mPullLoadMoreRecyclerView.isRefresh() || !this.mPullLoadMoreRecyclerView.isHasMore() || lastCompletelyVisibleItem != totalItemCount - 1 || this.mPullLoadMoreRecyclerView.isLoadMore()) {
            return;
        }
        if (dx > 0 || dy > 0) {
            this.mPullLoadMoreRecyclerView.setIsLoadMore(true);
            this.mPullLoadMoreRecyclerView.loadMore();
        }
    }

    private int findMax(int[] lastPositions) {
        int i = 0;
        int max = lastPositions[0];
        int length = lastPositions.length;
        while (i < length) {
            int value = lastPositions[i];
            if (value > max) {
                max = value;
            }
            i++;
        }
        return max;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        super.onScrollStateChanged(recyclerView, newState);
        if (this.move && newState == 0) {
            this.move = false;
            int n = this.mIndex - this.mLinearLayoutManager.findFirstVisibleItemPosition();
            if (n >= 0 && n < this.mRecyclerView.getChildCount()) {
                this.mRecyclerView.smoothScrollBy(0, this.mRecyclerView.getChildAt(n).getTop());
            }
        }
        if (this.imageLoader != null) {
            switch (newState) {
                case 0:
                    this.imageLoader.resume();
                    return;
                case 1:
                    if (this.pauseOnScroll) {
                        this.imageLoader.pause();
                        return;
                    } else {
                        this.imageLoader.resume();
                        return;
                    }
                case 2:
                    if (this.pauseOnFling) {
                        this.imageLoader.pause();
                        return;
                    } else {
                        this.imageLoader.resume();
                        return;
                    }
                default:
                    return;
            }
        }
    }
}
