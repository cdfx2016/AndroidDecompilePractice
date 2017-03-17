package com.fanyu.boundless.view.theclass;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryFinal.OnHanlderResultCallback;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AddHomeWorkApi;
import com.fanyu.boundless.bean.theclass.AttApi;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnClickListener;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.Builder;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.PublishJobPresenter;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.home.HuodongZuoyeActivity;
import com.fanyu.boundless.view.home.NoticeMessageActivity;
import com.fanyu.boundless.view.home.ZuoyeBobaoActivity;
import com.fanyu.boundless.view.myself.event.UpdateHuoDongEvent;
import com.fanyu.boundless.view.myself.event.UpdateNoticeEvent;
import com.fanyu.boundless.view.myself.event.UpdateZuoyeEvent;
import com.fanyu.boundless.widget.Exsit;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.xiaomi.mipush.sdk.Constants;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishJobActivity extends BaseActivity<PublishJobPresenter> implements PublishJobView {
    private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
    private AddPhotoAdapter addPhotoAdapter;
    private int allid;
    @Bind({2131624164})
    TextView banji;
    private List<ChildItem> banjiList = new ArrayList(0);
    private String banjiid = "";
    @Bind({2131624163})
    RelativeLayout banjilayout;
    private int biaoji = 0;
    @Bind({2131624172})
    TextView biaoti;
    private Builder builder;
    private String classid;
    private String classname;
    private String describe;
    @Bind({2131624160})
    EditText editdescribe;
    @Bind({2131624159})
    EditText edittittle;
    private FunctionConfig functionConfig;
    @Bind({2131624170})
    TextView geren;
    @Bind({2131624171})
    RecyclerView gerenGridPickerView;
    private List<ChildItem> gerenList = new ArrayList(0);
    private String gerenid = "";
    @Bind({2131624169})
    RelativeLayout gerenlayout;
    @Bind({2131624165})
    RecyclerView gvGridPickerView;
    private String hwtype = "0";
    private List<String> imglist = new ArrayList();
    private OnHanlderResultCallback mOnHanlderResultCallback = new OnHanlderResultCallback() {
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                PublishJobActivity.this.photoInfoList.addAll(0, resultList);
                if (PublishJobActivity.this.photoInfoList.size() < 7) {
                    PublishJobActivity.this.builder.setMutiSelectMaxSize(7 - PublishJobActivity.this.photoInfoList.size());
                    PublishJobActivity.this.functionConfig = PublishJobActivity.this.builder.build();
                } else {
                    PublishJobActivity.this.photoInfoList.remove(PublishJobActivity.this.photoInfoList.size() - 1);
                }
                PublishJobActivity.this.addPhotoAdapter.notifyDataSetChanged();
            }
        }

        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };
    @Bind({2131624067})
    TextView messageTitle;
    private int newbiaoji = 0;
    private PhotoInfo photoInfo;
    private List<PhotoInfo> photoInfoList;
    SharedPreferencesUtil sharedPreferencesUtil;
    private String tittle;
    private String userid;
    @Bind({2131624122})
    LinearLayout waibu;
    @Bind({2131624167})
    TextView xiaozu;
    private String xiaozuid = "";
    @Bind({2131624166})
    RelativeLayout xiaozulayout;
    @Bind({2131624162})
    TextView xiugai;
    private String zhuid;
    @Bind({2131624168})
    RecyclerView zuGridPickerView;
    private List<ChildItem> zuList = new ArrayList(0);
    @Bind({2131624161})
    RecyclerView zuoyeview;

    class ItemListener implements OnItemClickListener {
        ItemListener() {
        }

        public void onItemClick(ViewHolder arg0, int arg1) {
            PublishJobActivity.this.photoInfoList.remove(arg1);
            int size = PublishJobActivity.this.photoInfoList.size();
            if (size < 6 && !PublishJobActivity.this.photoInfoList.contains(PublishJobActivity.this.photoInfo)) {
                PublishJobActivity.this.photoInfoList.add(size, PublishJobActivity.this.photoInfo);
            }
            if (PublishJobActivity.this.photoInfoList.size() < 7) {
                PublishJobActivity.this.builder.setMutiSelectMaxSize(7 - PublishJobActivity.this.photoInfoList.size());
                PublishJobActivity.this.functionConfig = PublishJobActivity.this.builder.build();
            }
            PublishJobActivity.this.addPhotoAdapter.notifyDataSetChanged();
        }
    }

    class clickListener implements OnClickListener {
        clickListener() {
        }

        public void onClick(ViewHolder holder) {
            GalleryFinal.openGalleryMuti((int) PointerIconCompat.TYPE_CONTEXT_MENU, PublishJobActivity.this.functionConfig, PublishJobActivity.this.mOnHanlderResultCallback);
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_fabuzuoye);
    }

    protected void initPresenter() {
        this.mPresenter = new PublishJobPresenter(this.mContext, this);
    }

    protected void init() {
        getWindow().setSoftInputMode(3);
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.classid = this.sharedPreferencesUtil.getString(Preferences.CLASS_ID, "");
        this.classname = this.sharedPreferencesUtil.getString(Preferences.CLASS_NAME, "");
        this.hwtype = getIntent().getStringExtra(MessageEncoder.ATTR_TYPE);
        this.builder = new Builder();
        this.builder.setMutiSelectMaxSize(6);
        this.functionConfig = this.builder.build();
        if (this.hwtype.equals("0")) {
            this.edittittle.setText(StringUtils.covertDateToString2(new Date()) + " 家庭作业");
            this.tittle = StringUtils.covertDateToString2(new Date()) + " 家庭作业";
            this.messageTitle.setText("发作业");
        } else if (this.hwtype.equals("1")) {
            this.edittittle.setText(StringUtils.covertDateToString2(new Date()) + " 活动作业");
            this.tittle = StringUtils.covertDateToString2(new Date()) + " 活动作业";
            this.biaoti.setText("主题");
            this.messageTitle.setText("发活动作业");
        } else if (this.hwtype.equals("2")) {
            this.edittittle.setText(StringUtils.covertDateToString2(new Date()) + " 班级通知");
            this.tittle = StringUtils.covertDateToString2(new Date()) + " 班级通知";
            this.editdescribe.setHint("请输入通知内容（100字以内）");
            this.messageTitle.setText("发通知");
        }
        this.edittittle.setSelection(this.edittittle.getText().length());
        FullyGridLayoutManager zuoyeManager = new FullyGridLayoutManager(this.mContext, 4);
        zuoyeManager.setScrollEnabled(false);
        this.zuoyeview.setLayoutManager(zuoyeManager);
        this.photoInfoList = new ArrayList(0);
        this.photoInfo = new PhotoInfo();
        this.photoInfo.setPhotoId(ViewCompat.MEASURED_SIZE_MASK);
        this.photoInfo.setPhotoPath("2130903161");
        this.photoInfoList.add(this.photoInfo);
        this.addPhotoAdapter = new AddPhotoAdapter(this.mContext, R.layout.gridview_image_item, this.photoInfoList);
        this.addPhotoAdapter.setOnClickListener(new clickListener());
        this.addPhotoAdapter.setOnItemClickListener(new ItemListener());
        this.zuoyeview.setAdapter(this.addPhotoAdapter);
        if (StringUtils.isEmpty(this.classname) && StringUtils.isEmpty(this.classid)) {
            ChildItem cs = new ChildItem(this.classname, this.classid, false);
            this.banjiid = this.classid;
            this.banjiList.add(cs);
            FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(this.mContext, 3);
            fullyGridLayoutManager.setScrollEnabled(false);
            this.gvGridPickerView.setLayoutManager(fullyGridLayoutManager);
            this.gvGridPickerView.setAdapter(new UnClickZuAdapter(this.mContext, R.layout.new_grid_item, this.banjiList));
        }
    }

    @OnClick({2131624066, 2131624121, 2131624162})
    public void onClick(View view) {
        int i = 1;
        int i2;
        switch (view.getId()) {
            case R.id.img_return:
                this.describe = this.editdescribe.getText().toString().trim();
                this.photoInfoList.remove(this.photoInfo);
                if (this.describe.equals("")) {
                    i2 = 0;
                } else {
                    i2 = 1;
                }
                if (this.photoInfoList.size() <= 0) {
                    i = 0;
                }
                if ((i2 | i) != 0) {
                    showAlertDialog();
                    return;
                } else {
                    finish();
                    return;
                }
            case R.id.fabu:
                this.describe = this.editdescribe.getText().toString();
                if (this.banjiid != null) {
                    int i3;
                    if (this.banjiid.equals("")) {
                        i2 = 0;
                    } else {
                        i2 = 1;
                    }
                    if (this.xiaozuid == null || this.xiaozuid.equals("")) {
                        i3 = 0;
                    } else {
                        i3 = 1;
                    }
                    i3 |= i2;
                    if (this.gerenid == null || this.gerenid.equals("")) {
                        i2 = 0;
                    } else {
                        i2 = 1;
                    }
                    if ((i2 | i3) != 0) {
                        this.photoInfoList.remove(this.photoInfo);
                        if (this.describe.equals("")) {
                            i2 = 0;
                        } else {
                            i2 = 1;
                        }
                        if ((i2 | (this.photoInfoList.size() > 0 ? 1 : 0)) != 0) {
                            dosave();
                            if (this.banjiList.size() != 0) {
                                String saveclassid = ((ChildItem) this.banjiList.get(0)).getId();
                                String newclassname = ((ChildItem) this.banjiList.get(0)).getTitle();
                                SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this.mContext);
                                editor.putString(Preferences.CLASS_NAME, newclassname);
                                editor.putString(Preferences.CLASS_ID, saveclassid);
                                return;
                            }
                            return;
                        }
                        Toast.makeText(this, "请填写内容！", 1).show();
                        return;
                    }
                }
                Toast.makeText(this, "请选择发布对象！", 1).show();
                return;
            case R.id.xiugai:
                startActivityForResult(new Intent(this, UpdateStudentActivity.class), 8);
                return;
            default:
                return;
        }
    }

    public void dosave() {
        AddHomeWorkApi addHomeWorkApi = new AddHomeWorkApi();
        addHomeWorkApi.setHwdescribe(this.describe);
        addHomeWorkApi.setUserid(this.userid);
        addHomeWorkApi.setTittle(this.tittle);
        addHomeWorkApi.setBanjiid(this.banjiid);
        addHomeWorkApi.setXiaozuid(this.xiaozuid);
        addHomeWorkApi.setGerenid(this.gerenid);
        addHomeWorkApi.setHwtype(this.hwtype);
        ((PublishJobPresenter) this.mPresenter).startPost(this, addHomeWorkApi);
        showLoadingDialog();
    }

    public void fileList(String result) {
        AttApi attApi = new AttApi();
        attApi.setUserid(this.userid);
        attApi.setFilename(result);
        attApi.setIdid(this.zhuid);
        attApi.setItemtype("zuoyebobao");
        ((PublishJobPresenter) this.mPresenter).startPost(this, attApi);
    }

    protected void onDestroy() {
        super.onDestroy();
        closeLoadingDialog();
    }

    public void addAtt(String result) {
        this.newbiaoji++;
        if (this.allid == this.newbiaoji) {
            doFinish();
            this.newbiaoji = 0;
        }
    }

    public void publishJob(String result) {
        this.zhuid = result;
        if (this.photoInfoList.size() > 0) {
            doUpload();
        } else {
            doFinish();
        }
    }

    public void doFinish() {
        if (this.hwtype.equals("0")) {
            startActivity(new Intent(this, ZuoyeBobaoActivity.class));
            EventBus.getDefault().post(new UpdateZuoyeEvent());
        } else if (this.hwtype.equals("1")) {
            startActivity(new Intent(this, HuodongZuoyeActivity.class));
            EventBus.getDefault().post(new UpdateHuoDongEvent());
        } else if (this.hwtype.equals("2")) {
            startActivity(new Intent(this, NoticeMessageActivity.class));
            EventBus.getDefault().post(new UpdateNoticeEvent());
        }
        finish();
        Toast.makeText(this, "发表成功", 1).show();
    }

    public void doUpload() {
        this.allid = this.photoInfoList.size();
        ImageUtils imageUtils = new ImageUtils();
        for (int m = 0; m < this.photoInfoList.size(); m++) {
            String pathimg = ((PhotoInfo) this.photoInfoList.get(m)).getPhotoPath();
            String mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".jpg");
            Bitmap photo = imageUtils.getimage(pathimg, mBigImageName);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya/" + mBigImageName);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((PublishJobPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8 && data != null) {
            int i;
            String mString;
            this.banjiList.clear();
            this.zuList.clear();
            this.gerenList.clear();
            this.banjiList = (List) data.getSerializableExtra("banjilist");
            this.zuList = (List) data.getSerializableExtra("xiaozulist");
            this.gerenList = (List) data.getSerializableExtra("gerenlist");
            if (this.banjiList == null || this.banjiList.size() <= 0) {
                this.banjilayout.setVisibility(8);
            } else {
                this.banjilayout.setVisibility(0);
                this.gvGridPickerView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
                this.gvGridPickerView.setAdapter(new UnClickZuAdapter(this.mContext, R.layout.new_grid_item, this.banjiList));
            }
            if (this.zuList == null || this.zuList.size() <= 0) {
                this.xiaozulayout.setVisibility(8);
            } else {
                this.xiaozulayout.setVisibility(0);
                FullyGridLayoutManager zuManager = new FullyGridLayoutManager(this.mContext, 3);
                zuManager.setScrollEnabled(false);
                this.zuGridPickerView.setLayoutManager(zuManager);
                this.zuGridPickerView.setAdapter(new UnClickZuAdapter(this.mContext, R.layout.new_grid_item, this.zuList));
            }
            if (this.gerenList == null || this.gerenList.size() <= 0) {
                this.gerenlayout.setVisibility(8);
            } else {
                this.gerenlayout.setVisibility(0);
                FullyGridLayoutManager geManager = new FullyGridLayoutManager(this.mContext, 3);
                geManager.setScrollEnabled(false);
                this.gerenGridPickerView.setLayoutManager(geManager);
                this.gerenGridPickerView.setAdapter(new UnClickZuAdapter(this.mContext, R.layout.grid_item, this.gerenList));
            }
            this.banjiid = "";
            this.xiaozuid = "";
            this.gerenid = "";
            for (i = 0; i < this.banjiList.size(); i++) {
                mString = ((ChildItem) this.banjiList.get(i)).getId();
                if (!this.banjiid.equals("")) {
                    this.banjiid += Constants.ACCEPT_TIME_SEPARATOR_SP;
                }
                this.banjiid += mString;
            }
            for (i = 0; i < this.zuList.size(); i++) {
                mString = ((ChildItem) this.zuList.get(i)).getId();
                if (!this.xiaozuid.equals("")) {
                    this.xiaozuid += Constants.ACCEPT_TIME_SEPARATOR_SP;
                }
                this.xiaozuid += mString;
            }
            for (i = 0; i < this.gerenList.size(); i++) {
                mString = ((ChildItem) this.gerenList.get(i)).getId();
                if (!this.gerenid.equals("")) {
                    this.gerenid += Constants.ACCEPT_TIME_SEPARATOR_SP;
                }
                this.gerenid += mString;
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int i = 0;
        if (event.getKeyCode() != 4 || event.getAction() != 0) {
            return super.dispatchKeyEvent(event);
        }
        this.describe = this.editdescribe.getText().toString().trim();
        this.photoInfoList.remove(this.photoInfo);
        int i2 = !this.describe.equals("") ? 1 : 0;
        if (this.photoInfoList.size() > 0) {
            i = 1;
        }
        if ((i2 | i) != 0) {
            showAlertDialog();
            return true;
        }
        finish();
        return true;
    }

    public void showAlertDialog() {
        Exsit.Builder builder = new Exsit.Builder(this);
        builder.setTitle("退出此次编辑？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PublishJobActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
