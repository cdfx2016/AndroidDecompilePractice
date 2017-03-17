package com.fanyu.boundless.bean.registe;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class ResetPassWordApi extends BaseApi {
    private String fileName;
    private String fileValue;
    private String tableName;
    private String telephone;

    public ResetPassWordApi() {
        setMothed("editFilePassWord.action");
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

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = (HttpPostService) retrofit.create(HttpPostService.class);
        System.out.println(getTableName() + getFileName() + getFileValue() + getTelephone());
        return httpPostService.resetPassWord(getTableName(), getFileName(), getFileValue(), getTelephone());
    }
}
