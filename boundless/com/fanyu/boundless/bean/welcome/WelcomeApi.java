package com.fanyu.boundless.bean.welcome;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class WelcomeApi extends BaseApi {
    public WelcomeApi() {
        setMothed("getVersion.action");
        setShowProgress(false);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getVersion();
    }
}
