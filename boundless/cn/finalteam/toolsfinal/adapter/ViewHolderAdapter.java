package cn.finalteam.toolsfinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public abstract class ViewHolderAdapter<VH extends ViewHolder, T> extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater = LayoutInflater.from(this.mContext);
    private List<T> mList;

    public static class ViewHolder {
        View view;

        public ViewHolder(View view) {
            this.view = view;
        }
    }

    public abstract void onBindViewHolder(VH vh, int i);

    public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i);

    public ViewHolderAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }

    public int getCount() {
        return this.mList.size();
    }

    public T getItem(int position) {
        return this.mList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        VH holder;
        if (view == null) {
            holder = onCreateViewHolder(viewGroup, i);
            holder.view.setTag(holder);
        } else {
            ViewHolder holder2 = (ViewHolder) view.getTag();
        }
        onBindViewHolder(holder, i);
        return holder.view;
    }

    public View inflate(int resLayout, ViewGroup parent) {
        return this.mInflater.inflate(resLayout, parent, false);
    }

    public List<T> getDatas() {
        return this.mList;
    }

    public Context getContext() {
        return this.mContext;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }
}
