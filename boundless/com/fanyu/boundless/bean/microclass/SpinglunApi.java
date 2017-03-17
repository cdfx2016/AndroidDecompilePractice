package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class SpinglunApi extends BaseApi {
    private String page;
    private String pagesize;
    private String videoid;

    public SpinglunApi() {
        setMothed("queryCommentByVideoid.action");
        setCancel(true);
    }

    public String getVideoid() {
        return this.videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
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
        return ((HttpPostService) retrofit.create(HttpPostService.class)).xjObtainVedioComment(getVideoid(), getPage(), getPagesize());
    }
}
