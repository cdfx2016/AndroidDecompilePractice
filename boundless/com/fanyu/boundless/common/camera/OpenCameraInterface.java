package com.fanyu.boundless.common.camera;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public final class OpenCameraInterface {
    private static final String TAG = OpenCameraInterface.class.getName();

    private OpenCameraInterface() {
    }

    @SuppressLint({"NewApi"})
    public static Camera open(int cameraId) {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.w(TAG, "No cameras!");
            return null;
        }
        boolean explicitRequest;
        if (cameraId >= 0) {
            explicitRequest = true;
        } else {
            explicitRequest = false;
        }
        if (!explicitRequest) {
            int index = 0;
            while (index < numCameras) {
                CameraInfo cameraInfo = new CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == 0) {
                    break;
                }
                index++;
            }
            cameraId = index;
        }
        if (cameraId < numCameras) {
            Log.i(TAG, "Opening camera #" + cameraId);
            return Camera.open(cameraId);
        } else if (explicitRequest) {
            Log.w(TAG, "Requested camera does not exist: " + cameraId);
            return null;
        } else {
            Log.i(TAG, "No camera facing back; returning camera #0");
            return Camera.open(0);
        }
    }

    public static Camera open() {
        return open(-1);
    }
}
