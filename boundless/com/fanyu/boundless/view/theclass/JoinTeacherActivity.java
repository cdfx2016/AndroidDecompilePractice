package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.JoinClassApplyApi;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.JoinClassApplyPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.widget.OnTagClickListener;
import com.fanyu.boundless.widget.Tag;
import com.fanyu.boundless.widget.TagView;
import java.util.ArrayList;
import java.util.List;

public class JoinTeacherActivity extends BaseActivity<JoinClassApplyPresenter> implements IJoinTeacherView {
    private String beizhu;
    private String classid;
    private String classname;
    @Bind({2131624188})
    EditText editbeizhu;
    @Bind({2131624075})
    EditText editname;
    private final List<Tag> mTags = new ArrayList();
    SharedPreferencesUtil msharepreference;
    @Bind({2131624189})
    TagView tagview;
    private String teachername = "";
    private final String[] titles = new String[]{"语文", "数学", "英语", "美术", "舞蹈", "音乐", "+自定义"};
    private String userid;
    private String xueke = "";

    protected void initView() {
        setContentView((int) R.layout.activity_jointeacher);
    }

    protected void initPresenter() {
        this.mPresenter = new JoinClassApplyPresenter(this.mContext, this);
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.classname = getIntent().getStringExtra("classname");
        this.classid = getIntent().getStringExtra("classid");
        for (int i = 0; i < this.titles.length; i++) {
            Tag tag = new Tag(this.titles[i]);
            if (i == 0) {
                this.xueke = this.titles[0];
                tag.setChecked(true);
            }
            this.mTags.add(tag);
            this.tagview.add(tag);
        }
        this.tagview.setOnTagClickListener(new OnTagClickListener() {
            public void onTagClick(Tag tag, int position) {
                JoinTeacherActivity.this.xueke = tag.getText();
                if (tag.getText().equals("+自定义")) {
                    JoinTeacherActivity.this.startActivityForResult(new Intent(JoinTeacherActivity.this, ZiDingYiActivity.class), 3);
                    return;
                }
                for (Tag mTag : JoinTeacherActivity.this.mTags) {
                    if (mTag.equals(tag)) {
                        mTag.setChecked(true);
                    } else {
                        mTag.setChecked(false);
                    }
                }
                JoinTeacherActivity.this.tagview.refresh(tag);
            }

            public void onTagClick(Tag tag, View view, int position) {
            }
        });
    }

    @OnClick({2131624066, 2131624078})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.submit:
                this.teachername = this.editname.getText().toString();
                this.beizhu = this.editbeizhu.getText().toString();
                if (!StringUtils.isEmpty(this.teachername)) {
                    Toast.makeText(this, "请您输入姓名", 0).show();
                    return;
                } else if (!StringUtils.isEmpty(this.xueke) || this.xueke.equals("+自定义")) {
                    Toast.makeText(this, "请您选择学科", 0).show();
                    return;
                } else {
                    JoinClassApplyApi joinClassApplyApi = new JoinClassApplyApi();
                    joinClassApplyApi.setTeachername(this.teachername);
                    joinClassApplyApi.setBeizhu(this.beizhu);
                    joinClassApplyApi.setClassid(this.classid);
                    joinClassApplyApi.setState("0");
                    joinClassApplyApi.setRole("0");
                    joinClassApplyApi.setUserid(this.userid);
                    joinClassApplyApi.setXueke(this.xueke);
                    joinClassApplyApi.setXuehao("wu");
                    ((JoinClassApplyPresenter) this.mPresenter).startPost(this, joinClassApplyApi);
                    return;
                }
            default:
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 4:
                String mString = data.getExtras().getString(Preferences.sbry);
                if (StringUtils.isEmpty(mString)) {
                    Tag tag = new Tag(mString);
                    this.xueke = mString;
                    for (int i = 0; i < this.mTags.size(); i++) {
                        ((Tag) this.mTags.get(i)).setChecked(false);
                    }
                    tag.setChecked(true);
                    this.mTags.add(tag);
                    this.tagview.addone(tag);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void isapply(String isapply) {
        if (isapply.equals("yizhuce")) {
            Toast.makeText(this, "您已是班级老师！", 0).show();
            return;
        }
        Toast.makeText(this, "申请成功，请您等待审核！", 0).show();
        startActivity(new Intent(this, ClassXiaoXiListActivity.class));
        MyActivityManager.getsInstances().popAllActivityExceptOne("cls");
    }
}
