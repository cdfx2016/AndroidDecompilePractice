package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.applyentity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import java.io.Serializable;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClassXiaoXiListsAdapter extends CommonAdapter<applyentity> {
    public ClassXiaoXiListsAdapter(Context context, int layoutId, List<applyentity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(ViewHolder holder, applyentity applyentity, final int position) {
        ImageView userhead = (ImageView) holder.getView(R.id.userhead);
        try {
            if (StringUtils.isEmpty(((applyentity) this.mDatas.get(position)).getUserimg())) {
                Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(((applyentity) this.mDatas.get(position)).getUserimg())).error((int) R.mipmap.jiazai_shibai).placeholder((int) R.mipmap.jiazaizhong_yuan).bitmapTransform(new CropCircleTransformation(this.mContext)).dontAnimate().into(userhead);
            }
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(((applyentity) this.mDatas.get(position)).getUsername())) {
            holder.setText(R.id.sqname, ((applyentity) this.mDatas.get(position)).getUsername());
        }
        if (StringUtils.isEmpty(((applyentity) this.mDatas.get(position)).getClassname())) {
            holder.setText(R.id.sqbanji, ((applyentity) this.mDatas.get(position)).getClassname());
        }
        if (StringUtils.isEmpty(((applyentity) this.mDatas.get(position)).getCreatetime())) {
            holder.setText(R.id.sqtime, StringUtils.datestring(((applyentity) this.mDatas.get(position)).getCreatetime()));
        }
        TextView sqstate = (TextView) holder.getView(R.id.sqstate);
        if (((applyentity) this.mDatas.get(position)).getState().equals("0")) {
            sqstate.setBackgroundColor(Color.parseColor("#b27218"));
            holder.setText(R.id.sqstate, "待审核");
        } else if (((applyentity) this.mDatas.get(position)).getState().equals("1")) {
            sqstate.setBackgroundColor(Color.parseColor("#e1ba83"));
            holder.setText(R.id.sqstate, "已通过");
        } else if (((applyentity) this.mDatas.get(position)).getState().equals("2")) {
            sqstate.setBackgroundColor(Color.parseColor("#dd2727"));
            holder.setText(R.id.sqstate, "已驳回");
        }
        ((LinearLayout) holder.getView(R.id.item_classxiaoxi)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClassXiaoXiListsAdapter.this.mContext, ApplyXiaoXiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entity", (Serializable) ClassXiaoXiListsAdapter.this.mDatas.get(position));
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                ClassXiaoXiListsAdapter.this.mContext.startActivity(intent);
            }
        });
    }
}
