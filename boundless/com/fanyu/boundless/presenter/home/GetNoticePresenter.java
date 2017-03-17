package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IGetNoticeView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class GetNoticePresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model = new BaseModelImp(this);
    private IGetNoticeView view;

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getChildList.action")) {
            this.view.getChildList(JSON.parseArray(resulte, student.class));
            this.view.closeLoadingDialog();
        } else if (mothead.equals("getZuList.action")) {
            this.view.getZuList(JSON.parseArray(resulte, classzuentity.class));
            this.view.closeLoadingDialog();
        } else if (mothead.equals("addGetLeaveNotice.action")) {
            this.view.addGet(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        }
    }

    public void onError(ApiException e) {
        this.view.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.view.showTip(e.getDisplayMessage());
        }
    }

    public GetNoticePresenter(Context mContext, IGetNoticeView view) {
        super(mContext);
        this.view = view;
    }
}
