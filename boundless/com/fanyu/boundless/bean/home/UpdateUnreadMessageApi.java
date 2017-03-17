package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateUnreadMessageApi extends BaseApi {
    private String rtype;
    private String userid;

    public String getRtype() {
        return this.rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public UpdateUnreadMessageApi() {
        setMothed("updateUnreadMessageGet.action");
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).updateUnreadMessageGet(getUserid(), getRtype());
    }
}
