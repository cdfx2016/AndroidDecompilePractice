package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class ApplyXiaoXiActivity$$ViewBinder<T extends ApplyXiaoXiActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.sqname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.sqname, "field 'sqname'"), R.id.sqname, "field 'sqname'");
        target.sqtime = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.sqtime, "field 'sqtime'"), R.id.sqtime, "field 'sqtime'");
        target.sqrole = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.sqrole, "field 'sqrole'"), R.id.sqrole, "field 'sqrole'");
        target.shenqing = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.shenqing, "field 'shenqing'"), R.id.shenqing, "field 'shenqing'");
        target.xueke = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.xueke, "field 'xueke'"), R.id.xueke, "field 'xueke'");
        target.sqbeizhu = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.sqbeizhu, "field 'sqbeizhu'"), R.id.sqbeizhu, "field 'sqbeizhu'");
        target.classname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        target.apState = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.ap_state, "field 'apState'"), R.id.ap_state, "field 'apState'");
        View view = (View) finder.findRequiredView(source, R.id.no, "field 'no' and method 'onClick'");
        target.no = (TextView) finder.castView(view, R.id.no, "field 'no'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.yes, "field 'yes' and method 'onClick'");
        target.yes = (TextView) finder.castView(view, R.id.yes, "field 'yes'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.sqname = null;
        target.sqtime = null;
        target.sqrole = null;
        target.shenqing = null;
        target.xueke = null;
        target.sqbeizhu = null;
        target.classname = null;
        target.apState = null;
        target.no = null;
        target.yes = null;
    }
}
