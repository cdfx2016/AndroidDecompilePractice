package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IDeleteStudentView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class DeleteStudentPresenter extends BasePresenter implements PVBaseListener {
    private IDeleteStudentView mView;
    private BaseModel model = new BaseModelImp(this);

    public DeleteStudentPresenter(Context mContext, IDeleteStudentView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("xjObtainClassMemeber.action")) {
            this.mView.getStudentList(JSON.parseArray(resulte, classmember.class));
        } else if (mothead.equals("xjDeleteStudent.action")) {
            this.mView.isdelete(resulte.substring(0, resulte.length()));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
