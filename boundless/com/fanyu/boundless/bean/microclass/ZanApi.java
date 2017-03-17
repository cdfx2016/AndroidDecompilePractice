package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class ZanApi extends BaseApi {
    private String type;
    private String userid;
    private String videoid;

    public ZanApi() {
        setMothed("videoRated.action");
        setCancel(true);
    }

    public String getVideoid() {
        return this.videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).zanzan(getVideoid(), getUserid(), getType());
    }
}
