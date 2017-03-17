package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.theclass.GetClassZuApi;
import com.fanyu.boundless.bean.theclass.SelectMyRoleApi;
import com.fanyu.boundless.bean.theclass.StudentListApi;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.StudentListPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.AddClassZuEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentListActivity extends BaseActivity<StudentListPresenter> implements IStudentListView {
    private ImageView addzu;
    private Map<Integer, List<ChildItem>> childData;
    List<ChildItem> childItems;
    private String classid;
    private String classuserid;
    private List<student> dataList = new ArrayList();
    @Bind({2131624245})
    TextView deletestu;
    @Bind({2131624246})
    ExpandableListView expandlist;
    private List<String> groupData;
    private MyBaseExpandableListAdapter myAdapter;
    List<ChildItem> newItems;
    RelativeLayout rela_createstudent;
    private String roleString = "2";
    SharedPreferencesUtil sharedPreferencesUtil;
    private String userid;
    private List<student> ziList = new ArrayList();
    private List<classzuentity> zuList = new ArrayList();

    protected void initView() {
        setContentView((int) R.layout.activity_student_list);
    }

    protected void initPresenter() {
        this.mPresenter = new StudentListPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.classid = getIntent().getStringExtra("classid");
        this.classuserid = getIntent().getStringExtra("classuserid");
        SelectMyRoleApi selectMyRoleApi = new SelectMyRoleApi();
        selectMyRoleApi.setClassid(this.classid);
        selectMyRoleApi.setUserid(this.userid);
        ((StudentListPresenter) this.mPresenter).startPost(this, selectMyRoleApi);
        StudentListApi studentListApi = new StudentListApi();
        studentListApi.setClassid(this.classid);
        ((StudentListPresenter) this.mPresenter).startPost(this, studentListApi);
        if (this.userid.equals(this.classuserid)) {
            this.deletestu.setVisibility(0);
        } else {
            this.deletestu.setVisibility(8);
        }
        getClassZu();
    }

    public void getClassZu() {
        GetClassZuApi getClassZuApi = new GetClassZuApi();
        getClassZuApi.setClassid(this.classid);
        ((StudentListPresenter) this.mPresenter).startPost(this, getClassZuApi);
    }

    @OnClick({2131624066, 2131624245})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.deletestu:
                Intent intent = new Intent(this, DeleteStudentActivity.class);
                intent.putExtra("classid", this.classid);
                intent.putExtra("classname", "");
                startActivity(intent);
                return;
            default:
                return;
        }
    }

    private void initDatas() {
        this.groupData = new ArrayList();
        this.groupData.add("全部学生(" + this.dataList.size() + ")");
        this.childItems = new ArrayList();
        for (student ss : this.dataList) {
            this.childItems.add(new ChildItem(ss.getSnickname(), ss.getUserid()));
        }
        this.childData = new HashMap();
        this.childData.put(Integer.valueOf(0), this.childItems);
        if (this.zuList.size() > 0) {
            for (int i = 0; i < this.zuList.size(); i++) {
                if (((classzuentity) this.zuList.get(i)).getSlist() != null) {
                    this.groupData.add(((classzuentity) this.zuList.get(i)).getZuname() + "(" + ((classzuentity) this.zuList.get(i)).getSlist().size() + ")");
                    this.ziList = ((classzuentity) this.zuList.get(i)).getSlist();
                    this.newItems = new ArrayList();
                    for (student ss2 : this.ziList) {
                        this.newItems.add(new ChildItem(ss2.getSnickname(), ss2.getUserid()));
                    }
                    this.childData.put(Integer.valueOf(i + 1), this.newItems);
                }
            }
        }
        this.myAdapter = new MyBaseExpandableListAdapter(this, this.groupData, this.childData);
        this.expandlist.setGroupIndicator(null);
        this.expandlist.setAdapter(this.myAdapter);
        this.expandlist.expandGroup(0);
    }

    public void getChildList(List<student> result) {
        this.dataList.clear();
        this.dataList.addAll(result);
        initDatas();
        this.myAdapter.notifyDataSetChanged();
    }

    public void getZuList(List<classzuentity> result) {
        this.zuList.clear();
        this.zuList.addAll(result);
        initDatas();
        this.myAdapter.notifyDataSetChanged();
    }

    public void getMyRole(String myRole) {
        this.roleString = myRole;
        if (this.roleString.equals("1") || this.roleString.equals("0")) {
            View view = LinearLayout.inflate(this, R.layout.layout_addzu, null);
            this.expandlist.addFooterView(view);
            this.addzu = (ImageView) view.findViewById(R.id.addzu);
            this.rela_createstudent = (RelativeLayout) view.findViewById(R.id.rela_createstudent);
            initEvents();
        }
    }

    private void initEvents() {
        this.rela_createstudent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StudentListActivity.this, AddCLassZuActivity.class);
                intent.putExtra("list", (Serializable) StudentListActivity.this.childItems);
                intent.putExtra("classid", StudentListActivity.this.classid);
                StudentListActivity.this.startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(AddClassZuEvent addClassZuEvent) {
        getClassZu();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateClassEvent updateClassEvent) {
        StudentListApi studentListApi = new StudentListApi();
        studentListApi.setClassid(this.classid);
        ((StudentListPresenter) this.mPresenter).startPost(this, studentListApi);
        getClassZu();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
