package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod;

public enum DownState {
    START(0),
    DOWN(1),
    PAUSE(2),
    STOP(3),
    ERROR(4),
    FINISH(5);
    
    private int state;

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private DownState(int state) {
        this.state = state;
    }
}
