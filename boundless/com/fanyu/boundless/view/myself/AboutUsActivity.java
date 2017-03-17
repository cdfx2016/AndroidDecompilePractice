package com.fanyu.boundless.view.myself;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.view.base.BaseActivity;

public class AboutUsActivity extends BaseActivity {
    @Bind({2131624069})
    TextView banben;
    @Bind({2131624068})
    TextView banbenhao;

    protected void initView() {
        setContentView((int) R.layout.activity_aboutus);
    }

    protected void initPresenter() {
    }

    protected void init() {
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            this.banbenhao.setText("当前版本号" + packageInfo.versionName);
            this.banben.setText("定行(" + packageInfo.versionName + ")");
        } catch (Exception e) {
        }
    }

    @OnClick({2131624066, 2131624070})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.rela_erweima:
                startActivity(new Intent(this, ErWeiMaActivity.class));
                return;
            default:
                return;
        }
    }
}
