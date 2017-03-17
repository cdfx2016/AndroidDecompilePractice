package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class SendNoticeApi extends BaseApi {
    private String content;
    private String itemid;
    private String receiveid;
    private String senduserid;
    private String xuyaoid;
    private String zhurenid;

    public SendNoticeApi() {
        setMothed("sendNotice.action");
    }

    public String getSenduserid() {
        return this.senduserid;
    }

    public void setSenduserid(String senduserid) {
        this.senduserid = senduserid;
    }

    public String getReceiveid() {
        return this.receiveid;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getXuyaoid() {
        return this.xuyaoid;
    }

    public void setXuyaoid(String xuyaoid) {
        this.xuyaoid = xuyaoid;
    }

    public String getZhurenid() {
        return this.zhurenid;
    }

    public void setZhurenid(String zhurenid) {
        this.zhurenid = zhurenid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).sendNotice(getSenduserid(), getReceiveid(), getContent(), getItemid(), getXuyaoid(), getZhurenid());
    }
}
