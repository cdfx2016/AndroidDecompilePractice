package cn.finalteam.galleryfinal;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.PointerIconCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;
import cn.finalteam.galleryfinal.GalleryFinal.OnHanlderResultCallback;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import cn.finalteam.galleryfinal.permission.EasyPermissions.PermissionCallbacks;
import cn.finalteam.galleryfinal.utils.ILogger;
import cn.finalteam.galleryfinal.utils.MediaScanner;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.toolsfinal.ActivityManager;
import cn.finalteam.toolsfinal.DateUtils;
import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class PhotoBaseActivity extends Activity implements PermissionCallbacks {
    protected static String mPhotoTargetFolder;
    protected int RC_CAMERA_PERM = 10;
    protected Handler mFinishHanlder = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PhotoBaseActivity.this.finishGalleryFinalPage();
        }
    };
    private MediaScanner mMediaScanner;
    protected int mScreenHeight = 1280;
    protected int mScreenWidth = 720;
    protected boolean mTakePhotoAction;
    private Uri mTakePhotoUri;
    private String[] params = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    protected File toFile;

    protected abstract void takeResult(PhotoInfo photoInfo);

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("takePhotoUri", this.mTakePhotoUri);
        outState.putString("photoTargetFolder", mPhotoTargetFolder);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mTakePhotoUri = (Uri) savedInstanceState.getParcelable("takePhotoUri");
        mPhotoTargetFolder = savedInstanceState.getString("photoTargetFolder");
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);
        this.mMediaScanner = new MediaScanner(this);
        DisplayMetrics dm = DeviceUtils.getScreenPix(this);
        this.mScreenWidth = dm.widthPixels;
        this.mScreenHeight = dm.heightPixels;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mMediaScanner != null) {
            this.mMediaScanner.unScanFile();
        }
        ActivityManager.getActivityManager().finishActivity((Activity) this);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, 0).show();
    }

    protected void takePhotoAction() {
        if (DeviceUtils.existSDCard()) {
            File takePhotoFolder;
            if (StringUtils.isEmpty(mPhotoTargetFolder)) {
                takePhotoFolder = GalleryFinal.getCoreConfig().getTakePhotoFolder();
            } else {
                takePhotoFolder = new File(mPhotoTargetFolder);
            }
            boolean suc = FileUtils.mkdirs(takePhotoFolder);
            this.toFile = new File(takePhotoFolder, "IMG" + DateUtils.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
            ILogger.d("create folder=" + this.toFile.getAbsolutePath(), new Object[0]);
            if (!suc) {
                takePhotoFailure();
                ILogger.e("create file failure", new Object[0]);
                return;
            } else if (VERSION.SDK_INT >= 23) {
                if (EasyPermissions.hasPermissions(this, this.params)) {
                    this.mTakePhotoUri = getUriForFile(this, this.toFile);
                    captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    captureIntent.addCategory("android.intent.category.DEFAULT");
                    captureIntent.setFlags(1);
                    captureIntent.setFlags(2);
                    captureIntent.putExtra("output", this.mTakePhotoUri);
                    startActivityForResult(captureIntent, PointerIconCompat.TYPE_CONTEXT_MENU);
                    return;
                }
                EasyPermissions.requestPermissions(this, "请打开摄像头权限", this.RC_CAMERA_PERM, this.params);
                return;
            } else if (cameraIsCanUse()) {
                this.mTakePhotoUri = Uri.fromFile(this.toFile);
                captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                captureIntent.putExtra("output", this.mTakePhotoUri);
                startActivityForResult(captureIntent, PointerIconCompat.TYPE_CONTEXT_MENU);
                return;
            } else {
                SetTiShi("请打开摄像头权限");
                return;
            }
        }
        String errormsg = getString(R.string.empty_sdcard);
        toast(errormsg);
        if (this.mTakePhotoAction) {
            resultFailure(errormsg, true);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PointerIconCompat.TYPE_CONTEXT_MENU) {
            return;
        }
        if (resultCode != -1 || this.mTakePhotoUri == null) {
            takePhotoFailure();
            return;
        }
        String path = this.toFile.getPath();
        if (this.toFile.exists()) {
            PhotoInfo info = new PhotoInfo();
            info.setPhotoId(Utils.getRandom(10000, 99999));
            info.setPhotoPath(path);
            updateGallery(path);
            takeResult(info);
            return;
        }
        takePhotoFailure();
    }

    private void takePhotoFailure() {
        String errormsg = getString(R.string.take_photo_fail);
        if (this.mTakePhotoAction) {
            resultFailure(errormsg, true);
        } else {
            toast(errormsg);
        }
    }

    private void updateGallery(String filePath) {
        if (this.mMediaScanner != null) {
            this.mMediaScanner.scanFile(filePath, "image/jpeg");
        }
    }

    protected void resultData(ArrayList<PhotoInfo> photoList) {
        OnHanlderResultCallback callback = GalleryFinal.getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            if (photoList == null || photoList.size() <= 0) {
                callback.onHanlderFailure(requestCode, getString(R.string.photo_list_empty));
            } else {
                callback.onHanlderSuccess(requestCode, photoList);
            }
        }
        finishGalleryFinalPage();
    }

    protected void resultFailureDelayed(String errormsg, boolean delayFinish) {
        OnHanlderResultCallback callback = GalleryFinal.getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            callback.onHanlderFailure(requestCode, errormsg);
        }
        if (delayFinish) {
            this.mFinishHanlder.sendEmptyMessageDelayed(0, 500);
        } else {
            finishGalleryFinalPage();
        }
    }

    protected void resultFailure(String errormsg, boolean delayFinish) {
        OnHanlderResultCallback callback = GalleryFinal.getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            callback.onHanlderFailure(requestCode, errormsg);
        }
        if (delayFinish) {
            finishGalleryFinalPage();
        } else {
            finishGalleryFinalPage();
        }
    }

    private void finishGalleryFinalPage() {
        ActivityManager.getActivityManager().finishActivity(PhotoEditActivity.class);
        ActivityManager.getActivityManager().finishActivity(PhotoSelectActivity.class);
        Global.mPhotoSelectActivity = null;
        System.gc();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
                return;
            case 11:
                EasyPermissions.checkCallingObjectSuitability(this);
                PhotoBaseActivity callbacks = this;
                ArrayList<String> granted = new ArrayList();
                ArrayList<String> denied = new ArrayList();
                for (int i = 0; i < permissions.length; i++) {
                    String perm = permissions[i];
                    if (grantResults[i] == 0) {
                        granted.add(perm);
                    } else {
                        denied.add(perm);
                    }
                }
                if (!granted.isEmpty()) {
                    onPermissionsGranted(granted);
                }
                if (!denied.isEmpty()) {
                    onPermissionsDenied(denied);
                }
                if (!granted.isEmpty() && denied.isEmpty()) {
                    Toast.makeText(this, "权限未开启，请用户手动开启权限", 0).show();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onPermissionsGranted(List<String> list) {
        this.mTakePhotoUri = getUriForFile(this, this.toFile);
        Intent captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        captureIntent.putExtra("output", this.mTakePhotoUri);
        startActivityForResult(captureIntent, PointerIconCompat.TYPE_CONTEXT_MENU);
    }

    public void onPermissionsDenied(List<String> list) {
        SetTiShi("请打开摄像头权限");
    }

    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera camera = null;
        try {
            camera = Camera.open();
            camera.setParameters(camera.getParameters());
        } catch (Exception e) {
            isCanUse = false;
        }
        if (camera != null) {
            try {
                camera.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return isCanUse;
    }

    private void SetTiShi(String title) {
        new Builder(this).setMessage(title).setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EasyPermissions.executePermissionsRequest(this, PhotoBaseActivity.this.params, 11);
            }
        }).setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PhotoBaseActivity.this.finish();
            }
        }).create().show();
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        } else if (VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("_data", file.getAbsolutePath());
            return context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        }
    }
}
