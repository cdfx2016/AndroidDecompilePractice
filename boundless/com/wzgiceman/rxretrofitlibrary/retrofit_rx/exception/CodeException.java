package com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CodeException {
    public static final int HTTP_ERROR = 2;
    public static final int JSON_ERROR = 3;
    public static final int NETWORD_ERROR = 1;
    public static final int RUNTIME_ERROR = 5;
    public static final int UNKNOWN_ERROR = 4;
    public static final int UNKOWNHOST_ERROR = 6;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CodeEp {
    }
}
