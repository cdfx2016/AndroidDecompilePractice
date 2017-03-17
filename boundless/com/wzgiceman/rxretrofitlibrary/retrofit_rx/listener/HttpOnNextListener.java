package com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public interface HttpOnNextListener {
    void onError(ApiException apiException);

    void onNext(String str, String str2);
}
