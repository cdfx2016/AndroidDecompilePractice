package com.fanyu.boundless.presenter.microclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.microclass.SpinglunEntity;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.microclass.MicroClassDetailView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class MicroClassDetaiPresenter extends BasePresenter implements PVBaseListener {
    private MicroClassDetailView mView;
    private BaseModel model = new BaseModelImp(this);

    public MicroClassDetaiPresenter(Context mContext, MicroClassDetailView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        super.startPost(rxAppCompatActivity, baseApi, state);
        this.mState = state;
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if ("albumMessQuery.action".equals(mothead)) {
            this.mView.searchWeikeListSecond(JSON.parseArray(resulte, VideoEntity.class));
        } else if ("queryCommentByVideoid.action".equals(mothead)) {
            this.mView.xjObtainVedioComment(JSON.parseArray(resulte, SpinglunEntity.class), this.mState);
        } else if ("videoRated.action".equals(mothead)) {
            this.mView.zanzan(resulte.substring(0, resulte.length()));
        } else if ("pinglunsave.action".equals(mothead)) {
            this.mView.savePinglun(resulte.substring(0, resulte.length()));
        } else if ("videoPlayed.action".equals(mothead)) {
            this.mView.xjVCheckNum(resulte.substring(0, resulte.length()));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
