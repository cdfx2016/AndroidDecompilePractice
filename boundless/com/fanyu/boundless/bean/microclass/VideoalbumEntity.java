package com.fanyu.boundless.bean.microclass;

import java.util.List;

public class VideoalbumEntity {
    private String createtime;
    private String fileid;
    private String filename;
    private String id;
    private String name;
    private String ordernum;
    private String pid;
    private String remark;
    private String type;
    private String typeid;
    private String userid;
    private String username;
    private List videolist;

    public String getFileid() {
        return this.fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List getVideolist() {
        return this.videolist;
    }

    public void setVideolist(List videolist) {
        this.videolist = videolist;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeid() {
        return this.typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrdernum() {
        return this.ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
