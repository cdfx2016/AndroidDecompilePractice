package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.jude.easyrecyclerview.EasyRecyclerView;

public class DongTaiActivity$$ViewBinder<T extends DongTaiActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.recyclerView = (EasyRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        View view = (View) finder.findRequiredView(source, R.id.fabu, "field 'fabu' and method 'onClick'");
        target.fabu = (TextView) finder.castView(view, R.id.fabu, "field 'fabu'");
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
        target.fabu = null;
    }
}
