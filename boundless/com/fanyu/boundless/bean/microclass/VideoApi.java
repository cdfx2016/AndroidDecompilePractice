package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class VideoApi extends BaseApi {
    private String albumid;
    private String userid;

    public VideoApi() {
        setMothed("albumMessQuery.action");
        setCancel(true);
    }

    public String getAlbumid() {
        return this.albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).searchWeikeListSecond(getAlbumid(), getUserid());
    }
}
