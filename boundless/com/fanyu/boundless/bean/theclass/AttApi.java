package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AttApi extends BaseApi {
    private String filename;
    private String idid;
    private String itemtype;
    private String userid;

    public AttApi() {
        setMothed("saveAtt.action");
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIdid() {
        return this.idid;
    }

    public void setIdid(String idid) {
        this.idid = idid;
    }

    public String getItemtype() {
        return this.itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).saveAtts(getFilename(), getIdid(), getItemtype(), getUserid());
    }
}
