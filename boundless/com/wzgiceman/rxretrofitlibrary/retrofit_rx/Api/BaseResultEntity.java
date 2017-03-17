package com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api;

public class BaseResultEntity {
    private String data;
    private String msg;
    private int ret;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRet() {
        return this.ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
