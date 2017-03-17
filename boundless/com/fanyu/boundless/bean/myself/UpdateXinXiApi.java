package com.fanyu.boundless.bean.myself;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateXinXiApi extends BaseApi {
    private String fileName;
    private String fileValue;
    private String id;
    private String tableName;

    public UpdateXinXiApi() {
        setMothed("editFile.action");
        setShowProgress(true);
        setCancel(true);
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileValue() {
        return this.fileValue;
    }

    public void setFileValue(String fileValue) {
        this.fileValue = fileValue;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = (HttpPostService) retrofit.create(HttpPostService.class);
        System.out.println(getTableName() + getFileName() + getFileValue() + getId());
        return httpPostService.updateXinxi(getTableName(), getFileName(), getFileValue(), getId());
    }
}
