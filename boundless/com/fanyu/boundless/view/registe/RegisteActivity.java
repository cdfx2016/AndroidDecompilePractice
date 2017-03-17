package com.fanyu.boundless.view.registe;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.registe.RegisteApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.registe.RegistePresentr;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.login.LoginActivity;
import com.fanyu.boundless.view.main.MainAcitivity;

public class RegisteActivity extends BaseActivity<RegistePresentr> implements IRegisteView {
    @Bind({2131624214})
    EditText inputMima;
    @Bind({2131624213})
    EditText inputName;
    private String nickname;
    private String password;
    private String phonenumber;

    protected void initView() {
        setContentView((int) R.layout.activity_registe);
    }

    protected void initPresenter() {
        this.mPresenter = new RegistePresentr(this.mContext, this);
    }

    protected void init() {
        this.phonenumber = getIntent().getStringExtra("telephone");
    }

    @OnClick({2131624066, 2131624215})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.btnregiste:
                this.nickname = this.inputName.getText().toString();
                this.password = this.inputMima.getText().toString();
                if (this.nickname.trim().equals("")) {
                    Toast.makeText(this, "请输入昵称", 1).show();
                    return;
                } else if (this.password.length() < 6) {
                    Toast.makeText(this, "请输入至少6位的密码", 1).show();
                    return;
                } else {
                    RegisteApi registeApi = new RegisteApi();
                    registeApi.setPhonenumber(this.phonenumber);
                    registeApi.setNickname(this.nickname);
                    registeApi.setPassword(this.password);
                    registeApi.setUsertype("4");
                    ((RegistePresentr) this.mPresenter).startPost(this, registeApi);
                    return;
                }
            default:
                return;
        }
    }

    public void Registe(String isSuccess) {
        if (isSuccess != null) {
            Toast.makeText(this, "注册成功", 1).show();
            SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this);
            editor.putString(Preferences.NICKNAME, this.nickname);
            editor.putString(Preferences.LoginName, this.phonenumber);
            editor.putString(Preferences.USER_PWD, this.password);
            editor.putString(Preferences.USER_ID, isSuccess);
            editor.putBoolean(Preferences.IS_AUTO_CHECK, true);
            editor.putString(Preferences.USER_TYPE, "4");
            Intent intent = new Intent();
            intent.setClass(this, MainAcitivity.class);
            startActivity(intent);
            finish();
            LoginActivity.loginActivity.finish();
        }
    }
}
