package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.UnshowStuApi;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.presenter.home.UnshowStuPresenter;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.theclass.UnShowStudentAdapter;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class UnshowStuActivity extends BaseActivity<UnshowStuPresenter> implements IUnshowStuView {
    private UnShowStudentAdapter adapter;
    private String itemid;
    @Bind({2131624067})
    TextView messageTitle;
    List<ChildItem> mlist = new ArrayList();
    @Bind({2131624255})
    RecyclerView stuRecyclerview;
    @Bind({2131624254})
    TextView zanwu;

    protected void initView() {
        setContentView((int) R.layout.activity_unshow_stu);
    }

    protected void initPresenter() {
        this.mPresenter = new UnshowStuPresenter(this.mContext, this);
    }

    protected void init() {
        this.itemid = getIntent().getStringExtra("itemid");
        System.out.println("未读itemid =========== " + this.itemid);
        UnshowStuApi unshowStuApi = new UnshowStuApi();
        unshowStuApi.setItmeid(this.itemid);
        ((UnshowStuPresenter) this.mPresenter).startPost(this, unshowStuApi);
        this.stuRecyclerview.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.adapter = new UnShowStudentAdapter(this.mContext, R.layout.grid_item, this.mlist);
        this.stuRecyclerview.setAdapter(this.adapter);
    }

    public void getUnreadStu(List<student> list) {
        if (list.size() == 0) {
            this.zanwu.setVisibility(0);
            return;
        }
        this.zanwu.setVisibility(8);
        for (int i = 0; i < list.size(); i++) {
            this.mlist.add(new ChildItem(((student) list.get(i)).getSnickname(), ((student) list.get(i)).getUserid()));
        }
        this.adapter.notifyDataSetChanged();
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }
}
