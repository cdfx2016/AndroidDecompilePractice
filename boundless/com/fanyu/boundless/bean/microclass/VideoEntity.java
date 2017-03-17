package com.fanyu.boundless.bean.microclass;

import java.io.Serializable;

public class VideoEntity implements Serializable {
    private String albumid;
    private String albumname;
    private String collectcount;
    private String commentcount;
    private String content;
    private String createman;
    private String createtime;
    private String filename;
    private String id;
    private String iscollect;
    private String ispraise;
    private String istread;
    private String level;
    private String nickname;
    private String originalfilename;
    private String playcount;
    private String praisecount;
    private String praisetype;
    private String tagname;
    private String treadcount;
    private String type;
    private String videoname;
    private String vpath;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumid() {
        return this.albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getVideoname() {
        return this.videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreateman() {
        return this.createman;
    }

    public void setCreateman(String createman) {
        this.createman = createman;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPraisecount() {
        return this.praisecount;
    }

    public void setPraisecount(String praisecount) {
        this.praisecount = praisecount;
    }

    public String getTreadcount() {
        return this.treadcount;
    }

    public void setTreadcount(String treadcount) {
        this.treadcount = treadcount;
    }

    public String getPlaycount() {
        return this.playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getCollectcount() {
        return this.collectcount;
    }

    public void setCollectcount(String collectcount) {
        this.collectcount = collectcount;
    }

    public String getAlbumname() {
        return this.albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagname() {
        return this.tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getOriginalfilename() {
        return this.originalfilename;
    }

    public void setOriginalfilename(String originalfilename) {
        this.originalfilename = originalfilename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIstread() {
        return this.istread;
    }

    public void setIstread(String istread) {
        this.istread = istread;
    }

    public String getIspraise() {
        return this.ispraise;
    }

    public void setIspraise(String ispraise) {
        this.ispraise = ispraise;
    }

    public String getIscollect() {
        return this.iscollect;
    }

    public void setIscollect(String iscollect) {
        this.iscollect = iscollect;
    }

    public String getCommentcount() {
        return this.commentcount;
    }

    public void setCommentcount(String commentcount) {
        this.commentcount = commentcount;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getVpath() {
        return this.vpath;
    }

    public void setVpath(String vpath) {
        this.vpath = vpath;
    }

    public String getPraisetype() {
        return this.praisetype;
    }

    public void setPraisetype(String praisetype) {
        this.praisetype = praisetype;
    }
}
