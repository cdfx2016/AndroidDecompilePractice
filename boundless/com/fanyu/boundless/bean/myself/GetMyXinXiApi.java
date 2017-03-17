package com.fanyu.boundless.bean.myself;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class GetMyXinXiApi extends BaseApi {
    private String userid;

    public GetMyXinXiApi() {
        setMothed("findPersonInfo.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getMyXinXi(getUserid());
    }
}
