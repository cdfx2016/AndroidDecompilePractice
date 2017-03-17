package com.fanyu.boundless.view.myself.event;

public class UpdateClassNameEvent {
    private String classname;

    public UpdateClassNameEvent(String classname) {
        this.classname = classname;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
