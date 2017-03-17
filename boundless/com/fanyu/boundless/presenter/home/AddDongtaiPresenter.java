package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IAddDongtaiView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class AddDongtaiPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model = new BaseModelImp(this);
    private IAddDongtaiView view;

    public AddDongtaiPresenter(Context mContext, IAddDongtaiView view) {
        super(mContext);
        this.view = view;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getMyClass.action")) {
            this.view.getIMyClassName(JSON.parseArray(resulte, schoolclassentity.class));
        } else if (mothead.equals("shuoshuosave.action")) {
            this.view.addDongtai(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if ("saveAtt.action".equals(mothead)) {
            this.view.addAtt(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if ("uploadImage.action".equals(mothead)) {
            this.view.fileList(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.view.showTip(e.getDisplayMessage());
        }
    }
}
