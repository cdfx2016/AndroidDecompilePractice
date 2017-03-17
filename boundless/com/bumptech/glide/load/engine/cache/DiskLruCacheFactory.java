package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import java.io.File;

public class DiskLruCacheFactory implements Factory {
    private final CacheDirectoryGetter cacheDirectoryGetter;
    private final int diskCacheSize;

    public interface CacheDirectoryGetter {
        File getCacheDirectory();
    }

    class AnonymousClass1 implements CacheDirectoryGetter {
        final /* synthetic */ String val$diskCacheFolder;

        AnonymousClass1(String str) {
            this.val$diskCacheFolder = str;
        }

        public File getCacheDirectory() {
            return new File(this.val$diskCacheFolder);
        }
    }

    class AnonymousClass2 implements CacheDirectoryGetter {
        final /* synthetic */ String val$diskCacheFolder;
        final /* synthetic */ String val$diskCacheName;

        AnonymousClass2(String str, String str2) {
            this.val$diskCacheFolder = str;
            this.val$diskCacheName = str2;
        }

        public File getCacheDirectory() {
            return new File(this.val$diskCacheFolder, this.val$diskCacheName);
        }
    }

    public DiskLruCacheFactory(String diskCacheFolder, int diskCacheSize) {
        this(new AnonymousClass1(diskCacheFolder), diskCacheSize);
    }

    public DiskLruCacheFactory(String diskCacheFolder, String diskCacheName, int diskCacheSize) {
        this(new AnonymousClass2(diskCacheFolder, diskCacheName), diskCacheSize);
    }

    public DiskLruCacheFactory(CacheDirectoryGetter cacheDirectoryGetter, int diskCacheSize) {
        this.diskCacheSize = diskCacheSize;
        this.cacheDirectoryGetter = cacheDirectoryGetter;
    }

    public DiskCache build() {
        File cacheDir = this.cacheDirectoryGetter.getCacheDirectory();
        if (cacheDir == null) {
            return null;
        }
        if (cacheDir.mkdirs() || (cacheDir.exists() && cacheDir.isDirectory())) {
            return DiskLruCacheWrapper.get(cacheDir, this.diskCacheSize);
        }
        return null;
    }
}
