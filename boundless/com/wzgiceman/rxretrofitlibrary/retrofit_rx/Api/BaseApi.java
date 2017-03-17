package com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

public abstract class BaseApi<T> implements Func1<T, String> {
    private String baseUrl = "http://dx.gensaint.com/wuya/";
    private boolean cache = false;
    private boolean cancel = false;
    private int connectionTime = 6;
    private int cookieNetWorkTime = 60;
    private int cookieNoNetWorkTime = 2592000;
    private String mothed;
    private boolean showProgress = true;

    public abstract Observable getObservable(Retrofit retrofit);

    public int getCookieNoNetWorkTime() {
        return this.cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return this.cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getMothed() {
        return this.mothed;
    }

    public int getConnectionTime() {
        return this.connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setMothed(String mothed) {
        this.mothed = mothed;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return this.baseUrl + this.mothed;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isShowProgress() {
        return this.showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public String call(T httpResult) {
        return httpResult.toString();
    }
}
