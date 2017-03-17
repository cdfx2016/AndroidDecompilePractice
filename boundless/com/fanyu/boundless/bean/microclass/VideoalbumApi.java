package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class VideoalbumApi extends BaseApi {
    private String page;
    private String pagesize;
    private String typeid;

    public VideoalbumApi() {
        setMothed("queryVideoAlbumList.action");
        setCancel(true);
    }

    public String getTypeid() {
        return this.typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPagesize() {
        return this.pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).WeikeList(getTypeid(), getPage(), getPagesize());
    }
}
