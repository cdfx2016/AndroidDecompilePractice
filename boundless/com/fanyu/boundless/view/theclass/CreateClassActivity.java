package com.fanyu.boundless.view.theclass;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.CreateClassApi;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.CreateClassPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.IUpdateImgView;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.widget.NewDialog.Builder;
import de.greenrobot.event.EventBus;
import java.io.File;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CreateClassActivity extends BaseActivity<CreateClassPresenter> implements ICreateClassView, IUpdateImgView {
    @Bind({2131624099})
    ImageView classimg;
    private String classimgString;
    private String classname;
    @Bind({2131624117})
    EditText editclassname;
    String mBigImageName;
    private SharedPreferencesUtil msharepreference;
    private String userid;
    private String username;

    protected void initView() {
        setContentView((int) R.layout.activity_create_class);
    }

    protected void initPresenter() {
        this.mPresenter = new CreateClassPresenter(this.mContext, this);
    }

    protected void init() {
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.username = this.msharepreference.getString(Preferences.NICKNAME, "");
    }

    @OnClick({2131624066, 2131624099, 2131624118})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.classimg:
                Builder builder = new Builder(this.mContext, this);
                builder.setTitle("图片来源");
                builder.setItems(new String[]{"从相册选择", "拍照", "选一个"}, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 2) {
                            return;
                        }
                        if (ContextCompat.checkSelfPermission(CreateClassActivity.this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                            ActivityCompat.requestPermissions(CreateClassActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 5);
                            return;
                        }
                        CreateClassActivity.this.startActivityForResult(new Intent(CreateClassActivity.this, SelectOneActivity.class), 6);
                    }
                });
                builder.create().show();
                return;
            case R.id.create:
                this.classname = this.editclassname.getText().toString();
                if (!StringUtils.isEmpty(this.classname)) {
                    Toast.makeText(this, "请输入班级称号！", 1).show();
                    return;
                } else if (StringUtils.isEmpty(this.classimgString)) {
                    CreateClassApi createClassApi = new CreateClassApi();
                    createClassApi.setClassname(this.classname);
                    createClassApi.setClassimg(this.classimgString);
                    createClassApi.setUserid(this.userid);
                    createClassApi.setUsername(this.username);
                    ((CreateClassPresenter) this.mPresenter).startPost(this, createClassApi);
                    return;
                } else {
                    Toast.makeText(this, "请选择班徽！", 1).show();
                    return;
                }
            default:
                return;
        }
    }

    public void iscreate(String iscreate) {
        if (StringUtils.isEmpty(iscreate)) {
            this.msharepreference.putString(Preferences.CLASS_NAME, this.classname);
            this.msharepreference.putString(Preferences.CLASS_ID, iscreate);
            this.msharepreference.putString(Preferences.USER_TYPE, "3");
            EventBus.getDefault().post(new UpdateClassEvent());
            EventBus.getDefault().post(new UpdateMainMessageEvent());
            finish();
            return;
        }
        Toast.makeText(this, "班级创建失败", 1).show();
    }

    public void isupload(String imgpath) {
        if (StringUtils.isEmpty(imgpath)) {
            this.classimgString = imgpath;
            ImagePathUtil.getInstance().setImageUrl(this.mContext, this.classimg, this.classimgString, new CropCircleTransformation(this.mContext));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 6:
                    Bitmap photo = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), data.getIntExtra("image", 0)));
                    this.mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".png");
                    Bitmap myphoto = new ImageUtils().compressImage(photo, this.mBigImageName);
                    this.mBigImageName = Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya/" + this.mBigImageName;
                    if (photo != null) {
                        getPathImg(this.mBigImageName);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((CreateClassPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 5 && grantResults[0] == 0) {
            startActivityForResult(new Intent(this, SelectOneActivity.class), 6);
        }
    }
}
