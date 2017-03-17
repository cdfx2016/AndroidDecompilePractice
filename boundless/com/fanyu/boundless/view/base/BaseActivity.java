package com.fanyu.boundless.view.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.widget.MyProgressDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseActivity<P extends BasePresenter> extends RxAppCompatActivity implements IBaseView {
    protected Context mContext;
    protected P mPresenter;
    protected MyProgressDialog progressDialog;

    protected abstract void init();

    protected abstract void initPresenter();

    protected abstract void initView();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initView();
        ButterKnife.bind((Activity) this);
        initPresenter();
        init();
    }

    public void showTip(String msg) {
        Toast.makeText(this, msg, 0).show();
    }

    public void loadFailure(String errorMsg) {
    }

    public void loadSuccess(Object object) {
    }

    public void showLoadingDialog() {
        if (this.progressDialog == null) {
            this.progressDialog = new MyProgressDialog(this);
        }
        if (!isFinishing() && !this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
    }

    public void closeLoadingDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        } else if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        } else {
            return onTouchEvent(ev);
        }
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v == null || !(v instanceof EditText)) {
            return false;
        }
        int[] leftTop = new int[]{0, 0};
        v.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + v.getHeight();
        int right = left + v.getWidth();
        if (event.getX() <= ((float) left) || event.getX() >= ((float) right) || event.getY() <= ((float) top) || event.getY() >= ((float) bottom)) {
            return true;
        }
        return false;
    }
}
