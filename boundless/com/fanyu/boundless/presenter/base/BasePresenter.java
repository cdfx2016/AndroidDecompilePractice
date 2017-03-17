package com.fanyu.boundless.presenter.base;

import android.content.Context;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

public abstract class BasePresenter {
    protected Context mContext;
    protected int mState = 0;

    public BasePresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
    }
}
