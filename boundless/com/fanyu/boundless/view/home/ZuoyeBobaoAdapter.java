package com.fanyu.boundless.view.home;

import android.content.Context;
import android.view.ViewGroup;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class ZuoyeBobaoAdapter extends RecyclerArrayAdapter {
    public ZuoyeBobaoAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ZuoyeBobaoListViewHolder(parent);
    }
}
