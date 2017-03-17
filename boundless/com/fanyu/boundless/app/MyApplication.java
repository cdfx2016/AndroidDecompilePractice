package com.fanyu.boundless.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Process;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.ThemeConfig.Builder;
import cn.smssdk.SMSSDK;
import com.fanyu.boundless.R;
import com.fanyu.boundless.config.uimageloader.config.ImageLoaderConfig;
import com.fanyu.boundless.widget.UILImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.util.List;

public class MyApplication extends Application {
    private static final String APP_ID = "2882303761517498915";
    private static final String APP_KEY = "5571749873915";
    public static final String TAG = "com.fanyu.boundless";
    public static Context app;
    public static String localVersion = "1.0.0";
    public static int localVersionCode = 1;
    private static MyApplication mInstance;
    public static String serverVersion = "1.0.0";
    public static int serverVersionCode = 1;
    public FunctionConfig functionConfig;

    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    public void onCreate() {
        super.onCreate();
        app = getApplicationContext();
        SMSSDK.initSDK(this, "166409918b53c", "ba471cedfeb28a64173ef510d8200fed");
        RxRetrofitApp.init(this);
        mInstance = this;
        initImageLoader();
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        ThemeConfig theme = new Builder().setIconBack(R.mipmap.fanhui).setTitleBarTextColor(Color.parseColor("#333333")).setTitleBarBgColor(Color.parseColor("#d29642")).setTitleBarBgColor(Color.parseColor("#d29642")).setFabNornalColor(Color.parseColor("#d29642")).setCheckSelectedColor(Color.parseColor("#d29642")).build();
        this.functionConfig = new com.fanyu.boundless.config.Builder().build();
        GalleryFinal.init(new CoreConfig.Builder(this, new UILImageLoader(), theme).setFunctionConfig(this.functionConfig).build());
    }

    private boolean shouldInit() {
        List<RunningAppProcessInfo> processInfos = ((ActivityManager) getSystemService("activity")).getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void initImageLoader() {
        ImageLoader.getInstance().init(ImageLoaderConfig.getImageLoaderConfiguration());
    }
}
