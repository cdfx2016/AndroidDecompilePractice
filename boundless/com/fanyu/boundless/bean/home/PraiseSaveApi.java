package com.fanyu.boundless.bean.home;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class PraiseSaveApi extends BaseApi {
    private String itemtype;
    private String questionid;
    private String userid;

    public PraiseSaveApi() {
        setMothed("praiseSave.action");
        setCancel(true);
    }

    public String getQuestionid() {
        return this.questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getItemtype() {
        return this.itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public Observable getObservable(Retrofit retrofit) {
        return ((HttpPostService) retrofit.create(HttpPostService.class)).praiseSave(getQuestionid(), getUserid(), getItemtype());
    }
}
