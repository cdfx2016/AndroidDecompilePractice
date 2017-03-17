package com.fanyu.boundless.view.registe;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class ResetPassWordActivity$$ViewBinder<T extends ResetPassWordActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.newpassword = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.newpassword, "field 'newpassword'"), R.id.newpassword, "field 'newpassword'");
        target.repassword = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.repassword, "field 'repassword'"), R.id.repassword, "field 'repassword'");
        ((View) finder.findRequiredView(source, R.id.img_xxsb_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.btnAction1, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.newpassword = null;
        target.repassword = null;
    }
}
