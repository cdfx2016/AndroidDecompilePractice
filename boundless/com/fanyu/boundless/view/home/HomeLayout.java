package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.app.MyApplication;
import com.fanyu.boundless.bean.home.GetUnreadApi;
import com.fanyu.boundless.bean.home.SearchVideoListTop4Api;
import com.fanyu.boundless.bean.home.maincount;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.MyColorPointHintView;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.HomePresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.VersionManagementUtil;
import com.fanyu.boundless.view.base.BaseLlayout;
import com.fanyu.boundless.view.microclass.MicroClassDetailActivity;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.view.myself.event.UpdateUnreadEvent;
import com.fanyu.boundless.view.theclass.PublishJobActivity;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.jude.rollviewpager.RollPagerView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class HomeLayout extends BaseLlayout<HomePresenter> implements IHomeView {
    @Bind({2131624449})
    TextView count;
    @Bind({2131624121})
    LinearLayout fabu;
    private String getleaveString = "0";
    @Bind({2131624443})
    TextView getleavecount;
    @Bind({2131624146})
    RecyclerView gridView;
    private String huodongString = "0";
    @Bind({2131624440})
    TextView huodongcount;
    private List<Integer> imagelist;
    private int[] imagelists = new int[]{R.mipmap.b1, R.mipmap.b2, R.mipmap.b3};
    private Handler mHandler;
    private SharedPreferencesUtil msharepreference;
    private LinearLayout mylayout;
    private String noticeString = "0";
    @Bind({2131624446})
    TextView noticecount;
    private String userid;
    private String usertype;
    private List<VideoEntity> videoEntities = new ArrayList();
    @Bind({2131624433})
    RollPagerView viewpager;
    private String zuoyeString = "0";
    @Bind({2131624437})
    TextView zuoyecount;

    public HomeLayout(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.layout_home;
    }

    protected void initPresenter() {
        this.mPresenter = new HomePresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.imagelist = new ArrayList();
        this.imagelist.add(Integer.valueOf(R.mipmap.b1));
        this.imagelist.add(Integer.valueOf(R.mipmap.b2));
        this.imagelist.add(Integer.valueOf(R.mipmap.b3));
        this.viewpager.setHintView(new MyColorPointHintView(this.mContext, Color.parseColor("#7E633D"), Color.parseColor("#B9B9B9")));
        this.viewpager.setAdapter(new ImageLoopAdapter(this.viewpager, this.imagelist));
        getUnreadMessage();
        checkVersion();
        SearchVideoListTop4Api searchVideoListTop4Api = new SearchVideoListTop4Api();
        searchVideoListTop4Api.setUserid(this.userid);
        ((HomePresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, searchVideoListTop4Api);
    }

    public void getUnreadMessage() {
        this.usertype = this.msharepreference.getString(Preferences.USER_TYPE, "");
        if (this.usertype.equals("3")) {
            this.fabu.setVisibility(0);
        }
        GetUnreadApi getUnreadApi = new GetUnreadApi();
        getUnreadApi.setUserid(this.userid);
        ((HomePresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, getUnreadApi);
    }

    @OnClick({2131624435, 2131624438, 2131624441, 2131624444, 2131624447, 2131624121, 2131624302, 2131624452})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabu:
                showPopupWindowFaBu(view);
                return;
            case R.id.shuzi:
                Intent intent5 = new Intent(this.mContext, MicroClassDetailActivity.class);
                intent5.putExtra("tittle", "奇幻的数字之旅");
                intent5.putExtra("content", "学霸第一步，我为你指路。阿拉伯数字，是现今国际通用数字，最初由印度人发明，后由阿拉伯人传向欧洲，之后再经欧洲人将其现代化。正因阿拉伯人的传播，成为该种数字最终被国际通用的关键节点，所以人们称其为“阿拉伯数字”。阿拉伯数字由0，1，2，3，4，5，6，7，8，9共10个计数符号组成，人们利用这10个简单的数字编织出了多姿多彩的数学世界。本系列节目将带领小朋友了解这10个数字的有趣之处。");
                intent5.putExtra("pid", "3916050257584071927429b64b4366c1");
                this.mContext.startActivity(intent5);
                return;
            case R.id.ll_btn_zuoye:
                this.mContext.startActivity(new Intent(this.mContext, ZuoyeBobaoActivity.class));
                return;
            case R.id.ll_btn_huodong:
                this.mContext.startActivity(new Intent(this.mContext, HuodongZuoyeActivity.class));
                return;
            case R.id.ll_btn_daoli:
                this.mContext.startActivity(new Intent(this.mContext, ArriveOrLeaveSchoolActivity.class));
                return;
            case R.id.ll_btn_tongzhi:
                this.mContext.startActivity(new Intent(this.mContext, NoticeMessageActivity.class));
                return;
            case R.id.ll_btn_dongtai:
                this.mContext.startActivity(new Intent(this.mContext, DongTaiActivity.class));
                return;
            case R.id.yingyu:
                Intent intent6 = new Intent(this.mContext, MicroClassDetailActivity.class);
                intent6.putExtra("tittle", "科技英语 - Hello,Moon");
                intent6.putExtra("content", "玩转英语，渗透科普，洋溢趣味，你，不容错过！一座神奇的房子，一次神秘的奇遇，一趟精彩的月球探索之旅；沉醉在英语故事中，萌芽在科普知识时，成长在英语世界里。");
                intent6.putExtra("pid", "40e7dcb724484a48b44abd995555b585");
                this.mContext.startActivity(intent6);
                return;
            default:
                return;
        }
    }

    private void showPopupWindowFaBu(View view) {
        View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.layout_spiner_fabu, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, -1, -1, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(-1342177280));
        this.mylayout = (LinearLayout) contentView.findViewById(R.id.liebiao);
        contentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = HomeLayout.this.mylayout.getTop();
                int bottom = HomeLayout.this.mylayout.getBottom();
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
                Intent intent = new Intent(HomeLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "0");
                HomeLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.huodongzuoye)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "1");
                HomeLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.banjidongtai)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HomeLayout.this.mContext.startActivity(new Intent(HomeLayout.this.mContext, PublishDongtaiActivity.class));
                popupWindow.dismiss();
            }
        });
        ((RelativeLayout) contentView.findViewById(R.id.tongzhixiaoxi)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeLayout.this.mContext, PublishJobActivity.class);
                intent.putExtra(MessageEncoder.ATTR_TYPE, "2");
                HomeLayout.this.mContext.startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }

    public void getUnreadMessage(maincount mcount) {
        this.zuoyeString = mcount.getZuoyecount();
        this.huodongString = mcount.getHuodongcount();
        this.getleaveString = mcount.getGetleavecount();
        this.noticeString = mcount.getNoticecount();
        if (this.zuoyeString.equals("0")) {
            this.zuoyecount.setVisibility(4);
        } else {
            this.zuoyecount.setVisibility(0);
            this.zuoyecount.setText(this.zuoyeString);
        }
        if (this.huodongString.equals("0")) {
            this.huodongcount.setVisibility(4);
        } else {
            this.huodongcount.setVisibility(0);
            this.huodongcount.setText(this.huodongString);
        }
        if (this.getleaveString.equals("0")) {
            this.getleavecount.setVisibility(4);
        } else {
            this.getleavecount.setVisibility(0);
            this.getleavecount.setText(this.getleaveString);
        }
        if (this.noticeString.equals("0")) {
            this.noticecount.setVisibility(4);
        } else {
            this.noticecount.setVisibility(0);
            this.noticecount.setText(this.noticeString);
        }
        EventBus.getDefault().post(new UpdateUnreadEvent(String.valueOf(((Integer.valueOf(this.zuoyeString).intValue() + Integer.valueOf(this.huodongString).intValue()) + Integer.valueOf(this.getleaveString).intValue()) + Integer.valueOf(this.noticeString).intValue()), 2));
    }

    public void searchVideoListTop4(List<VideoEntity> videoEntityList) {
        this.videoEntities.clear();
        this.videoEntities.addAll(videoEntityList);
        FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(getContext(), 2);
        fullyGridLayoutManager.setScrollEnabled(false);
        this.gridView.setLayoutManager(fullyGridLayoutManager);
        this.gridView.setNestedScrollingEnabled(false);
        HomeVideoAdapter homeVideoAdapter = new HomeVideoAdapter(getContext(), R.layout.adapter_searchvideolisttop, this.videoEntities);
        homeVideoAdapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(ViewHolder arg0, int arg1) {
                VideoEntity entity = (VideoEntity) HomeLayout.this.videoEntities.get(arg1);
                Intent intent = new Intent();
                intent.setClass(HomeLayout.this.getContext(), MicroClassDetailActivity.class);
                intent.putExtra("tittle", entity.getVideoname());
                intent.putExtra("content", entity.getContent());
                intent.putExtra("pid", entity.getAlbumid());
                intent.putExtra("videoid", entity.getId());
                HomeLayout.this.mContext.startActivity(intent);
            }
        });
        this.gridView.setAdapter(homeVideoAdapter);
        homeVideoAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateMainMessageEvent updateMainMessageEvent) {
        getUnreadMessage();
    }

    private void checkVersion() {
        MyApplication.localVersionCode = VersionManagementUtil.getVersionCode(this.mContext);
        switch (VersionManagementUtil.VersionComparison(MyApplication.serverVersionCode, MyApplication.localVersionCode)) {
            case 1:
                new UpdateDialog(this.mContext).show();
                return;
            default:
                return;
        }
    }
}
