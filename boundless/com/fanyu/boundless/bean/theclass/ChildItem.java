package com.fanyu.boundless.bean.theclass;

import java.io.Serializable;

public class ChildItem implements Serializable {
    private String id;
    private boolean ischeck;
    private String studentnumber;
    private String title;

    public boolean isIscheck() {
        return this.ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public ChildItem(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public ChildItem(String title, String id, boolean ischeck) {
        this.title = title;
        this.id = id;
        this.ischeck = ischeck;
    }

    public ChildItem(String title, String id, boolean ischeck, String studentnumber) {
        this.title = title;
        this.id = id;
        this.ischeck = ischeck;
        this.studentnumber = studentnumber;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudentnumber() {
        return this.studentnumber;
    }

    public void setStudentnumber(String studentnumber) {
        this.studentnumber = studentnumber;
    }
}
