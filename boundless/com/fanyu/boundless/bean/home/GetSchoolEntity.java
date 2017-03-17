package com.fanyu.boundless.bean.home;

import java.io.Serializable;

public class GetSchoolEntity implements Serializable {
    private String classid;
    private String classname;
    private String createtime;
    private String daoxiao;
    private String gscontent;
    private String gstype;
    private String id;
    private String nickname;
    private String stuname;
    private String tittle;
    private String userid;
    private String userimg;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTittle() {
        return this.tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getGscontent() {
        return this.gscontent;
    }

    public void setGscontent(String gscontent) {
        this.gscontent = gscontent;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getGstype() {
        return this.gstype;
    }

    public void setGstype(String gstype) {
        this.gstype = gstype;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStuname() {
        return this.stuname;
    }

    public void setStuname(String stuname) {
        this.stuname = stuname;
    }

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getDaoxiao() {
        return this.daoxiao;
    }

    public void setDaoxiao(String daoxiao) {
        this.daoxiao = daoxiao;
    }
}
