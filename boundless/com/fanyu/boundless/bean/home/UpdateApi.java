package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateApi extends BaseApi {
    private String system;

    public UpdateApi() {
        setMothed("queryVersion.action");
        setShowProgress(false);
        setCancel(true);
    }

    public String getSystem() {
        return this.system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).updateContent(getSystem());
    }
}
