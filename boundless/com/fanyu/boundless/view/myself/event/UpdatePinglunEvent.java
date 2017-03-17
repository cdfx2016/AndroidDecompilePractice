package com.fanyu.boundless.view.myself.event;

public class UpdatePinglunEvent {
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UpdatePinglunEvent(int type) {
        this.type = type;
    }
}
