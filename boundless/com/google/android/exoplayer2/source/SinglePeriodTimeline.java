package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline extends Timeline {
    private static final Object ID = new Object();
    private final boolean isDynamic;
    private final boolean isSeekable;
    private final long periodDurationUs;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowPositionInPeriodUs;

    public SinglePeriodTimeline(long durationUs, boolean isSeekable) {
        this(durationUs, durationUs, 0, 0, isSeekable, false);
    }

    public SinglePeriodTimeline(long periodDurationUs, long windowDurationUs, long windowPositionInPeriodUs, long windowDefaultStartPositionUs, boolean isSeekable, boolean isDynamic) {
        this.periodDurationUs = periodDurationUs;
        this.windowDurationUs = windowDurationUs;
        this.windowPositionInPeriodUs = windowPositionInPeriodUs;
        this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
        this.isSeekable = isSeekable;
        this.isDynamic = isDynamic;
    }

    public int getWindowCount() {
        return 1;
    }

    public Window getWindow(int windowIndex, Window window, boolean setIds) {
        Assertions.checkIndex(windowIndex, 0, 1);
        return window.set(setIds ? ID : null, C.TIME_UNSET, C.TIME_UNSET, this.isSeekable, this.isDynamic, this.windowDefaultStartPositionUs, this.windowDurationUs, 0, 0, this.windowPositionInPeriodUs);
    }

    public int getPeriodCount() {
        return 1;
    }

    public Period getPeriod(int periodIndex, Period period, boolean setIds) {
        Assertions.checkIndex(periodIndex, 0, 1);
        Object id = setIds ? ID : null;
        return period.set(id, id, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }

    public int getIndexOfPeriod(Object uid) {
        return ID.equals(uid) ? 0 : -1;
    }
}
