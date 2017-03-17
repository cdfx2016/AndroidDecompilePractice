package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UnshowStuApi extends BaseApi {
    private String itmeid;

    public UnshowStuApi() {
        setMothed("getUnreadStuList");
        setCancel(true);
    }

    public String getItmeid() {
        return this.itmeid;
    }

    public void setItmeid(String itmeid) {
        this.itmeid = itmeid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getUnreadStuList(getItmeid());
    }
}
