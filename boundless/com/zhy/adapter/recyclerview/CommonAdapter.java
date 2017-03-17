package com.zhy.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.List;

public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mLayoutId;

    protected abstract void convert(ViewHolder viewHolder, T t, int i);

    public CommonAdapter(Context context, final int layoutId, List<T> datas) {
        super(context, datas);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutId = layoutId;
        this.mDatas = datas;
        addItemViewDelegate(new ItemViewDelegate<T>() {
            public int getItemViewLayoutId() {
                return layoutId;
            }

            public boolean isForViewType(T t, int position) {
                return true;
            }

            public void convert(ViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }
}
