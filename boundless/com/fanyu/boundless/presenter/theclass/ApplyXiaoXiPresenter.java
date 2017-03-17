package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IApplyXiaoXiView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class ApplyXiaoXiPresenter extends BasePresenter implements PVBaseListener {
    private IApplyXiaoXiView mView;
    private BaseModel model = new BaseModelImp(this);

    public ApplyXiaoXiPresenter(Context mContext, IApplyXiaoXiView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        String result;
        if (mothead.equals("updateApply.action")) {
            result = resulte.substring(0, resulte.length());
            this.mView.updateApply(resulte.replaceAll("\"", ""));
        } else if (mothead.equals("SelectMyRole.action")) {
            result = resulte.substring(0, resulte.length());
            this.mView.selectMyRole(resulte.replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
