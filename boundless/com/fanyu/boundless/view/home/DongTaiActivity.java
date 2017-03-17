package com.fanyu.boundless.view.home;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.DongTaiApi;
import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.home.SchoolClassEntityApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.DongtaiPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdatePinglunEvent;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnItemClickListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnLoadMoreListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DongTaiActivity extends BaseActivity<DongtaiPresenter> implements IGetDongtaiView, OnRefreshListener, OnLoadMoreListener {
    private RecyclerArrayAdapter<DongTaiEntity> adapter;
    private List<schoolclassentity> classList = new ArrayList();
    private String dailytypeid = "1";
    private List<DongTaiEntity> datalist = new ArrayList();
    private DongTaiApi dongTaiApi;
    private DongTaiEntity dongTaiEntity;
    private DongTaiListNewAdapter dongtaiAdapter;
    @Bind({2131624121})
    TextView fabu;
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    private List<DongTaiEntity> morelist = new ArrayList();
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    @Bind({2131624097})
    EasyRecyclerView recyclerView;
    private List<DongTaiEntity> refreshlist = new ArrayList();
    private SchoolClassEntityApi schoolClassEntityApi;
    private String userid;
    private String username;

    protected void initView() {
        setContentView((int) R.layout.activity_dong_tai);
    }

    protected void initPresenter() {
        this.mPresenter = new DongtaiPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.username = this.msharepreference.getString(Preferences.LoginName, "");
        this.dongTaiApi = new DongTaiApi();
        this.dongTaiApi.setUserid(this.userid);
        this.dongTaiApi.setPage(this.page + "");
        this.dongTaiApi.setPagesize("8");
        this.dongTaiApi.setDailytypeid(this.dailytypeid);
        this.dongTaiApi.setUsername(this.username);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.adapter = new DongtaiListAdapter(this.mContext);
        this.recyclerView.setProgressView((int) R.layout.progressbar_recylerview);
        this.recyclerView.setAdapterWithProgress(this.adapter);
        this.adapter.setMore((int) R.layout.view_more, (OnLoadMoreListener) this);
        this.adapter.setNoMore((int) R.layout.view_nomore);
        this.recyclerView.setRefreshListener(this);
        onRefresh();
        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(int position) {
                DongTaiActivity.this.dongTaiEntity = (DongTaiEntity) DongTaiActivity.this.datalist.get(position);
                Intent intent = new Intent();
                intent.putExtra("id", DongTaiActivity.this.dongTaiEntity.getId());
                intent.putExtra("entity", DongTaiActivity.this.dongTaiEntity);
                intent.setClass(DongTaiActivity.this, DongtaiListItemActivity.class);
                DongTaiActivity.this.startActivityForResult(intent, 1);
            }
        });
        this.schoolClassEntityApi = new SchoolClassEntityApi();
        this.schoolClassEntityApi.setUserid(this.userid);
        this.schoolClassEntityApi.setPage(this.page + "");
        this.schoolClassEntityApi.setPagesize("8");
        ((DongtaiPresenter) this.mPresenter).startPost(this, this.schoolClassEntityApi);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdatePinglunEvent updateNameEvent) {
        showLoadingDialog();
        onRefresh();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({2131624066, 2131624121})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.fabu:
                this.mContext.startActivity(new Intent(this.mContext, PublishDongtaiActivity.class));
                return;
            default:
                return;
        }
    }

    public void getDongtai(List<DongTaiEntity> dongTaiEntities, int state) {
        onSuccess(dongTaiEntities, state);
    }

    public void getMyClass(List<schoolclassentity> schoolclassentities) {
        this.classList.clear();
        this.classList.addAll(schoolclassentities);
        if (this.classList != null && this.classList.size() > 0) {
            this.fabu.setVisibility(0);
        }
    }

    public void getGerenShuoShuo(List<DongTaiEntity> list, int state) {
    }

    public void addPinglun(String result) {
    }

    public void praiseSave(String result) {
    }

    public void praiseCancel(String result) {
    }

    public void praiseIsOrNo(String result) {
    }

    public void deleteDongtai(String result) {
    }

    public void onSuccess(List<DongTaiEntity> result, int State) {
        if (State == 1) {
            this.adapter.clear();
            this.datalist.clear();
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
        } else if (State == 2) {
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
            closeLoadingDialog();
        }
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.dongTaiApi.setPage(this.page + "");
        ((DongtaiPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.dongTaiApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.dongTaiApi.setPage(this.page + "");
        ((DongtaiPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.dongTaiApi, 1);
    }
}
