package com.fanyu.boundless.view.home;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.bean.home.GetMyZuoYeListApi;
import com.fanyu.boundless.presenter.home.ZuoYeListPresenter;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import java.util.ArrayList;
import java.util.List;

public class ZuoYeListActivity extends BaseActivity<ZuoYeListPresenter> implements IZuoYeListView, PullLoadMoreListener {
    private RecyclerArrayAdapter<ClassHuifuEntity> adapter;
    private List<ClassHuifuEntity> dataList = new ArrayList();
    ClassHuifuEntity entity;
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    private GetMyZuoYeListApi getMyZuoYeListApi;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    private String itemid;
    private List<ClassHuifuEntity> moreList = new ArrayList();
    private int page = 1;
    @Bind({2131624097})
    PullLoadMoreRecyclerView recyclerView;
    private List<ClassHuifuEntity> refreshList = new ArrayList();
    private String senduserid;
    private ZuoYeListsAdapter zuoYeAdapter;

    protected void initView() {
        setContentView((int) R.layout.activity_zuoye_list);
    }

    protected void initPresenter() {
        this.mPresenter = new ZuoYeListPresenter(this.mContext, this);
    }

    protected void init() {
        this.itemid = getIntent().getStringExtra("itemid");
        this.senduserid = getIntent().getStringExtra("senduserid");
        this.getMyZuoYeListApi = new GetMyZuoYeListApi();
        this.getMyZuoYeListApi.setItemid(this.itemid);
        this.getMyZuoYeListApi.setPage(this.page + "");
        this.getMyZuoYeListApi.setPagesize("8");
        ((ZuoYeListPresenter) this.mPresenter).startPost(this, this.getMyZuoYeListApi, 0);
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.getMyZuoYeListApi.setPage(this.page + "");
        ((ZuoYeListPresenter) this.mPresenter).startPost(this, this.getMyZuoYeListApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.getMyZuoYeListApi.setPage(this.page + "");
        ((ZuoYeListPresenter) this.mPresenter).startPost(this, this.getMyZuoYeListApi, 1);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    public void getXiaoXiList(List<ClassHuifuEntity> list, int state) {
        if (state == 0) {
            this.dataList.clear();
            this.dataList.addAll(list);
            this.zuoYeAdapter = new ZuoYeListsAdapter(this.mContext, R.layout.adapter_zuoye_list, this.dataList, this.itemid, this.senduserid);
            this.footview = ((RxAppCompatActivity) this.mContext).getLayoutInflater().inflate(R.layout.footer_layout, null);
            findfootView(this.footview);
            this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.zuoYeAdapter);
            this.headerAndFooterWrapper.addFootView(this.footview);
            this.recyclerView.setAdapter(this.headerAndFooterWrapper);
            this.recyclerView.setOnPullLoadMoreListener(this);
            this.recyclerView.setPullLoadMoreCompleted();
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
            this.zuoYeAdapter.notifyDataSetChanged();
            if (list.size() == 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footTxt.setText("正在加载");
        }
    }
}
