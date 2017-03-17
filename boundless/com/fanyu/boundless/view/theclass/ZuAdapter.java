package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;

public class ZuAdapter extends CommonAdapter<ChildItem> {
    private OnItemClickListener onItemClickListener;

    public ZuAdapter(Context context, int layoutId, List<ChildItem> datas) {
        super(context, layoutId, datas);
    }

    public void convert(final ViewHolder holder, ChildItem childItem, final int position) {
        if (StringUtils.isEmpty(childItem.getTitle())) {
            holder.setText(R.id.name, childItem.getTitle());
        }
        holder.setNewchecked(R.id.select, Boolean.valueOf(childItem.isIscheck()));
        holder.setOnClickListener(R.id.nicaicai, new OnClickListener() {
            public void onClick(View v) {
                ZuAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
