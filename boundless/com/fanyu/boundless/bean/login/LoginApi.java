package com.fanyu.boundless.bean.login;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class LoginApi extends BaseApi {
    private String mPassWord;
    private String mUserName;

    public LoginApi() {
        setMothed("login.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getmUserName() {
        return this.mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmPassWord() {
        return this.mPassWord;
    }

    public void setmPassWord(String mPassWord) {
        this.mPassWord = mPassWord;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).login(getmUserName(), getmPassWord());
    }
}
