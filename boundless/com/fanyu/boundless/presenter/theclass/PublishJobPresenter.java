package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.PublishJobView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class PublishJobPresenter extends BasePresenter implements PVBaseListener {
    private PublishJobView mView;
    private BaseModel model = new BaseModelImp(this);

    public PublishJobPresenter(Context mContext, PublishJobView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if ("addZuoYeBoBao.action".equals(mothead)) {
            this.mView.publishJob(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if ("uploadImage.action".equals(mothead)) {
            this.mView.fileList(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("saveAtt.action")) {
            this.mView.addAtt("true");
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.closeLoadingDialog();
        }
    }
}
