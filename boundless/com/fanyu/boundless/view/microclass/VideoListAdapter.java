package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import java.util.List;

public class VideoListAdapter extends CommonAdapter<VideoEntity> {
    private boolean isSingle = true;
    private int old = -1;
    private OnItemClickListener onItemClickListener;
    private SparseBooleanArray selected = new SparseBooleanArray();

    public VideoListAdapter(Context context, int layoutId, List<VideoEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(final ViewHolder holder, VideoEntity videoEntity, final int position) {
        LinearLayout mybjLayout = (LinearLayout) holder.getView(R.id.wodebei);
        TextView dijike = (TextView) holder.getView(R.id.dijike);
        TextView kename = (TextView) holder.getView(R.id.kename);
        View line = holder.getView(R.id.line);
        ((LinearLayout) holder.getView(R.id.video_item)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VideoListAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
        if (this.selected.get(position)) {
            int m = position + 1;
            mybjLayout.setBackgroundColor(Color.parseColor("#D29846"));
            dijike.setTextColor(Color.parseColor("#ffffff"));
            kename.setTextColor(Color.parseColor("#ffffff"));
            dijike.setText("第" + m + "集");
            kename.setText(videoEntity.getVideoname());
            return;
        }
        m = position + 1;
        mybjLayout.setBackgroundColor(Color.parseColor("#00000000"));
        dijike.setTextColor(Color.parseColor("#333333"));
        kename.setTextColor(Color.parseColor("#333333"));
        dijike.setText("第" + m + "集");
        kename.setText(videoEntity.getVideoname());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedItem(int selected) {
        boolean z = this.old != -1;
        this.isSingle = z;
        if (z) {
            this.selected.put(this.old, false);
        }
        this.selected.put(selected, true);
        this.old = selected;
        notifyDataSetChanged();
    }
}
