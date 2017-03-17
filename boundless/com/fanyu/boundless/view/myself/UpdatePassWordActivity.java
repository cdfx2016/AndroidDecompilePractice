package com.fanyu.boundless.view.myself;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.registe.ResetPassWordApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.registe.ResetPassWordPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.login.LoginActivity;
import com.fanyu.boundless.view.main.MainAcitivity;
import com.fanyu.boundless.view.registe.IResetPassWordView;

public class UpdatePassWordActivity extends BaseActivity<ResetPassWordPresenter> implements IResetPassWordView {
    private SharedPreferencesUtil mSharedPreferences;
    @Bind({2131624216})
    EditText newpassword;
    private String oldpass;
    @Bind({2131624262})
    EditText oldpassword;
    @Bind({2131624217})
    EditText repassword;
    private String telephone;

    protected void initView() {
        setContentView((int) R.layout.activity_update_password);
    }

    protected void initPresenter() {
        this.mPresenter = new ResetPassWordPresenter(this.mContext, this);
    }

    protected void init() {
        getWindow().setSoftInputMode(4);
        this.mSharedPreferences = SharedPreferencesUtil.getsInstances(this.mContext);
        this.telephone = this.mSharedPreferences.getString(Preferences.LoginName, "");
        this.oldpass = this.mSharedPreferences.getString(Preferences.USER_PWD, "");
    }

    @OnClick({2131624207, 2131624218})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_xxsb_return:
                finish();
                return;
            case R.id.btnAction1:
                String npass = this.newpassword.getText().toString();
                String opass = this.oldpassword.getText().toString();
                String rpass = this.repassword.getText().toString();
                if (!StringUtils.isEmpty(opass)) {
                    Toast.makeText(this, "旧密码不能为空！", 0).show();
                    return;
                } else if (!StringUtils.isEmpty(npass) || npass.length() < 6) {
                    Toast.makeText(this, "请输入至少6位新密码！", 0).show();
                    return;
                } else if (!StringUtils.isEmpty(rpass) || rpass.length() < 6) {
                    Toast.makeText(this, "两次输入的密码不一致,请重新输入！", 0).show();
                    return;
                } else if (!this.oldpass.equals(opass)) {
                    Toast.makeText(this, "旧密码输入不正确！", 0).show();
                    return;
                } else if (npass.equals(rpass)) {
                    ResetPassWordApi resetPassWordApi = new ResetPassWordApi();
                    resetPassWordApi.setTableName("tsuser");
                    resetPassWordApi.setFileName("password");
                    resetPassWordApi.setFileValue(npass);
                    resetPassWordApi.setTelephone(this.telephone);
                    ((ResetPassWordPresenter) this.mPresenter).startPost(this, resetPassWordApi);
                    return;
                } else {
                    Toast.makeText(this, "两次输入的密码不一致,请重新输入！", 0).show();
                    return;
                }
            default:
                return;
        }
    }

    public void isreset(String isreset) {
        if (isreset.equals("true")) {
            Toast.makeText(this, "重置密码成功！", 0).show();
            SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this.mContext);
            editor.putString(Preferences.USER_ID, "");
            editor.putString(Preferences.NICKNAME, "");
            editor.putString(Preferences.LoginName, "");
            editor.putString(Preferences.USER_PWD, "");
            editor.putString(Preferences.USER_TYPE, "");
            editor.putString(Preferences.USER_IMG, "");
            editor.putBoolean(Preferences.IS_AUTO_CHECK, false);
            editor.putString(Preferences.CLASS_ID, "");
            editor.putString(Preferences.CLASS_NAME, "");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            UpdateXinXiActivity.updateXinXiActivity.finish();
            MainAcitivity.mainAcitivity.finish();
        }
    }
}
