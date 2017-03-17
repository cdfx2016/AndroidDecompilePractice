package com.fanyu.boundless.view.home;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.bean.home.DeleteGeRenZuoYeApi;
import com.fanyu.boundless.bean.home.GetHuiFuSubmitApi;
import com.fanyu.boundless.bean.home.SaveCLassLiuYanApi;
import com.fanyu.boundless.bean.home.SendNoticeApi;
import com.fanyu.boundless.bean.home.UpdateUnreadZuoYeApi;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.SubmitHomeWorkPresenter;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.home.HuifuLeft.deleteBack;
import com.fanyu.boundless.view.home.HuifuLeft.huifuBack;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnLoadMoreListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter.OnItemClickListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smackx.Form;

public class SubmitHomeWorkActivity extends BaseActivity<SubmitHomeWorkPresenter> implements ISubmitHomeWorkView, OnLoadMoreListener, OnRefreshListener {
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
    MultiItemTypeAdapter adapter;
    private String biaoji;
    private String classid;
    private String content;
    private List<ClassHuifuEntity> dataList = new ArrayList();
    private String filetype = "2";
    @Bind({2131624249})
    RelativeLayout framelayout;
    private GetHuiFuSubmitApi getHuiFuSubmitApi;
    HuifuLeft huifuLeft;
    HuifuRight huifuRight;
    private String isaddString = "kong";
    private String itemtype = Form.TYPE_SUBMIT;
    private OnHanlderResultCallback mOnHanlderResultCallback = new OnHanlderResultCallback() {
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(SubmitHomeWorkActivity.this.mPanelLayout);
                String pathString = ((PhotoInfo) resultList.get(0)).getPhotoPath();
                ImageUtils imageUtils = new ImageUtils();
                String mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".jpg");
                Bitmap photo = imageUtils.getimage(pathString, mBigImageName);
                SubmitHomeWorkActivity.this.getPathImg(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya/" + mBigImageName);
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
    private String mytype;
    private String noticetittle;
    private int page = 1;
    @Bind({2131624250})
    LinearLayout quxiaoMore;
    private String receiveid;
    private String recieveidString;
    @Bind({2131624097})
    EasyRecyclerView recyclerView;
    private TextView selcetPhoto;
    private TextView sendContent;
    private String senduserid;
    SharedPreferencesUtil sharedPreferencesUtil;
    private TextView takePhoto;
    private String upResult = "";
    private String userid;
    @Bind({2131624248})
    TextView wancheng;
    private String zhurenid;

    protected void initView() {
        setContentView((int) R.layout.activity_submit_homework);
    }

    protected void initPresenter() {
        this.mPresenter = new SubmitHomeWorkPresenter(this.mContext, this);
    }

    protected void init() {
        setJianPan();
        initdate();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.huifuLeft = new HuifuLeft(this);
        this.huifuRight = new HuifuRight(this);
        this.adapter = new MultiItemTypeAdapter(this, this.dataList);
        this.adapter.addItemViewDelegate(this.huifuLeft);
        this.adapter.addItemViewDelegate(this.huifuRight);
        this.recyclerView.setAdapterWithProgress(this.adapter);
        this.recyclerView.setRefreshListener(this);
        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, ViewHolder holder, int position) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(SubmitHomeWorkActivity.this.mPanelLayout);
            }

            public boolean onItemLongClick(View view, ViewHolder holder, int position) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(SubmitHomeWorkActivity.this.mPanelLayout);
                return false;
            }
        });
        this.recyclerView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(SubmitHomeWorkActivity.this.mPanelLayout);
                }
                return false;
            }
        });
        final int heightframe = this.framelayout.getHeight();
        final int height = KeyboardUtil.getKeyboardHeight(this);
        this.framelayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (height < heightframe) {
                    SubmitHomeWorkActivity.this.mPanelLayout.setVisibility(8);
                }
                return false;
            }
        });
        this.adapter.notifyDataSetChanged();
        this.huifuLeft.setHfBack(new huifuBack() {
            public void onhfListen(String name) {
                SubmitHomeWorkActivity.this.huifuZuoYe(name);
            }
        });
        this.huifuRight.setDeBack(new deleteBack() {
            public void onListen(int count) {
                DeleteGeRenZuoYeApi deleteGeRenZuoYeApi = new DeleteGeRenZuoYeApi();
                deleteGeRenZuoYeApi.setUserid(SubmitHomeWorkActivity.this.userid);
                deleteGeRenZuoYeApi.setItemid(((ClassHuifuEntity) SubmitHomeWorkActivity.this.dataList.get(count)).getId());
                ((SubmitHomeWorkPresenter) SubmitHomeWorkActivity.this.mPresenter).startPost(SubmitHomeWorkActivity.this, deleteGeRenZuoYeApi);
            }
        });
        onRefresh();
        UpdateUnreadZuoYeApi unreadZuoYeApi = new UpdateUnreadZuoYeApi();
        unreadZuoYeApi.setItemid(this.classid);
        unreadZuoYeApi.setUserid(this.senduserid);
        ((SubmitHomeWorkPresenter) this.mPresenter).startPost(this, unreadZuoYeApi);
    }

    public void huifuZuoYe(String name) {
        this.mSendEdt.requestFocus();
        this.mSendEdt.setText("");
        String mmString = "回复" + name + ":";
        this.mSendEdt.setText(mmString);
        this.mSendEdt.setSelection(mmString.length());
    }

    private void initdate() {
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.classid = getIntent().getStringExtra("classid");
        this.receiveid = getIntent().getStringExtra("receiveid");
        this.zhurenid = getIntent().getStringExtra("zhurenid");
        this.mytype = getIntent().getStringExtra("mytype");
        this.content = getIntent().getStringExtra("content");
        this.senduserid = getIntent().getStringExtra("senduserid");
        this.recieveidString = this.userid;
        if (this.userid.equals(this.zhurenid) && this.content.equals("作业已批阅，请及时查看！")) {
            this.noticetittle = "提醒学生查看作业批复？";
            this.mSendEdt.setHint("请输入作业批复");
            this.messageTitle.setText("批复作业");
        } else {
            this.mSendEdt.setHint("请输入文字作业");
            this.noticetittle = "提醒老师查收作业？";
        }
        this.getHuiFuSubmitApi = new GetHuiFuSubmitApi();
        this.getHuiFuSubmitApi.setPage(this.page + "");
        this.getHuiFuSubmitApi.setPagesize("8");
        this.getHuiFuSubmitApi.setClassid(this.classid);
        this.getHuiFuSubmitApi.setUserid(this.senduserid);
        this.getHuiFuSubmitApi.setBiaoji(TtmlNode.TAG_TT);
    }

    @OnClick({2131624066, 2131624248})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.wancheng:
                if (!this.isaddString.equals("kong")) {
                    showNoticeZuoYeDialog(this.noticetittle);
                    return;
                } else if (this.userid.equals(this.zhurenid) && this.content.equals("作业已批阅，请及时查看！")) {
                    Toast.makeText(this, "请您先批复作业再提醒学生！", 0).show();
                    return;
                } else {
                    Toast.makeText(this, "请您先提交作业再提醒老师！", 0).show();
                    return;
                }
            default:
                return;
        }
    }

    private void addZuoye() {
        SaveCLassLiuYanApi saveCLassLiuYanApi = new SaveCLassLiuYanApi();
        saveCLassLiuYanApi.setUserid(this.userid);
        saveCLassLiuYanApi.setItemid(this.classid);
        saveCLassLiuYanApi.setSenduserid(this.senduserid);
        saveCLassLiuYanApi.setUpResult(this.upResult);
        saveCLassLiuYanApi.setItemtype(this.itemtype);
        saveCLassLiuYanApi.setFiletype(this.filetype);
        ((SubmitHomeWorkPresenter) this.mPresenter).startPost(this, saveCLassLiuYanApi);
    }

    public void showNoticeZuoYeDialog(String tittle) {
        Builder builder = new Builder(this.mContext);
        builder.setTitle(tittle);
        builder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SendNoticeApi sendNoticeApi = new SendNoticeApi();
                sendNoticeApi.setSenduserid(SubmitHomeWorkActivity.this.userid);
                sendNoticeApi.setContent(SubmitHomeWorkActivity.this.content);
                sendNoticeApi.setItemid(SubmitHomeWorkActivity.this.classid);
                sendNoticeApi.setXuyaoid(SubmitHomeWorkActivity.this.senduserid);
                sendNoticeApi.setZhurenid(SubmitHomeWorkActivity.this.zhurenid);
                sendNoticeApi.setReceiveid(SubmitHomeWorkActivity.this.receiveid);
                ((SubmitHomeWorkPresenter) SubmitHomeWorkActivity.this.mPresenter).startPost(SubmitHomeWorkActivity.this, sendNoticeApi);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.getHuiFuSubmitApi.setPage(this.page + "");
        ((SubmitHomeWorkPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.getHuiFuSubmitApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.getHuiFuSubmitApi.setPage(this.page + "");
        ((SubmitHomeWorkPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.getHuiFuSubmitApi, 1);
    }

    public void getZuoyeList(List<ClassHuifuEntity> zuoyelist, int state) {
        if (state == 1) {
            this.dataList.clear();
            if (zuoyelist.size() > 0) {
                if (((ClassHuifuEntity) zuoyelist.get(0)).getUserid().equals(this.userid)) {
                    this.isaddString = "do";
                } else {
                    this.isaddString = "kong";
                }
            }
            for (ClassHuifuEntity cf : zuoyelist) {
                if (cf.getUserid().equals(this.userid)) {
                    cf.setMytype(2);
                } else {
                    cf.setMytype(1);
                }
                this.dataList.add(cf);
            }
            this.adapter.notifyDataSetChanged();
        } else if (state == 2) {
            for (ClassHuifuEntity cf2 : zuoyelist) {
                if (cf2.getUserid().equals(this.userid)) {
                    cf2.setMytype(2);
                } else {
                    cf2.setMytype(1);
                }
                this.dataList.add(cf2);
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public void isadd() {
        onRefresh();
    }

    public void uploadimg(String result) {
        this.upResult = result;
        this.filetype = "0";
        addZuoye();
    }

    public void issend() {
        finish();
    }

    public void isdelete(String result) {
        onRefresh();
    }

    public void updateUnread() {
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((SubmitHomeWorkPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }

    public void setJianPan() {
        this.mPanelLayout = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
        this.mSendEdt = (EditText) findViewById(R.id.send_edt);
        this.mPlusIv = (ImageView) findViewById(R.id.plus_iv);
        this.selcetPhoto = (TextView) findViewById(R.id.select_photo);
        this.takePhoto = (TextView) findViewById(R.id.take_photo);
        this.sendContent = (TextView) findViewById(R.id.send_btn);
        findViewById(R.id.rootView).setFitsSystemWindows(true);
        this.mPanelLayout.setIgnoreRecommendHeight(true);
        KeyboardUtil.attach(this, this.mPanelLayout);
        KPSwitchConflictUtil.attach(this.mPanelLayout, this.mPlusIv, this.mSendEdt, new SwitchClickListener() {
            public void onClickSwitch(boolean switchToPanel) {
                if (switchToPanel) {
                    SubmitHomeWorkActivity.this.mSendEdt.clearFocus();
                } else {
                    SubmitHomeWorkActivity.this.mSendEdt.requestFocus();
                }
            }
        });
        this.mSendEdt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (SubmitHomeWorkActivity.this.mSendEdt.getText().length() > 0) {
                    SubmitHomeWorkActivity.this.sendContent.setVisibility(0);
                    SubmitHomeWorkActivity.this.mPlusIv.setVisibility(8);
                    return;
                }
                SubmitHomeWorkActivity.this.mPlusIv.setVisibility(0);
                SubmitHomeWorkActivity.this.sendContent.setVisibility(8);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.selcetPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(PointerIconCompat.TYPE_CONTEXT_MENU, SubmitHomeWorkActivity.this.mOnHanlderResultCallback);
            }
        });
        this.takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GalleryFinal.openCamera(1000, SubmitHomeWorkActivity.this.mOnHanlderResultCallback);
            }
        });
        this.sendContent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (StringUtils.isEmpty(SubmitHomeWorkActivity.this.mSendEdt.getText().toString().trim())) {
                    SubmitHomeWorkActivity.this.upResult = SubmitHomeWorkActivity.this.mSendEdt.getText().toString().trim();
                    SubmitHomeWorkActivity.this.filetype = "2";
                    SubmitHomeWorkActivity.this.mSendEdt.setText("");
                    SubmitHomeWorkActivity.this.addZuoye();
                    return;
                }
                SubmitHomeWorkActivity.this.showTip("输入不能为空");
                SubmitHomeWorkActivity.this.mSendEdt.setText("");
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
