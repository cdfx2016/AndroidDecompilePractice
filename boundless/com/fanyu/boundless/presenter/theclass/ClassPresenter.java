package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IClassView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;
import java.util.List;

public class ClassPresenter extends BasePresenter implements PVBaseListener {
    private IClassView mView;
    private BaseModel model;
    private int state = 0;

    public ClassPresenter(Context mContext, IClassView mView) {
        super(mContext);
        this.mView = mView;
        this.model = new BaseModelImp(this);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.state = state;
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getMyClass.action")) {
            List<schoolclassentity> entityList = JSON.parseArray(resulte, schoolclassentity.class);
            System.out.println("onNext entitylist ======== " + entityList);
            this.mView.getClassList(entityList, this.state);
        } else if (mothead.equals("getUnreadMessage.action")) {
            this.mView.getUnread(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
