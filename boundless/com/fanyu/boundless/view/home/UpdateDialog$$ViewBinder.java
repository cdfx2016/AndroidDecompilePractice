package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class UpdateDialog$$ViewBinder<T extends UpdateDialog> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.ll_root, "field 'llRoot' and method 'onClick'");
        target.llRoot = (LinearLayout) finder.castView(view, R.id.ll_root, "field 'llRoot'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.title = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.title, "field 'title'"), R.id.title, "field 'title'");
        target.rvContent = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.rv_content, "field 'rvContent'"), R.id.rv_content, "field 'rvContent'");
        view = (View) finder.findRequiredView(source, R.id.btn_ok, "field 'btnOk' and method 'onClick'");
        target.btnOk = (Button) finder.castView(view, R.id.btn_ok, "field 'btnOk'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.btn_cancel, "field 'btnCancel' and method 'onClick'");
        target.btnCancel = (Button) finder.castView(view, R.id.btn_cancel, "field 'btnCancel'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.ll_son, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.llRoot = null;
        target.title = null;
        target.rvContent = null;
        target.btnOk = null;
        target.btnCancel = null;
    }
}
