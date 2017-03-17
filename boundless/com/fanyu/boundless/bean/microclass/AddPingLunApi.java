package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddPingLunApi extends BaseApi {
    private String dailyid;
    private String replycontent;
    private String userid;

    public AddPingLunApi() {
        setMothed("pinglunsave.action");
        setCancel(true);
    }

    public String getDailyid() {
        return this.dailyid;
    }

    public void setDailyid(String dailyid) {
        this.dailyid = dailyid;
    }

    public String getReplycontent() {
        return this.replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).savePinglun(getDailyid(), getReplycontent(), getUserid());
    }
}
