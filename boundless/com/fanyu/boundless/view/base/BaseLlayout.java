package com.fanyu.boundless.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.widget.MyProgressDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseLlayout<P extends BasePresenter> extends LinearLayout implements IBaseView {
    protected RxAppCompatActivity mActivity;
    Bundle mBundle;
    protected Context mContext;
    View mMainView;
    protected P mPresenter;
    protected MyProgressDialog progressDialog;

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract void initPresenter();

    public BaseLlayout(Context context) {
        this(context, null);
    }

    public BaseLlayout(Context context, Bundle bundle) {
        super(context);
        this.mMainView = null;
        this.mContext = context;
        this.mBundle = bundle;
        initLayoutView(getLayoutId());
        initPresenter();
        init();
    }

    private void initLayoutView(int layoutid) {
        this.mMainView = LayoutInflater.from(getContext()).inflate(layoutid, this);
        ButterKnife.bind((Object) this, this.mMainView);
    }

    public void showTip(String msg) {
        Toast.makeText(this.mContext, msg, 0).show();
    }

    public void loadFailure(String errorMsg) {
    }

    public void loadSuccess(Object object) {
    }

    public void showLoadingDialog() {
        if (this.progressDialog == null) {
            this.progressDialog = new MyProgressDialog(this.mContext);
            this.progressDialog.setCanceledOnTouchOutside(false);
        }
        if (!this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
    }

    public void closeLoadingDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }
}
