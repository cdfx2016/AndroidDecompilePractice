package cn.finalteam.galleryfinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.finalteam.toolsfinal.adapter.RecyclingPagerAdapter;
import java.util.List;

public abstract class ViewHolderRecyclingPagerAdapter<VH extends ViewHolder, T> extends RecyclingPagerAdapter {
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

    public ViewHolderRecyclingPagerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }

    public View getView(int position, View convertView, ViewGroup container) {
        VH holder;
        if (convertView == null) {
            holder = onCreateViewHolder(container, position);
            holder.view.setTag(holder);
        } else {
            ViewHolder holder2 = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return holder.view;
    }

    public int getCount() {
        return this.mList.size();
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
