package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelections;
import com.google.android.exoplayer2.upstream.Allocator;

public interface LoadControl {
    Allocator getAllocator();

    void onPrepared();

    void onReleased();

    void onStopped();

    void onTracksSelected(Renderer[] rendererArr, TrackGroupArray trackGroupArray, TrackSelections<?> trackSelections);

    boolean shouldContinueLoading(long j);

    boolean shouldStartPlayback(long j, boolean z);
}
