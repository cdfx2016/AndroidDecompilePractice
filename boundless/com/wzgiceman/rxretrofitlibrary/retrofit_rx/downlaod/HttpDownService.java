package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface HttpDownService {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String str, @Url String str2);
}
