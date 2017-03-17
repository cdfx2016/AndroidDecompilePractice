package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IUpdateClassView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class UpdateClassPresenter extends BasePresenter implements PVBaseListener {
    private IUpdateClassView mView;
    private BaseModel model = new BaseModelImp(this);

    public UpdateClassPresenter(Context mContext, IUpdateClassView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("uploadImage.action")) {
            this.mView.isupload(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("xjModifyClassInfo.action")) {
            this.mView.isupdate(resulte.substring(0, resulte.length()));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
