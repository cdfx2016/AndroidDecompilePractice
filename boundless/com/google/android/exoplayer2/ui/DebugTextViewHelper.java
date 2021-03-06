package com.google.android.exoplayer2.ui;

import android.support.v4.os.EnvironmentCompat;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;

public final class DebugTextViewHelper implements Runnable, EventListener {
    private static final int REFRESH_INTERVAL_MS = 1000;
    private final SimpleExoPlayer player;
    private boolean started;
    private final TextView textView;

    public DebugTextViewHelper(SimpleExoPlayer player, TextView textView) {
        this.player = player;
        this.textView = textView;
    }

    public void start() {
        if (!this.started) {
            this.started = true;
            this.player.addListener(this);
            updateAndPost();
        }
    }

    public void stop() {
        if (this.started) {
            this.started = false;
            this.player.removeListener(this);
            this.textView.removeCallbacks(this);
        }
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        updateAndPost();
    }

    public void onPositionDiscontinuity() {
        updateAndPost();
    }

    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    public void onPlayerError(ExoPlaybackException error) {
    }

    public void run() {
        updateAndPost();
    }

    private void updateAndPost() {
        this.textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + getVideoString() + getAudioString());
        this.textView.removeCallbacks(this);
        this.textView.postDelayed(this, 1000);
    }

    private String getPlayerStateString() {
        String text = "playWhenReady:" + this.player.getPlayWhenReady() + " playbackState:";
        switch (this.player.getPlaybackState()) {
            case 1:
                return text + "idle";
            case 2:
                return text + "buffering";
            case 3:
                return text + "ready";
            case 4:
                return text + "ended";
            default:
                return text + EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    private String getPlayerWindowIndexString() {
        return " window:" + this.player.getCurrentWindowIndex();
    }

    private String getVideoString() {
        Format format = this.player.getVideoFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType + "(id:" + format.id + " r:" + format.width + "x" + format.height + getDecoderCountersBufferCountString(this.player.getVideoDecoderCounters()) + ")";
    }

    private String getAudioString() {
        Format format = this.player.getAudioFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType + "(id:" + format.id + " hz:" + format.sampleRate + " ch:" + format.channelCount + getDecoderCountersBufferCountString(this.player.getAudioDecoderCounters()) + ")";
    }

    private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return " rb:" + counters.renderedOutputBufferCount + " sb:" + counters.skippedOutputBufferCount + " db:" + counters.droppedOutputBufferCount + " mcdb:" + counters.maxConsecutiveDroppedOutputBufferCount;
    }
}
