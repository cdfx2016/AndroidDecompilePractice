package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class JoinClassApi extends BaseApi {
    private String classnumber;

    public String getClassnumber() {
        return this.classnumber;
    }

    public void setClassnumber(String classnumber) {
        this.classnumber = classnumber;
    }

    public JoinClassApi() {
        setMothed("searchSchoolClassbyNumber.action");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).searchClassXinXi(getClassnumber());
    }
}
