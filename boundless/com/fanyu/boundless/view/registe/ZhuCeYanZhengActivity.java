package com.fanyu.boundless.view.registe;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.login.Login;
import com.fanyu.boundless.bean.registe.YanZhengApi;
import com.fanyu.boundless.presenter.registe.YanZhengPresenter;
import com.fanyu.boundless.view.base.BaseActivity;

public class ZhuCeYanZhengActivity extends BaseActivity<YanZhengPresenter> implements IZhuCeYanZhengView {
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result != -1) {
                Toast.makeText(ZhuCeYanZhengActivity.this.getApplicationContext(), "验证码输入错误，请重新输入！", 0).show();
            } else if (event == 3) {
                Toast.makeText(ZhuCeYanZhengActivity.this.getApplicationContext(), "提交验证码成功", 0).show();
                Intent intent = new Intent(ZhuCeYanZhengActivity.this, RegisteActivity.class);
                intent.putExtra("telephone", ZhuCeYanZhengActivity.this.phoneNums);
                ZhuCeYanZhengActivity.this.startActivity(intent);
                ZhuCeYanZhengActivity.this.finish();
            } else if (event == 2) {
                Toast.makeText(ZhuCeYanZhengActivity.this.getApplicationContext(), "验证码已经发送", 0).show();
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };
    int i = 60;
    @Bind({2131624174})
    EditText loginInputCodeEt;
    @Bind({2131624173})
    EditText loginInputPhoneEt;
    @Bind({2131624175})
    Button loginRequestCodeBtn;
    private String phoneNums = "";
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        public void onTick(long millisUntilFinished) {
            ZhuCeYanZhengActivity.this.loginRequestCodeBtn.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        public void onFinish() {
            ZhuCeYanZhengActivity.this.loginRequestCodeBtn.setClickable(true);
            ZhuCeYanZhengActivity.this.loginRequestCodeBtn.setText("获取验证码");
        }
    };
    private String yztxt = "";

    protected void initView() {
        setContentView((int) R.layout.activity_zhuce_yanzheng);
    }

    protected void initPresenter() {
        this.mPresenter = new YanZhengPresenter(this.mContext, this);
    }

    protected void init() {
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                ZhuCeYanZhengActivity.this.handler.sendMessage(msg);
            }
        });
    }

    @OnClick({2131624066, 2131624175, 2131624176})
    public void onClick(View view) {
        this.phoneNums = this.loginInputPhoneEt.getText().toString();
        this.yztxt = this.loginInputCodeEt.getText().toString();
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.login_request_code_btn:
                if (judgePhoneNums(this.phoneNums)) {
                    YanZhengApi yanZhengApi = new YanZhengApi();
                    yanZhengApi.setPhoneNumber(this.phoneNums);
                    ((YanZhengPresenter) this.mPresenter).startPost(this, yanZhengApi);
                    return;
                }
                return;
            case R.id.login_commit_btn:
                if (this.phoneNums.equals("") || this.yztxt.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入手机号和验证码。", 0).show();
                    return;
                } else {
                    SMSSDK.submitVerificationCode("86", this.phoneNums, this.yztxt);
                    return;
                }
            default:
                return;
        }
    }

    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "请输入11位手机号！", 0).show();
        return false;
    }

    public static boolean isMatchLength(String str, int length) {
        if (!str.isEmpty() && str.length() == length) {
            return true;
        }
        return false;
    }

    public static boolean isMobileNO(String mobileNums) {
        String telRegex = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(mobileNums)) {
            return false;
        }
        return mobileNums.matches(telRegex);
    }

    protected void onDestroy() {
        this.timer.cancel();
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    public void getLoginYanZheng(Login login) {
        if (login.getStatus() == 3) {
            SMSSDK.getVerificationCode("86", this.phoneNums);
            this.loginRequestCodeBtn.setClickable(false);
            this.loginRequestCodeBtn.setText("重新发送(" + this.i + ")");
            this.timer.start();
        } else if (login.getStatus() == 2) {
            Toast.makeText(this, "该手机号已注册，请您登录！", 0).show();
        }
    }
}
