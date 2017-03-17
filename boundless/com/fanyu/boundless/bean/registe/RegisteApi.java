package com.fanyu.boundless.bean.registe;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class RegisteApi extends BaseApi {
    private String nickname;
    private String password;
    private String phonenumber;
    private String usertype;

    public RegisteApi() {
        setMothed("register.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = (HttpPostService) retrofit.create(HttpPostService.class);
        System.out.println(getPhonenumber() + getNickname() + getPassword() + getUsertype());
        return httpPostService.registe(getPhonenumber(), getNickname(), getPassword(), getUsertype());
    }
}
