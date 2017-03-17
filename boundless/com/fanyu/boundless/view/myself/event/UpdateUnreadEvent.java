package com.fanyu.boundless.view.myself.event;

public class UpdateUnreadEvent {
    private String unread;
    private int updatetype;

    public UpdateUnreadEvent(String unread, int updatetype) {
        this.unread = unread;
        this.updatetype = updatetype;
    }

    public String getUnread() {
        return this.unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public int getUpdatetype() {
        return this.updatetype;
    }

    public void setUpdatetype(int updatetype) {
        this.updatetype = updatetype;
    }
}
