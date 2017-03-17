package com.fanyu.boundless.view.myself;

import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.view.base.BaseActivity;

public class ErWeiMaActivity extends BaseActivity {
    protected void initView() {
        setContentView((int) R.layout.activity_erweima);
    }

    protected void initPresenter() {
    }

    protected void init() {
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }
}
