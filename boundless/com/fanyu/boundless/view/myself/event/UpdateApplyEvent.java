package com.fanyu.boundless.view.myself.event;

import com.fanyu.boundless.bean.theclass.applyentity;

public class UpdateApplyEvent {
    private applyentity entity;
    private int position;

    public UpdateApplyEvent(int position, applyentity entity) {
        this.position = position;
        this.entity = entity;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public applyentity getEntity() {
        return this.entity;
    }

    public void setEntity(applyentity entity) {
        this.entity = entity;
    }
}
