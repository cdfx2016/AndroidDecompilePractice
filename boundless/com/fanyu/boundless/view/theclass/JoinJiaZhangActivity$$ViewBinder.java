package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class JoinJiaZhangActivity$$ViewBinder<T extends JoinJiaZhangActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.name = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.name, "field 'name'"), R.id.name, "field 'name'");
        target.editxuehao = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editxuehao, "field 'editxuehao'"), R.id.editxuehao, "field 'editxuehao'");
        target.editbeizhu = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editbeizhu, "field 'editbeizhu'"), R.id.editbeizhu, "field 'editbeizhu'");
        View view = (View) finder.findRequiredView(source, R.id.addchild, "field 'addchild' and method 'onClick'");
        target.addchild = (RelativeLayout) finder.castView(view, R.id.addchild, "field 'addchild'");
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
        ((View) finder.findRequiredView(source, R.id.submit, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.name = null;
        target.editxuehao = null;
        target.editbeizhu = null;
        target.addchild = null;
    }
}
