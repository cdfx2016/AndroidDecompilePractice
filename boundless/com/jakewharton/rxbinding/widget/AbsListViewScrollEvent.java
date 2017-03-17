package com.jakewharton.rxbinding.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.AbsListView;
import com.jakewharton.rxbinding.view.ViewEvent;

public final class AbsListViewScrollEvent extends ViewEvent<AbsListView> {
    private final int firstVisibleItem;
    private final int scrollState;
    private final int totalItemCount;
    private final int visibleItemCount;

    @CheckResult
    @NonNull
    public static AbsListViewScrollEvent create(AbsListView listView, int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        return new AbsListViewScrollEvent(listView, scrollState, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    private AbsListViewScrollEvent(@NonNull AbsListView view, int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super(view);
        this.scrollState = scrollState;
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    public int scrollState() {
        return this.scrollState;
    }

    public int firstVisibleItem() {
        return this.firstVisibleItem;
    }

    public int visibleItemCount() {
        return this.visibleItemCount;
    }

    public int totalItemCount() {
        return this.totalItemCount;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbsListViewScrollEvent that = (AbsListViewScrollEvent) o;
        if (this.scrollState != that.scrollState || this.firstVisibleItem != that.firstVisibleItem || this.visibleItemCount != that.visibleItemCount) {
            return false;
        }
        if (this.totalItemCount != that.totalItemCount) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((((this.scrollState * 31) + this.firstVisibleItem) * 31) + this.visibleItemCount) * 31) + this.totalItemCount;
    }

    public String toString() {
        return "AbsListViewScrollEvent{scrollState=" + this.scrollState + ", firstVisibleItem=" + this.firstVisibleItem + ", visibleItemCount=" + this.visibleItemCount + ", totalItemCount=" + this.totalItemCount + '}';
    }
}
