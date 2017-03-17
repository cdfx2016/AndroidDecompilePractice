package com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception;

public class ApiException extends Exception {
    private int code;
    private String displayMessage;

    public ApiException(Throwable e) {
        super(e);
    }

    public ApiException(Throwable cause, int code, String showMsg) {
        super(showMsg, cause);
        setCode(code);
        setDisplayMessage(showMsg);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDisplayMessage() {
        return this.displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
}
