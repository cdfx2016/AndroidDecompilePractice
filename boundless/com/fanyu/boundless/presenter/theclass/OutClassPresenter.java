package com.fanyu.boundless.presenter.theclass;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.theclass.IClassXiaoXiView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

public class OutClassPresenter extends BasePresenter implements PVBaseListener {
    private IClassXiaoXiView mView;
    private BaseModel model = new BaseModelImp(this);

    public OutClassPresenter(Context mContext, IClassXiaoXiView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("xjObtainClassDetailInfo.action")) {
            this.mView.updateXiaoXi((schoolclassentity) JSON.parseObject(resulte, schoolclassentity.class));
        } else if (mothead.equals("OutClass.action")) {
            this.mView.outClass(resulte.substring(0, resulte.length()));
        } else if (mothead.equals("DeleteClass.action")) {
            this.mView.deleteClass(resulte.substring(0, resulte.length()));
        }
    }

    public void onError(ApiException e) {
        if (e.getCode() != 4) {
            this.mView.showTip(e.getMessage());
        }
    }
}
