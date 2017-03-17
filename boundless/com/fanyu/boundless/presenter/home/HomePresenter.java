package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.maincount;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IHomeView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class HomePresenter extends BasePresenter implements PVBaseListener {
    private IHomeView mView;
    private BaseModel model = new BaseModelImp(this);

    public HomePresenter(Context mContext, IHomeView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getMainUnreadMessage.action")) {
            this.mView.getUnreadMessage((maincount) JSON.parseObject(resulte, maincount.class));
        } else if (mothead.equals("searchVideoListTop4.action")) {
            this.mView.searchVideoListTop4(JSON.parseArray(resulte, VideoEntity.class));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
