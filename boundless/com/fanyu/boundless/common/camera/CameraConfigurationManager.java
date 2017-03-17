package com.fanyu.boundless.common.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.fanyu.boundless.view.theclass.PreferencesActivity;
import java.lang.reflect.Method;

final class CameraConfigurationManager {
    private static final String TAG = "CameraConfiguration";
    private Point cameraResolution;
    private final Context context;
    private Point screenResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    @SuppressLint({"NewApi"})
    void initFromCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        Display display = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        this.screenResolution = new Point(display.getWidth(), display.getHeight());
        Log.i(TAG, "Screen resolution: " + this.screenResolution);
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = this.screenResolution.x;
        screenResolutionForCamera.y = this.screenResolution.y;
        if (this.screenResolution.x < this.screenResolution.y) {
            screenResolutionForCamera.x = this.screenResolution.y;
            screenResolutionForCamera.y = this.screenResolution.x;
        }
        this.cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
        Log.i(TAG, "Camera resolution: " + this.cameraResolution);
    }

    void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());
        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        CameraConfigurationUtils.setFocus(parameters, prefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true), prefs.getBoolean(PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true), safeMode);
        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        setDisplayOrientation(camera, 90);
        Log.i(TAG, "Final camera parameters: " + parameters.flatten());
        camera.setParameters(parameters);
        Size afterSize = camera.getParameters().getPreviewSize();
        if (afterSize == null) {
            return;
        }
        if (this.cameraResolution.x != afterSize.width || this.cameraResolution.y != afterSize.height) {
            Log.w(TAG, "Camera said it supported preview size " + this.cameraResolution.x + 'x' + this.cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            this.cameraResolution.x = afterSize.width;
            this.cameraResolution.y = afterSize.height;
        }
    }

    void setDisplayOrientation(Camera camera, int angle) {
        try {
            Method method = camera.getClass().getMethod("setDisplayOrientation", new Class[]{Integer.TYPE});
            if (method != null) {
                method.invoke(camera, new Object[]{Integer.valueOf(angle)});
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }

    Point getScreenResolution() {
        return this.screenResolution;
    }
}
