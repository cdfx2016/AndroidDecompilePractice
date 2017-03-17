package com.fanyu.boundless.view.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.fanyu.boundless.bean.home.AddDongtaiApi;
import com.fanyu.boundless.bean.home.SchoolClassEntityApi;
import com.fanyu.boundless.bean.theclass.AttApi;
import com.fanyu.boundless.bean.theclass.AttEntity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.config.Builder;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.AddDongtaiPresenter;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdatePinglunEvent;
import com.fanyu.boundless.view.theclass.AddPhotoAdapter;
import com.fanyu.boundless.widget.Exsit;
import com.fanyu.boundless.widget.SpinerPopWindow;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishDongtaiActivity extends BaseActivity<AddDongtaiPresenter> implements IAddDongtaiView {
    private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
    private OnClickListener _poptextClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.selectclass_pop_all:
                    PublishDongtaiActivity.this.mSpinerPopWindow1.dismiss();
                    return;
                default:
                    return;
            }
        }
    };
    private AddDongtaiApi addDongtaiApi;
    private AddPhotoAdapter addPhotoAdapter;
    private int allid;
    private int biaoji = 0;
    private String biaoqian = "学生动态";
    @Bind({2131624209})
    RadioGroup biaoqianGroup;
    private Builder builder;
    private List<schoolclassentity> classList = new ArrayList();
    private String className;
    private String classid;
    @Bind({2131624085})
    TextView classname;
    private String dailytypeid = "1";
    private String describe;
    @Bind({2131624143})
    RadioButton dongtai;
    @Bind({2131624160})
    EditText editdescribe;
    private FunctionConfig functionConfig;
    @Bind({2131624210})
    RadioButton heibanbao;
    @Bind({2131624207})
    ImageView imgXxsbReturn;
    private List<String> imglist = new ArrayList();
    private OnItemClickListener itemClickListener1 = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            PublishDongtaiActivity.this.mSpinerPopWindow1.dismiss();
            if (StringUtils.isEmpty((String) PublishDongtaiActivity.this.list1.get(position))) {
                PublishDongtaiActivity.this.classname.setText((CharSequence) PublishDongtaiActivity.this.list1.get(position));
            }
            PublishDongtaiActivity.this.classid = ((schoolclassentity) PublishDongtaiActivity.this.classList.get(position)).getId();
            PublishDongtaiActivity.this.className = ((schoolclassentity) PublishDongtaiActivity.this.classList.get(position)).getClassname();
        }
    };
    private List<String> list1 = new ArrayList();
    private List<AttEntity> listFilename = new ArrayList();
    OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            PublishDongtaiActivity.this.resetcolor();
            switch (checkedId) {
                case R.id.dongtai:
                    PublishDongtaiActivity.this.biaoqian = "学生动态";
                    PublishDongtaiActivity.this.dongtai.setTextColor(Color.parseColor("#ffffff"));
                    return;
                case R.id.heibanbao:
                    PublishDongtaiActivity.this.biaoqian = "黑板报";
                    PublishDongtaiActivity.this.heibanbao.setTextColor(Color.parseColor("#ffffff"));
                    return;
                case R.id.rongyubang:
                    PublishDongtaiActivity.this.biaoqian = "荣誉榜";
                    PublishDongtaiActivity.this.rongyubang.setTextColor(Color.parseColor("#ffffff"));
                    return;
                default:
                    return;
            }
        }
    };
    private String mBigImageName;
    private OnHanlderResultCallback mOnHanlderResultCallback = new OnHanlderResultCallback() {
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                PublishDongtaiActivity.this.photoInfoList.addAll(0, resultList);
                if (PublishDongtaiActivity.this.photoInfoList.size() < 7) {
                    PublishDongtaiActivity.this.builder.setMutiSelectMaxSize(7 - PublishDongtaiActivity.this.photoInfoList.size());
                    PublishDongtaiActivity.this.functionConfig = PublishDongtaiActivity.this.builder.build();
                } else {
                    PublishDongtaiActivity.this.photoInfoList.remove(PublishDongtaiActivity.this.photoInfoList.size() - 1);
                }
                PublishDongtaiActivity.this.addPhotoAdapter.notifyDataSetChanged();
            }
        }

        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };
    private SpinerPopWindow<String> mSpinerPopWindow1;
    @Bind({2131624067})
    TextView messageTitle;
    private SharedPreferencesUtil msharepreference;
    private int newbiaoji = 0;
    private PhotoInfo photoInfo;
    private List<PhotoInfo> photoInfoList;
    @Bind({2131624208})
    TextView rightBtn;
    @Bind({2131624211})
    RadioButton rongyubang;
    @Bind({2131624094})
    RelativeLayout selectclass;
    private String userid;
    private String zhuid;
    @Bind({2131624161})
    RecyclerView zuoyeview;

    class ItemListener implements com.fanyu.boundless.common.listener.OnItemClickListener {
        ItemListener() {
        }

        public void onItemClick(ViewHolder arg0, int arg1) {
            PublishDongtaiActivity.this.photoInfoList.remove(arg1);
            int size = PublishDongtaiActivity.this.photoInfoList.size();
            if (size < 6 && !PublishDongtaiActivity.this.photoInfoList.contains(PublishDongtaiActivity.this.photoInfo)) {
                PublishDongtaiActivity.this.photoInfoList.add(size, PublishDongtaiActivity.this.photoInfo);
            }
            if (PublishDongtaiActivity.this.photoInfoList.size() < 7) {
                PublishDongtaiActivity.this.builder.setMutiSelectMaxSize(7 - PublishDongtaiActivity.this.photoInfoList.size());
                PublishDongtaiActivity.this.functionConfig = PublishDongtaiActivity.this.builder.build();
            }
            PublishDongtaiActivity.this.addPhotoAdapter.notifyDataSetChanged();
        }
    }

    class clickListener implements com.fanyu.boundless.common.listener.OnClickListener {
        clickListener() {
        }

        public void onClick(ViewHolder holder) {
            GalleryFinal.openGalleryMuti((int) PointerIconCompat.TYPE_CONTEXT_MENU, PublishDongtaiActivity.this.functionConfig, PublishDongtaiActivity.this.mOnHanlderResultCallback);
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_publish_dongtai);
    }

    protected void initPresenter() {
        this.mPresenter = new AddDongtaiPresenter(this.mContext, this);
    }

    protected void init() {
        getWindow().setSoftInputMode(3);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        SchoolClassEntityApi schoolClassEntityApi = new SchoolClassEntityApi();
        schoolClassEntityApi.setPage("1");
        schoolClassEntityApi.setPagesize("20");
        schoolClassEntityApi.setUserid(this.userid);
        ((AddDongtaiPresenter) this.mPresenter).startPost(this, schoolClassEntityApi);
        this.builder = new Builder();
        this.builder.setMutiSelectMaxSize(6);
        this.functionConfig = this.builder.build();
        this.zuoyeview.setLayoutManager(new FullyGridLayoutManager(this.mContext, 4));
        this.photoInfoList = new ArrayList(0);
        this.photoInfo = new PhotoInfo();
        this.photoInfo.setPhotoId(ViewCompat.MEASURED_SIZE_MASK);
        this.photoInfo.setPhotoPath("2130903161");
        this.photoInfoList.add(this.photoInfo);
        this.addPhotoAdapter = new AddPhotoAdapter(this.mContext, R.layout.gridview_image_item, this.photoInfoList);
        this.addPhotoAdapter.setOnClickListener(new clickListener());
        this.addPhotoAdapter.setOnItemClickListener(new ItemListener());
        this.zuoyeview.setAdapter(this.addPhotoAdapter);
        this.biaoqianGroup.setOnCheckedChangeListener(this.listener);
    }

    public void resetcolor() {
        this.dongtai.setTextColor(Color.parseColor("#e1ba83"));
        this.heibanbao.setTextColor(Color.parseColor("#e1ba83"));
        this.rongyubang.setTextColor(Color.parseColor("#e1ba83"));
    }

    public void addDongtai(String result) {
        this.zhuid = result;
        System.out.println("zhid ============= " + this.zhuid);
        if (this.photoInfoList.size() > 0) {
            doUpload();
        } else {
            doFinish();
        }
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
            ((AddDongtaiPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }

    public void doFinish() {
        Toast.makeText(this, "发布成功", 0).show();
        EventBus.getDefault().post(new UpdatePinglunEvent(1));
        startActivity(new Intent(this, DongTaiActivity.class));
        finish();
    }

    public void getIMyClassName(List<schoolclassentity> list) {
        this.classList.clear();
        this.classList.addAll(list);
        if (this.classList != null && this.classList.size() != 0) {
            for (int i = 0; i < this.classList.size(); i++) {
                this.list1.add(((schoolclassentity) this.classList.get(i)).getClassname());
            }
            this.className = ((schoolclassentity) this.classList.get(0)).getClassname();
            if (StringUtils.isEmpty(this.className)) {
                this.classname.setText(this.className);
            }
            this.classid = ((schoolclassentity) this.classList.get(0)).getId();
            this.mSpinerPopWindow1 = new SpinerPopWindow(this, this.list1, this.itemClickListener1, this._poptextClickListener, this.selectclass);
        }
    }

    public void fileList(String result) {
        System.out.println("zhuid========" + this.zhuid);
        System.out.println("ms========" + result);
        AttApi attApi = new AttApi();
        attApi.setUserid(this.userid);
        attApi.setFilename(result);
        attApi.setIdid(this.zhuid);
        attApi.setItemtype("dongtai");
        ((AddDongtaiPresenter) this.mPresenter).startPost(this, attApi);
    }

    public void addAtt(String result) {
        this.newbiaoji++;
        if (this.allid == this.newbiaoji) {
            doFinish();
            this.newbiaoji = 0;
        }
    }

    @OnClick({2131624207, 2131624208, 2131624094})
    public void onclick(View v) {
        int i = 1;
        switch (v.getId()) {
            case R.id.selectclass:
                if (StringUtils.isEmpty(this.className)) {
                    this.mSpinerPopWindow1.setDataString(this.className);
                    this.mSpinerPopWindow1.showAtLocation(this.selectclass, 0, 0, 0);
                    return;
                }
                return;
            case R.id.img_xxsb_return:
                int i2;
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
            case R.id.right_btn:
                this.describe = this.editdescribe.getText().toString();
                this.addDongtaiApi = new AddDongtaiApi();
                this.addDongtaiApi.setUserid(this.userid);
                this.addDongtaiApi.setAddress("");
                this.addDongtaiApi.setBiaoqian(this.biaoqian);
                this.addDongtaiApi.setBiaoqianid("");
                this.addDongtaiApi.setDailytypeid(this.dailytypeid);
                this.addDongtaiApi.setClassname(this.className);
                this.addDongtaiApi.setClassid(this.classid);
                this.addDongtaiApi.setContent(this.describe);
                this.photoInfoList.remove(this.photoInfo);
                if (StringUtils.isEmpty(this.describe)) {
                    ((AddDongtaiPresenter) this.mPresenter).startPost(this, this.addDongtaiApi);
                    showLoadingDialog();
                    return;
                }
                Toast.makeText(this, "请填写内容！", 1).show();
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        closeLoadingDialog();
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
                PublishDongtaiActivity.this.finish();
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
