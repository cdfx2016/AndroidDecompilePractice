package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class PhoneLoginServletApi extends BaseApi {
    private String uname;
    private String upwd;
    private String uuid;

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUname() {
        return this.uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpwd() {
        return this.upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }

    public PhoneLoginServletApi() {
        setBaseUrl("http://dx.gensaint.com/");
        setMothed("PhoneLoginServlet");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).PhoneLoginServlet(getUuid(), getUname(), getUpwd());
    }
}
