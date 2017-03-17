package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.PraiseIView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class PraisePresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model = new BaseModelImp(this);
    private PraiseIView view;

    public PraisePresenter(Context mContext, PraiseIView view) {
        super(mContext);
        this.view = view;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("praiseSave.action")) {
            this.view.praiseSave(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        } else if (mothead.equals("praiseCancel.action")) {
            this.view.praiseCancel(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        } else if (mothead.equals("isPraise.action")) {
            this.view.praiseIsOrNo(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
    }
}
