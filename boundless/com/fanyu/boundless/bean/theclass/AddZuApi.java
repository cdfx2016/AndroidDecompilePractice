package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddZuApi extends BaseApi {
    private String classid;
    private String studentid;
    private String zuname;

    public AddZuApi() {
        setMothed("addXiaoZu.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getStudentid() {
        return this.studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getZuname() {
        return this.zuname;
    }

    public void setZuname(String zuname) {
        this.zuname = zuname;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).addClassZu(getStudentid(), getZuname(), getClassid());
    }
}
