package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IClassHuifuView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class ClassHuifuPresenter extends BasePresenter implements PVBaseListener {
    private IClassHuifuView mView;
    private BaseModel model;
    private int state = 0;

    public ClassHuifuPresenter(Context mContext, IClassHuifuView iView) {
        super(mContext);
        this.mView = iView;
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
        if (mothead.equals("getZuoYeHuiFu.action")) {
            this.mView.getIClassHuifu(JSON.parseArray(resulte, ClassHuifuEntity.class), this.state);
        } else if (mothead.equals("uploadImage.action")) {
            this.mView.uploadimg(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("deleteGeRenZuoYe.action")) {
            this.mView.isdelete(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("saveCLassLiuYan.action")) {
            this.mView.isadd();
        } else if (mothead.equals("deleteZuoYe.action")) {
            this.mView.isDeleteZuoye(resulte);
        } else if (mothead.equals("updateUnreadMessage.action")) {
            this.mView.updateUnread(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
