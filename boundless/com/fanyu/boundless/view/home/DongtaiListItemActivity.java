package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.Dailyreply;
import com.fanyu.boundless.bean.home.DongTaiDeleteApi;
import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.home.DongTaiGerenShuoShuoApi;
import com.fanyu.boundless.bean.home.PraiseCancleApi;
import com.fanyu.boundless.bean.home.PraiseOrNoApi;
import com.fanyu.boundless.bean.home.PraiseSaveApi;
import com.fanyu.boundless.bean.microclass.AddPingLunApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.DongtaiPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdatePinglunEvent;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnLoadMoreListener;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DongtaiListItemActivity extends BaseActivity<DongtaiPresenter> implements IGetDongtaiView, OnLoadMoreListener, OnRefreshListener {
    RecyclerArrayAdapter<Dailyreply> adapter;
    TextView banjiname;
    TextView biaoqian;
    @Bind({2131624127})
    EditText chatContent;
    @Bind({2131624130})
    Button chatSendbtn;
    TextView commitnum;
    TextView content;
    TextView delete_dongtai;
    private DongTaiEntity dongEntity;
    private DongTaiEntity dongTaiEntity;
    private List<DongTaiEntity> dongTaiEntityArrayList = new ArrayList();
    private DongTaiGerenShuoShuoApi dongTaiGerenShuoShuoApi;
    RecyclerView gridView;
    private View headView = null;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    private String id;
    ImageView img_praise;
    private InputMethodManager imm;
    private boolean isPraise = false;
    private SharedPreferencesUtil msharepreference;
    private int page = 1;
    private String pingluncontent = "";
    TextView pingluncount;
    TextView publishTime;
    @Bind({2131624097})
    EasyRecyclerView recyclerView;
    private String replycontent = "";
    ImageView userhead;
    private String userid;
    TextView username;
    @Bind({2131624122})
    RelativeLayout waibu;
    private LinearLayout zanlayout;

    protected void initView() {
        setContentView((int) R.layout.activity_dongtai_list_item);
    }

    protected void initPresenter() {
        this.mPresenter = new DongtaiPresenter(this.mContext, this);
    }

    protected void init() {
        getWindow().setSoftInputMode(18);
        this.imm = (InputMethodManager) getSystemService("input_method");
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.id = getIntent().getStringExtra("id");
        this.dongEntity = (DongTaiEntity) getIntent().getSerializableExtra("entity");
        this.headView = getLayoutInflater().inflate(R.layout.activity_dongtai_list_item_headview, null);
        findViewById(this.headView);
        initHeadView();
        this.chatSendbtn.setClickable(false);
        this.chatSendbtn.setBackgroundResource(R.drawable.edit_bg_no);
        this.chatSendbtn.setTextColor(Color.parseColor("#cbcbcb"));
        this.dongTaiGerenShuoShuoApi = new DongTaiGerenShuoShuoApi();
        this.dongTaiGerenShuoShuoApi.setUserid(this.userid);
        this.dongTaiGerenShuoShuoApi.setDailyid(this.id);
        ((DongtaiPresenter) this.mPresenter).startPost(this, this.dongTaiGerenShuoShuoApi);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.adapter = new DongtaiPinglunAdapter(this.mContext);
        this.recyclerView.setAdapterWithProgress(this.adapter);
        this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.adapter);
        this.headerAndFooterWrapper.addHeaderView(this.headView);
        this.recyclerView.setAdapter(this.headerAndFooterWrapper);
        this.headerAndFooterWrapper.notifyDataSetChanged();
        sendbutton();
    }

    private void sendbutton() {
        this.chatContent.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                DongtaiListItemActivity.this.pingluncontent = DongtaiListItemActivity.this.chatContent.getText().toString();
                if (DongtaiListItemActivity.this.pingluncontent.length() > 0) {
                    DongtaiListItemActivity.this.chatSendbtn.setBackgroundResource(R.drawable.edit_bg);
                    DongtaiListItemActivity.this.chatSendbtn.setTextColor(Color.parseColor("#727272"));
                    DongtaiListItemActivity.this.chatSendbtn.setClickable(true);
                    return;
                }
                DongtaiListItemActivity.this.chatSendbtn.setBackgroundResource(R.drawable.edit_bg_no);
                DongtaiListItemActivity.this.chatSendbtn.setTextColor(Color.parseColor("#cbcbcb"));
                DongtaiListItemActivity.this.chatSendbtn.setClickable(false);
            }
        });
    }

    public void showAlertDialog() {
        Builder builder = new Builder(this);
        builder.setTitle("删除本条动态？");
        builder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DongTaiDeleteApi dongTaiDeleteApi = new DongTaiDeleteApi();
                dongTaiDeleteApi.setUserid(DongtaiListItemActivity.this.userid);
                dongTaiDeleteApi.setDailyid(DongtaiListItemActivity.this.dongTaiEntity.getId());
                ((DongtaiPresenter) DongtaiListItemActivity.this.mPresenter).startPost(DongtaiListItemActivity.this, dongTaiDeleteApi);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @OnClick({2131624066, 2131624130})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.chat_sendbtn:
                InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                System.out.println("chatContent ===" + this.chatContent.getText().toString());
                System.out.println("chatContent.trim ===" + this.chatContent.getText().toString().trim());
                System.out.println("isempty ===" + StringUtils.isEmpty(this.chatContent.getText().toString()));
                if (StringUtils.isEmpty(this.chatContent.getText().toString().trim())) {
                    this.pingluncontent = this.chatContent.getText().toString();
                    AddPingLunApi addPingLunApi = new AddPingLunApi();
                    addPingLunApi.setDailyid(this.id);
                    addPingLunApi.setUserid(this.userid);
                    addPingLunApi.setReplycontent(this.pingluncontent);
                    ((DongtaiPresenter) this.mPresenter).startPost(this, addPingLunApi);
                } else {
                    Toast.makeText(this, "请您填写评论内容！", 0).show();
                }
                this.chatContent.setText("");
                return;
            default:
                return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdatePinglunEvent updateNameEvent) {
        if (updateNameEvent.getType() == 0) {
            this.dongTaiGerenShuoShuoApi = new DongTaiGerenShuoShuoApi();
            this.dongTaiGerenShuoShuoApi.setUserid(this.userid);
            this.dongTaiGerenShuoShuoApi.setDailyid(this.id);
            ((DongtaiPresenter) this.mPresenter).startPost(this, this.dongTaiGerenShuoShuoApi);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void findViewById(View headview) {
        this.username = (TextView) headview.findViewById(R.id.username);
        this.publishTime = (TextView) headview.findViewById(R.id.publish_time);
        this.banjiname = (TextView) headview.findViewById(R.id.banjiname);
        this.content = (TextView) headview.findViewById(R.id.content);
        this.gridView = (RecyclerView) headview.findViewById(R.id.gridView);
        this.userhead = (ImageView) headview.findViewById(R.id.mtxs);
        this.biaoqian = (TextView) headview.findViewById(R.id.biaoqian);
        this.commitnum = (TextView) headview.findViewById(R.id.commitnum);
        this.pingluncount = (TextView) headview.findViewById(R.id.pingluncount);
        this.img_praise = (ImageView) headview.findViewById(R.id.img_praise);
        this.zanlayout = (LinearLayout) headview.findViewById(R.id.zanlayout);
        this.delete_dongtai = (TextView) headview.findViewById(R.id.delete_dongtai);
    }

    public void getDongtai(List<DongTaiEntity> list, int state) {
    }

    public void getMyClass(List<schoolclassentity> list) {
    }

    public void getGerenShuoShuo(List<DongTaiEntity> dongTaiEntities, int state) {
        this.adapter.clear();
        this.dongTaiEntityArrayList.clear();
        this.dongTaiEntityArrayList.addAll(dongTaiEntities);
        this.dongTaiEntity = (DongTaiEntity) this.dongTaiEntityArrayList.get(0);
        this.adapter.addAll(this.dongTaiEntity.getDailreply());
        this.headerAndFooterWrapper.notifyDataSetChanged();
        setData();
    }

    public void addPinglun(String result) {
        if (StringUtils.isEmpty(result)) {
            Toast.makeText(this, "评论成功", 0).show();
            EventBus.getDefault().post(new UpdatePinglunEvent(0));
            return;
        }
        Toast.makeText(this, "评论失败", 0).show();
    }

    public void praiseSave(String result) {
        System.out.println("Save");
        this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_pre);
        EventBus.getDefault().post(new UpdatePinglunEvent(2));
        this.zanlayout.setClickable(true);
    }

    public void praiseCancel(String result) {
        System.out.println("Cancel");
        this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_grey);
        EventBus.getDefault().post(new UpdatePinglunEvent(2));
        this.zanlayout.setClickable(true);
    }

    public void praiseIsOrNo(String result) {
        if (StringUtils.isEmpty(result)) {
            this.isPraise = true;
        } else {
            this.isPraise = false;
        }
        if (this.isPraise) {
            this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_pre);
        } else {
            this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_grey);
        }
        this.zanlayout.setClickable(true);
    }

    public void deleteDongtai(String result) {
        if ("yes".equals(result)) {
            Toast.makeText(this, "删除动态成功", 0).show();
            EventBus.getDefault().post(new UpdatePinglunEvent(3));
            finish();
            return;
        }
        Toast.makeText(this, "删除动态失败", 0).show();
    }

    public void initHeadView() {
        if (StringUtils.isEmpty(this.dongEntity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this, this.userhead, this.dongEntity.getUserimg(), new CropCircleTransformation((Context) this));
        }
        if (StringUtils.isEmpty(this.dongEntity.getClassname())) {
            this.banjiname.setText(this.dongEntity.getClassname());
        }
        if (StringUtils.isEmpty(this.dongEntity.getUsername())) {
            this.username.setText(this.dongEntity.getUsername());
        }
        if (StringUtils.isEmpty(this.dongEntity.getContent())) {
            this.content.setText(this.dongEntity.getContent());
        }
        if (StringUtils.isEmpty(this.dongEntity.getCreatetime())) {
            this.publishTime.setText(this.dongEntity.getCreatetime());
        }
        if (!(this.dongEntity.getDailreply() == null || this.dongEntity.getDailreply().size() == 0)) {
            this.commitnum.setText(this.dongEntity.getDailreply().size() + "");
            this.pingluncount.setText(this.dongEntity.getDailreply().size() + "");
        }
        if (StringUtils.isEmpty(this.dongEntity.getBiaoqian())) {
            this.biaoqian.setText(this.dongEntity.getBiaoqian());
        }
        if (this.dongEntity.getAtt() == null || this.dongEntity.getAtt().size() == 0) {
            this.gridView.setVisibility(8);
        } else {
            this.gridView.setVisibility(0);
            int row = 3;
            List<AttEntitysa> list = new ArrayList();
            if (this.dongEntity.getAtt().size() == 1) {
                row = 1;
                list.clear();
                list.addAll(this.dongEntity.getAtt());
            } else if (this.dongEntity.getAtt().size() == 4) {
                list.clear();
                int i = 0;
                while (i < this.dongEntity.getAtt().size() + 2) {
                    if (i == 0 || i == 1) {
                        list.add(this.dongEntity.getAtt().get(i));
                    } else if (i == 2 || i == 5) {
                        list.add(null);
                    } else if (i == 3 || i == 4) {
                        list.add(this.dongEntity.getAtt().get(i - 1));
                    }
                    i++;
                }
            } else {
                list.clear();
                list.addAll(this.dongEntity.getAtt());
            }
            this.gridView.setLayoutManager(new FullyGridLayoutManager(this, row));
            CommonAdapter<AttEntitysa> zuoyeBobaoGridAdapter = new NewDongTaiGridAdapter(this, R.layout.adapter_zuoyebobaogrid, list);
            this.gridView.setAdapter(zuoyeBobaoGridAdapter);
            zuoyeBobaoGridAdapter.notifyDataSetChanged();
        }
        if (this.userid.equals(this.dongEntity.getUserid())) {
            this.delete_dongtai.setVisibility(0);
            this.delete_dongtai.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DongtaiListItemActivity.this.showAlertDialog();
                }
            });
        }
        this.zanlayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (DongtaiListItemActivity.this.isPraise) {
                    DongtaiListItemActivity.this.isPraise = false;
                    DongtaiListItemActivity.this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_grey);
                    PraiseCancleApi praiseCancleApi = new PraiseCancleApi();
                    praiseCancleApi.setUserid(DongtaiListItemActivity.this.dongTaiEntity.getUserid());
                    praiseCancleApi.setQuestionid(DongtaiListItemActivity.this.dongTaiEntity.getId());
                    praiseCancleApi.setItemtype("dongtai");
                    ((DongtaiPresenter) DongtaiListItemActivity.this.mPresenter).startPost(DongtaiListItemActivity.this, praiseCancleApi);
                } else {
                    DongtaiListItemActivity.this.isPraise = true;
                    DongtaiListItemActivity.this.img_praise.setBackgroundResource(R.mipmap.zhao_zan_pre);
                    PraiseSaveApi praiseSaveApi = new PraiseSaveApi();
                    praiseSaveApi.setUserid(DongtaiListItemActivity.this.dongTaiEntity.getUserid());
                    praiseSaveApi.setQuestionid(DongtaiListItemActivity.this.dongTaiEntity.getId());
                    praiseSaveApi.setItemtype("dongtai");
                    ((DongtaiPresenter) DongtaiListItemActivity.this.mPresenter).startPost(DongtaiListItemActivity.this, praiseSaveApi);
                }
                DongtaiListItemActivity.this.zanlayout.setClickable(false);
            }
        });
    }

    public void setData() {
        PraiseOrNoApi praiseOrNoApi = new PraiseOrNoApi();
        praiseOrNoApi.setItemtype("dongtai");
        praiseOrNoApi.setUserid(this.dongTaiEntity.getUserid());
        praiseOrNoApi.setQuestionid(this.dongTaiEntity.getId());
        ((DongtaiPresenter) this.mPresenter).startPost(this, praiseOrNoApi);
        if (this.dongTaiEntity.getDailreply() != null && this.dongTaiEntity.getDailreply().size() != 0) {
            this.commitnum.setText(this.dongTaiEntity.getDailreply().size() + "");
            this.pingluncount.setText(this.dongTaiEntity.getDailreply().size() + "");
        }
    }

    public void onRefresh() {
    }

    public void onLoadMore() {
    }
}
