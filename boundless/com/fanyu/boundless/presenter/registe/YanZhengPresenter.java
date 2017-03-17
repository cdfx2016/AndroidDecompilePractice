package com.fanyu.boundless.presenter.registe;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.registe.IZhuCeYanZhengView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class YanZhengPresenter extends BasePresenter implements PVBaseListener {
    private IZhuCeYanZhengView mView;
    private BaseModel model = new BaseModelImp(this);

    public YanZhengPresenter(Context mContext, IZhuCeYanZhengView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.getLoginYanZheng((Login) JSON.parseObject(resulte, Login.class));
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
