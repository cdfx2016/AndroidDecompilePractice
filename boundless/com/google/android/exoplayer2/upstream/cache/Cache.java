package com.google.android.exoplayer2.upstream.cache;

import java.io.File;
import java.util.NavigableSet;
import java.util.Set;

public interface Cache {

    public interface Listener {
        void onSpanAdded(Cache cache, CacheSpan cacheSpan);

        void onSpanRemoved(Cache cache, CacheSpan cacheSpan);

        void onSpanTouched(Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan2);
    }

    NavigableSet<CacheSpan> addListener(String str, Listener listener);

    void commitFile(File file);

    long getCacheSpace();

    NavigableSet<CacheSpan> getCachedSpans(String str);

    long getContentLength(String str);

    Set<String> getKeys();

    boolean isCached(String str, long j, long j2);

    void releaseHoleSpan(CacheSpan cacheSpan);

    void removeListener(String str, Listener listener);

    void removeSpan(CacheSpan cacheSpan);

    boolean setContentLength(String str, long j);

    File startFile(String str, long j, long j2);

    CacheSpan startReadWrite(String str, long j) throws InterruptedException;

    CacheSpan startReadWriteNonBlocking(String str, long j);
}
