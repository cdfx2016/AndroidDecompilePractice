package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateClassApi extends BaseApi {
    private String classgrade;
    private String classid;
    private String classimg;
    private String classname;
    private String schoolname;

    public UpdateClassApi() {
        setMothed("xjModifyClassInfo.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassimg() {
        return this.classimg;
    }

    public void setClassimg(String classimg) {
        this.classimg = classimg;
    }

    public String getClassgrade() {
        return this.classgrade;
    }

    public void setClassgrade(String classgrade) {
        this.classgrade = classgrade;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSchoolname() {
        return this.schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).xjModifyClassInfo(getClassid(), getClassimg(), getClassgrade(), getClassname(), getSchoolname());
    }
}
