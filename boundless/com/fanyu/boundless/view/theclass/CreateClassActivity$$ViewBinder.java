package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class CreateClassActivity$$ViewBinder<T extends CreateClassActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.editclassname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editclassname, "field 'editclassname'"), R.id.editclassname, "field 'editclassname'");
        View view = (View) finder.findRequiredView(source, R.id.classimg, "field 'classimg' and method 'onClick'");
        target.classimg = (ImageView) finder.castView(view, R.id.classimg, "field 'classimg'");
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
        ((View) finder.findRequiredView(source, R.id.create, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.editclassname = null;
        target.classimg = null;
    }
}
