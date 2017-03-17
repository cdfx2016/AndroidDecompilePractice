package com.zhy.adapter.recyclerview.wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import com.zhy.adapter.recyclerview.utils.WrapperUtils;
import com.zhy.adapter.recyclerview.utils.WrapperUtils.SpanSizeCallback;

public class EmptyWrapper<T> extends Adapter<ViewHolder> {
    public static final int ITEM_TYPE_EMPTY = 2147483646;
    private int mEmptyLayoutId;
    private View mEmptyView;
    private Adapter mInnerAdapter;

    public EmptyWrapper(Adapter adapter) {
        this.mInnerAdapter = adapter;
    }

    private boolean isEmpty() {
        return !(this.mEmptyView == null && this.mEmptyLayoutId == 0) && this.mInnerAdapter.getItemCount() == 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!isEmpty()) {
            return this.mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
        if (this.mEmptyView != null) {
            return com.zhy.adapter.recyclerview.base.ViewHolder.createViewHolder(parent.getContext(), this.mEmptyView);
        }
        return com.zhy.adapter.recyclerview.base.ViewHolder.createViewHolder(parent.getContext(), parent, this.mEmptyLayoutId);
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(this.mInnerAdapter, recyclerView, new SpanSizeCallback() {
            public int getSpanSize(GridLayoutManager gridLayoutManager, SpanSizeLookup oldLookup, int position) {
                if (EmptyWrapper.this.isEmpty()) {
                    return gridLayoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    public void onViewAttachedToWindow(ViewHolder holder) {
        this.mInnerAdapter.onViewAttachedToWindow(holder);
        if (isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    public int getItemViewType(int position) {
        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return this.mInnerAdapter.getItemViewType(position);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!isEmpty()) {
            this.mInnerAdapter.onBindViewHolder(holder, position);
        }
    }

    public int getItemCount() {
        if (isEmpty()) {
            return 1;
        }
        return this.mInnerAdapter.getItemCount();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public void setEmptyView(int layoutId) {
        this.mEmptyLayoutId = layoutId;
    }
}
