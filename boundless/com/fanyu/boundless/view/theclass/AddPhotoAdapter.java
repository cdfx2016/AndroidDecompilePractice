package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import com.fanyu.boundless.R;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnClickListener;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import java.util.List;

public class AddPhotoAdapter extends CommonAdapter<PhotoInfo> {
    private OnClickListener onClickListener;
    private OnItemClickListener onItemClickListener;

    public AddPhotoAdapter(Context context, int layoutId, List<PhotoInfo> datas) {
        super(context, layoutId, datas);
    }

    public void convert(final ViewHolder holder, PhotoInfo photoInfo, final int position) {
        if (photoInfo.getPhotoId() == ViewCompat.MEASURED_SIZE_MASK) {
            holder.setImageResource(R.id.ItemImage, Integer.parseInt(photoInfo.getPhotoPath()));
            holder.setVisible(R.id.delete_img, false);
            holder.setClickable(R.id.ItemImage, true);
            holder.setOnClickListener(R.id.ItemImage, new View.OnClickListener() {
                public void onClick(View v) {
                    AddPhotoAdapter.this.onClickListener.onClick(holder);
                }
            });
            return;
        }
        holder.setloadImageUrl(R.id.ItemImage, photoInfo.getPhotoPath());
        holder.setClickable(R.id.ItemImage, false);
        holder.setVisible(R.id.delete_img, true);
        holder.setOnClickListener(R.id.delete_img, new View.OnClickListener() {
            public void onClick(View v) {
                AddPhotoAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
