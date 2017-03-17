package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateGeRenUnreadMessageApi extends BaseApi {
    private String itemid;
    private String rtype;
    private String userid;

    public UpdateGeRenUnreadMessageApi() {
        setMothed("updateUnreadMessage.action");
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRtype() {
        return this.rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).updateUnreadMessage(getUserid(), getRtype(), getItemid());
    }
}
