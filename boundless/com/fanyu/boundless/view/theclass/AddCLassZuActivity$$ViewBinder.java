package com.fanyu.boundless.view.theclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class AddCLassZuActivity$$ViewBinder<T extends AddCLassZuActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.editzuname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editzuname, "field 'editzuname'"), R.id.editzuname, "field 'editzuname'");
        target.gridRecycleview = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.grid_recycleview, "field 'gridRecycleview'"), R.id.grid_recycleview, "field 'gridRecycleview'");
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
        target.editzuname = null;
        target.gridRecycleview = null;
    }
}
