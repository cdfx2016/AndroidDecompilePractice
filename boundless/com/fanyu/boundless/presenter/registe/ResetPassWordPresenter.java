package com.fanyu.boundless.presenter.registe;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.registe.IResetPassWordView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class ResetPassWordPresenter extends BasePresenter implements PVBaseListener {
    private IResetPassWordView mView;
    private BaseModel model = new BaseModelImp(this);

    public ResetPassWordPresenter(Context mContext, IResetPassWordView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        String isreset = resulte.substring(0, resulte.length());
        resulte = resulte.replaceAll("\"", "");
        this.mView.isreset(isreset);
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
