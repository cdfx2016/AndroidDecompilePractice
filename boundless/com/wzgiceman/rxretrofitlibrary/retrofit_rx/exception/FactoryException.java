package com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONPathException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import retrofit2.adapter.rxjava.HttpException;

public class FactoryException {
    private static final String ConnectException_MSG = "连接失败";
    private static final String HttpException_MSG = "网络错误";
    private static final String JSONException_MSG = "fastjeson解析失败";
    private static final String UnknownHostException_MSG = "无法解析该域名";

    public static ApiException analysisExcetpion(Throwable e) {
        ApiException apiException = new ApiException(e);
        if (e instanceof HttpException) {
            apiException.setCode(2);
            apiException.setDisplayMessage(HttpException_MSG);
        } else if (e instanceof HttpTimeException) {
            HttpTimeException exception = (HttpTimeException) e;
            apiException.setCode(5);
            apiException.setDisplayMessage(exception.getMessage());
        } else if ((e instanceof ConnectException) || (e instanceof SocketTimeoutException)) {
            apiException.setCode(2);
            apiException.setDisplayMessage(ConnectException_MSG);
        } else if ((e instanceof JSONPathException) || (e instanceof JSONException) || (e instanceof ParseException)) {
            apiException.setCode(3);
            apiException.setDisplayMessage(JSONException_MSG);
        } else if (e instanceof UnknownHostException) {
            apiException.setCode(6);
            apiException.setDisplayMessage(UnknownHostException_MSG);
        } else {
            apiException.setCode(4);
            apiException.setDisplayMessage(e.getMessage());
        }
        return apiException;
    }
}
