package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;

public class ZiDingYiActivity extends BaseActivity {
    @Bind({2131624272})
    EditText editZidingyi;
    private String name;

    protected void initView() {
        setContentView((int) R.layout.activity_zidingyi);
    }

    protected void initPresenter() {
    }

    protected void init() {
    }

    @OnClick({2131624066, 2131624071})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.queding:
                this.name = this.editZidingyi.getText().toString();
                if (StringUtils.isEmpty(this.name)) {
                    Intent intent = new Intent(this, JoinTeacherActivity.class);
                    intent.putExtra(Preferences.sbry, this.name);
                    setResult(4, intent);
                    finish();
                    return;
                }
                Toast.makeText(this, "请您输入自定义学科！", 0).show();
                return;
            default:
                return;
        }
    }
}
