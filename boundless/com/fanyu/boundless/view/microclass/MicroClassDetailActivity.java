package com.fanyu.boundless.view.microclass;

import android.hardware.SensorManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.AddPingLunApi;
import com.fanyu.boundless.bean.microclass.PlayNumApi;
import com.fanyu.boundless.bean.microclass.SpinglunApi;
import com.fanyu.boundless.bean.microclass.SpinglunEntity;
import com.fanyu.boundless.bean.microclass.VideoApi;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.bean.microclass.ZanApi;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.microclass.MicroClassDetaiPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.NetWorkUtils;
import com.fanyu.boundless.util.ScreenOrientationUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.widget.HVideoPlayer;
import com.fanyu.boundless.widget.HVideoPlayer.JCAutoFullscreenListener;
import com.fanyu.boundless.widget.HVideoPlayer.PlayCountListener;
import com.fanyu.boundless.widget.recyclerview.MyLinearLayoutManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import java.util.ArrayList;
import java.util.List;

public class MicroClassDetailActivity extends BaseActivity<MicroClassDetaiPresenter> implements MicroClassDetailView {
    private static final int SHRINK_UP_STATE = 1;
    private static final int SPREAD_STATE = 2;
    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 1;
    private static int mState = 1;
    private String Plcount = "0";
    private Button button;
    @Bind({2131624243})
    TextView clickmore;
    @Bind({2131624158})
    TextView commitnum;
    private String content = "";
    @Bind({2131624406})
    TextView continuePlay;
    private boolean diancai = true;
    private boolean dianzan = true;
    private EditText edittext;
    @Bind({2131624066})
    ImageView imgReturn;
    private InputMethodManager imm;
    private ScreenOrientationUtil instance;
    @Bind({2131624238})
    TextView kaile;
    @Bind({2131624240})
    LinearLayout llPinglunContent;
    @Bind({2131624405})
    LinearLayout llWifi;
    @Bind({2131624228})
    LinearLayout llayoutShowPinglunImg;
    @Bind({2131624067})
    TextView messageTitle;
    @Bind({2131624239})
    RecyclerView mylistivew;
    @Bind({2131624232})
    LinearLayout neirong;
    private int page = 1;
    private String pid = "";
    @Bind({2131624241})
    TextView pinglun;
    private CommonAdapter<SpinglunEntity> pinglunAdapter;
    private List<SpinglunEntity> plList;
    private PlayNumApi playNumApi;
    @Bind({2131624242})
    RecyclerView pllistivew;
    private PopupWindow popupWindow;
    @Bind({2131624233})
    LinearLayout rlDd;
    private JCAutoFullscreenListener sensorEventListener;
    private SensorManager sensorManager;
    @Bind({2131624226})
    TextView showCaiImg;
    @Bind({2131624227})
    TextView showCainums;
    @Bind({2131624224})
    TextView showDianzanImg;
    @Bind({2131624225})
    TextView showDianzannums;
    @Bind({2131624229})
    ImageView showPinglunImg;
    @Bind({2131624230})
    TextView showPinglunnums;
    @Bind({2131624235})
    TextView showShipinjianjie;
    @Bind({2131624234})
    TextView showShipinjianjieTitle;
    @Bind({2131624223})
    TextView showWatchnums;
    @Bind({2131624222})
    ImageView showWatchs;
    private RelativeLayout shurukuangLayout;
    private SpinglunApi spinglunApi;
    @Bind({2131624404})
    HVideoPlayer surfaceView;
    @Bind({2131624231})
    ScrollView svVideoscrollview;
    private String tittle = "";
    private String userId;
    private VideoListAdapter videoAdapter;
    private VideoEntity videoEntity;
    private List<VideoEntity> videoList;
    private String videoid = "";
    private ZanApi zanApi;
    private String zancai = "";
    private boolean zancaistate = true;
    @Bind({2131624237})
    ImageView zankai;
    @Bind({2131624236})
    LinearLayout zhankai;

    public class ItemListenter implements OnItemClickListener {
        public void onItemClick(ViewHolder arg0, int arg1) {
            if (!NetWorkUtils.getInstance().getCurrentNetType(MicroClassDetailActivity.this.mContext).equals("wifi")) {
                Toast.makeText(MicroClassDetailActivity.this.mContext, "您正在使用非wifi网络,播放将产生流量费用", 0).show();
            }
            MicroClassDetailActivity.this.instance.stop();
            MicroClassDetailActivity.this.videoEntity = (VideoEntity) MicroClassDetailActivity.this.videoList.get(arg1);
            if (StringUtils.isEmpty(MicroClassDetailActivity.this.videoEntity.getVideoname())) {
                MicroClassDetailActivity.this.messageTitle.setText(MicroClassDetailActivity.this.videoEntity.getVideoname());
            }
            MicroClassDetailActivity.this.videoAdapter.setSelectedItem(arg1);
            MicroClassDetailActivity.this.videoAdapter.notifyDataSetChanged();
            MicroClassDetailActivity.this.setcount(MicroClassDetailActivity.this.videoEntity);
            MicroClassDetailActivity.this.spinglunApi.setVideoid(MicroClassDetailActivity.this.videoEntity.getId());
            MicroClassDetailActivity.this.spinglunApi.setPage(MicroClassDetailActivity.this.page + "");
            MicroClassDetailActivity.this.spinglunApi.setPagesize("10");
            ((MicroClassDetaiPresenter) MicroClassDetailActivity.this.mPresenter).startPost(MicroClassDetailActivity.this, MicroClassDetailActivity.this.spinglunApi, 1);
            JCVideoPlayer.releaseAllVideos();
            MicroClassDetailActivity.this.videoPlayer(MicroClassDetailActivity.this.videoEntity.getFilename(), MicroClassDetailActivity.this.videoEntity.getVideoname());
        }
    }

    private class Listener implements OnClickListener {
        private Listener() {
        }

        public void onClick(View v) {
            String replycontent = MicroClassDetailActivity.this.edittext.getText().toString();
            MicroClassDetailActivity.this.edittext.setText("");
            if (MicroClassDetailActivity.this.imm != null) {
                MicroClassDetailActivity.this.imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            AddPingLunApi addPingLunApi = new AddPingLunApi();
            addPingLunApi.setDailyid(MicroClassDetailActivity.this.videoEntity.getId());
            addPingLunApi.setReplycontent(replycontent);
            addPingLunApi.setUserid(MicroClassDetailActivity.this.userId);
            if (replycontent == null || replycontent.equals("")) {
                Toast.makeText(MicroClassDetailActivity.this, "请您填写评论内容！", 0).show();
            } else {
                ((MicroClassDetaiPresenter) MicroClassDetailActivity.this.mPresenter).startPost(MicroClassDetailActivity.this, addPingLunApi);
            }
        }
    }

    private class PlaynumListener implements PlayCountListener {
        private PlaynumListener() {
        }

        public void playCount() {
            MicroClassDetailActivity.this.playNumApi.setVideoid(MicroClassDetailActivity.this.videoEntity.getId());
            ((MicroClassDetaiPresenter) MicroClassDetailActivity.this.mPresenter).startPost(MicroClassDetailActivity.this, MicroClassDetailActivity.this.playNumApi);
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_shiping);
    }

    protected void initPresenter() {
        this.mPresenter = new MicroClassDetaiPresenter(this.mContext, this);
    }

    protected void init() {
        showLoadingDialog();
        this.instance = ScreenOrientationUtil.getInstance();
        this.sensorManager = (SensorManager) getSystemService("sensor");
        HVideoPlayer hVideoPlayer = new HVideoPlayer(this.mContext);
        hVideoPlayer.getClass();
        this.sensorEventListener = new JCAutoFullscreenListener();
        getWindow().setSoftInputMode(18);
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.videoid = getIntent().getStringExtra("videoid");
        this.userId = SharedPreferencesUtil.getsInstances(this.mContext).getString(Preferences.USER_ID, "");
        this.tittle = getIntent().getStringExtra("tittle");
        if (StringUtils.isEmpty(this.tittle)) {
            this.showShipinjianjieTitle.setText(this.tittle);
        }
        this.content = getIntent().getStringExtra("content");
        if (StringUtils.isEmpty(this.content)) {
            this.showShipinjianjie.setText(this.content);
        }
        this.pid = getIntent().getStringExtra("pid");
        this.imgReturn.setVisibility(0);
        this.videoList = new ArrayList(0);
        this.mylistivew.setNestedScrollingEnabled(false);
        this.videoAdapter = new VideoListAdapter(this.mContext, R.layout.adapter_video, this.videoList);
        this.videoAdapter.setOnItemClickListener(new ItemListenter());
        MyLinearLayoutManager videolayoutManager = new MyLinearLayoutManager(this);
        videolayoutManager.setScrollEnabled(false);
        this.mylistivew.setHasFixedSize(true);
        this.mylistivew.setLayoutManager(videolayoutManager);
        this.mylistivew.setNestedScrollingEnabled(false);
        this.mylistivew.setAdapter(this.videoAdapter);
        VideoApi videoApi = new VideoApi();
        videoApi.setUserid(this.userId);
        videoApi.setAlbumid(this.pid);
        ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, videoApi);
        this.plList = new ArrayList(0);
        this.pinglunAdapter = new PingLunAdapter(this, R.layout.adapter_pinglun, this.plList);
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(this);
        linearLayoutManager.setScrollEnabled(false);
        this.pllistivew.setHasFixedSize(true);
        this.pllistivew.setLayoutManager(linearLayoutManager);
        this.pllistivew.setNestedScrollingEnabled(false);
        this.pllistivew.setAdapter(this.pinglunAdapter);
        this.zanApi = new ZanApi();
        this.playNumApi = new PlayNumApi();
        this.surfaceView.setPlayCountListener(new PlaynumListener());
        if (NetWorkUtils.getInstance().getCurrentNetType(this.mContext).equals("wifi")) {
            this.llWifi.setVisibility(8);
        } else {
            this.llWifi.setVisibility(0);
        }
    }

    @OnClick({2131624066, 2131624243, 2131624228, 2131624224, 2131624226, 2131624236, 2131624241, 2131624406})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                JCVideoPlayer.releaseAllVideos();
                finish();
                return;
            case R.id.show_dianzan_img:
                this.zancaistate = true;
                if (this.dianzan) {
                    this.dianzan = false;
                    this.zancai = "praise";
                    this.zanApi.setVideoid(this.videoEntity.getId());
                    this.zanApi.setUserid(this.userId);
                    this.zanApi.setType(this.zancai);
                    ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.zanApi);
                } else {
                    this.dianzan = true;
                    this.zancai = "nopraise";
                    this.zanApi.setVideoid(this.videoEntity.getId());
                    this.zanApi.setUserid(this.userId);
                    this.zanApi.setType(this.zancai);
                    ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.zanApi);
                }
                this.showDianzanImg.setClickable(false);
                return;
            case R.id.show_cai_img:
                this.zancaistate = false;
                if (this.diancai) {
                    this.diancai = false;
                    this.zancai = "tread";
                    this.zanApi.setVideoid(this.videoEntity.getId());
                    this.zanApi.setUserid(this.userId);
                    this.zanApi.setType(this.zancai);
                    ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.zanApi);
                } else {
                    this.diancai = true;
                    this.zancai = "notread";
                    this.zanApi.setVideoid(this.videoEntity.getId());
                    this.zanApi.setUserid(this.userId);
                    this.zanApi.setType(this.zancai);
                    ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.zanApi);
                }
                this.showCaiImg.setClickable(false);
                return;
            case R.id.llayout_show_pinglun_img:
                this.llPinglunContent.getTop();
                this.svVideoscrollview.scrollTo(0, this.llPinglunContent.getTop());
                return;
            case R.id.zhankai:
                if (mState == 2) {
                    this.zankai.setBackgroundResource(R.mipmap.iconfont_zhan);
                    this.kaile.setText("展开");
                    this.showShipinjianjie.setMaxLines(1);
                    this.showShipinjianjie.requestLayout();
                    mState = 1;
                    return;
                } else if (mState == 1) {
                    this.zankai.setBackgroundResource(R.mipmap.iconfont_shou);
                    this.kaile.setText("收起");
                    this.showShipinjianjie.setMaxLines(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    this.showShipinjianjie.requestLayout();
                    mState = 2;
                    return;
                } else {
                    return;
                }
            case R.id.pinglun:
                this.llPinglunContent.getTop();
                this.svVideoscrollview.scrollTo(0, this.llPinglunContent.getTop());
                shuru(view);
                return;
            case R.id.clickmore:
                this.page++;
                this.spinglunApi.setPage(this.page + "");
                ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.spinglunApi, 2);
                return;
            case R.id.continue_play:
                this.llWifi.setVisibility(8);
                this.surfaceView.startButton.performClick();
                return;
            default:
                return;
        }
    }

    public void searchWeikeListSecond(List<VideoEntity> result) {
        if (result != null) {
            this.videoList.clear();
            this.videoList.addAll(result);
            if (this.videoList.size() <= 0) {
                return;
            }
            if (this.videoid != null) {
                int i = 0;
                for (VideoEntity entity : this.videoList) {
                    if (entity.getId().equals(this.videoid)) {
                        i = this.videoList.indexOf(entity);
                        break;
                    }
                }
                this.videoAdapter.setSelectedItem(i);
                this.videoEntity = (VideoEntity) this.videoList.get(i);
                videoPlayer(this.videoEntity.getFilename(), this.videoEntity.getVideoname());
                if (StringUtils.isEmpty(this.videoEntity.getVideoname())) {
                    this.messageTitle.setText(this.videoEntity.getVideoname());
                }
                setcount(this.videoEntity);
                this.spinglunApi = new SpinglunApi();
                this.spinglunApi.setVideoid(this.videoEntity.getId());
                this.spinglunApi.setPage(this.page + "");
                this.spinglunApi.setPagesize("10");
                ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.spinglunApi, 1);
                return;
            }
            this.videoAdapter.setSelectedItem(0);
            this.videoEntity = (VideoEntity) this.videoList.get(0);
            videoPlayer(this.videoEntity.getFilename(), this.videoEntity.getVideoname());
            if (StringUtils.isEmpty(this.videoEntity.getVideoname())) {
                this.messageTitle.setText(this.videoEntity.getVideoname());
            }
            setcount(this.videoEntity);
            this.spinglunApi = new SpinglunApi();
            this.spinglunApi.setVideoid(this.videoEntity.getId());
            this.spinglunApi.setPage(this.page + "");
            this.spinglunApi.setPagesize("10");
            ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.spinglunApi, 1);
        }
    }

    public void videoPlayer(String url, String tittle) {
        try {
            this.surfaceView.setUp(ImagePathUtil.getInstance().getPath(url), 0, tittle);
            this.surfaceView.titleTextView.setVisibility(8);
            this.surfaceView.thumbImageView.setImageResource(R.mipmap.weike_bj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void xjObtainVedioComment(List<SpinglunEntity> result, int state) {
        onSuccess(result, state);
    }

    public void onSuccess(List<SpinglunEntity> result, int State) {
        if (State == 1) {
            this.plList.clear();
            this.plList.addAll(result);
            this.pinglunAdapter.notifyDataSetChanged();
            if (this.plList.size() == 0) {
                this.clickmore.setText("暂无评论");
            } else if (this.plList.size() < 10) {
                this.clickmore.setText("已加载全部");
            } else {
                this.clickmore.setText("查看更多");
            }
            if (this.plList.size() > 0) {
                this.Plcount = ((SpinglunEntity) this.plList.get(0)).getTotalRecord();
            } else {
                this.Plcount = "0";
            }
            this.commitnum.setText("共有" + this.Plcount);
            if (StringUtils.isEmpty(this.Plcount)) {
                this.showPinglunnums.setText(this.Plcount);
            }
            closeLoadingDialog();
        } else if (State == 2) {
            this.plList.addAll(result);
            this.pinglunAdapter.notifyDataSetChanged();
            if (result.size() == 0) {
                this.clickmore.setText("已加载全部");
            } else {
                this.clickmore.setText("查看更多");
            }
        }
    }

    public void zanzan(String result) {
        if (!result.equals("true")) {
            Toast.makeText(this, "点击失败...", 0).show();
            if (this.zancaistate) {
                this.showDianzanImg.setClickable(true);
            } else {
                this.showCaiImg.setClickable(true);
            }
        } else if (this.zancaistate) {
            int zai;
            if (this.dianzan) {
                zai = Integer.parseInt(this.showDianzannums.getText().toString());
                this.showDianzannums.setText((zai - 1) + "");
                this.showDianzanImg.setBackgroundResource(R.mipmap.shipinshow_iv_dianzan_press);
                this.videoEntity.setPraisecount((zai - 1) + "");
                this.videoEntity.setIspraise("0");
            } else {
                zai = Integer.parseInt(this.showDianzannums.getText().toString());
                this.showDianzannums.setText((zai + 1) + "");
                this.showDianzanImg.setBackgroundResource(R.mipmap.shipinshow_iv_dianzan_normal);
                this.videoEntity.setPraisecount((zai + 1) + "");
                this.videoEntity.setIspraise("1");
            }
            this.showDianzanImg.setClickable(true);
        } else {
            int cai;
            if (this.diancai) {
                cai = Integer.parseInt(this.showCainums.getText().toString());
                this.showCainums.setText((cai - 1) + "");
                this.showCaiImg.setBackgroundResource(R.mipmap.shipinshow_iv_cai_normal);
                this.videoEntity.setTreadcount((cai - 1) + "");
                this.videoEntity.setIstread("0");
            } else {
                cai = Integer.parseInt(this.showCainums.getText().toString());
                this.showCainums.setText((cai + 1) + "");
                this.showCaiImg.setBackgroundResource(R.mipmap.shipinshow_iv_cai_press);
                this.videoEntity.setTreadcount((cai + 1) + "");
                this.videoEntity.setIstread("1");
            }
            this.showCaiImg.setClickable(true);
        }
    }

    public void savePinglun(String result) {
        if (result == null && result == "") {
            Toast.makeText(this.mContext, "评论失败", 1).show();
            return;
        }
        Toast.makeText(this.mContext, "评论成功", 1).show();
        this.popupWindow.dismiss();
        if (this.imm.isActive()) {
            this.imm.toggleSoftInput(0, 2);
        }
        ((MicroClassDetaiPresenter) this.mPresenter).startPost(this, this.spinglunApi, 1);
    }

    public void xjVCheckNum(String result) {
        if ("true".equals(result)) {
            int r = Integer.parseInt(this.showWatchnums.getText().toString()) + 1;
            this.showWatchnums.setText(r + "");
            this.videoEntity.setPlaycount(r + "");
        }
    }

    public void setcount(VideoEntity vEntity) {
        VideoEntity ventity = vEntity;
        if (StringUtils.isEmpty(ventity.getPlaycount())) {
            this.showWatchnums.setText(ventity.getPlaycount());
        }
        if (StringUtils.isEmpty(ventity.getPraisecount())) {
            this.showDianzannums.setText(ventity.getPraisecount());
        }
        if (ventity.getIspraise().equals("1")) {
            this.showDianzanImg.setBackgroundResource(R.mipmap.shipinshow_iv_dianzan_normal);
            this.dianzan = false;
        } else {
            this.showDianzanImg.setBackgroundResource(R.mipmap.shipinshow_iv_dianzan_press);
            this.dianzan = true;
        }
        if (StringUtils.isEmpty(ventity.getTreadcount())) {
            this.showCainums.setText(ventity.getTreadcount());
        }
        if (ventity.getIstread().equals("1")) {
            this.showCaiImg.setBackgroundResource(R.mipmap.shipinshow_iv_cai_press);
            this.diancai = false;
        } else {
            this.showCaiImg.setBackgroundResource(R.mipmap.shipinshow_iv_cai_normal);
            this.diancai = true;
        }
        if (StringUtils.isEmpty(this.Plcount)) {
            this.showPinglunnums.setText(this.Plcount);
        }
    }

    public void shuru(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.edit_text, null);
        this.popupWindow = new PopupWindow(contentView, -1, -2, true);
        this.popupWindow.setTouchable(true);
        this.shurukuangLayout = (RelativeLayout) contentView.findViewById(R.id.rl_bottom);
        this.edittext = (EditText) contentView.findViewById(R.id.et_input);
        this.button = (Button) contentView.findViewById(R.id.ib_send);
        this.button.setOnClickListener(new Listener());
        this.edittext.requestFocus();
        if (this.imm != null) {
            this.imm.toggleSoftInput(0, 2);
        } else {
            this.imm.toggleSoftInput(0, 2);
        }
        contentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    MicroClassDetailActivity.this.imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    MicroClassDetailActivity.this.popupWindow.dismiss();
                }
                return true;
            }
        });
        this.popupWindow.showAtLocation(view, 80, 0, 0);
    }

    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }

    protected void onResume() {
        super.onResume();
        this.sensorManager.registerListener(this.sensorEventListener, this.sensorManager.getDefaultSensor(1), 3);
    }

    public void onBackPressed() {
        if (!JCVideoPlayer.backPress()) {
            super.onBackPressed();
        } else if (this.surfaceView != null) {
            this.surfaceView = null;
        }
    }
}
