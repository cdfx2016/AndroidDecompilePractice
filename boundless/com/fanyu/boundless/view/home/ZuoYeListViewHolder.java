package com.fanyu.boundless.view.home;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ZuoYeListViewHolder extends BaseViewHolder<ClassHuifuEntity> {
    private TextView content = ((TextView) $(R.id.content));
    private TextView hongdian = ((TextView) $(R.id.hongdian));
    private TextView publish_time = ((TextView) $(R.id.publish_time));
    private ImageView userhead = ((ImageView) $(R.id.userhead));
    private TextView username = ((TextView) $(R.id.username));

    public ZuoYeListViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_zuoye_list);
    }

    public void setData(ClassHuifuEntity entity) {
        try {
            Glide.with(getContext()).load(ImagePathUtil.getInstance().getPath(entity.getUserimg())).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new CropCircleTransformation(getContext())).into(this.userhead);
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(entity.getNickname())) {
            this.username.setText(entity.getNickname());
        }
        if (entity.getAtttype().equals("0")) {
            this.content.setText("图片");
        } else if (entity.getAtttype().equals("2")) {
            this.content.setText(entity.getContent());
        }
        if (entity.getIsread().equals("0")) {
            this.hongdian.setVisibility(0);
        } else {
            this.hongdian.setVisibility(8);
        }
        if (StringUtils.isEmpty(entity.getCreatetime())) {
            this.publish_time.setText(StringUtils.datestring(entity.getCreatetime()));
        }
    }
}
