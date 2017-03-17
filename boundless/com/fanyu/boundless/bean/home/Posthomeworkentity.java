package com.fanyu.boundless.bean.home;

import java.io.Serializable;
import java.util.List;

public class Posthomeworkentity implements Serializable {
    private List<AttEntitysa> att;
    private String classname;
    private String createtime;
    private List<Dailyreply> dailreply;
    private List<AttEntitysa> file;
    private String hwdescribe;
    private String hwtittle;
    private String hwtype;
    private String id;
    private String isread;
    private String liuyancount;
    private String nickname;
    private String showcount;
    private String stucount;
    private String userid;
    private String userimg;

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public List<AttEntitysa> getFile() {
        return this.file;
    }

    public void setFile(List<AttEntitysa> file) {
        this.file = file;
    }

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

    public String getHwtittle() {
        return this.hwtittle;
    }

    public void setHwtittle(String hwtittle) {
        this.hwtittle = hwtittle;
    }

    public String getHwdescribe() {
        return this.hwdescribe;
    }

    public void setHwdescribe(String hwdescribe) {
        this.hwdescribe = hwdescribe;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getHwtype() {
        return this.hwtype;
    }

    public void setHwtype(String hwtype) {
        this.hwtype = hwtype;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
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

    public String getShowcount() {
        return this.showcount;
    }

    public void setShowcount(String showcount) {
        this.showcount = showcount;
    }

    public String getStucount() {
        return this.stucount;
    }

    public void setStucount(String stucount) {
        this.stucount = stucount;
    }

    public String getLiuyancount() {
        return this.liuyancount;
    }

    public void setLiuyancount(String liuyancount) {
        this.liuyancount = liuyancount;
    }

    public String getIsread() {
        return this.isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }
}
