package com.mob.tools.gui;

import android.content.Context;
import android.view.View;

public abstract class PullToRequestAdatper {
    private Context context;
    private PullToRequestView parent;

    public abstract Scrollable getBodyView();

    public abstract View getFooterView();

    public abstract View getHeaderView();

    public abstract boolean isPullDownReady();

    public abstract boolean isPullUpReady();

    public PullToRequestAdatper(PullToRequestView view) {
        this.context = view.getContext();
        this.parent = view;
    }

    public Context getContext() {
        return this.context;
    }

    protected PullToRequestView getParent() {
        return this.parent;
    }

    public void notifyDataSetChanged() {
        this.parent.stopPulling();
    }

    public void onRefresh() {
    }

    public void onRequestNext() {
    }

    public void onReversed() {
    }

    public void onPullDown(int percent) {
    }

    public void onPullUp(int percent) {
    }
}
