package com.fanyu.boundless.bean.home;

public class Update {
    private String app_size;
    private String app_url;
    private int id;
    private String method;
    private String renew;
    private String system;
    private String version_name;
    private int version_time;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSystem() {
        return this.system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getApp_url() {
        return this.app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getRenew() {
        return this.renew;
    }

    public void setRenew(String renew) {
        this.renew = renew;
    }

    public String getApp_size() {
        return this.app_size;
    }

    public void setApp_size(String app_size) {
        this.app_size = app_size;
    }

    public String getVersion_name() {
        return this.version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_time() {
        return this.version_time;
    }

    public void setVersion_time(int version_time) {
        this.version_time = version_time;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
