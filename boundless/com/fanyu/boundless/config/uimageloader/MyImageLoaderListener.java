package com.fanyu.boundless.config.uimageloader;

import android.graphics.Bitmap;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MyImageLoaderListener implements ImageLoadingListener {
    public void onLoadingStarted(String s, View view) {
    }

    public void onLoadingFailed(String s, View view, FailReason failReason) {
    }

    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
    }

    public void onLoadingCancelled(String s, View view) {
    }
}
