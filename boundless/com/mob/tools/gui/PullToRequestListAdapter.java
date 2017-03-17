package com.mob.tools.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public abstract class PullToRequestListAdapter extends PullToRequestBaseListAdapter {
    private PullToRequestBaseAdapter adapter;
    private boolean fling;
    private ScrollableListView listView = onNewListView(getContext());
    private OnListStopScrollListener osListener;
    private boolean pullUpReady;

    public PullToRequestListAdapter(PullToRequestView view) {
        super(view);
        this.listView.setOnScrollListener(new OnScrollListener() {
            private int firstVisibleItem;
            private int visibleItemCount;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                PullToRequestListAdapter.this.fling = scrollState == 2;
                if (scrollState != 0) {
                    return;
                }
                if (PullToRequestListAdapter.this.osListener != null) {
                    PullToRequestListAdapter.this.osListener.onListStopScrolling(this.firstVisibleItem, this.visibleItemCount);
                } else if (PullToRequestListAdapter.this.adapter != null) {
                    PullToRequestListAdapter.this.adapter.notifyDataSetChanged();
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
                View v = view.getChildAt(visibleItemCount - 1);
                PullToRequestListAdapter pullToRequestListAdapter = PullToRequestListAdapter.this;
                boolean z = firstVisibleItem + visibleItemCount == totalItemCount && v != null && v.getBottom() <= view.getBottom();
                pullToRequestListAdapter.pullUpReady = z;
                PullToRequestListAdapter.this.onScroll(PullToRequestListAdapter.this.listView, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        this.adapter = new PullToRequestBaseAdapter(this);
        this.listView.setAdapter(this.adapter);
    }

    protected ScrollableListView onNewListView(Context context) {
        return new ScrollableListView(context);
    }

    public Scrollable getBodyView() {
        return this.listView;
    }

    public ListView getListView() {
        return this.listView;
    }

    public boolean isFling() {
        return this.fling;
    }

    public void onScroll(Scrollable scrollable, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.adapter.notifyDataSetChanged();
    }

    public void setDivider(Drawable divider) {
        this.listView.setDivider(divider);
    }

    public void setDividerHeight(int height) {
        this.listView.setDividerHeight(height);
    }

    public boolean isPullDownReady() {
        return this.listView.isReadyToPull();
    }

    public boolean isPullUpReady() {
        return this.pullUpReady;
    }
}
