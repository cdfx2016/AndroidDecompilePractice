package com.fanyu.boundless.bean.theclass;

import java.io.Serializable;

public class student implements Serializable {
    private String parentid;
    private String simg;
    private String snickname;
    private String special;
    private String sremark;
    private String ssex;
    private String userid;

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSnickname() {
        return this.snickname;
    }

    public void setSnickname(String snickname) {
        this.snickname = snickname;
    }

    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getSremark() {
        return this.sremark;
    }

    public void setSremark(String sremark) {
        this.sremark = sremark;
    }

    public String getSsex() {
        return this.ssex;
    }

    public void setSsex(String ssex) {
        this.ssex = ssex;
    }

    public String getSimg() {
        return this.simg;
    }

    public void setSimg(String simg) {
        this.simg = simg;
    }

    public String getParentid() {
        return this.parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }
}
