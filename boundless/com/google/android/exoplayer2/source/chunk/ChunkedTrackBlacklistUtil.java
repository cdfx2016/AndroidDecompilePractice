package com.google.android.exoplayer2.source.chunk;

import android.util.Log;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;

public final class ChunkedTrackBlacklistUtil {
    public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000;
    private static final String TAG = "ChunkedTrackBlacklist";

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int trackSelectionIndex, Exception e) {
        return maybeBlacklistTrack(trackSelection, trackSelectionIndex, e, 60000);
    }

    public static boolean maybeBlacklistTrack(TrackSelection trackSelection, int trackSelectionIndex, Exception e, long blacklistDurationMs) {
        boolean z = false;
        if (trackSelection.length() != 1 && (e instanceof InvalidResponseCodeException)) {
            int responseCode = ((InvalidResponseCodeException) e).responseCode;
            if (responseCode == 404 || responseCode == 410) {
                z = trackSelection.blacklist(trackSelectionIndex, blacklistDurationMs);
                if (z) {
                    Log.w(TAG, "Blacklisted: duration=" + blacklistDurationMs + ", responseCode=" + responseCode + ", format=" + trackSelection.getFormat(trackSelectionIndex));
                } else {
                    Log.w(TAG, "Blacklisting failed (cannot blacklist last enabled track): responseCode=" + responseCode + ", format=" + trackSelection.getFormat(trackSelectionIndex));
                }
            }
        }
        return z;
    }

    private ChunkedTrackBlacklistUtil() {
    }
}
