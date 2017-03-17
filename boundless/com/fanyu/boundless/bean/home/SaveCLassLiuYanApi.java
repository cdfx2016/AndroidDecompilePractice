package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class SaveCLassLiuYanApi extends BaseApi {
    private String filetype;
    private String itemid;
    private String itemtype;
    private String senduserid;
    private String upResult;
    private String userid;

    public SaveCLassLiuYanApi() {
        setMothed("saveCLassLiuYan.action");
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getSenduserid() {
        return this.senduserid;
    }

    public void setSenduserid(String senduserid) {
        this.senduserid = senduserid;
    }

    public String getUpResult() {
        return this.upResult;
    }

    public void setUpResult(String upResult) {
        this.upResult = upResult;
    }

    public String getItemtype() {
        return this.itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getFiletype() {
        return this.filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).saveCLassLiuYan(getUserid(), getItemid(), getSenduserid(), getUpResult(), getItemtype(), getFiletype());
    }
}
