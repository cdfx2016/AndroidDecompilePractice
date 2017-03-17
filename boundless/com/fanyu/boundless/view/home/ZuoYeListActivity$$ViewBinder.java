package com.fanyu.boundless.view.home;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class ZuoYeListActivity$$ViewBinder<T extends ZuoYeListActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.recyclerView = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick();
            }
        });
    }

    public void unbind(T target) {
        target.recyclerView = null;
    }
}
