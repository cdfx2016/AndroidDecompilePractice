package com.fanyu.boundless.view.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.bean.home.PosthomeworkentityApi;
import com.fanyu.boundless.bean.home.SchoolClassEntityApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.ZuoyeBoBaoPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.DeleteZuoYeEvent;
import com.fanyu.boundless.view.myself.event.UpdateNoticeEvent;
import com.fanyu.boundless.view.myself.event.UpdateZuoYeAdapterEvent;
import com.fanyu.boundless.view.theclass.PublishJobActivity;
import com.fanyu.boundless.widget.SpinerPopWindow;
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

public class NoticeMessageActivity extends BaseActivity<ZuoyeBoBaoPresenter> implements IZuoyeBoBaoView, PullLoadMoreListener {
    public static NoticeMessageActivity zuoyeBobaoActivity;
    private OnClickListener _poptextClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.selectclass_pop_all:
                    NoticeMessageActivity.this.mSpinerPopWindow.dismiss();
                    return;
                default:
                    return;
            }
        }
    };
    private RecyclerArrayAdapter<Posthomeworkentity> adapter;
    @Bind({2131624327})
    TextView addzuoye;
    private String className;
    @Bind({2131624085})
    TextView classname;
    private List<Posthomeworkentity> datalist = new ArrayList();
    private OnDismissListener dismissListener = new OnDismissListener() {
        public void onDismiss() {
        }
    };
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            NoticeMessageActivity.this.className = (String) NoticeMessageActivity.this.list.get(position);
            if (StringUtils.isEmpty(NoticeMessageActivity.this.className)) {
                NoticeMessageActivity.this.classname.setText(NoticeMessageActivity.this.className);
            }
            NoticeMessageActivity.this.mSpinerPopWindow.dismiss();
            if (NoticeMessageActivity.this.className.contains("全部班级")) {
                NoticeMessageActivity.this.posthomeworkentityApi.setClassid("");
                NoticeMessageActivity.this.onRefresh();
                return;
            }
            NoticeMessageActivity.this.posthomeworkentityApi.setClassid(((schoolclassentity) NoticeMessageActivity.this.schoolclasslist.get(position)).getId());
            NoticeMessageActivity.this.onRefresh();
        }
    };
    private List<String> list = new ArrayList();
    private SpinerPopWindow<String> mSpinerPopWindow;
    @Bind({2131624067})
    TextView messageTitle;
    private List<Posthomeworkentity> morelist = new ArrayList();
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    private Posthomeworkentity posthomeworkentity;
    private PosthomeworkentityApi posthomeworkentityApi;
    @Bind({2131624097})
    PullLoadMoreRecyclerView recyclerView;
    private List<Posthomeworkentity> refreshlist = new ArrayList();
    private List<schoolclassentity> schoolclasslist = new ArrayList();
    @Bind({2131624094})
    RelativeLayout selectclass;
    private String userid;
    private String usertype;
    private ZuoYeBoBaoNewAdapter zuoYeAdapter;

    protected void initView() {
        setContentView((int) R.layout.content_zuoye_bobao);
    }

    protected void initPresenter() {
        this.mPresenter = new ZuoyeBoBaoPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.messageTitle.setText("通知消息");
        this.usertype = this.msharepreference.getString(Preferences.USER_TYPE, "");
        if (this.usertype.equals("3")) {
            this.addzuoye.setVisibility(0);
        }
        this.posthomeworkentityApi = new PosthomeworkentityApi();
        this.posthomeworkentityApi.setUserid(this.userid);
        this.posthomeworkentityApi.setPage(this.page + "");
        this.posthomeworkentityApi.setPagesize("8");
        this.posthomeworkentityApi.setHwtype("2");
        this.posthomeworkentityApi.setClassid("");
        this.posthomeworkentityApi.setVersion("1.7");
        ((ZuoyeBoBaoPresenter) this.mPresenter).startPost(this, this.posthomeworkentityApi, 0);
        SchoolClassEntityApi schoolClassEntityApi = new SchoolClassEntityApi();
        schoolClassEntityApi.setPage("1");
        schoolClassEntityApi.setPagesize("20");
        schoolClassEntityApi.setUserid(this.userid);
        ((ZuoyeBoBaoPresenter) this.mPresenter).startPost(this, schoolClassEntityApi);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    public void getIZuoyeBoBao(List<Posthomeworkentity> result, int state) {
        if (state == 0) {
            System.out.println("ZUOYEBOBAO_result =======" + result);
            this.datalist.clear();
            this.datalist.addAll(result);
            System.out.println("datalist ===== " + this.datalist);
            this.zuoYeAdapter = new ZuoYeBoBaoNewAdapter(this.mContext, R.layout.adapter_zuoyebobaolist, this.datalist, this.userid, "2");
            this.footview = ((RxAppCompatActivity) this.mContext).getLayoutInflater().inflate(R.layout.footer_layout, null);
            findfootView(this.footview);
            this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.zuoYeAdapter);
            this.headerAndFooterWrapper.addFootView(this.footview);
            this.recyclerView.setAdapter(this.headerAndFooterWrapper);
            this.recyclerView.setOnPullLoadMoreListener(this);
            this.recyclerView.setPullLoadMoreCompleted();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footview.setVisibility(8);
            showTip("暂无数据");
        } else if (state == 1) {
            if (result.size() > 0) {
                this.refreshlist.clear();
                this.refreshlist.addAll(result);
                this.recyclerView.setPullLoadMoreCompleted();
                this.datalist.clear();
                this.datalist.addAll(this.refreshlist);
                this.datalist.addAll(this.morelist);
                this.headerAndFooterWrapper.notifyDataSetChanged();
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footview.setVisibility(8);
        } else if (state == 2) {
            this.morelist.clear();
            this.morelist.addAll(result);
            this.recyclerView.setPullLoadMoreCompleted();
            this.datalist.addAll(this.morelist);
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() == 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footTxt.setText("正在加载");
        }
    }

    public void getIMyClassName(List<schoolclassentity> schoolclassentities) {
        this.schoolclasslist.clear();
        if (schoolclassentities.size() > 0) {
            this.schoolclasslist.addAll(schoolclassentities);
            this.list.clear();
            for (schoolclassentity ss : schoolclassentities) {
                this.list.add(ss.getClassname());
            }
            this.className = "全部班级(" + this.list.size() + ")";
            this.list.add(this.className);
            this.classname.setText(this.className);
        } else {
            this.className = "全部班级(0)";
            this.list.add(this.className);
            this.classname.setText(this.className);
        }
        this.mSpinerPopWindow = new SpinerPopWindow(this, this.list, this.itemClickListener, this._poptextClickListener, this.selectclass);
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

    @OnClick({2131624066, 2131624094, 2131624327})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.selectclass:
                if (StringUtils.isEmpty(this.className)) {
                    this.mSpinerPopWindow.setDataString(this.className);
                    this.mSpinerPopWindow.showAtLocation(this.selectclass, 0, 0, 0);
                    return;
                }
                return;
            case R.id.addzuoye:
                Intent intent = new Intent(this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "2");
                this.mContext.startActivity(intent);
                return;
            default:
                return;
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
    public void helloEventBus(UpdateNoticeEvent updateNoticeEvent) {
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateZuoYeAdapterEvent updateZuoYeAdapterEvent) {
        if (updateZuoYeAdapterEvent.getType().equals("2")) {
            onRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(DeleteZuoYeEvent deleteZuoYeEvent) {
        if (deleteZuoYeEvent.getType().equals("2")) {
            onRefresh();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
