package com.jude.easyrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;

public class FixDataObserver extends AdapterDataObserver {
    private RecyclerView recyclerView;

    public FixDataObserver(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (this.recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            RecyclerArrayAdapter adapter = (RecyclerArrayAdapter) this.recyclerView.getAdapter();
            if (adapter.getFooterCount() > 0 && adapter.getCount() == itemCount) {
                this.recyclerView.scrollToPosition(0);
            }
        }
    }
}
