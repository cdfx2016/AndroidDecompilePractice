package com.fanyu.boundless.bean.theclass;

public class AttEntity {
    private long createtime = System.currentTimeMillis();
    private String customname = "";
    private int duration;
    private String filename = "";
    private int filetype = 0;
    private String itemid;
    private String itemtype;
    private String originalfilename = "";
    private String remark = "";
    private String userid;

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public String getItemtype() {
        return this.itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalfilename() {
        return this.originalfilename;
    }

    public void setOriginalfilename(String originalfilename) {
        this.originalfilename = originalfilename;
    }

    public String getCustomname() {
        return this.customname;
    }

    public void setCustomname(String customname) {
        this.customname = customname;
    }

    public long getCreatetime() {
        return this.createtime;
    }

    public void setCreattime(long createtime) {
        this.createtime = createtime;
    }

    public int getFiletype() {
        return this.filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
