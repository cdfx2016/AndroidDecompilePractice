package com.fanyu.boundless.view.theclass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import com.fanyu.boundless.bean.theclass.UpdateClassApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.presenter.theclass.UpdateClassPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.IUpdateImgView;
import com.fanyu.boundless.view.myself.event.SelectClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassNameEvent;
import com.fanyu.boundless.widget.NewDialog.Builder;
import com.fanyu.boundless.widget.SpinerPopWindow;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UpdateClassActivity extends BaseActivity<UpdateClassPresenter> implements IUpdateClassView, IUpdateImgView {
    @Bind({2131624259})
    TextView ban;
    @Bind({2131624108})
    TextView classgrade;
    private String classgradeString;
    private String classidString;
    @Bind({2131624099})
    ImageView classimg;
    private String classimgString;
    @Bind({2131624085})
    EditText classname;
    private String classnameString;
    @Bind({2131624101})
    TextView classnumber;
    private OnDismissListener dismissListener = new OnDismissListener() {
        public void onDismiss() {
        }
    };
    schoolclassentity entity;
    @Bind({2131624257})
    TextView grade;
    private String gradeString;
    private OnItemClickListener itemClickListener1 = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            UpdateClassActivity.this.mSpinerPopWindow1.dismiss();
            if (StringUtils.isEmpty((String) UpdateClassActivity.this.list1.get(position))) {
                UpdateClassActivity.this.grade.setText((CharSequence) UpdateClassActivity.this.list1.get(position));
            }
            UpdateClassActivity.this.gradeString = (String) UpdateClassActivity.this.list1.get(position);
        }
    };
    private OnItemClickListener itemClickListener2 = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            UpdateClassActivity.this.mSpinerPopWindow2.dismiss();
            if (StringUtils.isEmpty((String) UpdateClassActivity.this.list2.get(position))) {
                UpdateClassActivity.this.ban.setText((CharSequence) UpdateClassActivity.this.list2.get(position));
            }
            UpdateClassActivity.this.numberString = (String) UpdateClassActivity.this.list2.get(position);
        }
    };
    private List<String> list1;
    private List<String> list2;
    String mBigImageName;
    private SpinerPopWindow<String> mSpinerPopWindow1;
    private SpinerPopWindow<String> mSpinerPopWindow2;
    @Bind({2131624067})
    TextView messageTitle;
    private String numberString;
    @Bind({2131624105})
    EditText schoolname;
    private String schoolnameString;
    @Bind({2131624258})
    RelativeLayout selectban;
    @Bind({2131624256})
    RelativeLayout selectgrade;

    protected void initView() {
        setContentView((int) R.layout.activity_update_class);
    }

    protected void initPresenter() {
        this.mPresenter = new UpdateClassPresenter(this.mContext, this);
    }

    protected void init() {
        this.entity = (schoolclassentity) getIntent().getSerializableExtra("entity");
        this.classidString = this.entity.getId();
        this.classgradeString = this.entity.getClassgrade();
        this.classnameString = this.entity.getClassname();
        this.classimgString = this.entity.getClassimg();
        this.schoolnameString = this.entity.getSchoolname();
        this.messageTitle.setText(this.entity.getClassname());
        if (StringUtils.isEmpty(this.schoolnameString)) {
            this.schoolname.setText(this.schoolnameString);
        }
        this.classname.setText(this.classnameString);
        this.classname.setSelection(this.classnameString.length());
        if (StringUtils.isEmpty(this.classgradeString)) {
            this.grade.setText(this.classgradeString.substring(0, 1));
            this.gradeString = this.classgradeString.substring(0, 1);
            this.ban.setText(this.classgradeString.substring(1, 4));
            this.numberString = this.classgradeString.substring(1, 4);
        } else {
            this.grade.setText("请选择");
            this.ban.setText("请选择");
        }
        ImagePathUtil.getInstance().setImageUrl(this.mContext, this.classimg, this.entity.getClassimg(), new RoundedCornersTransformation(this.mContext, 15, 0));
        initData();
        this.mSpinerPopWindow1 = new SpinerPopWindow((Activity) this, this.list1, this.itemClickListener1, 6);
        this.mSpinerPopWindow1.setOnDismissListener(this.dismissListener);
        this.mSpinerPopWindow2 = new SpinerPopWindow((Activity) this, this.list2, this.itemClickListener2, 6);
        this.mSpinerPopWindow2.setOnDismissListener(this.dismissListener);
    }

    @OnClick({2131624066, 2131624248, 2131624099, 2131624256, 2131624258})
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
                        if (which == 2) {
                            UpdateClassActivity.this.startActivityForResult(new Intent(UpdateClassActivity.this, SelectOneActivity.class), 6);
                        }
                    }
                });
                builder.create().show();
                return;
            case R.id.wancheng:
                if (StringUtils.isEmpty(this.gradeString) && StringUtils.isEmpty(this.numberString)) {
                    this.classgradeString = this.gradeString + this.numberString + "班";
                } else {
                    this.classgradeString = "";
                }
                this.classnameString = this.classname.getText().toString();
                this.schoolnameString = this.schoolname.getText().toString();
                UpdateClassApi updateClassApi = new UpdateClassApi();
                updateClassApi.setClassid(this.classidString);
                updateClassApi.setSchoolname(this.schoolnameString);
                updateClassApi.setClassgrade(this.classgradeString);
                updateClassApi.setClassname(this.classnameString);
                updateClassApi.setClassimg(this.classimgString);
                ((UpdateClassPresenter) this.mPresenter).startPost(this, updateClassApi);
                return;
            case R.id.selectgrade:
                this.mSpinerPopWindow1.setWidth(this.selectgrade.getWidth());
                this.mSpinerPopWindow1.setHeight(256);
                this.mSpinerPopWindow1.showAsDropDown(this.selectgrade);
                return;
            case R.id.selectban:
                this.mSpinerPopWindow2.setWidth(this.selectban.getWidth());
                this.mSpinerPopWindow2.setHeight(256);
                this.mSpinerPopWindow2.showAsDropDown(this.selectban);
                return;
            default:
                return;
        }
    }

    private void initData() {
        this.list1 = new ArrayList();
        this.list1.add("一");
        this.list1.add("二");
        this.list1.add("三");
        this.list1.add("四");
        this.list1.add("五");
        this.list1.add("六");
        this.list1.add("七");
        this.list1.add("八");
        this.list1.add("九");
        this.list2 = new ArrayList();
        this.list2.add("(1)");
        this.list2.add("(2)");
        this.list2.add("(3)");
        this.list2.add("(4)");
        this.list2.add("(5)");
        this.list2.add("(6)");
        this.list2.add("(7)");
        this.list2.add("(8)");
        this.list2.add("(9)");
        this.list2.add("(10)");
        this.list2.add("(11)");
        this.list2.add("(12)");
        this.list2.add("(13)");
        this.list2.add("(14)");
        this.list2.add("(15)");
        this.list2.add("(16)");
        this.list2.add("(17)");
        this.list2.add("(18)");
        this.list2.add("(19)");
        this.list2.add("(20)");
    }

    public void isupdate(String isupdate) {
        EventBus.getDefault().post(new UpdateClassEvent());
        EventBus.getDefault().post(new SelectClassEvent());
        EventBus.getDefault().post(new UpdateClassNameEvent(this.classnameString));
        finish();
        Toast.makeText(this, "修改成功", 0).show();
    }

    public void isupload(String userimg) {
        this.classimgString = userimg;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 6:
                    int m = data.getIntExtra("image", 0);
                    try {
                        Glide.with(this.mContext).load(Integer.valueOf(m)).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new CropCircleTransformation(this.mContext)).into(this.classimg);
                    } catch (Exception e) {
                    }
                    Bitmap photo = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), m));
                    this.mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".png");
                    ImageUtils imageUtils = new ImageUtils();
                    ImageUtils.savePhotoToSDCard(photo, Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya", this.mBigImageName);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya", this.mBigImageName);
                    UploadApi uploadApi = new UploadApi();
                    uploadApi.setFilename(file.getName());
                    uploadApi.setFile(file);
                    ((UpdateClassPresenter) this.mPresenter).startPost(this, uploadApi);
                    return;
                default:
                    return;
            }
        }
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            try {
                Glide.with(this.mContext).load(pathimg).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().bitmapTransform(new CropCircleTransformation(this.mContext)).into(this.classimg);
            } catch (Exception e) {
            }
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((UpdateClassPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }
}
