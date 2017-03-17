package com.fanyu.boundless.bean.login;

import java.io.Serializable;

public class Login implements Serializable {
    private static final String TAG = "Login";
    private String classid;
    private String classname;
    private String id;
    private String nickname;
    private String oldid;
    private String password;
    private String sex;
    private long status = -1;
    private String userimg;
    private String username;
    private String usertype;
    private String x;
    private String y;

    public String getClassid() {
        return this.classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getX() {
        return this.x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return this.y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getOldid() {
        return this.oldid;
    }

    public void setOldid(String oldid) {
        this.oldid = oldid;
    }

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

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

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
