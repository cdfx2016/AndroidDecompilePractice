package com.zhy.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ItemViewDelegateManager;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.List;

public class MultiItemTypeAdapter<T> extends Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;
    protected ItemViewDelegateManager mItemViewDelegateManager = new ItemViewDelegateManager();
    protected OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int i);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i);
    }

    public MultiItemTypeAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    public int getItemViewType(int position) {
        if (useItemViewDelegateManager()) {
            return this.mItemViewDelegateManager.getItemViewType(this.mDatas.get(position), position);
        }
        return super.getItemViewType(position);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(this.mContext, parent, this.mItemViewDelegateManager.getItemViewDelegate(viewType).getItemViewLayoutId());
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {
    }

    public void convert(ViewHolder holder, T t) {
        this.mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    protected void setListener(ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (isEnabled(viewType)) {
            viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (MultiItemTypeAdapter.this.mOnItemClickListener != null) {
                        MultiItemTypeAdapter.this.mOnItemClickListener.onItemClick(v, viewHolder, viewHolder.getAdapterPosition());
                    }
                }
            });
            viewHolder.getConvertView().setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (MultiItemTypeAdapter.this.mOnItemClickListener == null) {
                        return false;
                    }
                    return MultiItemTypeAdapter.this.mOnItemClickListener.onItemLongClick(v, viewHolder, viewHolder.getAdapterPosition());
                }
            });
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, this.mDatas.get(position));
    }

    public int getItemCount() {
        return this.mDatas.size();
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        this.mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        this.mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return this.mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
