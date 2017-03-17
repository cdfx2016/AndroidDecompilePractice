package com.fanyu.boundless.widget;

import android.app.Activity;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.widget.GFImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class UILImageLoader implements ImageLoader {
    private Config mImageConfig;

    public UILImageLoader() {
        this(Config.RGB_565);
    }

    public UILImageLoader(Config config) {
        this.mImageConfig = config;
    }

    public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
        DisplayImageOptions options = new Builder().cacheOnDisk(false).cacheInMemory(false).bitmapConfig(this.mImageConfig).build();
        ImageSize imageSize = new ImageSize(width, height);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + path, new ImageViewAware(imageView), options);
    }

    public void clearMemoryCache() {
    }
}
