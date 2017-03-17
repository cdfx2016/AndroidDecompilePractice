package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class GetClassXiaoXiByIdApi extends BaseApi {
    private String classid;

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public GetClassXiaoXiByIdApi() {
        setMothed("xjObtainClassDetailInfo.action");
        setCancel(true);
        setShowProgress(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).getClassXiaoXiById(getClassid());
    }
}
