package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class PosthomeworkentityApi extends BaseApi {
    private String classid;
    private String hwtype;
    private String page;
    private String pagesize;
    private String userid;
    private String version;

    public PosthomeworkentityApi() {
        setMothed("getMyZuoYeList.action");
        setCancel(true);
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

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHwtype() {
        return this.hwtype;
    }

    public void setHwtype(String hwtype) {
        this.hwtype = hwtype;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getMyZuoYeList(getPage(), getPagesize(), getUserid(), getHwtype(), getClassid(), getVersion());
    }
}
