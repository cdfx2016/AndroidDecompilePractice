package com.fanyu.boundless.view.login;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.bean.login.LoginApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.login.LoginPresenter;
import com.fanyu.boundless.util.KeyboardUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.main.MainAcitivity;
import com.fanyu.boundless.view.registe.ForgetYanZhengActivity;
import com.fanyu.boundless.view.registe.ZhuCeYanZhengActivity;
import com.xiaomi.mipush.sdk.MiPushClient;

public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {
    public static LoginActivity loginActivity;
    @Bind({2131624196})
    TextView btnLogin;
    @Bind({2131624195})
    EditText etPassword;
    @Bind({2131624193})
    EditText etUsername;
    private String mPassWord;
    private String mUserName;

    protected void initView() {
        setContentView((int) R.layout.activity_login);
    }

    protected void initPresenter() {
        this.mPresenter = new LoginPresenter(this.mContext, this);
    }

    protected void init() {
        loginActivity = this;
        this.etUsername.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                LoginActivity.this.saveKeyBoardHeight();
                return false;
            }
        });
    }

    public void getLogin(Login login) {
        SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this);
        if (login.getStatus() == 0) {
            Toast.makeText(this, R.string.login_status_invalid_username_or_password, 0).show();
        }
        if (login.getStatus() == 1) {
            editor.putString(Preferences.USER_ID, login.getId());
            editor.putString(Preferences.USER_PWD, login.getPassword());
            editor.putString(Preferences.USER_IMG, login.getUserimg());
            editor.putString(Preferences.NICKNAME, login.getNickname());
            editor.putString(Preferences.LoginName, login.getUsername());
            editor.putString(Preferences.USER_TYPE, login.getUsertype());
            editor.putString(Preferences.CLASS_ID, login.getClassid());
            editor.putBoolean(Preferences.IS_AUTO_CHECK, true);
            editor.putString(Preferences.CLASS_NAME, login.getClassname());
            startActivity(new Intent(this, MainAcitivity.class));
            MiPushClient.setAlias(this, login.getId(), null);
            System.out.println("当前别名" + MiPushClient.getAllAlias(this.mContext));
            finish();
        }
        if (login.getStatus() == 2) {
            Toast.makeText(this, R.string.login_status_weizhuce, 0).show();
        }
    }

    @OnClick({2131624196, 2131624197, 2131624198})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                this.mUserName = this.etUsername.getText().toString();
                this.mPassWord = this.etPassword.getText().toString();
                LoginApi login = new LoginApi();
                login.setmUserName(this.mUserName);
                login.setmPassWord(this.mPassWord);
                ((LoginPresenter) this.mPresenter).startPost(this, login);
                return;
            case R.id.btnRegiste:
                startActivity(new Intent(this, ZhuCeYanZhengActivity.class));
                return;
            case R.id.forgetmima:
                startActivity(new Intent(this, ForgetYanZhengActivity.class));
                return;
            default:
                return;
        }
    }

    private void saveKeyBoardHeight() {
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                int heightDifference = decorView.getRootView().getHeight() - (r.bottom - r.top);
                Log.e("KeyboardSize", "KeyboardSize:  " + heightDifference);
                if (heightDifference > 200) {
                    KeyboardUtil.saveKeyboardHeight(LoginActivity.this, heightDifference - r.top);
                }
            }
        });
    }
}
