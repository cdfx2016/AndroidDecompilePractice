package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AddGetLeaveNoticeApi;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.theclass.GetClassZuApi;
import com.fanyu.boundless.bean.theclass.StudentListApi;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.GetNoticePresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdateAddEvent;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.view.theclass.StudentAdapter;
import com.fanyu.boundless.view.theclass.ZuAdapter;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.xiaomi.mipush.sdk.Constants;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNoticeActivity extends BaseActivity<GetNoticePresenter> implements IGetNoticeView {
    private AddGetLeaveNoticeApi addGetLeaveNoticeApi;
    private String biaojiString;
    private String className;
    private String classid;
    private String content;
    private List<student> dataList = new ArrayList();
    @Bind({2131624179})
    EditText editreason;
    @Bind({2131624177})
    TextView fasong;
    private String gstype;
    @Bind({2131624165})
    RecyclerView gvGridPickerView;
    private Map<Integer, Boolean> mSelectMap = new HashMap();
    @Bind({2131624067})
    TextView messageTitle;
    private StudentAdapter mgridadapter;
    private List<ChildItem> mlist = new ArrayList();
    @Bind({2131624120})
    TextView selectren;
    SharedPreferencesUtil sharedPreferencesUtil;
    @Bind({2131624178})
    TextView shuoming;
    @Bind({2131624180})
    TextView tishi;
    private String userid;
    @Bind({2131624167})
    TextView xiaozu;
    private List<ChildItem> zlist = new ArrayList();
    private ZuAdapter zuGridAdapter;
    @Bind({2131624168})
    RecyclerView zuGridPickerView;
    private List<classzuentity> zuList = new ArrayList();
    private Map<Integer, Boolean> zuSelectMap = new HashMap();
    @Bind({2131624181})
    TextView zuid;

    class StudentListener implements OnItemClickListener {
        StudentListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (GetNoticeActivity.this.mSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) GetNoticeActivity.this.mSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                GetNoticeActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) GetNoticeActivity.this.mlist.get(position)).setIscheck(false);
            } else {
                GetNoticeActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) GetNoticeActivity.this.mlist.get(position)).setIscheck(true);
            }
            GetNoticeActivity.this.mgridadapter.notifyDataSetChanged();
        }
    }

    class ZuListener implements OnItemClickListener {
        ZuListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (GetNoticeActivity.this.zuSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) GetNoticeActivity.this.zuSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                GetNoticeActivity.this.zuSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) GetNoticeActivity.this.zlist.get(position)).setIscheck(false);
            } else {
                GetNoticeActivity.this.zuSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) GetNoticeActivity.this.zlist.get(position)).setIscheck(true);
            }
            GetNoticeActivity.this.zuGridAdapter.notifyDataSetChanged();
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_get_notice);
    }

    protected void initPresenter() {
        this.mPresenter = new GetNoticePresenter(this.mContext, this);
    }

    protected void init() {
        getWindow().setSoftInputMode(3);
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.classid = getIntent().getStringExtra("classid");
        this.biaojiString = getIntent().getStringExtra("tittle");
        this.className = getIntent().getStringExtra("classname");
        this.gstype = getIntent().getStringExtra("gstype");
        StudentListApi studentListApi = new StudentListApi();
        studentListApi.setClassid(this.classid);
        ((GetNoticePresenter) this.mPresenter).startPost(this, studentListApi);
        this.gvGridPickerView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.mgridadapter = new StudentAdapter(this.mContext, R.layout.grid_item, this.mlist);
        this.gvGridPickerView.setAdapter(this.mgridadapter);
        this.mgridadapter.setOnItemClickListener(new StudentListener());
        setData();
    }

    @OnClick({2131624066})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            default:
                return;
        }
    }

    public void setData() {
        if (this.biaojiString.equals("getschoolentity")) {
            if (StringUtils.isEmpty(this.className)) {
                this.messageTitle.setText(this.className);
            }
            this.content = "全部到校";
            this.editreason.setHint("请填写迟到说明");
            this.shuoming.setText("迟到说明");
            this.selectren.setText("请勾选迟到学生");
            this.tishi.setText("提示：选中学生家长将收到“未准时到校”消息和迟到说明，未选中学生家长将收到“准时到校”消息");
            this.zuid.setVisibility(8);
            this.zuGridPickerView.setVisibility(8);
            return;
        }
        this.messageTitle.setText(this.className);
        this.content = "全部离校";
        this.shuoming.setText("留校说明");
        this.zuid.setText("请勾选留校学生");
        this.xiaozu.setVisibility(0);
        this.editreason.setVisibility(0);
        this.tishi.setText("提示：选中学生家长将收到“留校以及说明”消息，未选中学生家长将收到“已放学”消息");
        this.editreason.setHint("请填写留校说明");
        GetClassZuApi getClassZuApi = new GetClassZuApi();
        getClassZuApi.setClassid(this.classid);
        ((GetNoticePresenter) this.mPresenter).startPost(this, getClassZuApi);
        this.zuGridPickerView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.zuGridAdapter = new ZuAdapter(this.mContext, R.layout.new_grid_item, this.zlist);
        this.zuGridPickerView.setAdapter(this.zuGridAdapter);
        this.zuGridAdapter.setOnItemClickListener(new ZuListener());
    }

    public void getChildList(List<student> students) {
        this.dataList.clear();
        this.dataList.addAll(students);
        this.mlist.clear();
        for (int i = 0; i < this.dataList.size(); i++) {
            this.mlist.add(new ChildItem(((student) this.dataList.get(i)).getSnickname(), ((student) this.dataList.get(i)).getUserid()));
            this.mSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
        }
        this.mgridadapter.notifyDataSetChanged();
    }

    public void getZuList(List<classzuentity> students) {
        this.zuList.clear();
        this.zuList.addAll(students);
        this.zlist.clear();
        for (int i = 0; i < this.zuList.size(); i++) {
            this.zlist.add(new ChildItem(((classzuentity) this.zuList.get(i)).getZuname(), ((classzuentity) this.zuList.get(i)).getId()));
            this.zuSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
        }
        this.zuGridAdapter.notifyDataSetChanged();
    }

    public void addGet(String isadd) {
        EventBus.getDefault().post(new UpdateAddEvent());
        EventBus.getDefault().post(new UpdateMainMessageEvent());
        finish();
    }

    @OnClick({2131624177})
    public void onClick() {
        addGetLeave();
    }

    public void addGetLeave() {
        int i;
        String editcontont = this.editreason.getText().toString();
        String msg = "";
        String selectsum = "";
        String unselectsum = "";
        String stuname = "";
        String studentnamesum = "";
        String selectzu = "";
        for (i = 0; i < this.mSelectMap.size(); i++) {
            if (((Boolean) this.mSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                msg = ((ChildItem) this.mlist.get(i)).getId();
                if (!msg.equals("")) {
                    selectsum = selectsum + (msg + Constants.ACCEPT_TIME_SEPARATOR_SP);
                }
            } else {
                stuname = ((ChildItem) this.mlist.get(i)).getTitle();
                msg = ((ChildItem) this.mlist.get(i)).getId();
                if (!msg.equals("")) {
                    msg = msg + Constants.ACCEPT_TIME_SEPARATOR_SP;
                    stuname = stuname + Constants.ACCEPT_TIME_SEPARATOR_SP;
                    unselectsum = unselectsum + msg;
                    studentnamesum = studentnamesum + stuname;
                }
            }
        }
        for (i = 0; i < this.zuSelectMap.size(); i++) {
            if (((Boolean) this.zuSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                msg = ((classzuentity) this.zuList.get(i)).getId();
                if (!msg.equals("")) {
                    selectzu = selectzu + (msg + Constants.ACCEPT_TIME_SEPARATOR_SP);
                }
            }
        }
        if (!StringUtils.isEmpty(editcontont)) {
            editcontont = "无";
        }
        SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this.mContext);
        editor.putString(Preferences.CLASS_NAME, this.className);
        editor.putString(Preferences.CLASS_ID, this.classid);
        this.addGetLeaveNoticeApi = new AddGetLeaveNoticeApi();
        this.addGetLeaveNoticeApi.setClassid(this.classid);
        this.addGetLeaveNoticeApi.setUserid(this.userid);
        this.addGetLeaveNoticeApi.setClassname(this.className);
        this.addGetLeaveNoticeApi.setEditcontent(editcontont);
        this.addGetLeaveNoticeApi.setGstype(this.gstype);
        this.addGetLeaveNoticeApi.setTittle("通知");
        this.addGetLeaveNoticeApi.setYijian("noticeentity");
        this.addGetLeaveNoticeApi.setContent(this.content);
        this.addGetLeaveNoticeApi.setSelectsum(selectsum);
        this.addGetLeaveNoticeApi.setUnselectsum(unselectsum);
        this.addGetLeaveNoticeApi.setSelectzu(selectzu);
        this.addGetLeaveNoticeApi.setStudentnamesum(studentnamesum);
        ((GetNoticePresenter) this.mPresenter).startPost(this, this.addGetLeaveNoticeApi);
    }
}
