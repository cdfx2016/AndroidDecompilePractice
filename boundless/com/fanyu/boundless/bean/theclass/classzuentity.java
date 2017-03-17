package com.fanyu.boundless.bean.theclass;

import java.io.Serializable;
import java.util.List;

public class classzuentity implements Serializable {
    private String id;
    private List<student> slist;
    private String zuname;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZuname() {
        return this.zuname;
    }

    public void setZuname(String zuname) {
        this.zuname = zuname;
    }

    public List<student> getSlist() {
        return this.slist;
    }

    public void setSlist(List<student> slist) {
        this.slist = slist;
    }
}
