package com.fanyu.boundless.view.theclass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.DeleteClassApi;
import com.fanyu.boundless.bean.theclass.GetClassXiaoXiByIdApi;
import com.fanyu.boundless.bean.theclass.OutClassApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.OutClassPresenter;
import com.fanyu.boundless.util.CodeCreator;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.DeleteClassEvent;
import com.fanyu.boundless.view.myself.event.SelectClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.widget.AddPopWindow;
import com.fanyu.boundless.widget.Exsit.Builder;
import com.google.zxing.WriterException;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ClassXiaoXiActivity extends BaseActivity<OutClassPresenter> implements IClassXiaoXiView {
    private String bendiClassid;
    @Bind({2131624102})
    TextView classboss;
    @Bind({2131624108})
    TextView classgrade;
    @Bind({2131624107})
    RelativeLayout classgradeLayout;
    private String classid;
    @Bind({2131624099})
    ImageView classimg;
    @Bind({2131624085})
    TextView classname;
    @Bind({2131624101})
    TextView classnumber;
    private String classuserid;
    schoolclassentity entity;
    @Bind({2131624100})
    ImageView erweima;
    @Bind({2131624116})
    TextView existclass;
    @Bind({2131624106})
    View gradeView;
    @Bind({2131624115})
    Button jiesanclass;
    @Bind({2131624113})
    LinearLayout layoutUpdate;
    @Bind({2131624067})
    TextView messageTitle;
    @Bind({2131624104})
    RelativeLayout schoolLayout;
    @Bind({2131624103})
    View schoolView;
    @Bind({2131624105})
    TextView schoolname;
    private SharedPreferencesUtil sharedPreferencesUtil;
    @Bind({2131624112})
    TextView stunumber;
    @Bind({2131624110})
    TextView teachercount;
    @Bind({2131624114})
    Button update;
    @Bind({2131624098})
    ImageView updateclass;
    private String userid;

    protected void initView() {
        setContentView((int) R.layout.activity_class_xiaoxi);
    }

    protected void initPresenter() {
        this.mPresenter = new OutClassPresenter(this.mContext, this);
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        EventBus.getDefault().register(this);
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        this.bendiClassid = this.sharedPreferencesUtil.getString(Preferences.CLASS_ID, "");
        this.entity = (schoolclassentity) getIntent().getSerializableExtra("entity");
        this.classid = this.entity.getId();
        this.classuserid = this.entity.getUserid();
        if (this.userid.equals(this.entity.getUserid())) {
            this.existclass.setVisibility(8);
            this.layoutUpdate.setVisibility(0);
        } else {
            this.existclass.setVisibility(0);
            this.layoutUpdate.setVisibility(8);
        }
        initDate();
    }

    public void initDate() {
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.messageTitle.setText(this.entity.getClassname());
        }
        if (StringUtils.isEmpty(this.entity.getClassnumber() + "")) {
            this.classnumber.setText(this.entity.getClassnumber() + "");
        }
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.classname.setText(this.entity.getClassname());
        }
        if (StringUtils.isEmpty(this.entity.getCreatename())) {
            this.classboss.setText(this.entity.getCreatename());
        }
        if (StringUtils.isEmpty(this.entity.getTeanum())) {
            this.teachercount.setText(this.entity.getTeanum());
        }
        if (StringUtils.isEmpty(this.entity.getStunum())) {
            this.stunumber.setText(this.entity.getStunum());
        }
        if (StringUtils.isEmpty(this.entity.getClassgrade())) {
            this.gradeView.setVisibility(0);
            this.classgradeLayout.setVisibility(0);
            this.classgrade.setText(this.entity.getClassgrade());
        }
        if (StringUtils.isEmpty(this.entity.getSchoolname())) {
            this.schoolView.setVisibility(0);
            this.schoolLayout.setVisibility(0);
            this.schoolname.setText(this.entity.getSchoolname());
        }
        ImagePathUtil.getInstance().setImageUrl(this.mContext, this.classimg, this.entity.getClassimg(), new RoundedCornersTransformation(this.mContext, 0, 0));
        createErWeiMa(this.entity.getClassnumber() + "");
    }

    @OnClick({2131624066, 2131624109, 2131624111, 2131624116, 2131624098, 2131624114, 2131624115})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.updateclass:
                new AddPopWindow(this).showPopupWindow(this.updateclass, this.entity);
                return;
            case R.id.teacher:
                Intent intent = new Intent(this, TeacherListActivity.class);
                intent.putExtra("classid", this.classid);
                startActivity(intent);
                return;
            case R.id.student:
                Intent intent2 = new Intent(this, StudentListActivity.class);
                intent2.putExtra("classid", this.classid);
                intent2.putExtra("classuserid", this.classuserid);
                startActivity(intent2);
                return;
            case R.id.update:
                Intent update = new Intent(this, UpdateClassActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entity", this.entity);
                update.putExtras(bundle);
                startActivity(update);
                return;
            case R.id.jiesanclass:
                Builder jiesan = new Builder(this);
                jiesan.setTitle("解散班级？").setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new DeleteClassEvent());
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                jiesan.create().show();
                return;
            case R.id.existclass:
                Builder alert = new Builder(this);
                alert.setTitle("退出班级").setMessage("您确定退出当前班级吗？").setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OutClassApi outClassApi = new OutClassApi();
                        outClassApi.setUserid(ClassXiaoXiActivity.this.userid);
                        outClassApi.setClassid(ClassXiaoXiActivity.this.classid);
                        ((OutClassPresenter) ClassXiaoXiActivity.this.mPresenter).startPost(ClassXiaoXiActivity.this, outClassApi);
                    }
                }).setNegativeButton("取消", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
                return;
            default:
                return;
        }
    }

    private void createErWeiMa(String text) {
        try {
            this.erweima.setImageBitmap(CodeCreator.createQRCode(text));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void outClass(String isout) {
        out();
    }

    public void updateXiaoXi(schoolclassentity sentity) {
        this.entity = sentity;
        initDate();
    }

    public void deleteClass(String isdelete) {
        out();
    }

    public void out() {
        if (this.bendiClassid.equals(this.classid)) {
            SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this.mContext);
            editor.putString(Preferences.CLASS_NAME, "");
            editor.putString(Preferences.CLASS_ID, "");
        }
        EventBus.getDefault().post(new UpdateClassEvent());
        MyActivityManager.getsInstances().popAllActivityExceptOne("cls");
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(DeleteClassEvent deleteClassEvent) {
        DeleteClassApi deleteClassApi = new DeleteClassApi();
        deleteClassApi.setClassid(this.classid);
        ((OutClassPresenter) this.mPresenter).startPost(this, deleteClassApi);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(SelectClassEvent selectClassEvent) {
        GetClassXiaoXiByIdApi getClassXiaoXiByIdApi = new GetClassXiaoXiByIdApi();
        getClassXiaoXiByIdApi.setClassid(this.classid);
        ((OutClassPresenter) this.mPresenter).startPost(this, getClassXiaoXiByIdApi);
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind((Activity) this);
    }
}
