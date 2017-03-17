package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.VideoalbumEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MicroClassSonAdapter extends CommonAdapter<VideoalbumEntity> {
    private OnItemClickListener onItemClickListener;

    public MicroClassSonAdapter(Context context, int layoutId, List<VideoalbumEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(final ViewHolder holder, VideoalbumEntity VideoalbumEntity, final int position) {
        holder.setText(R.id.tv_text, VideoalbumEntity.getName());
        holder.setImageUrl(R.id.iv_weike1, VideoalbumEntity.getFilename(), new RoundedCornersTransformation(this.mContext, 15, 0));
        setLine(position, holder.getView(R.id.line));
        holder.setOnClickListener(R.id.llayout_weke, new OnClickListener() {
            public void onClick(View v) {
                MicroClassSonAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
    }

    private void setLine(int position, View view) {
        int i = getItemCount() % 3;
        if (getItemCount() == 3) {
            view.setVisibility(8);
        } else if ((position + i) + 1 > getItemCount()) {
            view.setVisibility(8);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
