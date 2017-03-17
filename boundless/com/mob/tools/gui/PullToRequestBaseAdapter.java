package com.mob.tools.gui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PullToRequestBaseAdapter extends BaseAdapter {
    private PullToRequestBaseListAdapter adapter;

    public PullToRequestBaseAdapter(PullToRequestBaseListAdapter adapter) {
        this.adapter = adapter;
    }

    public int getCount() {
        return this.adapter.getCount();
    }

    public Object getItem(int position) {
        return this.adapter.getItem(position);
    }

    public long getItemId(int position) {
        return this.adapter.getItemId(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return this.adapter.getView(position, convertView, parent);
    }

    public int getItemViewType(int position) {
        return this.adapter.getItemViewType(position);
    }

    public int getViewTypeCount() {
        return this.adapter.getViewTypeCount();
    }
}
