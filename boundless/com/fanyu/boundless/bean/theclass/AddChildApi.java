package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddChildApi extends BaseApi {
    private String parentid;
    private String sex;
    private String simg;
    private String sname;

    public String getSname() {
        return this.sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSimg() {
        return this.simg;
    }

    public void setSimg(String simg) {
        this.simg = simg;
    }

    public String getParentid() {
        return this.parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public AddChildApi() {
        setMothed("addChild.action");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).addChild(getSname(), getSex(), getSimg(), getParentid());
    }
}
