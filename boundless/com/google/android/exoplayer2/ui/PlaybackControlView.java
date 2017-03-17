package com.google.android.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.v4.media.TransportMediator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.R;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Util;
import java.util.Formatter;
import java.util.Locale;

public class PlaybackControlView extends FrameLayout {
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private static final int PROGRESS_BAR_MAX = 1000;
    private final ComponentListener componentListener;
    private final Window currentWindow;
    private boolean dragging;
    private final View fastForwardButton;
    private int fastForwardMs;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Runnable hideAction;
    private long hideAtMs;
    private boolean isAttachedToWindow;
    private final View nextButton;
    private final ImageButton playButton;
    private ExoPlayer player;
    private final View previousButton;
    private final SeekBar progressBar;
    private final View rewindButton;
    private int rewindMs;
    private int showTimeoutMs;
    private final TextView time;
    private final TextView timeCurrent;
    private final Runnable updateProgressAction;
    private VisibilityListener visibilityListener;

    private final class ComponentListener implements EventListener, OnSeekBarChangeListener, OnClickListener {
        private ComponentListener() {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            PlaybackControlView.this.removeCallbacks(PlaybackControlView.this.hideAction);
            PlaybackControlView.this.dragging = true;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                PlaybackControlView.this.timeCurrent.setText(PlaybackControlView.this.stringForTime(PlaybackControlView.this.positionValue(progress)));
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            PlaybackControlView.this.dragging = false;
            PlaybackControlView.this.player.seekTo(PlaybackControlView.this.positionValue(seekBar.getProgress()));
            PlaybackControlView.this.hideAfterTimeout();
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            PlaybackControlView.this.updatePlayPauseButton();
            PlaybackControlView.this.updateProgress();
        }

        public void onPositionDiscontinuity() {
            PlaybackControlView.this.updateNavigation();
            PlaybackControlView.this.updateProgress();
        }

        public void onTimelineChanged(Timeline timeline, Object manifest) {
            PlaybackControlView.this.updateNavigation();
            PlaybackControlView.this.updateProgress();
        }

        public void onLoadingChanged(boolean isLoading) {
        }

        public void onPlayerError(ExoPlaybackException error) {
        }

        public void onClick(View view) {
            Timeline currentTimeline = PlaybackControlView.this.player.getCurrentTimeline();
            if (PlaybackControlView.this.nextButton == view) {
                PlaybackControlView.this.next();
            } else if (PlaybackControlView.this.previousButton == view) {
                PlaybackControlView.this.previous();
            } else if (PlaybackControlView.this.fastForwardButton == view) {
                PlaybackControlView.this.fastForward();
            } else if (PlaybackControlView.this.rewindButton == view && currentTimeline != null) {
                PlaybackControlView.this.rewind();
            } else if (PlaybackControlView.this.playButton == view) {
                PlaybackControlView.this.player.setPlayWhenReady(!PlaybackControlView.this.player.getPlayWhenReady());
            }
            PlaybackControlView.this.hideAfterTimeout();
        }
    }

    public interface VisibilityListener {
        void onVisibilityChange(int i);
    }

    public PlaybackControlView(Context context) {
        this(context, null);
    }

    public PlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.updateProgressAction = new Runnable() {
            public void run() {
                PlaybackControlView.this.updateProgress();
            }
        };
        this.hideAction = new Runnable() {
            public void run() {
                PlaybackControlView.this.hide();
            }
        };
        this.rewindMs = 5000;
        this.fastForwardMs = 15000;
        this.showTimeoutMs = 5000;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlaybackControlView, 0, 0);
            try {
                this.rewindMs = a.getInt(R.styleable.PlaybackControlView_rewind_increment, this.rewindMs);
                this.fastForwardMs = a.getInt(R.styleable.PlaybackControlView_fastforward_increment, this.fastForwardMs);
                this.showTimeoutMs = a.getInt(R.styleable.PlaybackControlView_show_timeout, this.showTimeoutMs);
            } finally {
                a.recycle();
            }
        }
        this.currentWindow = new Window();
        this.formatBuilder = new StringBuilder();
        this.formatter = new Formatter(this.formatBuilder, Locale.getDefault());
        this.componentListener = new ComponentListener();
        LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view, this);
        this.time = (TextView) findViewById(R.id.time);
        this.timeCurrent = (TextView) findViewById(R.id.time_current);
        this.progressBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
        this.progressBar.setOnSeekBarChangeListener(this.componentListener);
        this.progressBar.setMax(1000);
        this.playButton = (ImageButton) findViewById(R.id.play);
        this.playButton.setOnClickListener(this.componentListener);
        this.previousButton = findViewById(R.id.prev);
        this.previousButton.setOnClickListener(this.componentListener);
        this.nextButton = findViewById(R.id.next);
        this.nextButton.setOnClickListener(this.componentListener);
        this.rewindButton = findViewById(R.id.rew);
        this.rewindButton.setOnClickListener(this.componentListener);
        this.fastForwardButton = findViewById(R.id.ffwd);
        this.fastForwardButton.setOnClickListener(this.componentListener);
    }

    public ExoPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(ExoPlayer player) {
        if (this.player != player) {
            if (this.player != null) {
                this.player.removeListener(this.componentListener);
            }
            this.player = player;
            if (player != null) {
                player.addListener(this.componentListener);
            }
            updateAll();
        }
    }

    public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    public int getShowTimeoutMs() {
        return this.showTimeoutMs;
    }

    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
    }

    public void show() {
        if (!isVisible()) {
            setVisibility(0);
            if (this.visibilityListener != null) {
                this.visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
        }
        hideAfterTimeout();
    }

    public void hide() {
        if (isVisible()) {
            setVisibility(8);
            if (this.visibilityListener != null) {
                this.visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(this.updateProgressAction);
            removeCallbacks(this.hideAction);
            this.hideAtMs = C.TIME_UNSET;
        }
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    private void hideAfterTimeout() {
        removeCallbacks(this.hideAction);
        if (this.showTimeoutMs > 0) {
            this.hideAtMs = SystemClock.uptimeMillis() + ((long) this.showTimeoutMs);
            if (this.isAttachedToWindow) {
                postDelayed(this.hideAction, (long) this.showTimeoutMs);
                return;
            }
            return;
        }
        this.hideAtMs = C.TIME_UNSET;
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (isVisible() && this.isAttachedToWindow) {
            boolean playing = this.player != null && this.player.getPlayWhenReady();
            this.playButton.setContentDescription(getResources().getString(playing ? R.string.exo_controls_pause_description : R.string.exo_controls_play_description));
            this.playButton.setImageResource(playing ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play);
        }
    }

    private void updateNavigation() {
        boolean z = true;
        if (isVisible() && this.isAttachedToWindow) {
            boolean haveTimeline;
            boolean z2;
            Timeline currentTimeline = this.player != null ? this.player.getCurrentTimeline() : null;
            if (currentTimeline != null) {
                haveTimeline = true;
            } else {
                haveTimeline = false;
            }
            boolean isSeekable = false;
            boolean enablePrevious = false;
            boolean enableNext = false;
            if (haveTimeline) {
                int currentWindowIndex = this.player.getCurrentWindowIndex();
                currentTimeline.getWindow(currentWindowIndex, this.currentWindow);
                isSeekable = this.currentWindow.isSeekable;
                if (currentWindowIndex > 0 || isSeekable || !this.currentWindow.isDynamic) {
                    enablePrevious = true;
                } else {
                    enablePrevious = false;
                }
                if (currentWindowIndex < currentTimeline.getWindowCount() - 1 || this.currentWindow.isDynamic) {
                    enableNext = true;
                } else {
                    enableNext = false;
                }
            }
            setButtonEnabled(enablePrevious, this.previousButton);
            setButtonEnabled(enableNext, this.nextButton);
            if (this.fastForwardMs <= 0 || !isSeekable) {
                z2 = false;
            } else {
                z2 = true;
            }
            setButtonEnabled(z2, this.fastForwardButton);
            if (this.rewindMs <= 0 || !isSeekable) {
                z = false;
            }
            setButtonEnabled(z, this.rewindButton);
            this.progressBar.setEnabled(isSeekable);
        }
    }

    private void updateProgress() {
        if (isVisible() && this.isAttachedToWindow) {
            long duration = this.player == null ? 0 : this.player.getDuration();
            long position = this.player == null ? 0 : this.player.getCurrentPosition();
            this.time.setText(stringForTime(duration));
            if (!this.dragging) {
                this.timeCurrent.setText(stringForTime(position));
            }
            if (!this.dragging) {
                this.progressBar.setProgress(progressBarValue(position));
            }
            this.progressBar.setSecondaryProgress(progressBarValue(this.player == null ? 0 : this.player.getBufferedPosition()));
            removeCallbacks(this.updateProgressAction);
            int playbackState = this.player == null ? 1 : this.player.getPlaybackState();
            if (playbackState != 1 && playbackState != 4) {
                long delayMs;
                if (this.player.getPlayWhenReady() && playbackState == 3) {
                    delayMs = 1000 - (position % 1000);
                    if (delayMs < 200) {
                        delayMs += 1000;
                    }
                } else {
                    delayMs = 1000;
                }
                postDelayed(this.updateProgressAction, delayMs);
            }
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        view.setEnabled(enabled);
        if (Util.SDK_INT >= 11) {
            setViewAlphaV11(view, enabled ? 1.0f : 0.3f);
            view.setVisibility(0);
            return;
        }
        view.setVisibility(enabled ? 0 : 4);
    }

    @TargetApi(11)
    private void setViewAlphaV11(View view, float alpha) {
        view.setAlpha(alpha);
    }

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (500 + timeMs) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        this.formatBuilder.setLength(0);
        if (hours > 0) {
            return this.formatter.format("%d:%02d:%02d", new Object[]{Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)}).toString();
        }
        return this.formatter.format("%02d:%02d", new Object[]{Long.valueOf(minutes), Long.valueOf(seconds)}).toString();
    }

    private int progressBarValue(long position) {
        long duration = this.player == null ? C.TIME_UNSET : this.player.getDuration();
        return (duration == C.TIME_UNSET || duration == 0) ? 0 : (int) ((1000 * position) / duration);
    }

    private long positionValue(int progress) {
        long duration = this.player == null ? C.TIME_UNSET : this.player.getDuration();
        return duration == C.TIME_UNSET ? 0 : (((long) progress) * duration) / 1000;
    }

    private void previous() {
        Timeline currentTimeline = this.player.getCurrentTimeline();
        if (currentTimeline != null) {
            int currentWindowIndex = this.player.getCurrentWindowIndex();
            currentTimeline.getWindow(currentWindowIndex, this.currentWindow);
            if (currentWindowIndex <= 0 || (this.player.getCurrentPosition() > MAX_POSITION_FOR_SEEK_TO_PREVIOUS && (!this.currentWindow.isDynamic || this.currentWindow.isSeekable))) {
                this.player.seekTo(0);
            } else {
                this.player.seekToDefaultPosition(currentWindowIndex - 1);
            }
        }
    }

    private void next() {
        Timeline currentTimeline = this.player.getCurrentTimeline();
        if (currentTimeline != null) {
            int currentWindowIndex = this.player.getCurrentWindowIndex();
            if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
                this.player.seekToDefaultPosition(currentWindowIndex + 1);
            } else if (currentTimeline.getWindow(currentWindowIndex, this.currentWindow, false).isDynamic) {
                this.player.seekToDefaultPosition();
            }
        }
    }

    private void rewind() {
        if (this.rewindMs > 0) {
            this.player.seekTo(Math.max(this.player.getCurrentPosition() - ((long) this.rewindMs), 0));
        }
    }

    private void fastForward() {
        if (this.fastForwardMs > 0) {
            this.player.seekTo(Math.min(this.player.getCurrentPosition() + ((long) this.fastForwardMs), this.player.getDuration()));
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttachedToWindow = true;
        if (this.hideAtMs != C.TIME_UNSET) {
            long delayMs = this.hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(this.hideAction, delayMs);
            }
        }
        updateAll();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttachedToWindow = false;
        removeCallbacks(this.updateProgressAction);
        removeCallbacks(this.hideAction);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean z = false;
        if (this.player == null || event.getAction() != 0) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case 21:
            case 89:
                rewind();
                break;
            case 22:
            case 90:
                fastForward();
                break;
            case 85:
                ExoPlayer exoPlayer = this.player;
                if (!this.player.getPlayWhenReady()) {
                    z = true;
                }
                exoPlayer.setPlayWhenReady(z);
                break;
            case 87:
                next();
                break;
            case 88:
                previous();
                break;
            case 126:
                this.player.setPlayWhenReady(true);
                break;
            case TransportMediator.KEYCODE_MEDIA_PAUSE /*127*/:
                this.player.setPlayWhenReady(false);
                break;
            default:
                return false;
        }
        show();
        return true;
    }
}
