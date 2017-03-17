package com.fanyu.boundless.view.theclass;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.JoinClassApi;
import com.fanyu.boundless.bean.theclass.PhoneReceiveServletApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.common.camera.BeepManager;
import com.fanyu.boundless.common.camera.CameraManager;
import com.fanyu.boundless.common.camera.CaptureActivityHandler;
import com.fanyu.boundless.common.camera.FinishListener;
import com.fanyu.boundless.common.camera.InactivityTimer;
import com.fanyu.boundless.common.camera.IntentSource;
import com.fanyu.boundless.common.camera.ViewfinderView;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.presenter.theclass.JoinClassPresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.SureLoginActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public final class CaptureActivity extends BaseActivity<JoinClassPresenter> implements Callback, IJoinClassView {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int WHAT_DID_LOAD_DATA = 0;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private String characterSet;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private schoolclassentity entity;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private ImageView imageButton_back;
    private InactivityTimer inactivityTimer;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1101) {
                Toast.makeText(CaptureActivity.this, "没有找到该班级", 1).show();
                CaptureActivity.this.finish();
            } else if (msg.what == 1102) {
                Toast.makeText(CaptureActivity.this, "扫描成功！", 1).show();
            }
        }
    };
    private String resultString;
    private IntentSource source;
    private String uuid;
    private ViewfinderView viewfinderView;

    public ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public CameraManager getCameraManager() {
        return this.cameraManager;
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        MyActivityManager.getsInstances().pushActivity(this);
        getWindow().addFlags(128);
        setContentView((int) R.layout.capture);
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        this.beepManager = new BeepManager(this);
        this.imageButton_back = (ImageView) findViewById(R.id.capture_imageview_back);
        this.imageButton_back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    protected void initView() {
    }

    protected void initPresenter() {
        this.mPresenter = new JoinClassPresenter(this.mContext, this);
    }

    protected void init() {
    }

    protected void onResume() {
        super.onResume();
        this.cameraManager = new CameraManager(getApplication());
        this.viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        this.viewfinderView.setCameraManager(this.cameraManager);
        this.handler = null;
        SurfaceHolder surfaceHolder = ((SurfaceView) findViewById(R.id.preview_view)).getHolder();
        if (this.hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        this.beepManager.updatePrefs();
        this.inactivityTimer.onResume();
        this.source = IntentSource.NONE;
        this.decodeFormats = null;
        this.characterSet = null;
    }

    protected void onPause() {
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }
        this.inactivityTimer.onPause();
        this.beepManager.close();
        this.cameraManager.closeDriver();
        if (!this.hasSurface) {
            ((SurfaceView) findViewById(R.id.preview_view)).getHolder().removeCallback(this);
        }
        super.onPause();
    }

    protected void onDestroy() {
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        this.inactivityTimer.onActivity();
        if (barcode != null) {
            this.beepManager.playBeepSoundAndVibrate();
            this.resultString = rawResult.getText();
            if (this.resultString.contains("LOGIN")) {
                PhoneReceiveServletApi phoneReceiveServletApi = new PhoneReceiveServletApi();
                this.uuid = this.resultString.substring(5);
                phoneReceiveServletApi.setUuid(this.uuid);
                ((JoinClassPresenter) this.mPresenter).startPost(this, phoneReceiveServletApi);
                return;
            }
            JoinClassApi joinClassApi = new JoinClassApi();
            joinClassApi.setClassnumber(this.resultString);
            ((JoinClassPresenter) this.mPresenter).startPost(this, joinClassApi);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        } else if (!this.cameraManager.isOpen()) {
            try {
                this.cameraManager.openDriver(surfaceHolder);
                if (this.handler == null) {
                    this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.decodeHints, this.characterSet, this.cameraManager);
                }
            } catch (IOException ioe) {
                Log.w(TAG, ioe);
                displayFrameworkBugMessageAndExit();
            } catch (RuntimeException e) {
                Log.w(TAG, "Unexpected error initializing camera", e);
                displayFrameworkBugMessageAndExit();
            }
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void getClassXinXi(schoolclassentity entity) {
        if (StringUtils.isEmpty(entity.getId())) {
            Toast.makeText(this, "扫描成功！", 1).show();
            Intent intent = new Intent();
            intent.setClass(this, SearchResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("entity", entity);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
            return;
        }
        Toast.makeText(this, "没有找到该班级", 1).show();
        finish();
    }

    public void phoneReceiveServlet(String result) {
        if (StringUtils.isEmpty(result)) {
            Intent intent = new Intent();
            intent.putExtra("uuid", this.uuid);
            intent.setClass(this, SureLoginActivity.class);
            startActivity(intent);
        }
    }
}
