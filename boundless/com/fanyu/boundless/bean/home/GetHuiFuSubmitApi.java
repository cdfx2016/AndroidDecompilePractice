package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class GetHuiFuSubmitApi extends BaseApi {
    private String biaoji;
    private String classid;
    private String page;
    private String pagesize;
    private String userid;

    public GetHuiFuSubmitApi() {
        setMothed("getHuiFuSubmit.action");
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

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBiaoji() {
        return this.biaoji;
    }

    public void setBiaoji(String biaoji) {
        this.biaoji = biaoji;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getHuiFuSubmit(getPage(), getPagesize(), getClassid(), getUserid(), getBiaoji());
    }
}
