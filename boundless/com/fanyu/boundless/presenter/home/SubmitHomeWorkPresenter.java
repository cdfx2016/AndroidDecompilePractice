package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.ISubmitHomeWorkView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class SubmitHomeWorkPresenter extends BasePresenter implements PVBaseListener {
    private ISubmitHomeWorkView mView;
    private BaseModel model;
    private int state = 0;

    public SubmitHomeWorkPresenter(Context mContext, ISubmitHomeWorkView mView) {
        super(mContext);
        this.mView = mView;
        this.model = new BaseModelImp(this);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.state = state;
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getHuiFuSubmit.action")) {
            this.mView.getZuoyeList(JSON.parseArray(resulte, ClassHuifuEntity.class), this.state);
        } else if (mothead.equals("sendNotice.action")) {
            this.mView.issend();
        } else if (mothead.equals("uploadImage.action")) {
            this.mView.uploadimg(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("deleteGeRenZuoYe.action")) {
            this.mView.isdelete(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("saveCLassLiuYan.action")) {
            this.mView.isadd();
        } else if (mothead.equals("updateUnreadZuoYe.action")) {
            this.mView.updateUnread();
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
