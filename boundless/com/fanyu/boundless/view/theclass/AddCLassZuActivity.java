package com.fanyu.boundless.view.theclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.AddZuApi;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.presenter.theclass.AddZuPresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.AddClassZuEvent;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.xiaomi.mipush.sdk.Constants;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCLassZuActivity extends BaseActivity<AddZuPresenter> implements IAddZuView {
    private StudentAdapter adapter;
    private String classid;
    @Bind({2131624072})
    EditText editzuname;
    @Bind({2131624073})
    RecyclerView gridRecycleview;
    private Map<Integer, Boolean> mSelectMap = new HashMap();
    List<ChildItem> mlist = new ArrayList();
    String selectsum = "";
    private String zuname;

    class StudentListener implements OnItemClickListener {
        StudentListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (AddCLassZuActivity.this.mSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) AddCLassZuActivity.this.mSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                AddCLassZuActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) AddCLassZuActivity.this.mlist.get(position)).setIscheck(false);
            } else {
                AddCLassZuActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) AddCLassZuActivity.this.mlist.get(position)).setIscheck(true);
            }
            System.out.println(position + "==============");
            System.out.println("mSelectMap.get(i)" + AddCLassZuActivity.this.mSelectMap.get(Integer.valueOf(position)));
            AddCLassZuActivity.this.adapter.notifyDataSetChanged();
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_add_classzu);
    }

    protected void initPresenter() {
        this.mPresenter = new AddZuPresenter(this.mContext, this);
    }

    protected void init() {
        this.mlist = (List) getIntent().getSerializableExtra("list");
        for (int i = 0; i < this.mlist.size(); i++) {
            this.mSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
        }
        this.classid = getIntent().getStringExtra("classid");
        this.gridRecycleview.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.adapter = new StudentAdapter(this.mContext, R.layout.grid_item, this.mlist);
        this.gridRecycleview.setAdapter(this.adapter);
        this.adapter.setOnItemClickListener(new StudentListener());
    }

    public void isadd(String isadd) {
        if (StringUtils.isEmpty(isadd)) {
            EventBus.getDefault().post(new AddClassZuEvent());
            finish();
        }
    }

    @OnClick({2131624066, 2131624071})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.queding:
                addzu();
                return;
            default:
                return;
        }
    }

    public void addzu() {
        String msg = "";
        String sum = "";
        System.out.println("mSelectMap.size()" + this.mSelectMap.size());
        for (int i = 0; i < this.mSelectMap.size(); i++) {
            System.out.println("mSelectMap.get(i)" + this.mSelectMap.get(Integer.valueOf(i)));
            if (((Boolean) this.mSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                msg = ((ChildItem) this.mlist.get(i)).getId();
                if (!msg.equals("")) {
                    sum = sum + (msg + Constants.ACCEPT_TIME_SEPARATOR_SP);
                }
            }
        }
        this.zuname = this.editzuname.getText().toString();
        if (sum == null || sum.equals("")) {
            Toast.makeText(this, "请选择成员", 0).show();
        } else if (this.zuname == null || this.zuname.equals("")) {
            Toast.makeText(this, "请输入学生组名称", 0).show();
        } else {
            AddZuApi addZuApi = new AddZuApi();
            addZuApi.setStudentid(sum);
            addZuApi.setZuname(this.zuname);
            addZuApi.setClassid(this.classid);
            ((AddZuPresenter) this.mPresenter).startPost(this, addZuApi);
        }
    }
}
