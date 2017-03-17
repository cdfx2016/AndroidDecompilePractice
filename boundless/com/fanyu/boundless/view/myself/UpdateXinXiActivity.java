package com.fanyu.boundless.view.myself;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.view.PointerIconCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.myself.GetMyXinXiApi;
import com.fanyu.boundless.bean.myself.Tsuser;
import com.fanyu.boundless.bean.myself.UpdateXinXiApi;
import com.fanyu.boundless.bean.upload.UploadApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.myself.UpdateXinXiPresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdateNameEvent;
import com.fanyu.boundless.widget.MyDatePickerDialog;
import com.fanyu.boundless.widget.MyDatePickerDialog.OnDateSetListener;
import com.fanyu.boundless.widget.NewDialog.Builder;
import com.fanyu.boundless.widget.SexDialog;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.io.File;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class UpdateXinXiActivity extends BaseActivity<UpdateXinXiPresenter> implements IUpdateXinXIView, IUpdateImgView {
    public static UpdateXinXiActivity updateXinXiActivity;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
    private String fileName;
    private ImageUtils imageUtils = new ImageUtils();
    private SharedPreferencesUtil msharepreference;
    private String nickname;
    private String tablename = "tsuser";
    @Bind({2131624074})
    ImageView touxiang;
    @Bind({2131624264})
    TextView txtLoginname;
    @Bind({2131624270})
    TextView txtUserage;
    @Bind({2131624266})
    TextView txtUsername;
    @Bind({2131624268})
    TextView txtUsersex;
    private String userage;
    private String userid;
    private String userimg;
    private String username;
    private String usersex;

    protected void initView() {
        setContentView((int) R.layout.activity_update_xinxi);
    }

    protected void initPresenter() {
        this.mPresenter = new UpdateXinXiPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        updateXinXiActivity = this;
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        GetMyXinXiApi getMyXinXiApi = new GetMyXinXiApi();
        getMyXinXiApi.setUserid(this.userid);
        ((UpdateXinXiPresenter) this.mPresenter).startPost(this, getMyXinXiApi);
    }

    @OnClick({2131624066, 2131624074, 2131624267, 2131624269, 2131624271, 2131624265})
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
            case R.id.rl_username:
                startActivity(new Intent(this, UpdateNickNameActivity.class));
                return;
            case R.id.rl_usersex:
                showDiologSex();
                return;
            case R.id.rl_userage:
                new MyDatePickerDialog(this, "日期选择", new OnDateSetListener() {
                    public void onDateSet(View view, String dateStr, int year, int monthOfYear, int dayOfMonth) {
                        UpdateXinXiActivity.this.txtUserage.setText(dateStr);
                        UpdateXinXiActivity.this.fileName = "birthday";
                        UpdateXinXiApi updateXinXiApi = new UpdateXinXiApi();
                        updateXinXiApi.setId(UpdateXinXiActivity.this.userid);
                        updateXinXiApi.setTableName(UpdateXinXiActivity.this.tablename);
                        updateXinXiApi.setFileName(UpdateXinXiActivity.this.fileName);
                        updateXinXiApi.setFileValue(dateStr);
                        ((UpdateXinXiPresenter) UpdateXinXiActivity.this.mPresenter).startPost(UpdateXinXiActivity.this, updateXinXiApi);
                    }
                }, null).show();
                return;
            case R.id.rl_userpwd:
                startActivity(new Intent(this, UpdatePassWordActivity.class));
                return;
            default:
                return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateNameEvent updateNameEvent) {
        this.nickname = this.msharepreference.getString(Preferences.NICKNAME, "");
        this.txtUsername.setText(this.nickname);
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void showDiologSex() {
        SexDialog.Builder alert = new SexDialog.Builder(this);
        alert.setTitle("请选择性别").setPositiveButton("男", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UpdateXinXiActivity.this.txtUsersex.setText("男");
                UpdateXinXiActivity.this.fileName = "sex";
                UpdateXinXiApi updateXinXiApi = new UpdateXinXiApi();
                updateXinXiApi.setId(UpdateXinXiActivity.this.userid);
                updateXinXiApi.setTableName(UpdateXinXiActivity.this.tablename);
                updateXinXiApi.setFileName(UpdateXinXiActivity.this.fileName);
                updateXinXiApi.setFileValue("男");
                ((UpdateXinXiPresenter) UpdateXinXiActivity.this.mPresenter).startPost(UpdateXinXiActivity.this, updateXinXiApi);
                dialog.dismiss();
            }
        }).setNegativeButton("女", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UpdateXinXiActivity.this.txtUsersex.setText("女");
                UpdateXinXiActivity.this.fileName = "sex";
                UpdateXinXiApi updateXinXiApi = new UpdateXinXiApi();
                updateXinXiApi.setId(UpdateXinXiActivity.this.userid);
                updateXinXiApi.setTableName(UpdateXinXiActivity.this.tablename);
                updateXinXiApi.setFileName(UpdateXinXiActivity.this.fileName);
                updateXinXiApi.setFileValue("女");
                ((UpdateXinXiPresenter) UpdateXinXiActivity.this.mPresenter).startPost(UpdateXinXiActivity.this, updateXinXiApi);
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    public void isupdate(String isupdate, String state) {
        if (state.equals("addimg") && StringUtils.isEmpty(isupdate)) {
            isupdate = isupdate.replaceAll("\"", "");
            this.fileName = "userimg";
            SharedPreferencesUtil.getsInstances(this.mContext).putString(Preferences.USER_IMG, isupdate);
            EventBus.getDefault().post(new UpdateNameEvent(Item.UPDATE_ACTION));
            UpdateXinXiApi updateXinXiApi = new UpdateXinXiApi();
            updateXinXiApi.setId(this.userid);
            updateXinXiApi.setTableName(this.tablename);
            updateXinXiApi.setFileName(this.fileName);
            updateXinXiApi.setFileValue(isupdate);
            ((UpdateXinXiPresenter) this.mPresenter).startPost(this, updateXinXiApi);
        }
    }

    public void getMyXinXi(Tsuser tsuser) {
        if (StringUtils.isEmpty(tsuser.getUsername())) {
            this.txtLoginname.setText(tsuser.getUsername());
        }
        if (StringUtils.isEmpty(tsuser.getNickname())) {
            this.txtUsername.setText(tsuser.getNickname());
        }
        if (StringUtils.isEmpty(tsuser.getBirthday())) {
            this.txtUserage.setText(tsuser.getBirthday());
        }
        if (StringUtils.isEmpty(tsuser.getSex())) {
            if (tsuser.getSex().equals("0")) {
                this.usersex = "女";
                this.txtUsersex.setText(this.usersex);
            } else {
                this.usersex = "男";
                this.txtUsersex.setText(this.usersex);
            }
        }
        if (StringUtils.isEmpty(tsuser.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this.mContext, this.touxiang, tsuser.getUserimg(), new CropCircleTransformation(this.mContext));
        }
    }

    public void getPathImg(String pathimg) {
        if (StringUtils.isEmpty(pathimg)) {
            try {
                Glide.with(this.mContext).load(pathimg).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).bitmapTransform(new CropCircleTransformation(this.mContext)).dontAnimate().into(this.touxiang);
            } catch (Exception e) {
            }
            File file = new File(pathimg);
            UploadApi uploadApi = new UploadApi();
            uploadApi.setFilename(file.getName());
            uploadApi.setFile(file);
            ((UpdateXinXiPresenter) this.mPresenter).startPost(this, uploadApi);
        }
    }
}
