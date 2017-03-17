package com.jude.easyrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;

public abstract class BaseViewHolder<M> extends ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }

    public void setData(M m) {
    }

    protected <T extends View> T $(@IdRes int id) {
        return this.itemView.findViewById(id);
    }

    protected Context getContext() {
        return this.itemView.getContext();
    }

    protected int getDataPosition() {
        Adapter adapter = getOwnerAdapter();
        if (adapter == null || !(adapter instanceof RecyclerArrayAdapter)) {
            return getAdapterPosition();
        }
        return getAdapterPosition() - ((RecyclerArrayAdapter) adapter).getHeaderCount();
    }

    @Nullable
    protected <T extends Adapter> T getOwnerAdapter() {
        RecyclerView recyclerView = getOwnerRecyclerView();
        return recyclerView == null ? null : recyclerView.getAdapter();
    }

    @Nullable
    protected RecyclerView getOwnerRecyclerView() {
        try {
            Field field = ViewHolder.class.getDeclaredField("mOwnerRecyclerView");
            field.setAccessible(true);
            return (RecyclerView) field.get(this);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e2) {
        }
        return null;
    }
}
