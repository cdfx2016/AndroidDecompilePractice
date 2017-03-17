package com.fanyu.boundless.bean.upload;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

public class UploadApi extends BaseApi {
    private File file;
    private String filename;

    public UploadApi() {
        setMothed("uploadImage.action");
        setShowProgress(true);
        setCancel(true);
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).uploadImage(getFilename(), Part.createFormData("files", getFile().getName(), RequestBody.create(MediaType.parse("image/jpg"), getFile())));
    }
}
