package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddDongtaiApi extends BaseApi {
    private String address;
    private String biaoqian;
    private String biaoqianid;
    private String classid;
    private String classname;
    private String content;
    private String dailytypeid;
    private String userid;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDailytypeid() {
        return this.dailytypeid;
    }

    public void setDailytypeid(String dailytypeid) {
        this.dailytypeid = dailytypeid;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getBiaoqian() {
        return this.biaoqian;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    public String getBiaoqianid() {
        return this.biaoqianid;
    }

    public void setBiaoqianid(String biaoqianid) {
        this.biaoqianid = biaoqianid;
    }

    public AddDongtaiApi() {
        setMothed("shuoshuosave.action");
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).addDongTai(getContent(), getUserid(), getDailytypeid(), getAddress(), getClassid(), getClassname(), getBiaoqian(), getBiaoqianid());
    }
}
