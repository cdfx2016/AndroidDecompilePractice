package com.fanyu.boundless.presenter.home;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.home.GetSchoolEntity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.home.IGetSchoolView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class GetSchoolPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model;
    private int state = 0;
    private IGetSchoolView view;

    public GetSchoolPresenter(Context mContext, IGetSchoolView mView) {
        super(mContext);
        this.view = mView;
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
        if (mothead.equals("getMyGetLeaveNoticeList.action")) {
            this.view.getArriveOrLeaveList(JSON.parseArray(resulte, GetSchoolEntity.class), this.state);
            this.view.closeLoadingDialog();
        } else if (mothead.equals("getMyClass.action")) {
            this.view.getIMyClassName(JSON.parseArray(resulte, schoolclassentity.class));
            this.view.closeLoadingDialog();
        } else if (mothead.equals("getTeacherClass.action")) {
            this.view.getITeacherClassName(JSON.parseArray(resulte, schoolclassentity.class));
            this.view.closeLoadingDialog();
        } else if (mothead.equals("addGetLeaveNotice.action")) {
            this.view.addGet(resulte.substring(0, resulte.length()).replaceAll("\"", ""));
        } else if (mothead.equals("updateUnreadMessageGet.action")) {
            this.view.updateUnread(resulte);
        }
    }

    public void onError(ApiException e) {
        this.view.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.view.showTip(e.getDisplayMessage());
        }
    }
}
