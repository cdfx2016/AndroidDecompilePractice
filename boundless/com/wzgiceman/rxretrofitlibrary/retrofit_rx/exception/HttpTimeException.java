package com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception;

public class HttpTimeException extends RuntimeException {
    public static final int CHACHE_TIMEOUT_ERROR = 4100;
    public static final int NO_CHACHE_ERROR = 4099;
    public static final int UNKOWN_ERROR = 4098;

    public HttpTimeException(int resultCode) {
        super(getApiExceptionMessage(resultCode));
    }

    public HttpTimeException(String detailMessage) {
        super(detailMessage);
    }

    private static String getApiExceptionMessage(int code) {
        switch (code) {
            case 4098:
                return "错误：网络错误";
            case 4099:
                return "错误：无缓存数据";
            case CHACHE_TIMEOUT_ERROR /*4100*/:
                return "错误：缓存数据过期";
            default:
                return "错误：未知错误";
        }
    }
}
