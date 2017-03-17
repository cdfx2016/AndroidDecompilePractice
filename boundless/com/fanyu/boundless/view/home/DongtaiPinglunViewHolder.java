package com.fanyu.boundless.view.home;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.Dailyreply;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DongtaiPinglunViewHolder extends BaseViewHolder<Dailyreply> {
    TextView content = ((TextView) $(R.id.content));
    TextView publishTime = ((TextView) $(R.id.publish_time));
    ImageView touxiang = ((ImageView) $(R.id.touxiang));
    TextView username = ((TextView) $(R.id.username));

    public DongtaiPinglunViewHolder(ViewGroup itemView) {
        super(itemView, R.layout.adapter_pinglun);
    }

    public void setData(Dailyreply entity) {
        Log.i("ViewHolder", "position" + getDataPosition());
        if (StringUtils.isEmpty(entity.getReplyuserimg())) {
            ImagePathUtil.getInstance().setImageUrl(getContext(), this.touxiang, entity.getReplyuserimg(), new CropCircleTransformation(getContext()));
        }
        if (StringUtils.isEmpty(entity.getReplyusername())) {
            this.username.setText(entity.getReplyusername());
        }
        if (StringUtils.isEmpty(entity.getReplytime())) {
            this.publishTime.setText(entity.getReplytime());
        }
        if (StringUtils.isEmpty(entity.getReplycontent())) {
            this.content.setVisibility(0);
            this.content.setText(entity.getReplycontent());
            return;
        }
        this.content.setVisibility(8);
    }
}
