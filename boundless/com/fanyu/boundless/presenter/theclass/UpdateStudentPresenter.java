package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.StudentsModel;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IUpdateStudentView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class UpdateStudentPresenter extends BasePresenter implements PVBaseListener {
    private int biaoji;
    private String classname;
    private IUpdateStudentView mView;
    private BaseModel model = new BaseModelImp(this);

    public UpdateStudentPresenter(Context mContext, IUpdateStudentView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int biaoji) {
        this.model.startPost(rxAppCompatActivity, baseApi);
        this.biaoji = biaoji;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, String classname) {
        this.model.startPost(rxAppCompatActivity, baseApi);
        this.classname = classname;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("getTeacherClass.action")) {
            this.mView.getClassList(JSON.parseArray(resulte, schoolclassentity.class));
        } else if (mothead.equals("getAllClassZuList.action")) {
            this.mView.getZuList(JSON.parseArray(resulte, classzuentity.class));
        } else if (mothead.equals("getChildLists.action")) {
            this.mView.getClassStuList(JSON.parseArray(resulte, StudentsModel.class));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
