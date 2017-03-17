package com.fanyu.boundless.view.myself;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ClassListViewHolder extends BaseViewHolder<schoolclassentity> {
    private TextView classbianhao = ((TextView) $(R.id.classbianhao));
    private ImageView classimg = ((ImageView) $(R.id.classimg));
    private TextView classname = ((TextView) $(R.id.classname));
    private TextView createtname = ((TextView) $(R.id.classboss));
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(getContext());
    private TextView stunum = ((TextView) $(R.id.classnumber));
    private String userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");

    public ClassListViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_classlist);
    }

    public void setData(schoolclassentity entity) {
        Log.i("ViewHolder", "position" + getDataPosition());
        try {
            Glide.with(getContext()).load(ImagePathUtil.getInstance().getPath(entity.getClassimg())).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 0)).into(this.classimg);
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(entity.getClassname())) {
            this.classname.setText(entity.getClassname());
        }
        if (StringUtils.isEmpty(entity.getCreatename())) {
            if (this.userid.equals(entity.getUserid())) {
                this.createtname.setText("我");
            } else {
                this.createtname.setText(entity.getCreatename());
            }
        }
        if (StringUtils.isEmpty(entity.getStunum())) {
            this.stunum.setText(entity.getStunum());
        }
        if (StringUtils.isEmpty(entity.getClassnumber() + "")) {
            this.classbianhao.setText(entity.getClassnumber() + "");
        }
    }
}
