package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class PlayNumApi extends BaseApi {
    private String videoid;

    public PlayNumApi() {
        setMothed("videoPlayed.action");
        setCancel(true);
    }

    public String getVideoid() {
        return this.videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).xjVCheckNum(getVideoid());
    }
}
