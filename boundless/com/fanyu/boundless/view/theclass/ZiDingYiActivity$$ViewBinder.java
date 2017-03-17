package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class ZiDingYiActivity$$ViewBinder<T extends ZiDingYiActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.editZidingyi = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.edit_zidingyi, "field 'editZidingyi'"), R.id.edit_zidingyi, "field 'editZidingyi'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.queding, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.editZidingyi = null;
    }
}
