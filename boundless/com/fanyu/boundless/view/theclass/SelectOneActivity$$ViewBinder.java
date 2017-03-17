package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.GridView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class SelectOneActivity$$ViewBinder<T extends SelectOneActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.gridview = (GridView) finder.castView((View) finder.findRequiredView(source, R.id.gridview, "field 'gridview'"), R.id.gridview, "field 'gridview'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.gridview = null;
    }
}
