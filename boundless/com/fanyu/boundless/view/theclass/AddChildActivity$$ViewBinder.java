package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class AddChildActivity$$ViewBinder<T extends AddChildActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.touxiang, "field 'touxiang' and method 'onClick'");
        target.touxiang = (ImageView) finder.castView(view, R.id.touxiang, "field 'touxiang'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.editname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editname, "field 'editname'"), R.id.editname, "field 'editname'");
        target.sex = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.sex, "field 'sex'"), R.id.sex, "field 'sex'");
        view = (View) finder.findRequiredView(source, R.id.selectsex, "field 'selectsex' and method 'onClick'");
        target.selectsex = (RelativeLayout) finder.castView(view, R.id.selectsex, "field 'selectsex'");
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
        target.touxiang = null;
        target.editname = null;
        target.sex = null;
        target.selectsex = null;
    }
}
