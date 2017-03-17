package com.fanyu.boundless.view.theclass;

import android.content.Context;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;

public class UnClickZuAdapter extends CommonAdapter<ChildItem> {
    private int layoutid;
    private OnItemClickListener onItemClickListener;

    public UnClickZuAdapter(Context context, int layoutId, List<ChildItem> datas) {
        super(context, layoutId, datas);
        this.layoutid = layoutId;
    }

    public void convert(ViewHolder holder, ChildItem childItem, int position) {
        if (this.layoutid == R.layout.grid_item && StringUtils.isEmpty(childItem.getStudentnumber())) {
            holder.setText(R.id.bianhao, childItem.getStudentnumber());
        }
        if (StringUtils.isEmpty(childItem.getTitle())) {
            holder.setText(R.id.name, childItem.getTitle());
        }
        holder.setNewchecked(R.id.select, Boolean.valueOf(childItem.isIscheck()));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
