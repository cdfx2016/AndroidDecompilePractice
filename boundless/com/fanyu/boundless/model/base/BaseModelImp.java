package com.fanyu.boundless.model.base;

import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.HttpManager;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.HttpOnNextListener;

public class BaseModelImp implements BaseModel, HttpOnNextListener {
    private PVBaseListener mListenter;

    public BaseModelImp(PVBaseListener mListenter) {
        this.mListenter = mListenter;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        new HttpManager(this, rxAppCompatActivity).doHttpDeal(baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mListenter.onNext(resulte, mothead);
    }

    public void onError(ApiException e) {
        this.mListenter.onError(e);
    }
}
