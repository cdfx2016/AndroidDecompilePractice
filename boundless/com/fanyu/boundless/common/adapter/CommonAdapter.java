package com.fanyu.boundless.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.ViewGroup;
import java.util.List;

public abstract class CommonAdapter<T> extends Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;
    protected int mLayoutId;

    public abstract void convert(ViewHolder viewHolder, T t, int i);

    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mDatas = datas;
    }

    public int getItemCount() {
        return this.mDatas.size();
    }

    public void onBindViewHolder(ViewHolder arg0, int arg1) {
        convert(arg0, this.mDatas.get(arg1), arg1);
    }

    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        return ViewHolder.get(this.mContext, arg0, this.mLayoutId);
    }
}
