package com.fanyu.boundless.bean.theclass;

import java.io.Serializable;
import java.sql.Date;

public class schoolclassentity implements Serializable {
    private String classgrade;
    private String classimg;
    private String classname;
    private int classnumber;
    private String classtype;
    private String classyear;
    private String createname;
    private Date createtime;
    private String erweima;
    private String id;
    private String reason;
    private String schoolid;
    private String schoolname;
    private String state;
    private int studentscount;
    private String stunum;
    private String teanum;
    private String total;
    private String userid;

    public String getSchoolname() {
        return this.schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getTotal() {
        return this.total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCreatename() {
        return this.createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public int getStudentscount() {
        return this.studentscount;
    }

    public void setStudentscount(int studentscount) {
        this.studentscount = studentscount;
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

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassimg() {
        return this.classimg;
    }

    public void setClassimg(String classimg) {
        this.classimg = classimg;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getClassyear() {
        return this.classyear;
    }

    public void setClassyear(String classyear) {
        this.classyear = classyear;
    }

    public String getClasstype() {
        return this.classtype;
    }

    public void setClasstype(String classtype) {
        this.classtype = classtype;
    }

    public String getClassgrade() {
        return this.classgrade;
    }

    public void setClassgrade(String classgrade) {
        this.classgrade = classgrade;
    }

    public int getClassnumber() {
        return this.classnumber;
    }

    public void setClassnumber(int classnumber) {
        this.classnumber = classnumber;
    }

    public String getSchoolid() {
        return this.schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getErweima() {
        return this.erweima;
    }

    public void setErweima(String erweima) {
        this.erweima = erweima;
    }

    public String getStunum() {
        return this.stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public String getTeanum() {
        return this.teanum;
    }

    public void setTeanum(String teanum) {
        this.teanum = teanum;
    }
}
