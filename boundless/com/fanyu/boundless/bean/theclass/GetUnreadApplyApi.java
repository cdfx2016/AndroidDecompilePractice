package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUnreadApplyApi extends BaseApi {
    private String userid;

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public GetUnreadApplyApi() {
        setMothed("getUnreadMessage.action");
        setCancel(true);
        setShowProgress(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getUnreadMessage(getUserid());
    }
}
