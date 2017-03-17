package com.fanyu.boundless.bean.myself;

import com.fanyu.boundless.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import retrofit2.Retrofit;
import rx.Observable;

public class QuestionApi extends BaseApi {
    private String questioncontent;

    public String getQuestioncontent() {
        return this.questioncontent;
    }

    public void setQuestioncontent(String questioncontent) {
        this.questioncontent = questioncontent;
    }

    public QuestionApi() {
        setMothed("addQuestion.action");
        setShowProgress(true);
        setCancel(true);
    }

    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = (HttpPostService) retrofit.create(HttpPostService.class);
        System.out.println(getQuestioncontent());
        return httpPostService.addQuestion(getQuestioncontent());
    }
}
