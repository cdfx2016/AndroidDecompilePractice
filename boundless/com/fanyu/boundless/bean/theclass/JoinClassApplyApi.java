package com.fanyu.boundless.bean.theclass;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class JoinClassApplyApi extends BaseApi {
    private String beizhu;
    private String classid;
    private String role;
    private String state;
    private String teachername;
    private String userid;
    private String xuehao;
    private String xueke;

    public String getTeachername() {
        return this.teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getBeizhu() {
        return this.beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getXueke() {
        return this.xueke;
    }

    public void setXueke(String xueke) {
        this.xueke = xueke;
    }

    public String getXuehao() {
        return this.xuehao;
    }

    public void setXuehao(String xuehao) {
        this.xuehao = xuehao;
    }

    public JoinClassApplyApi() {
        setMothed("joinClass.action");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).joinClass(getTeachername(), getBeizhu(), getClassid(), getState(), getRole(), getUserid(), getXueke(), getXuehao());
    }
}
