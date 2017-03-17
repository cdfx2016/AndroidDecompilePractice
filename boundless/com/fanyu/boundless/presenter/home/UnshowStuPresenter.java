package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IUnshowStuView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class UnshowStuPresenter extends BasePresenter implements PVBaseListener {
    private IUnshowStuView mView;
    private BaseModel model = new BaseModelImp(this);

    public UnshowStuPresenter(Context mContext, IUnshowStuView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        this.mView.getUnreadStu(JSON.parseArray(resulte, student.class));
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
