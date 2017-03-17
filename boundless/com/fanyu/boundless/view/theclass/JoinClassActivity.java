package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.JoinClassApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.EasyPermissions;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.presenter.theclass.JoinClassPresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;

public class JoinClassActivity extends BaseActivity<JoinClassPresenter> implements IJoinClassView {
    @Bind({2131624183})
    EditText editClassnumber;
    private String[] perms = new String[]{"android.permission.CAMERA"};
    private String searchcontent;

    protected void initView() {
        setContentView((int) R.layout.activity_joinclass);
    }

    protected void initPresenter() {
        this.mPresenter = new JoinClassPresenter(this.mContext, this);
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
    }

    @OnClick({2131624066, 2131624182, 2131624184})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.saoyisao:
                if (EasyPermissions.hasPermissions(this.mContext, this.perms)) {
                    this.mContext.startActivity(new Intent(this, CaptureActivity.class));
                    return;
                } else {
                    EasyPermissions.MyRequestPermissions(this.mContext, 1, this.perms);
                    return;
                }
            case R.id.search_schoolclass:
                this.searchcontent = this.editClassnumber.getText().toString();
                if (StringUtils.isEmpty(this.searchcontent)) {
                    JoinClassApi joinClassApi = new JoinClassApi();
                    joinClassApi.setClassnumber(this.searchcontent);
                    ((JoinClassPresenter) this.mPresenter).startPost(this, joinClassApi);
                    return;
                }
                Toast.makeText(this, "请输入班级编号", 1).show();
                return;
            default:
                return;
        }
    }

    public void getClassXinXi(schoolclassentity entity) {
        if (entity != null) {
            Intent intent = new Intent();
            intent.setClass(this, SearchResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("entity", entity);
            intent.putExtras(bundle);
            startActivity(intent);
            return;
        }
        Toast.makeText(this, "没有找到该班级", 1).show();
    }

    public void phoneReceiveServlet(String result) {
    }
}
