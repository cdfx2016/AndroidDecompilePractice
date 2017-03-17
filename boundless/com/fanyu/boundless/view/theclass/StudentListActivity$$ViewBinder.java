package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class StudentListActivity$$ViewBinder<T extends StudentListActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.expandlist = (ExpandableListView) finder.castView((View) finder.findRequiredView(source, R.id.expandlist, "field 'expandlist'"), R.id.expandlist, "field 'expandlist'");
        View view = (View) finder.findRequiredView(source, R.id.deletestu, "field 'deletestu' and method 'onClick'");
        target.deletestu = (TextView) finder.castView(view, R.id.deletestu, "field 'deletestu'");
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
        target.expandlist = null;
        target.deletestu = null;
    }
}
