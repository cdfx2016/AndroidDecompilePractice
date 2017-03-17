package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.ICreateClassView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class CreateClassPresenter extends BasePresenter implements PVBaseListener {
    private ICreateClassView mView;
    private BaseModel model = new BaseModelImp(this);

    public CreateClassPresenter(Context mContext, ICreateClassView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
        this.mView.showLoadingDialog();
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("uploadImage.action")) {
            String isupload = resulte.substring(0, resulte.length()).replaceAll("\"", "");
            this.mView.closeLoadingDialog();
            this.mView.isupload(isupload);
        } else if (mothead.equals("createSchoolClass.action")) {
            String iscreate = resulte.substring(0, resulte.length()).replaceAll("\"", "");
            this.mView.closeLoadingDialog();
            this.mView.iscreate(iscreate);
        }
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
