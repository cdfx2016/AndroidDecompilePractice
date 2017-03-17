package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class ApplyXiaoXiApi extends BaseApi {
    private String applyid;
    private String beizhu;
    private String classid;
    private String classname;
    private String classrole;
    private String membername;
    private String role;
    private String state;
    private String userid;
    private String xueke;

    public ApplyXiaoXiApi() {
        setMothed("updateApply.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getApplyid() {
        return this.applyid;
    }

    public void setApplyid(String applyid) {
        this.applyid = applyid;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getXueke() {
        return this.xueke;
    }

    public void setXueke(String xueke) {
        this.xueke = xueke;
    }

    public String getMembername() {
        return this.membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getBeizhu() {
        return this.beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassrole() {
        return this.classrole;
    }

    public void setClassrole(String classrole) {
        this.classrole = classrole;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).updateApply(getApplyid(), getState(), getClassid(), getUserid(), getRole(), getXueke(), getMembername(), getBeizhu(), getClassname(), getClassrole());
    }
}
