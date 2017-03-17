package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.ISureLoginView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class SureLoginPresenter extends BasePresenter implements PVBaseListener {
    private ISureLoginView mView;
    private BaseModel model = new BaseModelImp(this);

    public SureLoginPresenter(Context mContext, ISureLoginView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.phoneLoginServlet(resulte.substring(0, resulte.length()));
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
