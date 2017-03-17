package com.fanyu.boundless.bean.home;

import java.io.Serializable;
import java.util.List;

public class ClassHuifuEntity implements Serializable {
    private List<AttEntitysa> att;
    private String atttype;
    private String content;
    private String createtime;
    private List<ClassHuifuEntity> huifu;
    private String id;
    private String isread;
    private String itemid;
    private String itemtype;
    private String liuyancount;
    private int mytype;
    private String nickname;
    private String recieveid;
    private String showcount;
    private String stucount;
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

    public String getItemid() {
        return this.itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getAtttype() {
        return this.atttype;
    }

    public void setAtttype(String atttype) {
        this.atttype = atttype;
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

    public String getItemtype() {
        return this.itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getRecieveid() {
        return this.recieveid;
    }

    public void setRecieveid(String recieveid) {
        this.recieveid = recieveid;
    }

    public String getIsread() {
        return this.isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getShowcount() {
        return this.showcount;
    }

    public void setShowcount(String showcount) {
        this.showcount = showcount;
    }

    public String getLiuyancount() {
        return this.liuyancount;
    }

    public void setLiuyancount(String liuyancount) {
        this.liuyancount = liuyancount;
    }

    public String getStucount() {
        return this.stucount;
    }

    public void setStucount(String stucount) {
        this.stucount = stucount;
    }

    public List<ClassHuifuEntity> getHuifu() {
        return this.huifu;
    }

    public void setHuifu(List<ClassHuifuEntity> huifu) {
        this.huifu = huifu;
    }

    public List<AttEntitysa> getAtt() {
        return this.att;
    }

    public void setAtt(List<AttEntitysa> att) {
        this.att = att;
    }

    public int getMytype() {
        return this.mytype;
    }

    public void setMytype(int mytype) {
        this.mytype = mytype;
    }
}
