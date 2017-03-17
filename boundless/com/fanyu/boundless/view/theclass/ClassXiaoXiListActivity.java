package com.fanyu.boundless.view.theclass;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ClassXiaoXiListApi;
import com.fanyu.boundless.bean.theclass.applyentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.ClassXiaoXiListPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.RefreshApplyEvent;
import com.fanyu.boundless.view.myself.event.UpdateApplyEvent;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassXiaoXiListActivity extends BaseActivity<ClassXiaoXiListPresenter> implements IClassXiaoXiListView, PullLoadMoreListener {
    private RecyclerArrayAdapter<applyentity> adapter;
    private ClassXiaoXiListApi classXiaoXiListApi;
    private ClassXiaoXiListsAdapter classXiaoXiListsAdapter;
    private List<applyentity> dataList = new ArrayList();
    private applyentity entity;
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    private List<applyentity> moreList = new ArrayList();
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    @Bind({2131624097})
    PullLoadMoreRecyclerView recyclerView;
    private List<applyentity> refreshList = new ArrayList();
    private String userid;

    protected void initView() {
        setContentView((int) R.layout.activity_class_xiaoxi_list);
    }

    protected void initPresenter() {
        this.mPresenter = new ClassXiaoXiListPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.classXiaoXiListApi = new ClassXiaoXiListApi();
        this.classXiaoXiListApi.setUserid(this.userid);
        this.classXiaoXiListApi.setPage(this.page + "");
        this.classXiaoXiListApi.setPagesize("8");
        ((ClassXiaoXiListPresenter) this.mPresenter).startPost(this, this.classXiaoXiListApi, 0);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }

    public void onSuccess(List<applyentity> result, int State) {
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
        this.classXiaoXiListApi.setPage(this.page + "");
        ((ClassXiaoXiListPresenter) this.mPresenter).startPost(this, this.classXiaoXiListApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.classXiaoXiListApi.setPage(this.page + "");
        ((ClassXiaoXiListPresenter) this.mPresenter).startPost(this, this.classXiaoXiListApi, 1);
    }

    public void getXiaoXiList(List<applyentity> list, int state) {
        if (state == 0) {
            this.dataList.clear();
            this.dataList.addAll(list);
            System.out.println("datalist ===== " + list);
            this.classXiaoXiListsAdapter = new ClassXiaoXiListsAdapter(this.mContext, R.layout.adapter_classxiaoxi_list, this.dataList);
            this.footview = ((RxAppCompatActivity) this.mContext).getLayoutInflater().inflate(R.layout.footer_layout, null);
            findfootView(this.footview);
            this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.classXiaoXiListsAdapter);
            this.headerAndFooterWrapper.addFootView(this.footview);
            this.recyclerView.setAdapter(this.headerAndFooterWrapper);
            this.recyclerView.setOnPullLoadMoreListener(this);
            this.recyclerView.setPullLoadMoreCompleted();
            this.classXiaoXiListsAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (list.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            showTip("暂无数据");
            this.footview.setVisibility(8);
        } else if (state == 1) {
            this.refreshList.clear();
            this.refreshList.addAll(list);
            this.recyclerView.setPullLoadMoreCompleted();
            this.dataList.clear();
            this.dataList.addAll(this.refreshList);
            this.dataList.addAll(this.moreList);
            this.classXiaoXiListsAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (list.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footview.setVisibility(8);
        } else if (state == 2) {
            this.moreList.clear();
            this.moreList.addAll(list);
            this.recyclerView.setPullLoadMoreCompleted();
            this.dataList.addAll(this.moreList);
            this.classXiaoXiListsAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (list.size() == 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footTxt.setText("正在加载");
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateApplyEvent updateApplyEvent) {
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(RefreshApplyEvent refreshApplyEvent) {
        onRefresh();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
