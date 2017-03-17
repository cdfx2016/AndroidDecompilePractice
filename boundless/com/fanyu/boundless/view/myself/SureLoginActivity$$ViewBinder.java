package com.fanyu.boundless.view.myself;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class SureLoginActivity$$ViewBinder<T extends SureLoginActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        ((View) finder.findRequiredView(source, R.id.capture_imageview_back, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.btnLogin, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T t) {
    }
}
