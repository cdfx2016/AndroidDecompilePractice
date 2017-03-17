package com.fanyu.boundless.bean.home;

import java.io.Serializable;

public class Dailyreply implements Serializable {
    private String dailyid;
    private String id;
    private String pingjiagrade;
    private String replycontent;
    private String replyid;
    private String replytime;
    private String replyuserimg;
    private String replyusername;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDailyid() {
        return this.dailyid;
    }

    public void setDailyid(String dailyid) {
        this.dailyid = dailyid;
    }

    public String getReplycontent() {
        return this.replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public String getPingjiagrade() {
        return this.pingjiagrade;
    }

    public void setPingjiagrade(String pingjiagrade) {
        this.pingjiagrade = pingjiagrade;
    }

    public String getReplytime() {
        return this.replytime;
    }

    public void setReplytime(String replytime) {
        this.replytime = replytime;
    }

    public String getReplyid() {
        return this.replyid;
    }

    public void setReplyid(String replyid) {
        this.replyid = replyid;
    }

    public String getReplyusername() {
        return this.replyusername;
    }

    public void setReplyusername(String replyusername) {
        this.replyusername = replyusername;
    }

    public String getReplyuserimg() {
        return this.replyuserimg;
    }

    public void setReplyuserimg(String replyuserimg) {
        this.replyuserimg = replyuserimg;
    }
}
