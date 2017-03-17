package com.fanyu.boundless.view.myself;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.app.MyApplication;
import com.fanyu.boundless.bean.home.Update;
import com.fanyu.boundless.bean.home.UpdateApi;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.config.EasyPermissions;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.welcome.WelcomePresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.util.VersionManagementUtil;
import com.fanyu.boundless.view.base.BaseLlayout;
import com.fanyu.boundless.view.home.UpdateDialog;
import com.fanyu.boundless.view.login.LoginActivity;
import com.fanyu.boundless.view.main.MainAcitivity;
import com.fanyu.boundless.view.myself.event.UpdateNameEvent;
import com.fanyu.boundless.view.theclass.CaptureActivity;
import com.fanyu.boundless.view.welcome.IWelcomeView;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xiaomi.mipush.sdk.MiPushClient;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MySelfLayout extends BaseLlayout<WelcomePresenter> implements IWelcomeView {
    private SharedPreferencesUtil mSharedPreferences;
    private String nickname;
    private String[] perms = new String[]{"android.permission.CAMERA"};
    @Bind({2131624277})
    ImageView userhead;
    private String userimg;
    @Bind({2131624139})
    TextView username;

    public MySelfLayout(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.layout_myself;
    }

    protected void initPresenter() {
        this.mPresenter = new WelcomePresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.mSharedPreferences = SharedPreferencesUtil.getsInstances(this.mContext);
        this.nickname = this.mSharedPreferences.getString(Preferences.NICKNAME, "");
        this.userimg = this.mSharedPreferences.getString(Preferences.USER_IMG, "");
        if (StringUtils.isEmpty(this.userimg)) {
            ImagePathUtil.getInstance().setImageUrl(this.mContext, this.userhead, this.userimg, new CropCircleTransformation(this.mContext));
        }
        if (StringUtils.isEmpty(this.nickname)) {
            this.username.setText(this.nickname);
        }
    }

    @OnClick({2131624454, 2131624455, 2131624457, 2131624460, 2131624463, 2131624466, 2131624469})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yonghuxinxi:
                this.mContext.startActivity(new Intent(this.mContext, UpdateXinXiActivity.class));
                return;
            case R.id.layout_saoyisao:
                if (EasyPermissions.hasPermissions(this.mContext, this.perms)) {
                    this.mContext.startActivity(new Intent(this.mContext, CaptureActivity.class));
                    return;
                } else {
                    EasyPermissions.MyRequestPermissions(this.mContext, 1, this.perms);
                    return;
                }
            case R.id.layout_clearcache:
                Toast.makeText(this.mContext, "缓存清理完毕！", 1).show();
                return;
            case R.id.layout_aboutus:
                this.mContext.startActivity(new Intent(this.mContext, AboutUsActivity.class));
                return;
            case R.id.layout_fankui:
                this.mContext.startActivity(new Intent(this.mContext, QuestionActivity.class));
                return;
            case R.id.layout_checkupdate:
                selectVersion();
                return;
            case R.id.button_exit:
                showalertdialogzhuxiao();
                return;
            default:
                return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateNameEvent updateNameEvent) {
        this.nickname = this.mSharedPreferences.getString(Preferences.NICKNAME, "");
        this.userimg = this.mSharedPreferences.getString(Preferences.USER_IMG, "");
        this.username.setText(this.nickname);
        ImagePathUtil.getInstance().setImageUrl(this.mContext, this.userhead, this.userimg, new CropCircleTransformation(this.mContext));
    }

    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void showalertdialogzhuxiao() {
        Builder alert = new Builder(this.mContext);
        alert.setTitle("退出当前账号？").setMessage("确认退出？").setPositiveButton("退出", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(MySelfLayout.this.mContext);
                editor.putString(Preferences.USER_ID, "");
                editor.putString(Preferences.NICKNAME, "");
                editor.putString(Preferences.LoginName, "");
                editor.putString(Preferences.USER_PWD, "");
                editor.putString(Preferences.USER_TYPE, "");
                editor.putString(Preferences.USER_IMG, "");
                editor.putBoolean(Preferences.IS_AUTO_CHECK, false);
                editor.putString(Preferences.CLASS_ID, "");
                editor.putString(Preferences.CLASS_NAME, "");
                List<String> mString = MiPushClient.getAllAlias(MySelfLayout.this.mContext);
                for (int i = 0; i < mString.size(); i++) {
                    MiPushClient.unsetAlias(MySelfLayout.this.mContext, (String) mString.get(i), null);
                }
                Intent intent = new Intent();
                intent.setClass(MySelfLayout.this.mContext, LoginActivity.class);
                MySelfLayout.this.mContext.startActivity(intent);
                MainAcitivity.mainAcitivity.finish();
            }
        }).setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    public void selectVersion() {
        UpdateApi updateApi = new UpdateApi();
        updateApi.setSystem("android");
        ((WelcomePresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, updateApi);
    }

    private void checkVersion() {
        MyApplication.localVersionCode = VersionManagementUtil.getVersionCode(this.mContext);
        switch (VersionManagementUtil.VersionComparison(MyApplication.serverVersionCode, MyApplication.localVersionCode)) {
            case -1:
                Toast.makeText(this.mContext, "当前已是最新版本", 0).show();
                return;
            case 0:
                Toast.makeText(this.mContext, "当前已是最新版本", 0).show();
                return;
            case 1:
                new UpdateDialog(this.mContext).show();
                return;
            default:
                return;
        }
    }

    public void getVersion(Update serverVersion) {
        MyApplication.serverVersionCode = serverVersion.getId();
        checkVersion();
    }

    public void getLogin(Login login) {
    }
}
