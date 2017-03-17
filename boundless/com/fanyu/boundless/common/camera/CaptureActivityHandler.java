package com.fanyu.boundless.common.camera;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.fanyu.boundless.R;
import com.fanyu.boundless.view.theclass.CaptureActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import java.util.Collection;
import java.util.Map;

public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private final CameraManager cameraManager;
    private final DecodeThread decodeThread;
    private State state = State.SUCCESS;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureActivityHandler(CaptureActivity activity, Collection<BarcodeFormat> decodeFormats, Map<DecodeHintType, ?> baseHints, String characterSet, CameraManager cameraManager) {
        this.activity = activity;
        this.decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet, new ViewfinderResultPointCallback(activity.getViewfinderView()));
        this.decodeThread.start();
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode_failed:
                this.state = State.PREVIEW;
                this.cameraManager.requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
                return;
            case R.id.decode_succeeded:
                this.state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = null;
                float scaleFactor = 1.0f;
                if (bundle != null) {
                    byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (compressedBitmap != null) {
                        barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null).copy(Config.ARGB_8888, true);
                    }
                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                }
                this.activity.handleDecode((Result) message.obj, barcode, scaleFactor);
                return;
            case R.id.launch_product_query:
                String url = message.obj;
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(524288);
                intent.setData(Uri.parse(url));
                ResolveInfo resolveInfo = this.activity.getPackageManager().resolveActivity(intent, 65536);
                String browserPackageName = null;
                if (!(resolveInfo == null || resolveInfo.activityInfo == null)) {
                    browserPackageName = resolveInfo.activityInfo.packageName;
                    Log.d(TAG, "Using browser in package " + browserPackageName);
                }
                if ("com.android.browser".equals(browserPackageName) || "com.android.chrome".equals(browserPackageName)) {
                    intent.setPackage(browserPackageName);
                    intent.addFlags(268435456);
                    intent.putExtra("com.android.browser.application_id", browserPackageName);
                }
                try {
                    this.activity.startActivity(intent);
                    return;
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
                    return;
                }
            case R.id.restart_preview:
                restartPreviewAndDecode();
                return;
            case R.id.return_scan_result:
                this.activity.setResult(-1, (Intent) message.obj);
                this.activity.finish();
                return;
            default:
                return;
        }
    }

    public void quitSynchronously() {
        this.state = State.DONE;
        this.cameraManager.stopPreview();
        Message.obtain(this.decodeThread.getHandler(), R.id.quit).sendToTarget();
        try {
            this.decodeThread.join(500);
        } catch (InterruptedException e) {
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (this.state == State.SUCCESS) {
            this.state = State.PREVIEW;
            this.cameraManager.requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
            this.activity.drawViewfinder();
        }
    }
}
