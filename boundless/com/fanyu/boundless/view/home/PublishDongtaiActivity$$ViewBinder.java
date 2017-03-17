package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class PublishDongtaiActivity$$ViewBinder<T extends PublishDongtaiActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.img_xxsb_return, "field 'imgXxsbReturn' and method 'onclick'");
        target.imgXxsbReturn = (ImageView) finder.castView(view, R.id.img_xxsb_return, "field 'imgXxsbReturn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        view = (View) finder.findRequiredView(source, R.id.right_btn, "field 'rightBtn' and method 'onclick'");
        target.rightBtn = (TextView) finder.castView(view, R.id.right_btn, "field 'rightBtn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
        target.classname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        view = (View) finder.findRequiredView(source, R.id.selectclass, "field 'selectclass' and method 'onclick'");
        target.selectclass = (RelativeLayout) finder.castView(view, R.id.selectclass, "field 'selectclass'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
        target.editdescribe = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editdescribe, "field 'editdescribe'"), R.id.editdescribe, "field 'editdescribe'");
        target.zuoyeview = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.zuoyeview, "field 'zuoyeview'"), R.id.zuoyeview, "field 'zuoyeview'");
        target.dongtai = (RadioButton) finder.castView((View) finder.findRequiredView(source, R.id.dongtai, "field 'dongtai'"), R.id.dongtai, "field 'dongtai'");
        target.heibanbao = (RadioButton) finder.castView((View) finder.findRequiredView(source, R.id.heibanbao, "field 'heibanbao'"), R.id.heibanbao, "field 'heibanbao'");
        target.rongyubang = (RadioButton) finder.castView((View) finder.findRequiredView(source, R.id.rongyubang, "field 'rongyubang'"), R.id.rongyubang, "field 'rongyubang'");
        target.biaoqianGroup = (RadioGroup) finder.castView((View) finder.findRequiredView(source, R.id.biaoqian_group, "field 'biaoqianGroup'"), R.id.biaoqian_group, "field 'biaoqianGroup'");
    }

    public void unbind(T target) {
        target.imgXxsbReturn = null;
        target.messageTitle = null;
        target.rightBtn = null;
        target.classname = null;
        target.selectclass = null;
        target.editdescribe = null;
        target.zuoyeview = null;
        target.dongtai = null;
        target.heibanbao = null;
        target.rongyubang = null;
        target.biaoqianGroup = null;
    }
}
