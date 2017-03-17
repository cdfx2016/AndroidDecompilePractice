package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PointerIconCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil.SwitchClickListener;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryFinal.OnHanlderResultCallback;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.bean.home.ClassHuifuEntityApi;
import com.fanyu.boundless.bean.home.DeleteGeRenZuoYeApi;
import com.fanyu.boundless.bean.home.DeleteZuoYeApi;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.bean.home.SaveCLassLiuYanApi;
import com.fanyu.boundless.bean.home.UpdateGeRenUnreadMessageApi;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.ClassHuifuPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.home.HuifuLeft.deleteBack;
import com.fanyu.boundless.view.home.HuifuLeft.huifuBack;
import com.fanyu.boundless.view.myself.event.DeleteZuoYeEvent;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.view.myself.event.UpdateZuoYeAdapterEvent;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.fanyu.boundless.widget.tagview.Tag;
import com.fanyu.boundless.widget.tagview.TagView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xiaomi.mipush.sdk.Constants;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter.OnItemClickListener;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ZuoyeBobaoListItemActivity extends BaseActivity<ClassHuifuPresenter> implements IClassHuifuView, PullLoadMoreListener {
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
    MultiItemTypeAdapter adapter;
    TextView banjiname;
    private int biaoji;
    @Bind({2131624253})
    TextView chuli;
    private List<ClassHuifuEntity> classHuifuEntities = new ArrayList();
    ClassHuifuEntityApi classHuifuEntityApi;
    TextView content;
    TextView deleteTextview;
    private TagView dx_tagview;
    private ImageView fabuImageView;
    private String filetype;
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    RecyclerView gridView;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    View headview = null;
    HuifuLeft huifuLeft;
    HuifuRight huifuRight;
    private String hwtype;
    private String itemid;
    private String leixing;
    private String liucount;
    TextView liuyancount;
    LoadMoreWrapper mLoadMoreWrapper;
    private OnHanlderResultCallback mOnHanlderResultCallback = new OnHanlderResultCallback() {
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(ZuoyeBobaoListItemActivity.this.mPanelLayout);
                String pathString = ((PhotoInfo) resultList.get(0)).getPhotoPath();
                ImageUtils imageUtils = new ImageUtils();
                String mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".jpg");
                Bitmap photo = imageUtils.getimage(pathString, mBigImageName);
                ZuoyeBobaoListItemActivity.this.getPathImg(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya/" + mBigImageName);
                ZuoyeBobaoListItemActivity.this.recyclerView.setscrollToPosition(1);
            }
        }

        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };
    private KPSwitchPanelLinearLayout mPanelLayout;
    private ImageView mPlusIv;
    private EditText mSendEdt;
    @Bind({2131624067})
    TextView messageTitle;
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    private Posthomeworkentity posthomeworkentity;
    TextView publishTime;
    @Bind({2131624097})
    PullLoadMoreRecyclerView recyclerView;
    private TextView selcetPhoto;
    private TextView sendContent;
    private LinearLayout sendMsgLayout;
    TextView showcount;
    LinearLayout showlayout;
    private String showstucount;
    TextView stucount;
    private TextView takePhoto;
    private String teacherid;
    TextView title;
    private String upResult;
    ImageView userhead;
    private String userid;
    TextView username;

    protected void initView() {
        setContentView((int) R.layout.activity_zuoye_bobao_item);
    }

    protected void initPresenter() {
        this.mPresenter = new ClassHuifuPresenter(this.mContext, this);
    }

    protected void init() {
        setJianPan();
        this.posthomeworkentity = (Posthomeworkentity) getIntent().getSerializableExtra("entity");
        this.biaoji = getIntent().getIntExtra("position", 0);
        this.leixing = getIntent().getStringExtra("leixing");
        this.teacherid = this.posthomeworkentity.getUserid();
        this.itemid = this.posthomeworkentity.getId();
        this.hwtype = this.posthomeworkentity.getHwtype();
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.headview = getLayoutInflater().inflate(R.layout.activity_zuoye_bobao_item_headview, null);
        this.footview = getLayoutInflater().inflate(R.layout.footer_layout, null);
        findViewById(this.headview);
        findfootView(this.footview);
        initdate();
        this.classHuifuEntityApi = new ClassHuifuEntityApi();
        this.classHuifuEntityApi.setPage(this.page + "");
        this.classHuifuEntityApi.setPagesize("8");
        this.classHuifuEntityApi.setItemid(this.posthomeworkentity.getId());
        this.recyclerView.setOnPullLoadMoreListener(this);
        this.huifuLeft = new HuifuLeft(this);
        this.huifuRight = new HuifuRight(this);
        this.adapter = new MultiItemTypeAdapter(this, this.classHuifuEntities);
        this.adapter.addItemViewDelegate(this.huifuLeft);
        this.adapter.addItemViewDelegate(this.huifuRight);
        this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.adapter);
        this.headerAndFooterWrapper.addHeaderView(this.headview);
        this.headerAndFooterWrapper.addFootView(this.footview);
        this.recyclerView.setAdapter(this.headerAndFooterWrapper);
        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, ViewHolder holder, int position) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(ZuoyeBobaoListItemActivity.this.mPanelLayout);
            }

            public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(ZuoyeBobaoListItemActivity.this.mPanelLayout);
                return false;
            }
        });
        this.recyclerView.getmRecyclerView().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(ZuoyeBobaoListItemActivity.this.mPanelLayout);
                }
                return false;
            }
        });
        this.headerAndFooterWrapper.notifyDataSetChanged();
        this.huifuLeft.setHfBack(new huifuBack() {
            public void onhfListen(String name) {
                ZuoyeBobaoListItemActivity.this.huifuZuoYe(name);
            }
        });
        this.huifuRight.setDeBack(new deleteBack() {
            public void onListen(int count) {
                if (count > 0) {
                    count--;
                }
                DeleteGeRenZuoYeApi deleteGeRenZuoYeApi = new DeleteGeRenZuoYeApi();
                deleteGeRenZuoYeApi.setUserid(ZuoyeBobaoListItemActivity.this.userid);
                deleteGeRenZuoYeApi.setItemid(((ClassHuifuEntity) ZuoyeBobaoListItemActivity.this.classHuifuEntities.get(count)).getId());
                ((ClassHuifuPresenter) ZuoyeBobaoListItemActivity.this.mPresenter).startPost(ZuoyeBobaoListItemActivity.this, deleteGeRenZuoYeApi);
            }
        });
        onRefresh();
        setData();
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    public void initdate() {
        int i = 0;
        if (this.posthomeworkentity.getHwtype().equals("2")) {
            this.chuli.setVisibility(8);
            this.messageTitle.setText("通知详情");
        } else {
            this.messageTitle.setText("作业详情");
            this.chuli.setVisibility(0);
        }
        if (this.userid.equals(this.teacherid) && this.posthomeworkentity.getIsread().equals("2")) {
            this.chuli.setText("批作业");
            this.banjiname.setVisibility(8);
            this.fabuImageView.setVisibility(0);
            this.dx_tagview.setVisibility(0);
            this.deleteTextview.setVisibility(0);
            this.deleteTextview.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ZuoyeBobaoListItemActivity.this.showAlertDialog();
                }
            });
            String[] sz = this.posthomeworkentity.getClassname().split(Constants.ACCEPT_TIME_SEPARATOR_SP);
            this.username.setText("我");
            this.dx_tagview.clear();
            int length = sz.length;
            while (i < length) {
                String ss = sz[i];
                if (StringUtils.isEmpty(ss)) {
                    this.dx_tagview.add(new Tag(ss));
                }
                i++;
            }
        } else {
            this.fabuImageView.setVisibility(8);
            this.dx_tagview.setVisibility(8);
            if (StringUtils.isEmpty(this.posthomeworkentity.getClassname())) {
                this.banjiname.setVisibility(0);
                this.banjiname.setText(this.posthomeworkentity.getClassname());
            } else {
                this.banjiname.setVisibility(8);
            }
            this.username.setText(this.posthomeworkentity.getNickname());
            this.chuli.setText("交作业");
        }
        if (this.posthomeworkentity.getIsread().equals("0")) {
            UpdateGeRenUnreadMessageApi updateGeRenUnreadMessageApi = new UpdateGeRenUnreadMessageApi();
            updateGeRenUnreadMessageApi.setUserid(this.userid);
            updateGeRenUnreadMessageApi.setItemid(this.itemid);
            updateGeRenUnreadMessageApi.setRtype(this.hwtype);
            ((ClassHuifuPresenter) this.mPresenter).startPost(this, updateGeRenUnreadMessageApi);
        }
    }

    public void huifuZuoYe(String name) {
        this.mSendEdt.requestFocus();
        this.mSendEdt.setText("");
        String mmString = "回复" + name + ":";
        this.mSendEdt.setText(mmString);
        this.mSendEdt.setSelection(mmString.length());
    }

    public void findViewById(View headview) {
        this.username = (TextView) headview.findViewById(R.id.username);
        this.publishTime = (TextView) headview.findViewById(R.id.publish_time);
        this.banjiname = (TextView) headview.findViewById(R.id.banjiname);
        this.content = (TextView) headview.findViewById(R.id.content);
        this.gridView = (RecyclerView) headview.findViewById(R.id.gridView);
        this.userhead = (ImageView) headview.findViewById(R.id.userhead);
        this.title = (TextView) headview.findViewById(R.id.tittle);
        this.stucount = (TextView) headview.findViewById(R.id.stucount);
        this.showcount = (TextView) headview.findViewById(R.id.showcount);
        this.liuyancount = (TextView) headview.findViewById(R.id.liuyancount);
        this.showlayout = (LinearLayout) headview.findViewById(R.id.showlayout);
        this.showlayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ZuoyeBobaoListItemActivity.this, UnshowStuActivity.class);
                intent.putExtra("itemid", ZuoyeBobaoListItemActivity.this.posthomeworkentity.getId());
                ZuoyeBobaoListItemActivity.this.startActivity(intent);
            }
        });
        this.fabuImageView = (ImageView) headview.findViewById(R.id.fabu_tp);
        this.dx_tagview = (TagView) headview.findViewById(R.id.dx_tagview);
        this.deleteTextview = (TextView) headview.findViewById(R.id.delete);
    }

    public void setData() {
        if (StringUtils.isEmpty(this.posthomeworkentity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this, this.userhead, this.posthomeworkentity.getUserimg(), new CropCircleTransformation((Context) this));
        }
        this.liucount = this.posthomeworkentity.getLiuyancount();
        this.showstucount = this.posthomeworkentity.getShowcount();
        if (StringUtils.isEmpty(this.posthomeworkentity.getClassname())) {
            this.banjiname.setText(this.posthomeworkentity.getClassname());
        }
        if (StringUtils.isEmpty(this.showstucount)) {
            this.showcount.setText(this.showstucount);
        }
        if (StringUtils.isEmpty(this.posthomeworkentity.getStucount())) {
            this.stucount.setText(this.posthomeworkentity.getStucount());
        }
        if (StringUtils.isEmpty(this.liucount)) {
            this.liuyancount.setText(this.liucount);
        }
        if (StringUtils.isEmpty(this.posthomeworkentity.getCreatetime())) {
            this.publishTime.setText(StringUtils.datestring(this.posthomeworkentity.getCreatetime()));
        }
        if (StringUtils.isEmpty(this.posthomeworkentity.getHwdescribe())) {
            this.content.setVisibility(0);
            this.content.setText(this.posthomeworkentity.getHwdescribe());
        } else {
            this.content.setVisibility(8);
        }
        if (this.posthomeworkentity.getHwtype().equals("1")) {
            this.title.setText("主题：" + this.posthomeworkentity.getHwtittle());
        } else {
            this.title.setText(this.posthomeworkentity.getHwtittle());
        }
        if (this.posthomeworkentity.getAtt() == null || this.posthomeworkentity.getAtt().size() == 0) {
            this.gridView.setVisibility(8);
            return;
        }
        this.gridView.setVisibility(0);
        List<AttEntitysa> list = new ArrayList();
        int row = 3;
        if (this.posthomeworkentity.getAtt().size() == 1) {
            row = 1;
            list.clear();
            list.addAll(this.posthomeworkentity.getAtt());
        } else if (this.posthomeworkentity.getAtt().size() == 4) {
            list.clear();
            int i = 0;
            while (i < this.posthomeworkentity.getAtt().size() + 2) {
                if (i == 0 || i == 1) {
                    list.add(this.posthomeworkentity.getAtt().get(i));
                } else if (i == 2 || i == 5) {
                    list.add(null);
                } else if (i == 3 || i == 4) {
                    list.add(this.posthomeworkentity.getAtt().get(i - 1));
                }
                i++;
            }
        } else {
            list.clear();
            list.addAll(this.posthomeworkentity.getAtt());
        }
        this.gridView.setLayoutManager(new FullyGridLayoutManager(this, row));
        CommonAdapter<AttEntitysa> zuoyeBobaoGridAdapter = new NewDongTaiGridAdapter(this, R.layout.adapter_zuoyebobaogrid, list);
        this.gridView.setAdapter(zuoyeBobaoGridAdapter);
        zuoyeBobaoGridAdapter.notifyDataSetChanged();
    }

    public void getIClassHuifu(List<ClassHuifuEntity> list, int state) {
        onSuccess(list, state);
    }

    public void isadd() {
        onRefresh();
    }

    public void uploadimg(String result) {
        this.upResult = result;
        this.filetype = "0";
        addZuoye();
    }

    public void isdelete(String result) {
        onRefresh();
    }

    public void isDeleteZuoye(String result) {
        if (this.leixing.equals("0")) {
            EventBus.getDefault().post(new DeleteZuoYeEvent(this.biaoji, "0"));
        } else if (this.leixing.equals("1")) {
            EventBus.getDefault().post(new DeleteZuoYeEvent(this.biaoji, "1"));
        } else if (this.leixing.equals("2")) {
            EventBus.getDefault().post(new DeleteZuoYeEvent(this.biaoji, "2"));
        } else {
            EventBus.getDefault().post(new DeleteZuoYeEvent(this.biaoji, "3"));
        }
        finish();
    }

    public void updateUnread(String result) {
        EventBus.getDefault().post(new UpdateMainMessageEvent());
        this.showstucount = result;
        if (StringUtils.isEmpty(this.showstucount)) {
            this.showcount.setText(this.showstucount);
        }
        if (this.leixing.equals("0")) {
            EventBus.getDefault().post(new UpdateZuoYeAdapterEvent(this.biaoji, this.showstucount, this.liucount, "0"));
        } else if (this.leixing.equals("1")) {
            EventBus.getDefault().post(new UpdateZuoYeAdapterEvent(this.biaoji, this.showstucount, this.liucount, "1"));
        } else if (this.leixing.equals("2")) {
            EventBus.getDefault().post(new UpdateZuoYeAdapterEvent(this.biaoji, this.showstucount, this.liucount, "2"));
        } else {
            EventBus.getDefault().post(new UpdateZuoYeAdapterEvent(this.biaoji, this.showstucount, this.liucount, "3"));
        }
    }

    public void onSuccess(List<ClassHuifuEntity> result, int State) {
        if (State == 1) {
            this.classHuifuEntities.clear();
            if (result.size() > 0) {
                this.showstucount = ((ClassHuifuEntity) result.get(0)).getShowcount();
                this.liucount = ((ClassHuifuEntity) result.get(0)).getLiuyancount();
                if (StringUtils.isEmpty(this.showstucount)) {
                    this.showcount.setText(this.showstucount);
                }
                if (StringUtils.isEmpty(this.liucount)) {
                    this.liuyancount.setText(this.liucount);
                }
                updateUnread(this.showstucount);
                this.footTxt.setText("已加载全部");
                this.footProgressBar.setVisibility(8);
            } else {
                this.footview.setVisibility(8);
            }
            for (ClassHuifuEntity cf : result) {
                if (cf.getUserid().equals(this.teacherid)) {
                    if (cf.getUserid().equals(this.userid)) {
                        cf.setMytype(2);
                    } else {
                        cf.setMytype(1);
                    }
                } else if (this.posthomeworkentity.getUserid().equals(this.userid)) {
                    cf.setMytype(1);
                } else {
                    cf.setMytype(2);
                }
                this.classHuifuEntities.add(cf);
            }
            this.headerAndFooterWrapper.notifyDataSetChanged();
            this.recyclerView.setPullLoadMoreCompleted();
        } else if (State == 2) {
            for (ClassHuifuEntity cf2 : result) {
                if (cf2.getUserid().equals(this.teacherid)) {
                    if (cf2.getUserid().equals(this.userid)) {
                        cf2.setMytype(2);
                    } else {
                        cf2.setMytype(1);
                    }
                } else if (this.posthomeworkentity.getUserid().equals(this.userid)) {
                    cf2.setMytype(1);
                } else {
                    cf2.setMytype(2);
                }
                this.classHuifuEntities.add(cf2);
            }
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (result.size() == 0) {
                this.footTxt.setText("已加载全部");
                this.footProgressBar.setVisibility(8);
            } else {
                this.footTxt.setText("正在加载");
            }
            this.recyclerView.setPullLoadMoreCompleted();
        }
    }

    @OnClick({2131624066, 2131624253})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.chuli:
                Intent intent;
                if (this.userid.equals(this.teacherid) && this.posthomeworkentity.getIsread().equals("2")) {
                    intent = new Intent();
                    intent.putExtra("itemid", this.posthomeworkentity.getId());
                    intent.putExtra("senduserid", this.posthomeworkentity.getUserid());
                    intent.setClass(this, ZuoYeListActivity.class);
                    startActivity(intent);
                    return;
                }
                intent = new Intent();
                intent.putExtra("classid", this.posthomeworkentity.getId());
                intent.putExtra("senduserid", this.userid);
                intent.putExtra("mytype", "student");
                intent.putExtra("content", "有新作业提交，请注意查收！");
                intent.putExtra("receiveid", this.posthomeworkentity.getUserid());
                intent.putExtra("zhurenid", this.posthomeworkentity.getUserid());
                intent.setClass(this, SubmitHomeWorkActivity.class);
                startActivity(intent);
                return;
            default:
                return;
        }
    }

    private void addZuoye() {
        SaveCLassLiuYanApi saveCLassLiuYanApi = new SaveCLassLiuYanApi();
        saveCLassLiuYanApi.setUserid(this.userid);
        saveCLassLiuYanApi.setItemid(this.itemid);
        saveCLassLiuYanApi.setUpResult(this.upResult);
        saveCLassLiuYanApi.setItemtype("liuyan");
        saveCLassLiuYanApi.setFiletype(this.filetype);
        ((ClassHuifuPresenter) this.mPresenter).startPost(this, saveCLassLiuYanApi);
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.classHuifuEntityApi.setPage(this.page + "");
        ((ClassHuifuPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.classHuifuEntityApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.classHuifuEntityApi.setPage(this.page + "");
        ((ClassHuifuPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.classHuifuEntityApi, 1);
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((ClassHuifuPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }

    public void showAlertDialog() {
        Builder builder = new Builder(this);
        if (this.leixing.equals("2")) {
            builder.setTitle("撤回本条通知？");
        } else {
            builder.setTitle("撤回本条作业？");
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DeleteZuoYeApi deleteZuoYeApi = new DeleteZuoYeApi();
                deleteZuoYeApi.setUserid(ZuoyeBobaoListItemActivity.this.userid);
                deleteZuoYeApi.setItemid(ZuoyeBobaoListItemActivity.this.itemid);
                ((ClassHuifuPresenter) ZuoyeBobaoListItemActivity.this.mPresenter).startPost(ZuoyeBobaoListItemActivity.this, deleteZuoYeApi);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void setJianPan() {
        this.mPanelLayout = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
        this.mSendEdt = (EditText) findViewById(R.id.send_edt);
        this.mPlusIv = (ImageView) findViewById(R.id.plus_iv);
        this.selcetPhoto = (TextView) findViewById(R.id.select_photo);
        this.takePhoto = (TextView) findViewById(R.id.take_photo);
        this.sendContent = (TextView) findViewById(R.id.send_btn);
        this.sendMsgLayout = (LinearLayout) findViewById(R.id.sendMsgLayout);
        findViewById(R.id.rootView).setFitsSystemWindows(true);
        this.mPanelLayout.setIgnoreRecommendHeight(true);
        KeyboardUtil.attach(this, this.mPanelLayout);
        KPSwitchConflictUtil.attach(this.mPanelLayout, this.mPlusIv, this.mSendEdt, new SwitchClickListener() {
            public void onClickSwitch(boolean switchToPanel) {
                if (switchToPanel) {
                    ZuoyeBobaoListItemActivity.this.mSendEdt.clearFocus();
                } else {
                    ZuoyeBobaoListItemActivity.this.mSendEdt.requestFocus();
                }
            }
        });
        this.mSendEdt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ZuoyeBobaoListItemActivity.this.mSendEdt.getText().length() > 0) {
                    ZuoyeBobaoListItemActivity.this.sendContent.setVisibility(0);
                    ZuoyeBobaoListItemActivity.this.mPlusIv.setVisibility(8);
                    return;
                }
                ZuoyeBobaoListItemActivity.this.mPlusIv.setVisibility(0);
                ZuoyeBobaoListItemActivity.this.sendContent.setVisibility(8);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.selcetPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(PointerIconCompat.TYPE_CONTEXT_MENU, ZuoyeBobaoListItemActivity.this.mOnHanlderResultCallback);
            }
        });
        this.takePhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GalleryFinal.openCamera(1000, ZuoyeBobaoListItemActivity.this.mOnHanlderResultCallback);
            }
        });
        this.sendContent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (StringUtils.isEmpty(ZuoyeBobaoListItemActivity.this.mSendEdt.getText().toString().trim())) {
                    ZuoyeBobaoListItemActivity.this.upResult = ZuoyeBobaoListItemActivity.this.mSendEdt.getText().toString().trim();
                    ZuoyeBobaoListItemActivity.this.filetype = "2";
                    ZuoyeBobaoListItemActivity.this.mSendEdt.setText("");
                    ZuoyeBobaoListItemActivity.this.addZuoye();
                    ZuoyeBobaoListItemActivity.this.recyclerView.setscrollToPosition(1);
                    return;
                }
                ZuoyeBobaoListItemActivity.this.showTip("输入不能为空");
                ZuoyeBobaoListItemActivity.this.mSendEdt.setText("");
            }
        });
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != 1 || event.getKeyCode() != 4 || this.mPanelLayout.getVisibility() != 0) {
            return super.dispatchKeyEvent(event);
        }
        KPSwitchConflictUtil.hidePanelAndKeyboard(this.mPanelLayout);
        return true;
    }
}
