package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IZuoYeListView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class ZuoYeListPresenter extends BasePresenter implements PVBaseListener {
    private IZuoYeListView mView;
    private BaseModel model;
    private int state = 0;

    public ZuoYeListPresenter(Context mContext, IZuoYeListView mView) {
        super(mContext);
        this.mView = mView;
        this.model = new BaseModelImp(this);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        this.state = state;
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.getXiaoXiList(JSON.parseArray(resulte, ClassHuifuEntity.class), this.state);
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
