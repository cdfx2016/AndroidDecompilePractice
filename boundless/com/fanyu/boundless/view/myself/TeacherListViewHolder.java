package com.fanyu.boundless.view.myself;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.util.ImagePathUtil;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherListViewHolder extends BaseViewHolder<classmember> {
    private ImageView teacherimg = ((ImageView) $(R.id.teacherimg));
    private TextView teachername = ((TextView) $(R.id.teachername));
    private TextView teacherxueke = ((TextView) $(R.id.teacherxueke));

    public TeacherListViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_teacherlist);
    }

    public void setData(classmember data) {
        super.setData(data);
        try {
            Glide.with(getContext()).load(ImagePathUtil.getInstance().getPath(data.getUserimg())).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).bitmapTransform(new CropCircleTransformation(getContext())).dontAnimate().into(this.teacherimg);
        } catch (Exception e) {
        }
        this.teachername.setText(data.getMembername());
        this.teacherxueke.setText(data.getXueke());
    }
}
