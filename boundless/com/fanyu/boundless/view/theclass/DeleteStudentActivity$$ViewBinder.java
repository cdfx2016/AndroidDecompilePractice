package com.fanyu.boundless.view.theclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class DeleteStudentActivity$$ViewBinder<T extends DeleteStudentActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        target.gridRecycleview = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.grid_recycleview, "field 'gridRecycleview'"), R.id.grid_recycleview, "field 'gridRecycleview'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.shanchu, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.messageTitle = null;
        target.gridRecycleview = null;
    }
}
