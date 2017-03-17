package com.fanyu.boundless.bean.microclass;

import java.util.List;

public class VideoTypeEntity {
    private List albumlist;
    private int counts;
    private String createman;
    private Long createtime;
    private String createtime1;
    private String filename;
    private String id;
    private String img;
    private String nickname;
    private String ordernum;
    private String pid;
    private String remark;
    private String type;
    private String typenames;
    private String yesorno;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getOrdernum() {
        return this.ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getCreateman() {
        return this.createman;
    }

    public void setCreateman(String createman) {
        this.createman = createman;
    }

    public Long getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public String getCreatetime1() {
        return this.createtime1;
    }

    public void setCreatetime1(String createtime1) {
        this.createtime1 = createtime1;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List getAlbumlist() {
        return this.albumlist;
    }

    public void setAlbumlist(List albumlist) {
        this.albumlist = albumlist;
    }

    public int getCounts() {
        return this.counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public String getYesorno() {
        return this.yesorno;
    }

    public void setYesorno(String yesorno) {
        this.yesorno = yesorno;
    }

    public String getTypenames() {
        return this.typenames;
    }

    public void setTypenames(String typenames) {
        this.typenames = typenames;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
