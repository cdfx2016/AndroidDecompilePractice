package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSink.Factory;

public final class CacheDataSinkFactory implements Factory {
    private final Cache cache;
    private final long maxCacheFileSize;

    public CacheDataSinkFactory(Cache cache, long maxCacheFileSize) {
        this.cache = cache;
        this.maxCacheFileSize = maxCacheFileSize;
    }

    public DataSink createDataSink() {
        return new CacheDataSink(this.cache, this.maxCacheFileSize);
    }
}
