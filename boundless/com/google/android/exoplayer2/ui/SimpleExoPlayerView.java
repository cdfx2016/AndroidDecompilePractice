package com.google.android.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer.VideoListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer.Output;
import com.google.android.exoplayer2.ui.PlaybackControlView.VisibilityListener;
import java.util.List;

@TargetApi(16)
public final class SimpleExoPlayerView extends FrameLayout {
    private final ComponentListener componentListener;
    private final PlaybackControlView controller;
    private int controllerShowTimeoutMs;
    private final AspectRatioFrameLayout layout;
    private SimpleExoPlayer player;
    private final View shutterView;
    private final SubtitleView subtitleLayout;
    private final View surfaceView;
    private boolean useController;

    private final class ComponentListener implements VideoListener, Output, EventListener {
        private ComponentListener() {
        }

        public void onCues(List<Cue> cues) {
            SimpleExoPlayerView.this.subtitleLayout.onCues(cues);
        }

        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            SimpleExoPlayerView.this.layout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height));
        }

        public void onRenderedFirstFrame() {
            SimpleExoPlayerView.this.shutterView.setVisibility(8);
        }

        public void onVideoTracksDisabled() {
            SimpleExoPlayerView.this.shutterView.setVisibility(0);
        }

        public void onLoadingChanged(boolean isLoading) {
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            SimpleExoPlayerView.this.maybeShowController(false);
        }

        public void onPlayerError(ExoPlaybackException e) {
        }

        public void onPositionDiscontinuity() {
        }

        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }
    }

    public SimpleExoPlayerView(Context context) {
        this(context, null);
    }

    public SimpleExoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.useController = true;
        boolean useTextureView = false;
        int resizeMode = 0;
        int rewindMs = 5000;
        int fastForwardMs = 15000;
        int controllerShowTimeoutMs = 5000;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SimpleExoPlayerView, 0, 0);
            try {
                this.useController = a.getBoolean(R.styleable.SimpleExoPlayerView_use_controller, this.useController);
                useTextureView = a.getBoolean(R.styleable.SimpleExoPlayerView_use_texture_view, false);
                resizeMode = a.getInt(R.styleable.SimpleExoPlayerView_resize_mode, 0);
                rewindMs = a.getInt(R.styleable.SimpleExoPlayerView_rewind_increment, 5000);
                fastForwardMs = a.getInt(R.styleable.SimpleExoPlayerView_fastforward_increment, 15000);
                controllerShowTimeoutMs = a.getInt(R.styleable.SimpleExoPlayerView_show_timeout, controllerShowTimeoutMs);
            } finally {
                a.recycle();
            }
        }
        LayoutInflater.from(context).inflate(R.layout.exo_simple_player_view, this);
        this.componentListener = new ComponentListener();
        this.layout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        this.layout.setResizeMode(resizeMode);
        this.shutterView = findViewById(R.id.shutter);
        this.subtitleLayout = (SubtitleView) findViewById(R.id.subtitles);
        this.subtitleLayout.setUserDefaultStyle();
        this.subtitleLayout.setUserDefaultTextSize();
        this.controller = (PlaybackControlView) findViewById(R.id.control);
        this.controller.hide();
        this.controller.setRewindIncrementMs(rewindMs);
        this.controller.setFastForwardIncrementMs(fastForwardMs);
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
        View view = useTextureView ? new TextureView(context) : new SurfaceView(context);
        view.setLayoutParams(new LayoutParams(-1, -1));
        this.surfaceView = view;
        this.layout.addView(this.surfaceView, 0);
    }

    public SimpleExoPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player != player) {
            if (this.player != null) {
                this.player.setTextOutput(null);
                this.player.setVideoListener(null);
                this.player.removeListener(this.componentListener);
                this.player.setVideoSurface(null);
            }
            this.player = player;
            if (this.useController) {
                this.controller.setPlayer(player);
            }
            if (player != null) {
                if (this.surfaceView instanceof TextureView) {
                    player.setVideoTextureView((TextureView) this.surfaceView);
                } else if (this.surfaceView instanceof SurfaceView) {
                    player.setVideoSurfaceView((SurfaceView) this.surfaceView);
                }
                player.setVideoListener(this.componentListener);
                player.addListener(this.componentListener);
                player.setTextOutput(this.componentListener);
                maybeShowController(false);
                return;
            }
            this.shutterView.setVisibility(0);
            this.controller.hide();
        }
    }

    public void setResizeMode(int resizeMode) {
        this.layout.setResizeMode(resizeMode);
    }

    public boolean getUseController() {
        return this.useController;
    }

    public void setUseController(boolean useController) {
        if (this.useController != useController) {
            this.useController = useController;
            if (useController) {
                this.controller.setPlayer(this.player);
                return;
            }
            this.controller.hide();
            this.controller.setPlayer(null);
        }
    }

    public int getControllerShowTimeoutMs() {
        return this.controllerShowTimeoutMs;
    }

    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
    }

    public void setControllerVisibilityListener(VisibilityListener listener) {
        this.controller.setVisibilityListener(listener);
    }

    public void setRewindIncrementMs(int rewindMs) {
        this.controller.setRewindIncrementMs(rewindMs);
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.controller.setFastForwardIncrementMs(fastForwardMs);
    }

    public View getVideoSurfaceView() {
        return this.surfaceView;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.useController || this.player == null || ev.getActionMasked() != 0) {
            return false;
        }
        if (this.controller.isVisible()) {
            this.controller.hide();
            return true;
        }
        maybeShowController(true);
        return true;
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        if (!this.useController || this.player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.useController ? this.controller.dispatchKeyEvent(event) : super.dispatchKeyEvent(event);
    }

    private void maybeShowController(boolean isForced) {
        int i = 0;
        if (this.useController && this.player != null) {
            boolean showIndefinitely;
            int playbackState = this.player.getPlaybackState();
            if (playbackState == 1 || playbackState == 4 || !this.player.getPlayWhenReady()) {
                showIndefinitely = true;
            } else {
                showIndefinitely = false;
            }
            boolean wasShowingIndefinitely;
            if (!this.controller.isVisible() || this.controller.getShowTimeoutMs() > 0) {
                wasShowingIndefinitely = false;
            } else {
                wasShowingIndefinitely = true;
            }
            PlaybackControlView playbackControlView = this.controller;
            if (!showIndefinitely) {
                i = this.controllerShowTimeoutMs;
            }
            playbackControlView.setShowTimeoutMs(i);
            if (isForced || showIndefinitely || wasShowingIndefinitely) {
                this.controller.show();
            }
        }
    }
}
