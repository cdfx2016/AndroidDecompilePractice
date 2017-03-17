package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class SearchVideoListTop4Api extends BaseApi {
    private String userid;

    public SearchVideoListTop4Api() {
        setMothed("searchVideoListTop4.action");
        setShowProgress(false);
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).searchVideoListTop4(getUserid());
    }
}
