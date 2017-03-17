package fm.jiecao.jcvideoplayer_lib;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer.VideoListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection.Factory;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import java.util.Map;

public class JCMediaManager implements EventListener, VideoListener, SurfaceTextureListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public static String CURRENT_PLAYING_URL = null;
    public static final int HANDLER_PREPARE = 0;
    public static final int HANDLER_RELEASE = 2;
    private static JCMediaManager JCMediaManager;
    public static String TAG = JCVideoPlayer.TAG;
    public static String USER_AGENT = "android_jcvd";
    public static boolean isPreparing = false;
    public static SurfaceTexture savedSurfaceTexture;
    public static JCResizeTextureView textureView;
    public int currentVideoHeight = 0;
    public int currentVideoWidth = 0;
    public int lastState;
    MediaHandler mMediaHandler;
    HandlerThread mMediaHandlerThread = new HandlerThread(TAG);
    Handler mainThreadHandler;
    public SimpleExoPlayer simpleExoPlayer;
    MappingTrackSelector trackSelector;

    private class FuckBean {
        Context context;
        boolean looping;
        Map<String, String> mapHeadData;
        String url;

        FuckBean(Context context, String url, Map<String, String> mapHeadData, boolean loop) {
            this.context = context;
            this.url = url;
            this.mapHeadData = mapHeadData;
            this.looping = loop;
        }
    }

    public class MediaHandler extends Handler {
        public MediaHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        JCMediaManager.this.currentVideoWidth = 0;
                        JCMediaManager.this.currentVideoHeight = 0;
                        if (JCMediaManager.this.simpleExoPlayer != null) {
                            JCMediaManager.this.simpleExoPlayer.release();
                        }
                        JCMediaManager.isPreparing = true;
                        Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(JCMediaManager.BANDWIDTH_METER);
                        JCMediaManager.this.trackSelector = new DefaultTrackSelector(JCMediaManager.this.mMediaHandler, videoTrackSelectionFactory);
                        JCMediaManager.this.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(((FuckBean) msg.obj).context, JCMediaManager.this.trackSelector, new DefaultLoadControl(), null, false);
                        JCMediaManager.this.simpleExoPlayer.setPlayWhenReady(true);
                        MediaSource mediaSource = JCMediaManager.this.buildMediaSource(((FuckBean) msg.obj).context, Uri.parse(((FuckBean) msg.obj).url));
                        JCMediaManager.this.simpleExoPlayer.addListener(JCMediaManager.this);
                        JCMediaManager.this.simpleExoPlayer.setVideoListener(JCMediaManager.this);
                        JCMediaManager.this.simpleExoPlayer.prepare(mediaSource, true, true);
                        JCMediaManager.this.simpleExoPlayer.setVideoSurface(new Surface(JCMediaManager.savedSurfaceTexture));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                case 2:
                    if (JCMediaManager.this.simpleExoPlayer != null) {
                        JCMediaManager.this.simpleExoPlayer.release();
                    }
                    JCMediaManager.this.simpleExoPlayer = null;
                    return;
                default:
                    return;
            }
        }
    }

    public static JCMediaManager instance() {
        if (JCMediaManager == null) {
            JCMediaManager = new JCMediaManager();
        }
        return JCMediaManager;
    }

    public JCMediaManager() {
        this.mMediaHandlerThread.start();
        this.mMediaHandler = new MediaHandler(this.mMediaHandlerThread.getLooper());
        this.mainThreadHandler = new Handler();
    }

    public Point getVideoSize() {
        if (this.currentVideoWidth == 0 || this.currentVideoHeight == 0) {
            return null;
        }
        return new Point(this.currentVideoWidth, this.currentVideoHeight);
    }

    private MediaSource buildMediaSource(Context context, Uri uri) {
        int type = JCUtils.getUrlType(uri.toString());
        switch (type) {
            case 0:
                return new DashMediaSource(uri, new DefaultDataSourceFactory(context, null, new DefaultHttpDataSourceFactory(USER_AGENT, null)), new DefaultDashChunkSource.Factory(new DefaultDataSourceFactory(context, BANDWIDTH_METER, new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER))), this.mMediaHandler, null);
            case 1:
                return new SsMediaSource(uri, new DefaultDataSourceFactory(context, null, new DefaultHttpDataSourceFactory(USER_AGENT, null)), new DefaultSsChunkSource.Factory(new DefaultDataSourceFactory(context, BANDWIDTH_METER, new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER))), this.mMediaHandler, null);
            case 2:
                return new HlsMediaSource(uri, new DefaultDataSourceFactory(context, BANDWIDTH_METER, new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER)), this.mMediaHandler, null);
            case 3:
                return new ExtractorMediaSource(uri, new DefaultDataSourceFactory(context, BANDWIDTH_METER, new DefaultHttpDataSourceFactory(USER_AGENT, BANDWIDTH_METER)), new DefaultExtractorsFactory(), this.mMediaHandler, null);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    public void prepare(Context context, String url, Map<String, String> mapHeadData, boolean loop) {
        if (!TextUtils.isEmpty(url)) {
            releaseMediaPlayer();
            Message msg = new Message();
            msg.what = 0;
            msg.obj = new FuckBean(context, url, mapHeadData, loop);
            this.mMediaHandler.sendMessage(msg);
        }
    }

    public void releaseMediaPlayer() {
        Message msg = new Message();
        msg.what = 2;
        this.mMediaHandler.sendMessage(msg);
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (isPreparing && playbackState == 3) {
            isPreparing = false;
            this.mainThreadHandler.post(new Runnable() {
                public void run() {
                    if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                        JCVideoPlayerManager.getCurrentJcvd().onPrepared();
                    }
                }
            });
        } else if (playbackState == 4) {
            this.mainThreadHandler.post(new Runnable() {
                public void run() {
                    if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                        JCVideoPlayerManager.getCurrentJcvd().onAutoCompletion();
                    }
                }
            });
        }
    }

    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    public void onPlayerError(ExoPlaybackException error) {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onError(-10000, -10000);
                }
            }
        });
    }

    public void onPositionDiscontinuity() {
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.currentVideoWidth = width;
        this.currentVideoHeight = height;
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onVideoSizeChanged();
                }
            }
        });
    }

    public void onRenderedFirstFrame() {
    }

    public void onVideoTracksDisabled() {
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i(TAG, "onSurfaceTextureAvailable [" + hashCode() + "] ");
        if (savedSurfaceTexture == null) {
            savedSurfaceTexture = surfaceTexture;
            prepare(textureView.getContext(), CURRENT_PLAYING_URL, null, false);
            return;
        }
        textureView.setSurfaceTexture(savedSurfaceTexture);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + hashCode() + "] ");
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return savedSurfaceTexture == null;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
}
