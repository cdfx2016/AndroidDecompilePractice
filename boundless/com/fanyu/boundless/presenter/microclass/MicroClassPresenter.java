package com.fanyu.boundless.presenter.microclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.microclass.VideoTypeEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.microclass.IMicroClassView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class MicroClassPresenter extends BasePresenter implements PVBaseListener {
    private IMicroClassView mView;
    private BaseModel model = new BaseModelImp(this);

    public MicroClassPresenter(Context mContext, IMicroClassView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.gettitlelist(JSON.parseArray(resulte, VideoTypeEntity.class));
        this.mView.closeLoadingDialog();
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
