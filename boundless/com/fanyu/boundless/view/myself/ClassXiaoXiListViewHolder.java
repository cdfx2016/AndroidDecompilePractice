package com.fanyu.boundless.view.myself;

import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.applyentity;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClassXiaoXiListViewHolder extends BaseViewHolder<applyentity> {
    private TextView sqbanji = ((TextView) $(R.id.sqbanji));
    private TextView sqname = ((TextView) $(R.id.sqname));
    private TextView sqstate = ((TextView) $(R.id.sqstate));
    private TextView sqtime = ((TextView) $(R.id.sqtime));
    private ImageView userhead = ((ImageView) $(R.id.userhead));

    public ClassXiaoXiListViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_classxiaoxi_list);
    }

    public void setData(applyentity entity) {
        Log.i("ViewHolder", "position" + getDataPosition());
        try {
            if (StringUtils.isEmpty(entity.getUserimg())) {
                Glide.with(getContext()).load(ImagePathUtil.getInstance().getPath(entity.getUserimg())).error((int) R.mipmap.jiazai_shibai).placeholder((int) R.mipmap.jiazaizhong_yuan).bitmapTransform(new CropCircleTransformation(getContext())).dontAnimate().into(this.userhead);
            }
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(entity.getUsername())) {
            this.sqname.setText(entity.getUsername());
        }
        if (StringUtils.isEmpty(entity.getClassname())) {
            this.sqbanji.setText(entity.getClassname());
        }
        if (StringUtils.isEmpty(entity.getCreatetime())) {
            this.sqtime.setText(StringUtils.datestring(entity.getCreatetime()));
        }
        if (entity.getState().equals("0")) {
            this.sqstate.setBackgroundColor(Color.parseColor("#b27218"));
            this.sqstate.setText("待审核");
        } else if (entity.getState().equals("1")) {
            this.sqstate.setBackgroundColor(Color.parseColor("#e1ba83"));
            this.sqstate.setText("已通过");
        } else if (entity.getState().equals("2")) {
            this.sqstate.setBackgroundColor(Color.parseColor("#dd2727"));
            this.sqstate.setText("已驳回");
        }
    }
}
