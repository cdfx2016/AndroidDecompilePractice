package com.fanyu.boundless.presenter.welcome;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.Update;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.welcome.IWelcomeView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class WelcomePresenter extends BasePresenter implements PVBaseListener {
    private IWelcomeView mView;
    private BaseModel model = new BaseModelImp(this);

    public WelcomePresenter(Context mContext, IWelcomeView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("queryVersion.action")) {
            this.mView.getVersion((Update) JSON.parseObject(resulte, Update.class));
        } else if (mothead.equals("login.action")) {
            this.mView.getLogin((Login) JSON.parseObject(resulte, Login.class));
        }
    }

    public void onError(ApiException e) {
        this.mView.showTip(e.getMessage());
    }
}
