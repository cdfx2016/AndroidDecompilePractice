package com.fanyu.boundless.view.registe;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class RegisteActivity$$ViewBinder<T extends RegisteActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.inputName = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.input_name, "field 'inputName'"), R.id.input_name, "field 'inputName'");
        target.inputMima = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.input_mima, "field 'inputMima'"), R.id.input_mima, "field 'inputMima'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.btnregiste, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.inputName = null;
        target.inputMima = null;
    }
}
