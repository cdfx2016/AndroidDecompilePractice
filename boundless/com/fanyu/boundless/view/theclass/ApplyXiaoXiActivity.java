package com.fanyu.boundless.view.theclass;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ApplyXiaoXiApi;
import com.fanyu.boundless.bean.theclass.SelectMyRoleApi;
import com.fanyu.boundless.bean.theclass.applyentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.ApplyXiaoXiPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdateApplyEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import de.greenrobot.event.EventBus;

public class ApplyXiaoXiActivity extends BaseActivity<ApplyXiaoXiPresenter> implements IApplyXiaoXiView {
    @Bind({2131624086})
    TextView apState;
    private ApplyXiaoXiApi applyXiaoXiApi;
    @Bind({2131624085})
    TextView classname;
    private String classrole;
    private applyentity entity;
    @Bind({2131624087})
    TextView no;
    private int position;
    private SharedPreferencesUtil sharedPreferencesUtil;
    @Bind({2131624082})
    TextView shenqing;
    @Bind({2131624084})
    TextView sqbeizhu;
    @Bind({2131624079})
    TextView sqname;
    @Bind({2131624081})
    TextView sqrole;
    @Bind({2131624080})
    TextView sqtime;
    private String userid;
    @Bind({2131624083})
    TextView xueke;
    @Bind({2131624088})
    TextView yes;

    protected void initView() {
        setContentView((int) R.layout.activity_apply_xiaoxi);
    }

    protected void initPresenter() {
        this.mPresenter = new ApplyXiaoXiPresenter(this.mContext, this);
    }

    protected void init() {
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.position = getIntent().getIntExtra("position", 0);
        this.entity = (applyentity) getIntent().getSerializableExtra("entity");
        if (StringUtils.isEmpty(this.entity.getUsername())) {
            this.sqname.setText(this.entity.getUsername());
        }
        if (this.entity.getRole().equals("0")) {
            this.sqrole.setText("教师");
            this.shenqing.setText("申请学科");
            if (StringUtils.isEmpty(this.entity.getXueke())) {
                this.xueke.setText(this.entity.getXueke());
            }
            this.classrole = "1";
        } else {
            this.sqrole.setText("学生");
            this.shenqing.setText("申请学号");
            if (StringUtils.isEmpty(this.entity.getBianhao())) {
                this.xueke.setText(this.entity.getBianhao());
            }
            this.classrole = "2";
        }
        if (this.entity.getState().equals("0")) {
            this.apState.setText("审核中");
            this.apState.setTextColor(Color.parseColor("#003e94"));
        } else if (this.entity.getState().equals("1")) {
            this.apState.setText("已通过");
            this.apState.setTextColor(Color.parseColor("#3e9400"));
        } else if (this.entity.getState().equals("2")) {
            this.apState.setText("未通过");
            this.apState.setTextColor(Color.parseColor("#dd2727"));
        }
        SelectMyRoleApi selectMyRoleApi = new SelectMyRoleApi();
        selectMyRoleApi.setUserid(this.userid);
        selectMyRoleApi.setClassid(this.entity.getClassid());
        ((ApplyXiaoXiPresenter) this.mPresenter).startPost(this, selectMyRoleApi);
        String timeString = StringUtils.datestring(this.entity.getCreatetime());
        if (StringUtils.isEmpty(timeString)) {
            this.sqtime.setText(timeString);
        }
        if (StringUtils.isEmpty(this.entity.getRemark())) {
            this.sqbeizhu.setText(this.entity.getRemark());
        }
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.classname.setText(this.entity.getClassname());
        }
        this.applyXiaoXiApi = new ApplyXiaoXiApi();
        this.applyXiaoXiApi.setApplyid(this.entity.getId());
        this.applyXiaoXiApi.setClassid(this.entity.getClassid());
        this.applyXiaoXiApi.setUserid(this.entity.getUserid());
        this.applyXiaoXiApi.setRole(this.entity.getRole());
        this.applyXiaoXiApi.setXueke(this.entity.getXueke());
        this.applyXiaoXiApi.setMembername(this.entity.getUsername());
        this.applyXiaoXiApi.setBeizhu(this.entity.getRemark());
        this.applyXiaoXiApi.setClassid(this.entity.getClassid());
        this.applyXiaoXiApi.setClassrole(this.classrole);
    }

    @OnClick({2131624066, 2131624087, 2131624088})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.no:
                this.applyXiaoXiApi.setState("2");
                ((ApplyXiaoXiPresenter) this.mPresenter).startPost(this, this.applyXiaoXiApi);
                return;
            case R.id.yes:
                this.applyXiaoXiApi.setState("1");
                ((ApplyXiaoXiPresenter) this.mPresenter).startPost(this, this.applyXiaoXiApi);
                return;
            default:
                return;
        }
    }

    public void selectMyRole(String role) {
        if (this.entity.getState().equals("0") && role.equals("0")) {
            this.yes.setVisibility(0);
            this.no.setVisibility(0);
        }
    }

    public void updateApply(String isapply) {
        if (this.applyXiaoXiApi.getState().equals("1")) {
            this.entity.setState("1");
            EventBus.getDefault().post(new UpdateApplyEvent(this.position, this.entity));
            EventBus.getDefault().post(new UpdateClassEvent());
            finish();
        } else if (this.applyXiaoXiApi.getState().equals("2")) {
            this.entity.setState("2");
            EventBus.getDefault().post(new UpdateApplyEvent(this.position, this.entity));
            finish();
        }
    }
}
