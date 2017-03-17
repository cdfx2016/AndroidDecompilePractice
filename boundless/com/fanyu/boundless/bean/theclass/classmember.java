package com.fanyu.boundless.bean.theclass;

import java.io.Serializable;

public class classmember implements Serializable {
    private String beizhu;
    private String classid;
    private String membername;
    private String role;
    private String userid;
    private String userimg;
    private String xueke;

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getXueke() {
        return this.xueke;
    }

    public void setXueke(String xueke) {
        this.xueke = xueke;
    }

    public String getBeizhu() {
        return this.beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMembername() {
        return this.membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
