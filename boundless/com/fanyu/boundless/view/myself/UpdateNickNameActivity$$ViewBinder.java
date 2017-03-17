package com.fanyu.boundless.view.myself;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class UpdateNickNameActivity$$ViewBinder<T extends UpdateNickNameActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.editContext = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.edit_context, "field 'editContext'"), R.id.edit_context, "field 'editContext'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.txt_save, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.editContext = null;
    }
}
