package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class ClassLayout$$ViewBinder<T extends ClassLayout> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.paopao = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.paopao, "field 'paopao'"), R.id.paopao, "field 'paopao'");
        target.isclass = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.isclass, "field 'isclass'"), R.id.isclass, "field 'isclass'");
        target.recyclerView = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        View view = (View) finder.findRequiredView(source, R.id.fabu_textview, "field 'fabuTextview' and method 'onClick'");
        target.fabuTextview = (TextView) finder.castView(view, R.id.fabu_textview, "field 'fabuTextview'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.head_icon, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.createclass, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.joinclass, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.paopao = null;
        target.isclass = null;
        target.recyclerView = null;
        target.fabuTextview = null;
    }
}
