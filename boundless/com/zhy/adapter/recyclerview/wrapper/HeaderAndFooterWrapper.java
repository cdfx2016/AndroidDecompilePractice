package com.zhy.adapter.recyclerview.wrapper;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import com.zhy.adapter.recyclerview.utils.WrapperUtils;
import com.zhy.adapter.recyclerview.utils.WrapperUtils.SpanSizeCallback;

public class HeaderAndFooterWrapper<T> extends Adapter<ViewHolder> {
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat();
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat();
    private Adapter mInnerAdapter;

    public HeaderAndFooterWrapper(Adapter adapter) {
        this.mInnerAdapter = adapter;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.mHeaderViews.get(viewType) != null) {
            return com.zhy.adapter.recyclerview.base.ViewHolder.createViewHolder(parent.getContext(), (View) this.mHeaderViews.get(viewType));
        }
        if (this.mFootViews.get(viewType) != null) {
            return com.zhy.adapter.recyclerview.base.ViewHolder.createViewHolder(parent.getContext(), (View) this.mFootViews.get(viewType));
        }
        return this.mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return this.mHeaderViews.keyAt(position);
        }
        if (isFooterViewPos(position)) {
            return this.mFootViews.keyAt((position - getHeadersCount()) - getRealItemCount());
        }
        return this.mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount() {
        return this.mInnerAdapter.getItemCount();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!isHeaderViewPos(position) && !isFooterViewPos(position)) {
            this.mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
        }
    }

    public int getItemCount() {
        return (getHeadersCount() + getFootersCount()) + getRealItemCount();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(this.mInnerAdapter, recyclerView, new SpanSizeCallback() {
            public int getSpanSize(GridLayoutManager layoutManager, SpanSizeLookup oldLookup, int position) {
                int viewType = HeaderAndFooterWrapper.this.getItemViewType(position);
                if (HeaderAndFooterWrapper.this.mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }
                if (HeaderAndFooterWrapper.this.mFootViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
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
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(View view) {
        this.mHeaderViews.put(this.mHeaderViews.size() + 100000, view);
    }

    public void addFootView(View view) {
        this.mFootViews.put(this.mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return this.mHeaderViews.size();
    }

    public int getFootersCount() {
        return this.mFootViews.size();
    }
}
