package com.fanyu.boundless.view.myself;

import android.view.View;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.PhoneLoginServletApi;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.SureLoginPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.theclass.ISureLoginView;

public class SureLoginActivity extends BaseActivity<SureLoginPresenter> implements ISureLoginView {
    private SharedPreferencesUtil mSharedPreferences;
    private String userid;
    private String userpassword;
    private String uuid;

    protected void initView() {
        setContentView((int) R.layout.activity_sure_login);
    }

    protected void initPresenter() {
        this.mPresenter = new SureLoginPresenter(this.mContext, this);
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        this.mSharedPreferences = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.mSharedPreferences.getString(Preferences.LoginName, "");
        this.userpassword = this.mSharedPreferences.getString(Preferences.USER_PWD, "");
        this.uuid = getIntent().getStringExtra("uuid");
    }

    @OnClick({2131624252, 2131624196})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                PhoneLoginServletApi phoneLoginServletApi = new PhoneLoginServletApi();
                phoneLoginServletApi.setUuid(this.uuid);
                phoneLoginServletApi.setUpwd(this.userpassword);
                phoneLoginServletApi.setUname(this.userid);
                ((SureLoginPresenter) this.mPresenter).startPost(this, phoneLoginServletApi);
                return;
            case R.id.capture_imageview_back:
                finish();
                return;
            default:
                return;
        }
    }

    public void phoneLoginServlet(String result) {
        MyActivityManager.getsInstances().popAllActivityExceptOne("string");
    }
}
