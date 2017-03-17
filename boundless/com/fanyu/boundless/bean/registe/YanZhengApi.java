package com.fanyu.boundless.bean.registe;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class YanZhengApi extends BaseApi {
    private String phoneNumber;

    public YanZhengApi() {
        setMothed("loginyanzheng.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getYanZheng(getPhoneNumber());
    }
}
