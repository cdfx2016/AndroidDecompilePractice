package com.fanyu.boundless.view.theclass;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.AddChildApi;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.AddChildPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.IUpdateImgView;
import com.fanyu.boundless.view.myself.event.AddChildEvent;
import com.fanyu.boundless.widget.NewDialog.Builder;
import com.fanyu.boundless.widget.SpinerPopWindow;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class AddChildActivity extends BaseActivity<AddChildPresenter> implements IAddChildView, IUpdateImgView {
    private OnDismissListener dismissListener = new OnDismissListener() {
        public void onDismiss() {
        }
    };
    @Bind({2131624075})
    EditText editname;
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            AddChildActivity.this.mSpinerPopWindow.dismiss();
            if (StringUtils.isEmpty((String) AddChildActivity.this.list.get(position))) {
                AddChildActivity.this.sex.setText((CharSequence) AddChildActivity.this.list.get(position));
            }
            AddChildActivity.this.ssex = (String) AddChildActivity.this.list.get(position);
        }
    };
    private List<String> list;
    private SpinerPopWindow<String> mSpinerPopWindow;
    SharedPreferencesUtil msharepreference;
    private String parentid;
    @Bind({2131624076})
    RelativeLayout selectsex;
    @Bind({2131624077})
    TextView sex;
    private String simg;
    private String sname;
    private String ssex = "男";
    @Bind({2131624074})
    ImageView touxiang;

    protected void initView() {
        setContentView((int) R.layout.activity_addchild);
    }

    protected void initPresenter() {
        this.mPresenter = new AddChildPresenter(this.mContext, this);
    }

    protected void init() {
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.parentid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.list = new ArrayList();
        this.list.add("男");
        this.list.add("女");
        this.mSpinerPopWindow = new SpinerPopWindow((Activity) this, this.list, this.itemClickListener, 6);
        this.mSpinerPopWindow.setOnDismissListener(this.dismissListener);
    }

    @OnClick({2131624066, 2131624074, 2131624076, 2131624078})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.touxiang:
                Builder builder = new Builder(this.mContext, this);
                builder.setTitle("图片来源");
                builder.setItems(new String[]{"从相册选择", "拍照"});
                builder.create().show();
                return;
            case R.id.selectsex:
                this.mSpinerPopWindow.setWidth(this.selectsex.getWidth());
                this.mSpinerPopWindow.showAsDropDown(this.selectsex);
                return;
            case R.id.submit:
                this.sname = this.editname.getText().toString();
                if (StringUtils.isEmpty(this.sname)) {
                    AddChildApi addChildApi = new AddChildApi();
                    addChildApi.setSname(this.sname);
                    addChildApi.setSex(this.ssex);
                    addChildApi.setSimg(this.simg);
                    addChildApi.setParentid(this.parentid);
                    ((AddChildPresenter) this.mPresenter).startPost(this, addChildApi);
                    return;
                }
                Toast.makeText(this, "请输入孩子姓名", 0).show();
                return;
            default:
                return;
        }
    }

    public void addchild(String isadd) {
        if (StringUtils.isEmpty(isadd)) {
            EventBus.getDefault().post(new AddChildEvent());
            finish();
        }
    }

    public void isupload(String isupload) {
        this.simg = isupload;
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            try {
                Glide.with(this.mContext).load(pathimg).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new CropCircleTransformation(this.mContext)).into(this.touxiang);
            } catch (Exception e) {
            }
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((AddChildPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }
}
