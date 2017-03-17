package com.fanyu.boundless.view.welcome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.fanyu.boundless.R;
import com.fanyu.boundless.app.MyApplication;
import com.fanyu.boundless.bean.home.Update;
import com.fanyu.boundless.bean.home.UpdateApi;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.bean.login.LoginApi;
import com.fanyu.boundless.config.EasyPermissions;
import com.fanyu.boundless.config.EasyPermissions.PermissionCallbacks;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.welcome.WelcomePresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.login.LoginActivity;
import com.fanyu.boundless.view.main.MainAcitivity;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.util.List;

public class WelcomeActivity extends BaseActivity<WelcomePresenter> implements IWelcomeView, PermissionCallbacks {
    public static String version;
    boolean is_auto_check = false;
    private boolean mLogout = false;
    private String mPassWord;
    private boolean mService = false;
    private SharedPreferencesUtil mSharedPreferences;
    private String mUserName;
    private boolean mVersion = true;
    private String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

    class Thread1 implements Runnable {
        Thread1() {
        }

        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            WelcomeActivity.this.startActivity(intent);
            WelcomeActivity.this.finish();
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_welcome);
    }

    protected void initPresenter() {
        this.mPresenter = new WelcomePresenter(this.mContext, this);
    }

    protected void init() {
        this.mSharedPreferences = SharedPreferencesUtil.getsInstances(this.mContext);
        version = MyApplication.localVersion;
        this.is_auto_check = this.mSharedPreferences.getBoolean(Preferences.IS_AUTO_CHECK, false);
        this.mUserName = this.mSharedPreferences.getString(Preferences.LoginName, "");
        this.mPassWord = this.mSharedPreferences.getString(Preferences.USER_PWD, "");
        MiPushClient.setAlias(this, this.mSharedPreferences.getString(Preferences.USER_ID, ""), null);
        System.out.println("当前别名" + MiPushClient.getAllAlias(this.mContext));
        UpdateApi updateApi = new UpdateApi();
        updateApi.setSystem("android");
        ((WelcomePresenter) this.mPresenter).startPost(this, updateApi);
        if (this.is_auto_check) {
            if (EasyPermissions.hasPermissions(this, this.perms)) {
                LoginApi loginApi = new LoginApi();
                loginApi.setmUserName(this.mUserName);
                loginApi.setmPassWord(this.mPassWord);
                ((WelcomePresenter) this.mPresenter).startPost(this, loginApi);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, MainAcitivity.class));
                return;
            }
            EasyPermissions.MyRequestPermissions(this, 2, this.perms);
        } else if (EasyPermissions.hasPermissions(this, this.perms)) {
            new Thread(new Thread1()).start();
        } else {
            EasyPermissions.MyRequestPermissions(this, 1, this.perms);
        }
    }

    public void getVersion(Update serverVersion) {
        MyApplication.serverVersionCode = serverVersion.getId();
        System.out.println("serverVersionCode ======= " + MyApplication.serverVersionCode);
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
            finish();
        }
        if (login.getStatus() == 2) {
            Toast.makeText(this, R.string.login_status_weizhuce, 0).show();
        }
    }

    public void onPermissionsGranted(int requestCode, List<String> list) {
        switch (requestCode) {
            case 1:
                new Thread(new Thread1()).start();
                return;
            case 2:
                LoginApi loginApi = new LoginApi();
                loginApi.setmUserName(this.mUserName);
                loginApi.setmPassWord(this.mPassWord);
                ((WelcomePresenter) this.mPresenter).startPost(this, loginApi);
                startActivity(new Intent(this, MainAcitivity.class));
                finish();
                return;
            default:
                return;
        }
    }

    public void onPermissionsDenied(int requestCode, List<String> list) {
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
