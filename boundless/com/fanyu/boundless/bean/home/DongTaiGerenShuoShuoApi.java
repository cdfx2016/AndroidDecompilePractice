package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class DongTaiGerenShuoShuoApi extends BaseApi {
    private String dailyid;
    private String userid;

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDailyid() {
        return this.dailyid;
    }

    public void setDailyid(String dailyid) {
        this.dailyid = dailyid;
    }

    public DongTaiGerenShuoShuoApi() {
        setMothed("getGerenShuoShuo.action");
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getGerenShuoShuo(getDailyid(), getUserid());
    }
}
