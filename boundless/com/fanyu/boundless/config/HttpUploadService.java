package com.fanyu.boundless.config;

import com.fanyu.boundless.bean.upload.UploadApi;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface HttpUploadService {
    @POST("AppYuFaKu/uploadHeadImg")
    @Multipart
    Observable<UploadApi> uploadImage(@Part("uid") RequestBody requestBody, @Part("auth_key") RequestBody requestBody2, @Part MultipartBody.Part part);
}
