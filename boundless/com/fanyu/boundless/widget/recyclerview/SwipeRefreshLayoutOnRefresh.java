package com.fanyu.boundless.widget.recyclerview;

import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

public class SwipeRefreshLayoutOnRefresh implements OnRefreshListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    public SwipeRefreshLayoutOnRefresh(PullLoadMoreRecyclerView pullLoadMoreRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    public void onRefresh() {
        if (!this.mPullLoadMoreRecyclerView.isRefresh()) {
            this.mPullLoadMoreRecyclerView.setIsRefresh(true);
            this.mPullLoadMoreRecyclerView.refresh();
        }
    }
}
