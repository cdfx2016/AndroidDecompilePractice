package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IJoinJiaZhangView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class JoinJiaZhangPresenter extends BasePresenter implements PVBaseListener {
    private IJoinJiaZhangView mView;
    private BaseModel model = new BaseModelImp(this);

    public JoinJiaZhangPresenter(Context mContext, IJoinJiaZhangView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("selectChild.action")) {
            this.mView.selectChildList(JSON.parseArray(resulte, student.class));
        } else if (mothead.equals("joinClass.action")) {
            this.mView.isapply(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
