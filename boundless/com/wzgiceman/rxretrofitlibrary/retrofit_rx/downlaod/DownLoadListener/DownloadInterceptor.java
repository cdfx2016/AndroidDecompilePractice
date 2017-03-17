package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownLoadListener;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {
    private DownloadProgressListener listener;

    public DownloadInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new DownloadResponseBody(originalResponse.body(), this.listener)).build();
    }
}
