package com.fanyu.boundless.bean.home;

import java.io.Serializable;
import java.util.List;

public class DongTaiEntity implements Serializable {
    private String address;
    private List<AttEntitysa> att;
    private String biaojiString;
    private String biaoqian;
    private String classname;
    private String content;
    private String createtime;
    private List<Dailyreply> dailreply;
    private String day;
    private String id;
    private String isfriend;
    private String ispraise;
    private String month;
    private String praise;
    private String titleimg;
    private String userid;
    private String userimg;
    private String username;
    private String userphone;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitleimg() {
        return this.titleimg;
    }

    public void setTitleimg(String titleimg) {
        this.titleimg = titleimg;
    }

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPraise() {
        return this.praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBiaoqian() {
        return this.biaoqian;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getUserphone() {
        return this.userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getIsfriend() {
        return this.isfriend;
    }

    public void setIsfriend(String isfriend) {
        this.isfriend = isfriend;
    }

    public String getBiaojiString() {
        return this.biaojiString;
    }

    public void setBiaojiString(String biaojiString) {
        this.biaojiString = biaojiString;
    }

    public String getIspraise() {
        return this.ispraise;
    }

    public void setIspraise(String ispraise) {
        this.ispraise = ispraise;
    }

    public List<Dailyreply> getDailreply() {
        return this.dailreply;
    }

    public void setDailreply(List<Dailyreply> dailreply) {
        this.dailreply = dailreply;
    }

    public List<AttEntitysa> getAtt() {
        return this.att;
    }

    public void setAtt(List<AttEntitysa> att) {
        this.att = att;
    }
}
