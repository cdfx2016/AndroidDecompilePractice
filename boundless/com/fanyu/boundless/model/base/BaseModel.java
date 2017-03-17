package com.fanyu.boundless.model.base;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

public interface BaseModel {
    void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi);
}
