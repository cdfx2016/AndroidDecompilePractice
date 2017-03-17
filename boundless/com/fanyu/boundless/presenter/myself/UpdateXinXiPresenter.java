package com.fanyu.boundless.presenter.myself;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.fanyu.boundless.bean.myself.Tsuser;
import com.fanyu.boundless.model.base.BaseModel;
import com.fanyu.boundless.model.base.BaseModelImp;
import com.fanyu.boundless.presenter.base.BasePresenter;
import com.fanyu.boundless.presenter.base.PVBaseListener;
import com.fanyu.boundless.view.myself.IUpdateXinXIView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class UpdateXinXiPresenter extends BasePresenter implements PVBaseListener {
    IUpdateXinXIView mView;
    BaseModel model = new BaseModelImp(this);

    public UpdateXinXiPresenter(Context mContext, IUpdateXinXIView mView) {
        super(mContext);
        this.mView = mView;
    }

    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        super.startPost(rxAppCompatActivity, baseApi);
        this.model.startPost(rxAppCompatActivity, baseApi);
    }

    public void onNext(String resulte, String mothead) {
        if (mothead.equals("editFile.action")) {
            this.mView.isupdate(resulte.substring(0, resulte.length()).replaceAll("\"", ""), Item.UPDATE_ACTION);
        } else if (mothead.equals("findPersonInfo.action")) {
            this.mView.getMyXinXi((Tsuser) JSON.parseObject(resulte, Tsuser.class));
        } else if (mothead.equals("uploadImage.action")) {
            this.mView.isupdate(resulte.substring(0, resulte.length()).replaceAll("\"", ""), "addimg");
        }
    }

    public void onError(ApiException e) {
        this.mView.closeLoadingDialog();
        if (e.getCode() != 4) {
            this.mView.showTip(e.getDisplayMessage());
        }
    }
}
