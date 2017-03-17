package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.SpinglunEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PingLunAdapter extends CommonAdapter<SpinglunEntity> {
    public PingLunAdapter(Context context, int layoutId, List<SpinglunEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(ViewHolder holder, SpinglunEntity spinglunEntity, int position) {
        if (StringUtils.isEmpty(spinglunEntity.getSenduserimg())) {
            holder.setImageUrlSquare(R.id.touxiang, spinglunEntity.getSenduserimg(), new CropCircleTransformation(this.mContext));
        }
        if (StringUtils.isEmpty(spinglunEntity.getSendusername())) {
            holder.setText(R.id.username, spinglunEntity.getSendusername());
        }
        if (StringUtils.isEmpty(spinglunEntity.getCreatetime())) {
            holder.setText(R.id.publish_time, spinglunEntity.getCreatetime());
        }
        if (StringUtils.isEmpty(spinglunEntity.getContent())) {
            holder.setText(R.id.content, spinglunEntity.getContent());
        }
        holder.setTextColor(R.id.content, Color.parseColor("#000000"));
        Log.d("count", getItemCount() + "********" + position + "");
    }
}
