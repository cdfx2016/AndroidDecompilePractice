package com.fanyu.boundless.presenter.microclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.microclass.VideoalbumEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.microclass.IMicroClassSonView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class MicroClassSonPresenter extends BasePresenter implements PVBaseListener {
    private IMicroClassSonView mView;
    private BaseModel model;
    private int state = 0;

    public MicroClassSonPresenter(Context mContext, IMicroClassSonView mView) {
        super(mContext);
        this.mView = mView;
        this.model = new BaseModelImp(this);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.state = state;
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.getVideoList(JSON.parseArray(resulte, VideoalbumEntity.class), this.state);
    }

    public void onError(ApiException e) {
        this.mView.loadFailure(e.getDisplayMessage());
    }
}
