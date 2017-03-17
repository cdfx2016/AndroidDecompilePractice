package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import java.io.Serializable;
import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ClassAdapter extends CommonAdapter<schoolclassentity> {
    private String mUserid;

    public ClassAdapter(Context context, int layoutId, List<schoolclassentity> datas, String userid) {
        super(context, layoutId, datas);
        this.mUserid = userid;
    }

    public void convert(ViewHolder holder, schoolclassentity schoolclassentity, final int position) {
        ImageView classimg = (ImageView) holder.getView(R.id.classimg);
        try {
            Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(((schoolclassentity) this.mDatas.get(position)).getClassimg())).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new RoundedCornersTransformation(this.mContext, 15, 0)).into(classimg);
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(((schoolclassentity) this.mDatas.get(position)).getClassname())) {
            holder.setText(R.id.classname, ((schoolclassentity) this.mDatas.get(position)).getClassname());
        }
        if (StringUtils.isEmpty(((schoolclassentity) this.mDatas.get(position)).getCreatename())) {
            if (this.mUserid.equals(((schoolclassentity) this.mDatas.get(position)).getUserid())) {
                holder.setText(R.id.classboss, "我");
            } else {
                holder.setText(R.id.classboss, ((schoolclassentity) this.mDatas.get(position)).getCreatename());
            }
        }
        if (StringUtils.isEmpty(((schoolclassentity) this.mDatas.get(position)).getStunum())) {
            holder.setText(R.id.classnumber, ((schoolclassentity) this.mDatas.get(position)).getStunum());
        }
        if (StringUtils.isEmpty(((schoolclassentity) this.mDatas.get(position)).getClassnumber() + "")) {
            holder.setText(R.id.classbianhao, ((schoolclassentity) this.mDatas.get(position)).getClassnumber() + "");
        }
        ((LinearLayout) holder.getView(R.id.item_class_ll)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClassAdapter.this.mContext, ClassAllNoticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entity", (Serializable) ClassAdapter.this.mDatas.get(position));
                intent.putExtras(bundle);
                ClassAdapter.this.mContext.startActivity(intent);
            }
        });
    }
}
