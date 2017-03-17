package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class DongTaiApi extends BaseApi {
    private String dailytypeid;
    private String page;
    private String pagesize;
    private String userid;
    private String username;

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getDailytypeid() {
        return this.dailytypeid;
    }

    public void setDailytypeid(String dailytypeid) {
        this.dailytypeid = dailytypeid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DongTaiApi() {
        setMothed("getDongTai.action");
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getDongTai(getUserid(), getPage(), getPagesize(), getDailytypeid(), getUsername());
    }
}
