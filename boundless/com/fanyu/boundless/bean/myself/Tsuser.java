package com.fanyu.boundless.bean.myself;

import java.io.Serializable;

public class Tsuser implements Serializable {
    private int age;
    private String birthday;
    private String id;
    private String nickname;
    private String password;
    private String phonenumber;
    private String sex;
    private String userimg;
    private String username;

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Tsuser(String id, String username, String nickname, String password, String sex, int age, String phonenumber, String userimg, String birthday) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.nickname = nickname;
        this.phonenumber = phonenumber;
        this.password = password;
        this.age = age;
        this.userimg = userimg;
        this.birthday = birthday;
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

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUserimg() {
        return this.userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }
}
