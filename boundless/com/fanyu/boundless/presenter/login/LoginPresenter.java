package com.fanyu.boundless.presenter.login;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.login.ILoginView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class LoginPresenter extends BasePresenter implements PVBaseListener {
    private ILoginView mView;
    private BaseModel model = new BaseModelImp(this);

    public LoginPresenter(Context mContext, ILoginView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        this.mView.showLoadingDialog();
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("login.action")) {
            System.out.println("login_result ====== " + resulte);
            this.mView.getLogin((Login) JSON.parseObject(resulte, Login.class));
            this.mView.closeLoadingDialog();
        }
    }

    public void onError(ApiException e) {
        System.out.println("onError");
        this.mView.showTip(e.getDisplayMessage());
        this.mView.closeLoadingDialog();
    }
}
