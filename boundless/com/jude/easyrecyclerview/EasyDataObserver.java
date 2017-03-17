package com.jude.easyrecyclerview;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class EasyDataObserver extends AdapterDataObserver {
    private RecyclerArrayAdapter adapter;
    private EasyRecyclerView recyclerView;

    public EasyDataObserver(EasyRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            this.adapter = (RecyclerArrayAdapter) recyclerView.getAdapter();
        }
    }

    private boolean isHeaderFooter(int position) {
        return this.adapter != null && (position < this.adapter.getHeaderCount() || position >= this.adapter.getHeaderCount() + this.adapter.getCount());
    }

    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        update();
    }

    public void onChanged() {
        super.onChanged();
        update();
    }

    private void update() {
        int count;
        if (this.recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            count = ((RecyclerArrayAdapter) this.recyclerView.getAdapter()).getCount();
        } else {
            count = this.recyclerView.getAdapter().getItemCount();
        }
        if (count == 0) {
            this.recyclerView.showEmpty();
        } else {
            this.recyclerView.showRecycler();
        }
    }
}
