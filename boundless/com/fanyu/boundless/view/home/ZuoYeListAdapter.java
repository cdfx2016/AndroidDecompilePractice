package com.fanyu.boundless.view.home;

import android.content.Context;
import android.view.ViewGroup;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class ZuoYeListAdapter extends RecyclerArrayAdapter {
    public ZuoYeListAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ZuoYeListViewHolder(parent);
    }
}
