package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.view.ViewGroup;
import com.fanyu.boundless.view.myself.ClassListViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class SchoolClassAdapter extends RecyclerArrayAdapter {
    public SchoolClassAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClassListViewHolder(parent);
    }
}
