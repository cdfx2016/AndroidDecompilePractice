package com.fanyu.boundless.view.myself.event;

public class UpdateZuoYeAdapterEvent {
    private String liuyancount;
    private int position;
    private String showcount;
    private String type;

    public String getShowcount() {
        return this.showcount;
    }

    public void setShowcount(String showcount) {
        this.showcount = showcount;
    }

    public String getLiuyancount() {
        return this.liuyancount;
    }

    public void setLiuyancount(String liuyancount) {
        this.liuyancount = liuyancount;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UpdateZuoYeAdapterEvent(int position, String showcount, String liuyancount, String type) {
        this.position = position;
        this.showcount = showcount;
        this.liuyancount = liuyancount;
        this.type = type;
    }
}
