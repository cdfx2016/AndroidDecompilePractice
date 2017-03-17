package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class AddGetLeaveNoticeApi extends BaseApi {
    private String classid;
    private String classname;
    private String content;
    private String editcontent;
    private String gstype;
    private String selectsum;
    private String selectzu;
    private String studentnamesum;
    private String tittle;
    private String unselectsum;
    private String userid;
    private String yijian;

    public AddGetLeaveNoticeApi() {
        setMothed("addGetLeaveNotice.action");
        setCancel(true);
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getTittle() {
        return this.tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getEditcontent() {
        return this.editcontent;
    }

    public void setEditcontent(String editcontent) {
        this.editcontent = editcontent;
    }

    public String getYijian() {
        return this.yijian;
    }

    public void setYijian(String yijian) {
        this.yijian = yijian;
    }

    public String getGstype() {
        return this.gstype;
    }

    public void setGstype(String gstype) {
        this.gstype = gstype;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelectsum() {
        return this.selectsum;
    }

    public void setSelectsum(String selectsum) {
        this.selectsum = selectsum;
    }

    public String getSelectzu() {
        return this.selectzu;
    }

    public void setSelectzu(String selectzu) {
        this.selectzu = selectzu;
    }

    public String getUnselectsum() {
        return this.unselectsum;
    }

    public void setUnselectsum(String unselectsum) {
        this.unselectsum = unselectsum;
    }

    public String getStudentnamesum() {
        return this.studentnamesum;
    }

    public void setStudentnamesum(String studentnamesum) {
        this.studentnamesum = studentnamesum;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).AddGetLeaveNotice(getUserid(), getClassid(), getTittle(), getEditcontent(), getYijian(), getGstype(), getClassname(), getContent(), getSelectsum(), getSelectzu(), getUnselectsum(), getStudentnamesum());
    }
}
