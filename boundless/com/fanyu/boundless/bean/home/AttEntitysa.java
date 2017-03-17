package com.fanyu.boundless.bean.home;

import java.io.Serializable;

public class AttEntitysa implements Serializable {
    private String createtime = "";
    private String customname = "";
    private String filename = "";
    private int filetype = 0;
    private String id = "";
    private String itemid = "";
    private String itemtype = "";
    private String originalfilename = "";
    private String remark = "";

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
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
