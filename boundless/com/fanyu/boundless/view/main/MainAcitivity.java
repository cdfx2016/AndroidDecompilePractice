package com.fanyu.boundless.view.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.fanyu.boundless.R;
import com.fanyu.boundless.config.EasyPermissions;
import com.fanyu.boundless.config.EasyPermissions.PermissionCallbacks;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.home.HomeLayout;
import com.fanyu.boundless.view.microclass.MicroClassLayout;
import com.fanyu.boundless.view.myself.MySelfLayout;
import com.fanyu.boundless.view.myself.event.UpdateUnreadEvent;
import com.fanyu.boundless.view.theclass.CaptureActivity;
import com.fanyu.boundless.view.theclass.ClassLayout;
import com.fanyu.boundless.widget.CustomViewPager;
import com.fanyu.boundless.widget.Exsit.Builder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.List;

public class MainAcitivity extends BaseActivity implements PermissionCallbacks {
    public static MainAcitivity mainAcitivity;
    @Bind({2131624206})
    TextView classPaopao;
    @Bind({2131624205})
    TextView homePaopao;
    private LinearLayout mClass;
    private LinearLayout mHome;
    private LinearLayout mMicroClass;
    private LinearLayout mMySelf;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) MainAcitivity.this.viewList.get(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = (View) MainAcitivity.this.viewList.get(position);
            container.addView(view);
            return view;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public int getCount() {
            return MainAcitivity.this.viewList.size();
        }
    };
    @Bind({2131624201})
    RadioGroup mainTabGroup;
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.banji:
                    MainAcitivity.this.vpViewpage.setCurrentItem(1);
                    return;
                case R.id.shouye:
                    MainAcitivity.this.vpViewpage.setCurrentItem(0);
                    return;
                case R.id.weke:
                    MainAcitivity.this.vpViewpage.setCurrentItem(2);
                    return;
                case R.id.wode:
                    MainAcitivity.this.vpViewpage.setCurrentItem(3);
                    return;
                default:
                    return;
            }
        }
    };
    TextView textView = null;
    private List<View> viewList;
    @Bind({2131624200})
    CustomViewPager vpViewpage;

    protected void initView() {
        setContentView((int) R.layout.activity_main);
    }

    protected void initPresenter() {
    }

    protected void init() {
        mainAcitivity = this;
        EventBus.getDefault().register(this);
        this.viewList = new ArrayList();
        this.mHome = new HomeLayout(this);
        this.mClass = new ClassLayout(this);
        this.mMicroClass = new MicroClassLayout(this);
        this.mMySelf = new MySelfLayout(this);
        this.viewList.add(this.mHome);
        this.viewList.add(this.mClass);
        this.viewList.add(this.mMicroClass);
        this.viewList.add(this.mMySelf);
        this.vpViewpage.setAdapter(this.mPagerAdapter);
        this.mainTabGroup.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateUnreadEvent updateUnreadEvent) {
        int updatetype = updateUnreadEvent.getUpdatetype();
        String unread = updateUnreadEvent.getUnread();
        if (updatetype == 1) {
            this.textView = this.classPaopao;
        } else if (updatetype == 2) {
            this.textView = this.homePaopao;
        }
        if (unread.equals("0")) {
            this.textView.setVisibility(4);
            return;
        }
        this.textView.setVisibility(0);
        this.textView.setText(unread);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != 4 || event.getAction() != 0) {
            return super.dispatchKeyEvent(event);
        }
        showAlertDialog();
        return true;
    }

    public void showAlertDialog() {
        Builder builder = new Builder(this);
        builder.setTitle("确定退出定行？");
        builder.setPositiveButton("退出", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainAcitivity.this.finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsGranted(int requestCode, List<String> list) {
        switch (requestCode) {
            case 1:
                startActivity(new Intent(this.mContext, CaptureActivity.class));
                return;
            default:
                return;
        }
    }

    public void onPermissionsDenied(int requestCode, List<String> list) {
        Toast.makeText(this.mContext, "请允许摄像头权限的使用！", 0).show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind((Activity) this);
    }
}
