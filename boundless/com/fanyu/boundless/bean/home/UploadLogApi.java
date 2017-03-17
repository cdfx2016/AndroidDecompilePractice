package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;

public class UploadLogApi extends BaseApi {
    private File file;
    private String filename;

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

    public UploadLogApi() {
        setMothed("uploadLog.action");
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).uploadLog(getFilename(), Part.createFormData("files", getFile().getName(), RequestBody.create(MediaType.parse("text/txt"), getFile())));
    }
}
