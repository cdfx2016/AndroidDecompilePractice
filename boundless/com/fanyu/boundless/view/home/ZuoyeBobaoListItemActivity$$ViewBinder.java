package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class ZuoyeBobaoListItemActivity$$ViewBinder<T extends ZuoyeBobaoListItemActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.recyclerView = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        View view = (View) finder.findRequiredView(source, R.id.chuli, "field 'chuli' and method 'onClick'");
        target.chuli = (TextView) finder.castView(view, R.id.chuli, "field 'chuli'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.recyclerView = null;
        target.chuli = null;
        target.messageTitle = null;
    }
}
