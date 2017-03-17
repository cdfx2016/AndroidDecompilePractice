package com.fanyu.boundless.bean.microclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class VideoTypeApi extends BaseApi {
    public VideoTypeApi() {
        setMothed("queryVideoTypeList.action");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getweimain();
    }
}
