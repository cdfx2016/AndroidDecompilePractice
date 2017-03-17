package com.fanyu.boundless.view.theclass;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.JoinClassApplyApi;
import com.fanyu.boundless.bean.theclass.SelectChildApi;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.JoinJiaZhangPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.AddChildEvent;
import com.fanyu.boundless.widget.SpinerPopWindow;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class JoinJiaZhangActivity extends BaseActivity<JoinJiaZhangPresenter> implements IJoinJiaZhangView {
    @Bind({2131624185})
    RelativeLayout addchild;
    private String beizhu;
    private List<student> childList = new ArrayList();
    private String classid;
    private String classname;
    private OnClickListener clickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addchild:
                    JoinJiaZhangActivity.this.mSpinerPopWindow.setWidth(JoinJiaZhangActivity.this.addchild.getWidth());
                    JoinJiaZhangActivity.this.mSpinerPopWindow.showAsDropDown(JoinJiaZhangActivity.this.addchild);
                    return;
                default:
                    return;
            }
        }
    };
    private int dijige = -1;
    private OnDismissListener dismissListener = new OnDismissListener() {
        public void onDismiss() {
        }
    };
    @Bind({2131624188})
    EditText editbeizhu;
    @Bind({2131624187})
    EditText editxuehao;
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            JoinJiaZhangActivity.this.mSpinerPopWindow.dismiss();
            JoinJiaZhangActivity.this.dijige = position;
            if (StringUtils.isEmpty((String) JoinJiaZhangActivity.this.list.get(position))) {
                JoinJiaZhangActivity.this.name.setText((CharSequence) JoinJiaZhangActivity.this.list.get(position));
            }
            System.out.println("woshi" + JoinJiaZhangActivity.this.dijige);
            JoinJiaZhangActivity.this.stuname = (String) JoinJiaZhangActivity.this.list.get(position);
            if (((String) JoinJiaZhangActivity.this.list.get(position)).equals("添加孩子")) {
                JoinJiaZhangActivity.this.startActivity(new Intent(JoinJiaZhangActivity.this, AddChildActivity.class));
            }
        }
    };
    private List<String> list;
    private SpinerPopWindow<String> mSpinerPopWindow;
    SharedPreferencesUtil msharepreference;
    @Bind({2131624186})
    TextView name;
    private String stuid;
    private String stuname = "";
    private String userid;
    private String xuehao;

    protected void initView() {
        setContentView((int) R.layout.activity_joinjiazhang);
    }

    protected void initPresenter() {
        this.mPresenter = new JoinJiaZhangPresenter(this.mContext, this);
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.classid = getIntent().getStringExtra("classid");
        this.classname = getIntent().getStringExtra("classname");
        selectChild();
        this.list = new ArrayList();
    }

    public void selectChild() {
        SelectChildApi selectChildApi = new SelectChildApi();
        selectChildApi.setUserid(this.userid);
        selectChildApi.setPage("1");
        selectChildApi.setPagesize("8");
        ((JoinJiaZhangPresenter) this.mPresenter).startPost(this, selectChildApi);
    }

    @OnClick({2131624066, 2131624185, 2131624078})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.submit:
                this.xuehao = this.editxuehao.getText().toString();
                this.beizhu = this.editbeizhu.getText().toString();
                if (!StringUtils.isEmpty(this.stuname) || !StringUtils.isEmpty(this.stuid)) {
                    Toast.makeText(this, "请您选择孩子", 0).show();
                    return;
                } else if (StringUtils.isEmpty(this.xuehao)) {
                    JoinClassApplyApi joinClassApplyApi = new JoinClassApplyApi();
                    joinClassApplyApi.setTeachername(this.stuname);
                    joinClassApplyApi.setBeizhu(this.beizhu);
                    joinClassApplyApi.setClassid(this.classid);
                    joinClassApplyApi.setState("0");
                    joinClassApplyApi.setRole("1");
                    if (this.childList.size() > 0 && this.dijige != -1) {
                        this.stuid = ((student) this.childList.get(this.dijige)).getUserid();
                        joinClassApplyApi.setUserid(this.stuid);
                    }
                    joinClassApplyApi.setXueke("wu");
                    joinClassApplyApi.setXuehao(this.xuehao);
                    ((JoinJiaZhangPresenter) this.mPresenter).startPost(this, joinClassApplyApi);
                    return;
                } else {
                    Toast.makeText(this, "请您输入学号", 0).show();
                    return;
                }
            default:
                return;
        }
    }

    public void selectChildList(List<student> studentList) {
        this.list.clear();
        if (studentList.size() > 0) {
            this.childList.clear();
            for (student ss : studentList) {
                this.list.add(ss.getSnickname());
            }
            this.stuid = ((student) studentList.get(0)).getUserid();
            this.dijige = 0;
            this.childList.addAll(studentList);
        }
        this.list.add("添加孩子");
        if (StringUtils.isEmpty((String) this.list.get(0))) {
            this.name.setText((CharSequence) this.list.get(0));
        }
        this.stuname = (String) this.list.get(0);
        this.addchild.setOnClickListener(this.clickListener);
        this.mSpinerPopWindow = new SpinerPopWindow((Activity) this, this.list, this.itemClickListener, 6);
        this.mSpinerPopWindow.setOnDismissListener(this.dismissListener);
    }

    public void isapply(String isapply) {
        if (isapply.equals("yizhuce")) {
            Toast.makeText(this, "您已是班级成员！", 0).show();
            return;
        }
        Toast.makeText(this, "申请成功，请您等待审核！", 0).show();
        Intent intent = new Intent(this, ClassXiaoXiListActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        MyActivityManager.getsInstances().popAllActivityExceptOne("cls");
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(AddChildEvent addChildEvent) {
        selectChild();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
