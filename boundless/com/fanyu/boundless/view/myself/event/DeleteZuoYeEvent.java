package com.fanyu.boundless.view.myself.event;

public class DeleteZuoYeEvent {
    private int position;
    private String type;

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

    public DeleteZuoYeEvent(int position, String type) {
        this.position = position;
        this.type = type;
    }
}
