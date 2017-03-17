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

public class UnShowStudentAdapter extends CommonAdapter<ChildItem> {
    private OnItemClickListener onItemClickListener;

    public UnShowStudentAdapter(Context context, int layoutId, List<ChildItem> datas) {
        super(context, layoutId, datas);
    }

    public void convert(ViewHolder holder, ChildItem childItem, int position) {
        String mString = "01";
        if (position < 9) {
            mString = "0" + (position + 1);
        } else {
            mString = (position + 1) + "";
        }
        if (StringUtils.isEmpty(mString)) {
            holder.setText(R.id.bianhao, mString);
        }
        if (StringUtils.isEmpty(childItem.getTitle())) {
            holder.setText(R.id.name, childItem.getTitle());
        }
        holder.setIschecked(R.id.nicaicai, Boolean.valueOf(childItem.isIscheck()));
        holder.setOnClickListener(R.id.nicaicai, new OnClickListener() {
            public void onClick(View v) {
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
