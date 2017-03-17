package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class JoinClassActivity$$ViewBinder<T extends JoinClassActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.editClassnumber = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.edit_classnumber, "field 'editClassnumber'"), R.id.edit_classnumber, "field 'editClassnumber'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.saoyisao, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.search_schoolclass, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.editClassnumber = null;
    }
}
