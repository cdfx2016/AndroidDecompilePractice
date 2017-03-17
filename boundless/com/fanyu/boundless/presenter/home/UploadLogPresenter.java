package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.UploadLogView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class UploadLogPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model = new BaseModelImp(this);
    private UploadLogView view;

    public UploadLogPresenter(Context mContext, UploadLogView mView) {
        super(mContext);
        this.view = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.view.uploadlog(resulte.substring(0, resulte.length()));
    }

    public void onError(ApiException e) {
        this.view.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.view.showTip(e.getDisplayMessage());
        }
    }
}
