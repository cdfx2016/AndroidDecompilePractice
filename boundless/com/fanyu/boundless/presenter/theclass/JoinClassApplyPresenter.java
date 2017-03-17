package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IJoinTeacherView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class JoinClassApplyPresenter extends BasePresenter implements PVBaseListener {
    private IJoinTeacherView mView;
    private BaseModel model = new BaseModelImp(this);

    public JoinClassApplyPresenter(Context mContext, IJoinTeacherView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.isapply(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
    }

    public void onError(ApiException e) {
        this.mView.loadFailure(e.getMessage());
    }
}
