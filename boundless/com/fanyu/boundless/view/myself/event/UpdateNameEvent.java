package com.fanyu.boundless.view.myself.event;

public class UpdateNameEvent {
    public String updatenme;

    public UpdateNameEvent(String updatenme) {
        this.updatenme = updatenme;
    }

    public String getUpdatenme() {
        return this.updatenme;
    }

    public void setUpdatenme(String updatenme) {
        this.updatenme = updatenme;
    }
}
