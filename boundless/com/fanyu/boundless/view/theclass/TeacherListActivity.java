package com.fanyu.boundless.view.theclass;

import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.TeachListApi;
import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.presenter.theclass.TeacherListPresenter;
import com.fanyu.boundless.view.base.BaseActivity;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnLoadMoreListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TeacherListActivity extends BaseActivity<TeacherListPresenter> implements ITeacherListView, OnLoadMoreListener, OnRefreshListener {
    private RecyclerArrayAdapter<classmember> adapter;
    private String classid;
    private List<classmember> dataList = new ArrayList();
    private int page = 1;
    @Bind({2131624097})
    EasyRecyclerView recyclerView;
    private TeachListApi teachListApi;

    protected void initView() {
        setContentView((int) R.layout.activity_teacher_list);
    }

    protected void initPresenter() {
        this.mPresenter = new TeacherListPresenter(this.mContext, this);
    }

    protected void init() {
        this.classid = getIntent().getStringExtra("classid");
        this.teachListApi = new TeachListApi();
        this.teachListApi.setClassid(this.classid);
        this.teachListApi.setPage(this.page + "");
        this.teachListApi.setPagesize("8");
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.adapter = new TeacherListAdapter(this.mContext);
        this.recyclerView.setAdapterWithProgress(this.adapter);
        this.adapter.setMore((int) R.layout.view_more, (OnLoadMoreListener) this);
        this.adapter.setNoMore((int) R.layout.view_nomore);
        this.recyclerView.setRefreshListener(this);
        onRefresh();
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }

    public void getTeacherList(List<classmember> list, int state) {
        onSuccess(list, state);
    }

    public void onSuccess(List<classmember> result, int State) {
        if (State == 1) {
            this.adapter.clear();
            this.dataList.clear();
            this.dataList.addAll(result);
            this.adapter.addAll(this.dataList);
        } else if (State == 2) {
            this.dataList.addAll(result);
            this.adapter.addAll((Collection) result);
        }
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.teachListApi.setPage(this.page + "");
        ((TeacherListPresenter) this.mPresenter).startPost(this, this.teachListApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.teachListApi.setPage(this.page + "");
        ((TeacherListPresenter) this.mPresenter).startPost(this, this.teachListApi, 1);
    }
}
