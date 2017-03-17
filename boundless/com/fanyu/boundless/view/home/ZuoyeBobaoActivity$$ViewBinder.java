package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class ZuoyeBobaoActivity$$ViewBinder<T extends ZuoyeBobaoActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.recyclerView = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        View view = (View) finder.findRequiredView(source, R.id.selectclass, "field 'selectclass' and method 'onClick'");
        target.selectclass = (RelativeLayout) finder.castView(view, R.id.selectclass, "field 'selectclass'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.classname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        view = (View) finder.findRequiredView(source, R.id.addzuoye, "field 'addzuoye' and method 'onClick'");
        target.addzuoye = (TextView) finder.castView(view, R.id.addzuoye, "field 'addzuoye'");
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
        target.recyclerView = null;
        target.selectclass = null;
        target.classname = null;
        target.addzuoye = null;
    }
}
