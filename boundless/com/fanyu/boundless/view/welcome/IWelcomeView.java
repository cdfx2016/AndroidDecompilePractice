package com.fanyu.boundless.view.welcome;

import com.fanyu.boundless.bean.home.Update;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.view.base.IBaseView;

public interface IWelcomeView extends IBaseView {
    void getLogin(Login login);

    void getVersion(Update update);
}
