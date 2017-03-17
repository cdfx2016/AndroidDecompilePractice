package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.view.View;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.view.base.BaseActivity;

public class SelectClassRoleActivity extends BaseActivity {
    private String classid;
    private String classname;

    protected void initView() {
        setContentView((int) R.layout.activity_select_role);
    }

    protected void initPresenter() {
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        this.classid = getIntent().getStringExtra("classid");
        this.classname = getIntent().getStringExtra("classname");
    }

    @OnClick({2131624066, 2131624109, 2131624111})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.teacher:
                Intent intent = new Intent(this, JoinTeacherActivity.class);
                intent.putExtra("classid", this.classid);
                intent.putExtra("classname", this.classname);
                startActivity(intent);
                return;
            case R.id.student:
                Intent intent2 = new Intent(this, JoinJiaZhangActivity.class);
                intent2.putExtra("classid", this.classid);
                intent2.putExtra("classname", this.classname);
                startActivity(intent2);
                return;
            default:
                return;
        }
    }
}
