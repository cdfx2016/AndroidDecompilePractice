package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IZuoyeBoBaoView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class ZuoyeBoBaoPresenter extends BasePresenter implements PVBaseListener {
    private IZuoyeBoBaoView mView;
    private BaseModel model;
    private int state = 0;

    public ZuoyeBoBaoPresenter(Context mContext, IZuoyeBoBaoView mView) {
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

    public void onNext(String result, String mothead) {
        if (mothead.equals("getMyZuoYeList.action")) {
            this.mView.getIZuoyeBoBao(JSON.parseArray(result, Posthomeworkentity.class), this.state);
            this.mView.closeLoadingDialog();
        } else if (mothead.equals("getMyClass.action")) {
            this.mView.getIMyClassName(JSON.parseArray(result, schoolclassentity.class));
            this.mView.closeLoadingDialog();
        }
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
