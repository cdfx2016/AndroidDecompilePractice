package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ClassListApi;
import com.fanyu.boundless.bean.theclass.GetUnreadApplyApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.ClassPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseLlayout;
import com.fanyu.boundless.view.home.PublishDongtaiActivity;
import com.fanyu.boundless.view.myself.event.UpdateApplyEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateUnreadEvent;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class ClassLayout extends BaseLlayout<ClassPresenter> implements IClassView, PullLoadMoreListener {
    private RecyclerArrayAdapter<schoolclassentity> adapter;
    private ClassAdapter classAdapter;
    private ClassListApi classListApi;
    private List<schoolclassentity> dataList = new ArrayList();
    schoolclassentity entity;
    @Bind({2131624429})
    TextView fabuTextview;
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    private boolean hasNetWork = true;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    @Bind({2131624432})
    TextView isclass;
    private List<schoolclassentity> moreList = new ArrayList();
    private SharedPreferencesUtil msharepreference;
    private LinearLayout mylayout;
    private int page = 1;
    @Bind({2131624428})
    TextView paopao;
    @Bind({2131624097})
    PullLoadMoreRecyclerView recyclerView;
    private List<schoolclassentity> refreshList = new ArrayList();
    private String userid;
    private String usertype;

    public ClassLayout(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.layout_class;
    }

    protected void initPresenter() {
        this.mPresenter = new ClassPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        selectUnread();
        showfabu();
        this.page = 1;
        this.classListApi = new ClassListApi();
        this.classListApi.setUserid(this.userid);
        this.classListApi.setPage(this.page + "");
        this.classListApi.setPagesize("8");
        ((ClassPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.classListApi, 0);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    public void showfabu() {
        this.usertype = this.msharepreference.getString(Preferences.USER_TYPE, "");
        if (this.usertype.equals("3")) {
            this.fabuTextview.setVisibility(0);
        }
    }

    @OnClick({2131624427, 2131624429, 2131624430, 2131624431})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_icon:
                this.mContext.startActivity(new Intent(this.mContext, ClassXiaoXiListActivity.class));
                return;
            case R.id.fabu_textview:
                showPopupWindowFaBu(view);
                return;
            case R.id.createclass:
                this.mContext.startActivity(new Intent(this.mContext, CreateClassActivity.class));
                return;
            case R.id.joinclass:
                this.mContext.startActivity(new Intent(this.mContext, JoinClassActivity.class));
                return;
            default:
                return;
        }
    }

    public void selectUnread() {
        GetUnreadApplyApi getUnreadApplyApi = new GetUnreadApplyApi();
        getUnreadApplyApi.setUserid(this.userid);
        ((ClassPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, getUnreadApplyApi);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateClassEvent updateClassEvent) {
        showfabu();
        selectUnread();
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateApplyEvent updateApplyEvent) {
        selectUnread();
    }

    public void getClassList(List<schoolclassentity> result, int state) {
        System.out.println("result ==========" + result);
        if (state == 0) {
            this.dataList.clear();
            this.dataList.addAll(result);
            System.out.println("datalist ===== " + this.dataList);
            this.classAdapter = new ClassAdapter(this.mContext, R.layout.adapter_classlist, this.dataList, this.userid);
            this.footview = ((RxAppCompatActivity) this.mContext).getLayoutInflater().inflate(R.layout.footer_layout, null);
            findfootView(this.footview);
            this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.classAdapter);
            this.headerAndFooterWrapper.addFootView(this.footview);
            this.recyclerView.setAdapter(this.headerAndFooterWrapper);
            this.recyclerView.setOnPullLoadMoreListener(this);
            this.recyclerView.setPullLoadMoreCompleted();
            this.classAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
            } else {
                showTip("暂无数据");
                this.footview.setVisibility(8);
            }
        }
        if (state == 1) {
            this.refreshList.clear();
            this.refreshList.addAll(result);
            this.recyclerView.setPullLoadMoreCompleted();
            this.dataList.clear();
            this.dataList.addAll(this.refreshList);
            this.dataList.addAll(this.moreList);
            this.classAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footview.setVisibility(8);
        } else if (state == 2) {
            this.moreList.clear();
            this.moreList.addAll(result);
            this.recyclerView.setPullLoadMoreCompleted();
            this.dataList.addAll(this.moreList);
            this.classAdapter.notifyDataSetChanged();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() == 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footTxt.setText("正在加载");
        }
    }

    public void getUnread(String unread) {
        if (unread.equals("0")) {
            this.paopao.setVisibility(8);
        } else {
            this.paopao.setVisibility(0);
            this.paopao.setText(unread);
        }
        EventBus.getDefault().post(new UpdateUnreadEvent(unread, 1));
    }

    private void showPopupWindowFaBu(View view) {
        View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.layout_spiner_fabu, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, -1, -1, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(-1342177280));
        this.mylayout = (LinearLayout) contentView.findViewById(R.id.liebiao);
        contentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = ClassLayout.this.mylayout.getTop();
                int bottom = ClassLayout.this.mylayout.getBottom();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 1) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                    if (y > bottom) {
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });
        popupWindow.showAtLocation(view, 17, 0, 0);
        ((RelativeLayout) contentView.findViewById(R.id.zuoyebobao)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ClassLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "0");
                ClassLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.huodongzuoye)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ClassLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "1");
                ClassLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.banjidongtai)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ClassLayout.this.mContext.startActivity(new Intent(ClassLayout.this.mContext, PublishDongtaiActivity.class));
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.tongzhixiaoxi)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ClassLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "2");
                ClassLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }

    public void onLoadMore() {
        this.page++;
        Log.i("onLoadMore", "page" + this.page);
        this.classListApi.setPage(this.page + "");
        ((ClassPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.classListApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        Log.w("onRefresh", "page" + this.page);
        this.classListApi.setPage(this.page + "");
        ((ClassPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.classListApi, 1);
    }
}
