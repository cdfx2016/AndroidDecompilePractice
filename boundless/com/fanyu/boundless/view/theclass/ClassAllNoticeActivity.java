package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.bean.home.PosthomeworkentityApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.ZuoyeBoBaoPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.home.IZuoyeBoBaoView;
import com.fanyu.boundless.view.home.ZuoyeBobaoAdapter;
import com.fanyu.boundless.view.home.ZuoyeBobaoListItemActivity;
import com.fanyu.boundless.view.myself.event.DeleteZuoYeEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassNameEvent;
import com.fanyu.boundless.view.myself.event.UpdateZuoYeAdapterEvent;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnItemClickListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnLoadMoreListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassAllNoticeActivity extends BaseActivity<ZuoyeBoBaoPresenter> implements IZuoyeBoBaoView, OnLoadMoreListener, OnRefreshListener {
    private RecyclerArrayAdapter<Posthomeworkentity> adapter;
    private List<Posthomeworkentity> datalist = new ArrayList();
    schoolclassentity entity;
    @Bind({2131624067})
    TextView messageTitle;
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    private Posthomeworkentity posthomeworkentity;
    private PosthomeworkentityApi posthomeworkentityApi;
    @Bind({2131624097})
    EasyRecyclerView recyclerView;
    private String userid;

    protected void initView() {
        setContentView((int) R.layout.activity_class_allnotice);
    }

    protected void initPresenter() {
        this.mPresenter = new ZuoyeBoBaoPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        MyActivityManager.getsInstances().pushActivity(this);
        this.entity = (schoolclassentity) getIntent().getSerializableExtra("entity");
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.messageTitle.setText(this.entity.getClassname());
        }
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.posthomeworkentityApi = new PosthomeworkentityApi();
        this.posthomeworkentityApi.setUserid(this.userid);
        this.posthomeworkentityApi.setPage(this.page + "");
        this.posthomeworkentityApi.setPagesize("8");
        this.posthomeworkentityApi.setClassid(this.entity.getId());
        this.posthomeworkentityApi.setVersion("1.7");
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.adapter = new ZuoyeBobaoAdapter(this.mContext);
        this.recyclerView.setAdapterWithProgress(this.adapter);
        this.adapter.setMore((int) R.layout.view_more, (OnLoadMoreListener) this);
        this.adapter.setNoMore((int) R.layout.view_nomore);
        this.recyclerView.setRefreshListener(this);
        onRefresh();
        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(int position) {
                ClassAllNoticeActivity.this.posthomeworkentity = (Posthomeworkentity) ClassAllNoticeActivity.this.datalist.get(position);
                Intent intent = new Intent();
                intent.putExtra("entity", ClassAllNoticeActivity.this.posthomeworkentity);
                intent.putExtra("position", position);
                intent.putExtra("leixing", "all");
                intent.setClass(ClassAllNoticeActivity.this.mContext, ZuoyeBobaoListItemActivity.class);
                ClassAllNoticeActivity.this.mContext.startActivity(intent);
            }
        });
    }

    @OnClick({2131624066, 2131624096})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.classxinxi:
                Intent intent = new Intent();
                intent.setClass(this, ClassXiaoXiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entity", this.entity);
                intent.putExtras(bundle);
                startActivityForResult(intent, 9);
                return;
            default:
                return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateClassNameEvent updateClassNameEvent) {
        this.messageTitle.setText(updateClassNameEvent.getClassname());
    }

    public void getIZuoyeBoBao(List<Posthomeworkentity> result, int state) {
        onSuccess(result, state);
    }

    public void getIMyClassName(List<schoolclassentity> list) {
    }

    public void onSuccess(List<Posthomeworkentity> result, int State) {
        if (State == 1) {
            this.adapter.clear();
            this.datalist.clear();
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
        } else if (State == 2) {
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
        }
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.posthomeworkentityApi.setPage(this.page + "");
        ((ZuoyeBoBaoPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.posthomeworkentityApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.posthomeworkentityApi.setPage(this.page + "");
        ((ZuoyeBoBaoPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.posthomeworkentityApi, 1);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateZuoYeAdapterEvent updateZuoYeAdapterEvent) {
        if (updateZuoYeAdapterEvent.getType().equals("3")) {
            this.posthomeworkentity = (Posthomeworkentity) this.datalist.get(updateZuoYeAdapterEvent.getPosition());
            this.posthomeworkentity.setShowcount(updateZuoYeAdapterEvent.getShowcount());
            this.posthomeworkentity.setLiuyancount(updateZuoYeAdapterEvent.getLiuyancount());
            this.posthomeworkentity.setIsread("2");
            this.adapter.update(this.posthomeworkentity, updateZuoYeAdapterEvent.getPosition());
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(DeleteZuoYeEvent deleteZuoYeEvent) {
        if (deleteZuoYeEvent.getType().equals("3")) {
            this.adapter.remove(deleteZuoYeEvent.getPosition());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
