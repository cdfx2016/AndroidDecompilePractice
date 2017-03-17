package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class PhoneReceiveServletApi extends BaseApi {
    private String uuid;

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public PhoneReceiveServletApi() {
        setBaseUrl("http://dx.gensaint.com/");
        setMothed("PhoneReceiveServlet");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).phoneReceiveServlet(getUuid());
    }
}
