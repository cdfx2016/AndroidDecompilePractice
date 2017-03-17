package com.fanyu.boundless.view.home;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HomeVideoAdapter extends CommonAdapter<VideoEntity> {
    private OnItemClickListener onItemClickListener;

    public HomeVideoAdapter(Context context, int layoutId, List<VideoEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(final ViewHolder holder, VideoEntity videoEntity, final int position) {
        holder.setImageUrl(R.id.shuzi, videoEntity.getFilename(), new RoundedCornersTransformation(this.mContext, 15, 0));
        holder.setText(R.id.vidioname, videoEntity.getVideoname());
        holder.setOnClickListener(R.id.llayout_weke, new OnClickListener() {
            public void onClick(View v) {
                HomeVideoAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
