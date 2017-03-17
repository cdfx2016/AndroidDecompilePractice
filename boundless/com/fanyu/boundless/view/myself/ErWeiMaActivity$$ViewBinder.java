package com.fanyu.boundless.view.myself;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class ErWeiMaActivity$$ViewBinder<T extends ErWeiMaActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick();
            }
        });
    }

    public void unbind(T t) {
    }
}
