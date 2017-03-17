package com.fanyu.boundless.view.theclass;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.theclass.DeleteStudentApi;
import com.fanyu.boundless.bean.theclass.GetAllClassMemberApi;
import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.presenter.theclass.DeleteStudentPresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.SelectClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.xiaomi.mipush.sdk.Constants;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteStudentActivity extends BaseActivity<DeleteStudentPresenter> implements IDeleteStudentView {
    private StudentAdapter adapter;
    private String classid;
    @Bind({2131624073})
    RecyclerView gridRecycleview;
    private Map<Integer, Boolean> mSelectMap = new HashMap();
    @Bind({2131624067})
    TextView messageTitle;
    List<ChildItem> mlist = new ArrayList();
    String selectsum = "";

    class StudentListener implements OnItemClickListener {
        StudentListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (DeleteStudentActivity.this.mSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) DeleteStudentActivity.this.mSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                DeleteStudentActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) DeleteStudentActivity.this.mlist.get(position)).setIscheck(false);
            } else {
                DeleteStudentActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) DeleteStudentActivity.this.mlist.get(position)).setIscheck(true);
            }
            System.out.println(position + "==============");
            System.out.println("mSelectMap.get(i)" + DeleteStudentActivity.this.mSelectMap.get(Integer.valueOf(position)));
            DeleteStudentActivity.this.adapter.notifyDataSetChanged();
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_deletestudent);
    }

    protected void initPresenter() {
        this.mPresenter = new DeleteStudentPresenter(this.mContext, this);
    }

    protected void init() {
        this.classid = getIntent().getStringExtra("classid");
        this.messageTitle.setText("删除学生");
        this.gridRecycleview.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.adapter = new StudentAdapter(this.mContext, R.layout.grid_item, this.mlist);
        this.gridRecycleview.setAdapter(this.adapter);
        GetAllClassMemberApi getAllClassMemberApi = new GetAllClassMemberApi();
        getAllClassMemberApi.setClassid(this.classid);
        ((DeleteStudentPresenter) this.mPresenter).startPost(this, getAllClassMemberApi);
        this.adapter.setOnItemClickListener(new StudentListener());
    }

    @OnClick({2131624066, 2131624119})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.shanchu:
                delete();
                return;
            default:
                return;
        }
    }

    public void getStudentList(List<classmember> result) {
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                this.mlist.add(new ChildItem(((classmember) result.get(i)).getMembername(), ((classmember) result.get(i)).getUserid()));
                this.mSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public void isdelete(String isdelete) {
        if (StringUtils.isEmpty(isdelete)) {
            Toast.makeText(this, "删除成功！", 0).show();
            EventBus.getDefault().post(new UpdateClassEvent());
            EventBus.getDefault().post(new SelectClassEvent());
            finish();
        }
    }

    public void delete() {
        String msg = "";
        this.selectsum = "";
        for (int i = 0; i < this.mSelectMap.size(); i++) {
            if (((Boolean) this.mSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                msg = ((ChildItem) this.mlist.get(i)).getId();
                if (!msg.equals("")) {
                    this.selectsum += (msg + Constants.ACCEPT_TIME_SEPARATOR_SP);
                }
            }
        }
        if (StringUtils.isEmpty(this.selectsum)) {
            Builder alert = new Builder(this);
            alert.setTitle("确定要删除勾选的学生？").setPositiveButton("确定", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DeleteStudentApi deleteStudentApi = new DeleteStudentApi();
                    deleteStudentApi.setClassid(DeleteStudentActivity.this.classid);
                    deleteStudentApi.setUserid(DeleteStudentActivity.this.selectsum);
                    ((DeleteStudentPresenter) DeleteStudentActivity.this.mPresenter).startPost(DeleteStudentActivity.this, deleteStudentApi);
                }
            }).setNegativeButton("取消", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.create().show();
            return;
        }
        Toast.makeText(this, "请您选择删除的成员！", 0).show();
    }
}
