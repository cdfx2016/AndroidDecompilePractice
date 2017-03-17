package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IGetDongtaiView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class DongtaiPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model;
    private int state = 0;
    private IGetDongtaiView view;

    public DongtaiPresenter(Context mContext, IGetDongtaiView view) {
        super(mContext);
        this.view = view;
        this.model = new BaseModelImp(this);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        super.startPost(rxAppCompatActivity, baseApi, state);
        this.state = state;
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getDongTai.action")) {
            this.view.getDongtai(JSON.parseArray(resulte, DongTaiEntity.class), this.state);
            this.view.closeLoadingDialog();
        } else if (mothead.equals("getMyClass.action")) {
            this.view.getMyClass(JSON.parseArray(resulte, schoolclassentity.class));
            this.view.closeLoadingDialog();
        } else if (mothead.equals("getGerenShuoShuo.action")) {
            this.view.getGerenShuoShuo(JSON.parseArray(resulte, DongTaiEntity.class), this.state);
            this.view.closeLoadingDialog();
        } else if (mothead.equals("pinglunsave.action")) {
            this.view.addPinglun(resulte);
        } else if (mothead.equals("praiseSave.action")) {
            this.view.praiseSave(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        } else if (mothead.equals("praiseCancel.action")) {
            this.view.praiseCancel(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        } else if (mothead.equals("isPraise.action")) {
            this.view.praiseIsOrNo(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        } else if (mothead.equals("deleteDongTai.action")) {
            this.view.deleteDongtai(resulte.substring(1, resulte.length() - 1).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        this.view.closeLoadingDialog();
        if (e.getCode() != 4) {
            System.out.println("CodeException ==== " + e.getCode());
            System.out.println(e.getDisplayMessage());
            this.view.showTip(e.getDisplayMessage());
        }
    }
}
