package com.fanyu.boundless.presenter.base;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public interface PVBaseListener {
    void onError(ApiException apiException);

    void onNext(String str, String str2);
}
