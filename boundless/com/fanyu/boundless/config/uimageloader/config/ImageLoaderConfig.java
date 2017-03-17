package com.fanyu.boundless.config.uimageloader.config;

import android.graphics.Bitmap.Config;
import com.fanyu.boundless.R;
import com.fanyu.boundless.app.MyApplication;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;

public class ImageLoaderConfig {
    private static final DisplayImageOptions DEFAULT_DISPLAY_IMAGE_OPTION = new Builder().showImageOnLoading((int) R.drawable.empty_photo).showImageForEmptyUri((int) R.drawable.empty_photo).showImageOnFail((int) R.mipmap.jiazaishibai).cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.NONE).bitmapConfig(Config.RGB_565).delayBeforeLoading(100).imageScaleType(ImageScaleType.EXACTLY).build();
    private static final File IMAGE_LOADER_CACHE_DIR = StorageUtils.getOwnCacheDirectory(MyApplication.getInstance().getApplicationContext(), "policexin/imageloader/Cache");
    private static final DisplayImageOptions ROUND_DISPLAY_IMAGE_OPTION = new Builder().showImageForEmptyUri((int) R.mipmap.ic_launcher).showImageOnFail((int) R.mipmap.ic_launcher).cacheInMemory(true).displayer(new RoundedBitmapDisplayer(7)).cacheOnDisc(true).imageScaleType(ImageScaleType.NONE).bitmapConfig(Config.RGB_565).delayBeforeLoading(100).build();

    public static ImageLoaderConfiguration getImageLoaderConfiguration() {
        return new ImageLoaderConfiguration.Builder(MyApplication.getInstance().getApplicationContext()).memoryCacheExtraOptions(480, 800).threadPoolSize(3).threadPriority(3).denyCacheImageMultipleSizesInMemory().memoryCache(new WeakMemoryCache()).memoryCacheSize(5242880).discCacheSize(52428800).tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(100).discCache(new UnlimitedDiscCache(IMAGE_LOADER_CACHE_DIR)).imageDownloader(new BaseImageDownloader(MyApplication.getInstance().getApplicationContext(), 5000, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS)).build();
    }

    public static DisplayImageOptions getDefaultDisplayImageOption() {
        return DEFAULT_DISPLAY_IMAGE_OPTION;
    }

    public static DisplayImageOptions getRoundDisplayImageOption() {
        return ROUND_DISPLAY_IMAGE_OPTION;
    }
}
