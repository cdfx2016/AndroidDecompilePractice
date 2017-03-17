package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class CreateClassApi extends BaseApi {
    private String classimg;
    private String classname;
    private String userid;
    private String username;

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassimg() {
        return this.classimg;
    }

    public void setClassimg(String classimg) {
        this.classimg = classimg;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CreateClassApi() {
        setMothed("createSchoolClass.action");
        setCancel(true);
        setShowProgress(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).createClass(getClassname(), getClassimg(), getUserid(), getUsername());
    }
}
