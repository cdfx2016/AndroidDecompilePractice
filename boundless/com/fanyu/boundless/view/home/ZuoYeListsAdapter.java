package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ZuoYeListsAdapter extends CommonAdapter<ClassHuifuEntity> {
    private String itemid;
    private String senduserid;

    public ZuoYeListsAdapter(Context context, int layoutId, List<ClassHuifuEntity> datas, String itemid, String senduserid) {
        super(context, layoutId, datas);
        this.itemid = itemid;
        this.senduserid = senduserid;
    }

    public void convert(ViewHolder holder, ClassHuifuEntity classHuifuEntity, final int position) {
        ImageView userhead = (ImageView) holder.getView(R.id.userhead);
        TextView hongdian = (TextView) holder.getView(R.id.hongdian);
        LinearLayout item = (LinearLayout) holder.getView(R.id.item_zuoye_ll);
        try {
            if (StringUtils.isEmpty(((ClassHuifuEntity) this.mDatas.get(position)).getUserimg())) {
                Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(((ClassHuifuEntity) this.mDatas.get(position)).getUserimg())).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new CropCircleTransformation(this.mContext)).into(userhead);
            } else {
                userhead.setImageResource(R.mipmap.morenimg);
            }
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(((ClassHuifuEntity) this.mDatas.get(position)).getNickname())) {
            holder.setText(R.id.username, ((ClassHuifuEntity) this.mDatas.get(position)).getNickname());
        }
        if (((ClassHuifuEntity) this.mDatas.get(position)).getAtttype().equals("0")) {
            holder.setText(R.id.content, "图片");
        } else if (((ClassHuifuEntity) this.mDatas.get(position)).getAtttype().equals("2")) {
            holder.setText(R.id.content, ((ClassHuifuEntity) this.mDatas.get(position)).getContent());
        }
        if (((ClassHuifuEntity) this.mDatas.get(position)).getIsread().equals("0")) {
            hongdian.setVisibility(0);
        } else {
            hongdian.setVisibility(8);
        }
        if (StringUtils.isEmpty(((ClassHuifuEntity) this.mDatas.get(position)).getCreatetime())) {
            holder.setText(R.id.publish_time, StringUtils.datestring(((ClassHuifuEntity) this.mDatas.get(position)).getCreatetime()));
        }
        item.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZuoYeListsAdapter.this.mDatas.size() > position) {
                    Intent intent = new Intent();
                    intent.setClass(ZuoYeListsAdapter.this.mContext, SubmitHomeWorkActivity.class);
                    intent.putExtra("classid", ZuoYeListsAdapter.this.itemid);
                    intent.putExtra("senduserid", ((ClassHuifuEntity) ZuoYeListsAdapter.this.mDatas.get(position)).getUserid());
                    intent.putExtra("zhurenid", ZuoYeListsAdapter.this.senduserid);
                    intent.putExtra("mytype", "teacher");
                    intent.putExtra("content", "作业已批阅，请及时查看！");
                    intent.putExtra("receiveid", ((ClassHuifuEntity) ZuoYeListsAdapter.this.mDatas.get(position)).getUserid());
                    ZuoYeListsAdapter.this.mContext.startActivity(intent);
                    ((ClassHuifuEntity) ZuoYeListsAdapter.this.mDatas.get(position)).setIsread("1");
                    ZuoYeListsAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }
}
