package com.fanyu.boundless.view.registe;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class ZhuCeYanZhengActivity$$ViewBinder<T extends ZhuCeYanZhengActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.login_request_code_btn, "field 'loginRequestCodeBtn' and method 'onClick'");
        target.loginRequestCodeBtn = (Button) finder.castView(view, R.id.login_request_code_btn, "field 'loginRequestCodeBtn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.loginInputPhoneEt = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.login_input_phone_et, "field 'loginInputPhoneEt'"), R.id.login_input_phone_et, "field 'loginInputPhoneEt'");
        target.loginInputCodeEt = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.login_input_code_et, "field 'loginInputCodeEt'"), R.id.login_input_code_et, "field 'loginInputCodeEt'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.login_commit_btn, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.loginRequestCodeBtn = null;
        target.loginInputPhoneEt = null;
        target.loginInputCodeEt = null;
    }
}
