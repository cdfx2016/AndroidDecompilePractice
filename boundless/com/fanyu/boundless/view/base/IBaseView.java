package com.fanyu.boundless.view.base;

public interface IBaseView {
    void closeLoadingDialog();

    void loadFailure(String str);

    void loadSuccess(Object obj);

    void showLoadingDialog();

    void showTip(String str);
}
