package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class DeleteStudentApi extends BaseApi {
    private String classid;
    private String userid;

    public DeleteStudentApi() {
        setMothed("xjDeleteStudent.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).xjDeleteStudent(getClassid(), getUserid());
    }
}
