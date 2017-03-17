package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddHomeWorkApi extends BaseApi {
    private String banjiid;
    private String gerenid;
    private String hwdescribe;
    private String hwtype;
    private String tittle;
    private String userid;
    private String xiaozuid;

    public AddHomeWorkApi() {
        setMothed("addZuoYeBoBao.action");
        setCancel(true);
    }

    public String getHwdescribe() {
        return this.hwdescribe;
    }

    public void setHwdescribe(String hwdescribe) {
        this.hwdescribe = hwdescribe;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getHwtype() {
        return this.hwtype;
    }

    public void setHwtype(String hwtype) {
        this.hwtype = hwtype;
    }

    public String getTittle() {
        return this.tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBanjiid() {
        return this.banjiid;
    }

    public void setBanjiid(String banjiid) {
        this.banjiid = banjiid;
    }

    public String getXiaozuid() {
        return this.xiaozuid;
    }

    public void setXiaozuid(String xiaozuid) {
        this.xiaozuid = xiaozuid;
    }

    public String getGerenid() {
        return this.gerenid;
    }

    public void setGerenid(String gerenid) {
        this.gerenid = gerenid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).addHomeWork(getHwdescribe(), getUserid(), getHwtype(), getTittle(), getBanjiid(), getXiaozuid(), getGerenid());
    }
}
