package com.fanyu.boundless.view.login;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class LoginActivity$$ViewBinder<T extends LoginActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.etUsername = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.etUsername, "field 'etUsername'"), R.id.etUsername, "field 'etUsername'");
        target.etPassword = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.etPassword, "field 'etPassword'"), R.id.etPassword, "field 'etPassword'");
        View view = (View) finder.findRequiredView(source, R.id.btnLogin, "field 'btnLogin' and method 'onClick'");
        target.btnLogin = (TextView) finder.castView(view, R.id.btnLogin, "field 'btnLogin'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.btnRegiste, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.forgetmima, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.etUsername = null;
        target.etPassword = null;
        target.btnLogin = null;
    }
}
