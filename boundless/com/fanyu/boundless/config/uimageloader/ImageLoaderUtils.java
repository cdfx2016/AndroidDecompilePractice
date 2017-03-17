package com.fanyu.boundless.config.uimageloader;

import android.widget.ImageView;
import com.fanyu.boundless.config.uimageloader.config.ImageLoaderConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ImageLoaderUtils {
    public static void defaultDisPlayImage(String url, ImageView imageView) {
        String str = url;
        ImageLoader.getInstance().displayImage(str, new ImageViewAware(imageView, false), ImageLoaderConfig.getDefaultDisplayImageOption(), new MyImageLoaderListener(), new MyImageLoadingProgressListener());
    }

    public static void defaultDisPlayRoundImage(String url, ImageView imageView) {
        String str = url;
        ImageLoader.getInstance().displayImage(str, new ImageViewAware(imageView, false), ImageLoaderConfig.getRoundDisplayImageOption(), new MyImageLoaderListener(), new MyImageLoadingProgressListener());
    }

    public static void defaultDisPlayImageFormSDcard(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage("file://" + url, new ImageViewAware(imageView, false), ImageLoaderConfig.getDefaultDisplayImageOption(), new MyImageLoaderListener(), new MyImageLoadingProgressListener());
    }

    public static void defaultDisPlayRoundImageFormSDcard(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage("file://" + url, new ImageViewAware(imageView, false), ImageLoaderConfig.getRoundDisplayImageOption(), new MyImageLoaderListener(), new MyImageLoadingProgressListener());
    }

    public static void onDestroy() {
        ImageLoader.getInstance().destroy();
    }
}
