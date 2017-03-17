package fm.jiecao.jcvideoplayer_lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.MimeTypes;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public abstract class JCVideoPlayer extends FrameLayout implements JCMediaPlayerListener, OnClickListener, OnSeekBarChangeListener, OnTouchListener {
    public static boolean ACTION_BAR_EXIST = true;
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    public static final int CURRENT_STATE_ERROR = 7;
    public static final int CURRENT_STATE_NORMAL = 0;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_PLAYING = 2;
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    public static final int CURRENT_STATE_PREPARING = 1;
    @IdRes
    public static final int FULLSCREEN_ID = 33797;
    public static int FULLSCREEN_ORIENTATION = 4;
    public static final int FULL_SCREEN_NORMAL_DELAY = 200;
    protected static JCUserAction JC_USER_EVENT = null;
    public static int NORMAL_ORIENTATION = 1;
    public static final int SCREEN_LAYOUT_LIST = 1;
    public static final int SCREEN_LAYOUT_NORMAL = 0;
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    public static final int SCREEN_WINDOW_TINY = 3;
    public static final String TAG = "JieCaoVideoPlayer";
    public static final int THRESHOLD = 80;
    @IdRes
    public static final int TINY_ID = 33798;
    public static boolean TOOL_BAR_EXIST = true;
    protected static Timer UPDATE_PROGRESS_TIMER;
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;
    public static long lastAutoFullscreenTime = 0;
    public static OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case -2:
                    if (JCMediaManager.instance().simpleExoPlayer != null && JCMediaManager.instance().simpleExoPlayer.getPlaybackState() == 3) {
                        JCMediaManager.instance().simpleExoPlayer.setPlayWhenReady(false);
                    }
                    Log.d(JCVideoPlayer.TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + hashCode() + "]");
                    return;
                case -1:
                    JCVideoPlayer.releaseAllVideos();
                    Log.d(JCVideoPlayer.TAG, "AUDIOFOCUS_LOSS [" + hashCode() + "]");
                    return;
                default:
                    return;
            }
        }
    };
    public ViewGroup bottomContainer;
    public int currentScreen = -1;
    public int currentState = -1;
    public TextView currentTimeTextView;
    public ImageView fullscreenButton;
    protected AudioManager mAudioManager;
    protected boolean mChangePosition;
    protected boolean mChangeVolume;
    protected int mDownPosition;
    protected float mDownX;
    protected float mDownY;
    protected int mGestureDownVolume;
    protected Handler mHandler;
    protected ProgressTimerTask mProgressTimerTask;
    protected int mScreenHeight;
    protected int mScreenWidth;
    protected int mSeekTimePosition;
    protected boolean mTouchingProgressBar;
    public Object[] objects = null;
    public SeekBar progressBar;
    public int seekToInAdvance = -1;
    public ImageView startButton;
    public ViewGroup textureViewContainer;
    public ViewGroup topContainer;
    public TextView totalTimeTextView;
    public String url = "";

    public static class JCAutoFullscreenListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (((x > -15.0f && x < -10.0f) || (x < 15.0f && x > 10.0f)) && ((double) Math.abs(y)) < 1.5d && System.currentTimeMillis() - JCVideoPlayer.lastAutoFullscreenTime > 2000) {
                if (JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null) {
                    JCVideoPlayerManager.getCurrentJcvdOnFirtFloor().autoFullscreen(x);
                }
                JCVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public class ProgressTimerTask extends TimerTask {
        public void run() {
            if (JCVideoPlayer.this.currentState == 2 || JCVideoPlayer.this.currentState == 5 || JCVideoPlayer.this.currentState == 3) {
                int position = JCVideoPlayer.this.getCurrentPositionWhenPlaying();
                int duration = JCVideoPlayer.this.getDuration();
                JCVideoPlayer.this.mHandler.post(new Runnable() {
                    public void run() {
                        JCVideoPlayer.this.setTextAndProgress();
                    }
                });
            }
        }
    }

    public abstract int getLayoutId();

    public JCVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JCVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        this.startButton = (ImageView) findViewById(R.id.start);
        this.fullscreenButton = (ImageView) findViewById(R.id.fullscreen);
        this.progressBar = (SeekBar) findViewById(R.id.progress);
        this.currentTimeTextView = (TextView) findViewById(R.id.current);
        this.totalTimeTextView = (TextView) findViewById(R.id.total);
        this.bottomContainer = (ViewGroup) findViewById(R.id.layout_bottom);
        this.textureViewContainer = (ViewGroup) findViewById(R.id.surface_container);
        this.topContainer = (ViewGroup) findViewById(R.id.layout_top);
        this.startButton.setOnClickListener(this);
        this.fullscreenButton.setOnClickListener(this);
        this.progressBar.setOnSeekBarChangeListener(this);
        this.bottomContainer.setOnClickListener(this);
        this.textureViewContainer.setOnClickListener(this);
        this.textureViewContainer.setOnTouchListener(this);
        this.mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        this.mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        this.mAudioManager = (AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.mHandler = new Handler();
    }

    public void setUp(String url, int screen, Object... objects) {
        if (TextUtils.isEmpty(this.url) || !TextUtils.equals(this.url, url)) {
            this.url = url;
            this.objects = objects;
            this.currentScreen = screen;
            JCVideoPlayerManager.putFirstFloor(this);
            setUiWitStateAndScreen(0);
        }
    }

    public int getScreenType() {
        return this.currentScreen;
    }

    public String getUrl() {
        return this.url;
    }

    public int getState() {
        return this.currentState;
    }

    public void onClick(View v) {
        int i = 0;
        int i2 = v.getId();
        if (i2 == R.id.start) {
            Log.i(TAG, "onClick start [" + hashCode() + "] ");
            if (TextUtils.isEmpty(this.url)) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), 0).show();
            } else if (this.currentState == 0 || this.currentState == 7) {
                if (this.url.startsWith("file") || JCUtils.isWifiConnected(getContext()) || WIFI_TIP_DIALOG_SHOWED) {
                    prepareVideo();
                    if (this.currentState == 7) {
                        i = 1;
                    }
                    onEvent(i);
                    return;
                }
                showWifiDialog();
            } else if (this.currentState == 2) {
                onEvent(3);
                Log.d(TAG, "pauseVideo [" + hashCode() + "] ");
                JCMediaManager.instance().simpleExoPlayer.setPlayWhenReady(false);
                setUiWitStateAndScreen(5);
            } else if (this.currentState == 5) {
                onEvent(4);
                JCMediaManager.instance().simpleExoPlayer.setPlayWhenReady(true);
                setUiWitStateAndScreen(2);
            } else if (this.currentState == 6) {
                onEvent(2);
                prepareVideo();
            }
        } else if (i2 == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + hashCode() + "] ");
            if (this.currentState == 6) {
                return;
            }
            if (this.currentScreen == 2) {
                backPress();
                return;
            }
            Log.d(TAG, "toFullscreenActivity [" + hashCode() + "] ");
            onEvent(7);
            startWindowFullscreen();
        } else if (i2 == R.id.surface_container && this.currentState == 7) {
            Log.i(TAG, "onClick surfaceContainer State=Error [" + hashCode() + "] ");
            prepareVideo();
        }
    }

    public void prepareVideo() {
        JCVideoPlayerManager.completeAll();
        Log.d(TAG, "prepareVideo [" + hashCode() + "] ");
        initTextureView();
        addTextureView();
        ((AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO)).requestAudioFocus(onAudioFocusChangeListener, 3, 2);
        JCUtils.scanForActivity(getContext()).getWindow().addFlags(128);
        JCMediaManager.CURRENT_PLAYING_URL = this.url;
        setUiWitStateAndScreen(1);
    }

    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (v.getId() == R.id.surface_container) {
            switch (event.getAction()) {
                case 0:
                    Log.i(TAG, "onTouch surfaceContainer actionDown [" + hashCode() + "] ");
                    this.mTouchingProgressBar = true;
                    this.mDownX = x;
                    this.mDownY = y;
                    this.mChangeVolume = false;
                    this.mChangePosition = false;
                    break;
                case 1:
                    Log.i(TAG, "onTouch surfaceContainer actionUp [" + hashCode() + "] ");
                    this.mTouchingProgressBar = false;
                    dismissProgressDialog();
                    dismissVolumeDialog();
                    if (this.mChangePosition) {
                        onEvent(12);
                        JCMediaManager.instance().simpleExoPlayer.seekTo((long) this.mSeekTimePosition);
                        int duration = getDuration();
                        int i = this.mSeekTimePosition * 100;
                        if (duration == 0) {
                            duration = 1;
                        }
                        this.progressBar.setProgress(i / duration);
                    }
                    if (this.mChangeVolume) {
                        onEvent(11);
                    }
                    startProgressTimer();
                    break;
                case 2:
                    Log.i(TAG, "onTouch surfaceContainer actionMove [" + hashCode() + "] ");
                    float deltaX = x - this.mDownX;
                    float deltaY = y - this.mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (this.currentScreen == 2 && !this.mChangePosition && !this.mChangeVolume && (absDeltaX > 80.0f || absDeltaY > 80.0f)) {
                        cancelProgressTimer();
                        if (absDeltaX < 80.0f) {
                            this.mChangeVolume = true;
                            this.mGestureDownVolume = this.mAudioManager.getStreamVolume(3);
                        } else if (this.currentState != 7) {
                            this.mChangePosition = true;
                            this.mDownPosition = getCurrentPositionWhenPlaying();
                        }
                    }
                    if (this.mChangePosition) {
                        int totalTimeDuration = getDuration();
                        this.mSeekTimePosition = (int) (((float) this.mDownPosition) + ((((float) totalTimeDuration) * deltaX) / ((float) this.mScreenWidth)));
                        if (this.mSeekTimePosition > totalTimeDuration) {
                            this.mSeekTimePosition = totalTimeDuration;
                        }
                        showProgressDialog(deltaX, JCUtils.stringForTime(this.mSeekTimePosition), this.mSeekTimePosition, JCUtils.stringForTime(totalTimeDuration), totalTimeDuration);
                    }
                    if (this.mChangeVolume) {
                        deltaY = -deltaY;
                        int max = this.mAudioManager.getStreamMaxVolume(3);
                        this.mAudioManager.setStreamVolume(3, this.mGestureDownVolume + ((int) (((((float) max) * deltaY) * 3.0f) / ((float) this.mScreenHeight))), 0);
                        showVolumeDialog(-deltaY, (int) (((float) ((this.mGestureDownVolume * 100) / max)) + (((3.0f * deltaY) * 100.0f) / ((float) this.mScreenHeight))));
                        break;
                    }
                    break;
            }
        }
        return false;
    }

    public void initTextureView() {
        removeTextureView();
        JCMediaManager.textureView = new JCResizeTextureView(getContext());
        JCMediaManager.textureView.setSurfaceTextureListener(JCMediaManager.instance());
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + hashCode() + "] ");
        this.textureViewContainer.addView(JCMediaManager.textureView, new LayoutParams(-1, -1, 17));
    }

    public void removeTextureView() {
        JCMediaManager.savedSurfaceTexture = null;
        if (JCMediaManager.textureView != null && JCMediaManager.textureView.getParent() != null) {
            ((ViewGroup) JCMediaManager.textureView.getParent()).removeView(JCMediaManager.textureView);
        }
    }

    public void setUiWitStateAndScreen(int state) {
        this.currentState = state;
        switch (this.currentState) {
            case 0:
                if (isCurrentMediaListenerOnFirstFloor()) {
                    cancelProgressTimer();
                    JCMediaManager.instance().releaseMediaPlayer();
                    return;
                }
                return;
            case 1:
                resetProgressAndTime();
                return;
            case 2:
            case 3:
            case 5:
                startProgressTimer();
                return;
            case 6:
                cancelProgressTimer();
                this.progressBar.setProgress(100);
                this.currentTimeTextView.setText(this.totalTimeTextView.getText());
                return;
            case 7:
                cancelProgressTimer();
                if (isCurrentMediaListenerOnFirstFloor()) {
                    JCMediaManager.instance().releaseMediaPlayer();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        this.mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(this.mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (this.mProgressTimerTask != null) {
            this.mProgressTimerTask.cancel();
        }
    }

    public void onPrepared() {
        Log.i(TAG, "onPrepared  [" + hashCode() + "] ");
        if (this.currentState == 1) {
            if (this.seekToInAdvance != -1) {
                JCMediaManager.instance().simpleExoPlayer.seekTo((long) this.seekToInAdvance);
                this.seekToInAdvance = -1;
            }
            startProgressTimer();
            setUiWitStateAndScreen(2);
        }
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (ViewGroup) JCUtils.scanForActivity(getContext()).findViewById(16908290);
        View oldF = vp.findViewById(FULLSCREEN_ID);
        View oldT = vp.findViewById(TINY_ID);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        showSupportActionBar(getContext());
    }

    public void onAutoCompletion() {
        Log.i(TAG, "onAutoCompletion  [" + hashCode() + "] ");
        onEvent(6);
        dismissVolumeDialog();
        dismissProgressDialog();
        setUiWitStateAndScreen(6);
        if (this.currentScreen == 2) {
            backPress();
        }
    }

    public void onCompletion() {
        Log.i(TAG, "onCompletion  [" + hashCode() + "] ");
        setUiWitStateAndScreen(0);
        this.textureViewContainer.removeView(JCMediaManager.textureView);
        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;
        ((AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO)).abandonAudioFocus(onAudioFocusChangeListener);
        JCUtils.scanForActivity(getContext()).getWindow().clearFlags(128);
        clearFullscreenLayout();
        JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(NORMAL_ORIENTATION);
    }

    public boolean downStairs() {
        Log.i(TAG, "downStairs  [" + hashCode() + "] ");
        JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(NORMAL_ORIENTATION);
        showSupportActionBar(getContext());
        if (this.currentScreen != 2 && this.currentScreen != 3) {
            return false;
        }
        this.textureViewContainer.removeView(JCMediaManager.textureView);
        ((ViewGroup) JCUtils.scanForActivity(getContext()).findViewById(16908290)).removeView(this);
        onEvent(this.currentScreen == 2 ? 8 : 10);
        if (JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() == this) {
            JCVideoPlayerManager.completeAll();
            JCMediaManager.instance().releaseMediaPlayer();
            JCMediaManager.CURRENT_PLAYING_URL = null;
            return true;
        }
        JCVideoPlayerManager.putSecondFloor(null);
        JCMediaManager.instance().lastState = this.currentState;
        if (JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null) {
            JCVideoPlayerManager.getCurrentJcvdOnFirtFloor().goBackOnThisFloor();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } else {
            JCVideoPlayerManager.completeAll();
        }
        return true;
    }

    public void autoFullscreen(float x) {
        if (isCurrentMediaListenerOnFirstFloor() && this.currentState == 2 && this.currentScreen != 2 && this.currentScreen != 3) {
            if (JCVideoPlayerManager.getCurrentJcvdOnSecondFloor() == null || JCVideoPlayerManager.getCurrentJcvdOnSecondFloor().getScreenType() != 2) {
                if (x > 0.0f) {
                    JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(0);
                } else {
                    JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(8);
                }
                startWindowFullscreen();
            }
        }
    }

    public void autoQuitFullscreen() {
        if (System.currentTimeMillis() - lastAutoFullscreenTime > 2000 && isCurrentMediaListenerOnFirstFloor() && this.currentState == 2 && this.currentScreen == 2) {
            lastAutoFullscreenTime = System.currentTimeMillis();
            backPress();
        }
    }

    public void goBackOnThisFloor() {
        Log.i(TAG, "goBackOnThisFloor  [" + hashCode() + "] ");
        this.currentState = JCMediaManager.instance().lastState;
        setUiWitStateAndScreen(this.currentState);
        addTextureView();
    }

    public void onBufferingUpdate(int percent) {
    }

    public void onSeekComplete() {
    }

    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + hashCode() + "] ");
        if (what != 38 && what != -38) {
            setUiWitStateAndScreen(7);
        }
    }

    public void onInfo(int what, int extra) {
    }

    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged  [" + hashCode() + "] ");
        JCMediaManager.textureView.setVideoSize(JCMediaManager.instance().getVideoSize());
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + hashCode() + "] ");
        cancelProgressTimer();
        for (ViewParent vpdown = getParent(); vpdown != null; vpdown = vpdown.getParent()) {
            vpdown.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + hashCode() + "] ");
        onEvent(5);
        startProgressTimer();
        for (ViewParent vpup = getParent(); vpup != null; vpup = vpup.getParent()) {
            vpup.requestDisallowInterceptTouchEvent(false);
        }
        if (this.currentState == 2 || this.currentState == 5) {
            int time = (seekBar.getProgress() * getDuration()) / 100;
            JCMediaManager.instance().simpleExoPlayer.seekTo((long) time);
            Log.i(TAG, "seekTo " + time + " [" + hashCode() + "] ");
        }
    }

    public static boolean backPress() {
        Log.i(TAG, "backPress");
        if (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME >= 200 && JCVideoPlayerManager.getCurrentJcvd() != null) {
            return JCVideoPlayerManager.getCurrentJcvd().downStairs();
        }
        return false;
    }

    public void startWindowFullscreen() {
        Log.i(TAG, "startWindowFullscreen  [" + hashCode() + "] ");
        hideSupportActionBar(getContext());
        JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(FULLSCREEN_ORIENTATION);
        ViewGroup vp = (ViewGroup) JCUtils.scanForActivity(getContext()).findViewById(16908290);
        View old = vp.findViewById(FULLSCREEN_ID);
        if (old != null) {
            vp.removeView(old);
        }
        this.textureViewContainer.removeView(JCMediaManager.textureView);
        try {
            JCVideoPlayer jcVideoPlayer = (JCVideoPlayer) getClass().getConstructor(new Class[]{Context.class}).newInstance(new Object[]{getContext()});
            jcVideoPlayer.setId(FULLSCREEN_ID);
            vp.addView(jcVideoPlayer, new LayoutParams(-1, -1));
            jcVideoPlayer.setUp(this.url, 2, this.objects);
            jcVideoPlayer.setUiWitStateAndScreen(this.currentState);
            jcVideoPlayer.addTextureView();
            JCVideoPlayerManager.putSecondFloor(jcVideoPlayer);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startWindowTiny() {
        Log.i(TAG, "startWindowTiny  [" + hashCode() + "] ");
        onEvent(9);
        if (this.currentState != 0 && this.currentState != 7) {
            ViewGroup vp = (ViewGroup) JCUtils.scanForActivity(getContext()).findViewById(16908290);
            View old = vp.findViewById(TINY_ID);
            if (old != null) {
                vp.removeView(old);
            }
            this.textureViewContainer.removeView(JCMediaManager.textureView);
            try {
                JCVideoPlayer jcVideoPlayer = (JCVideoPlayer) getClass().getConstructor(new Class[]{Context.class}).newInstance(new Object[]{getContext()});
                jcVideoPlayer.setId(TINY_ID);
                LayoutParams lp = new LayoutParams(400, 400);
                lp.gravity = 85;
                vp.addView(jcVideoPlayer, lp);
                jcVideoPlayer.setUp(this.url, 3, this.objects);
                jcVideoPlayer.setUiWitStateAndScreen(this.currentState);
                jcVideoPlayer.addTextureView();
                JCVideoPlayerManager.putSecondFloor(jcVideoPlayer);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (this.currentState == 2 || this.currentState == 5 || this.currentState == 3) {
            try {
                position = (int) JCMediaManager.instance().simpleExoPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return position;
    }

    public int getDuration() {
        try {
            return (int) JCMediaManager.instance().simpleExoPlayer.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setTextAndProgress() {
        int i;
        int position = getCurrentPositionWhenPlaying();
        int duration = getDuration();
        int i2 = position * 100;
        if (duration == 0) {
            i = 1;
        } else {
            i = duration;
        }
        setProgressAndTime(i2 / i, progressBarValue(JCMediaManager.instance().simpleExoPlayer.getBufferedPosition()), position, duration);
    }

    public void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {
        if (!(this.mTouchingProgressBar || progress == 0)) {
            this.progressBar.setProgress(progress);
        }
        if (secProgress > 95) {
            secProgress = 100;
        }
        if (secProgress != 0) {
            this.progressBar.setSecondaryProgress(secProgress);
        }
        if (currentTime != 0) {
            this.currentTimeTextView.setText(JCUtils.stringForTime(currentTime));
        }
        this.totalTimeTextView.setText(JCUtils.stringForTime(totalTime));
    }

    public void resetProgressAndTime() {
        this.progressBar.setProgress(0);
        this.progressBar.setSecondaryProgress(0);
        this.currentTimeTextView.setText(JCUtils.stringForTime(0));
        this.totalTimeTextView.setText(JCUtils.stringForTime(0));
    }

    private int progressBarValue(long position) {
        long duration = JCMediaManager.instance().simpleExoPlayer == null ? C.TIME_UNSET : JCMediaManager.instance().simpleExoPlayer.getDuration();
        return (duration == C.TIME_UNSET || duration == 0) ? 0 : (int) ((100 * position) / duration);
    }

    public void release() {
        if (this.url.equals(JCMediaManager.CURRENT_PLAYING_URL) && System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME > 200 && JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null && JCVideoPlayerManager.getCurrentJcvdOnFirtFloor().getScreenType() != 2) {
            Log.d(TAG, "release [" + hashCode() + "]");
            releaseAllVideos();
        }
    }

    public boolean isCurrentMediaListenerOnFirstFloor() {
        return JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null && JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() == this;
    }

    public static void releaseAllVideos() {
        if (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME > 200) {
            Log.d(TAG, "releaseAllVideos");
            JCVideoPlayerManager.completeAll();
            JCMediaManager.instance().releaseMediaPlayer();
        }
    }

    public static void setJcUserAction(JCUserAction jcUserEvent) {
        JC_USER_EVENT = jcUserEvent;
    }

    public void onEvent(int type) {
        if (JC_USER_EVENT != null && isCurrentMediaListenerOnFirstFloor()) {
            JC_USER_EVENT.onEvent(type, this.url, this.currentScreen, this.objects);
        }
    }

    public void onScrollChange() {
    }

    public static void onScroll() {
        if (JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null && JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() != null && JCVideoPlayerManager.getCurrentJcvdOnFirtFloor().getUrl() == JCMediaManager.CURRENT_PLAYING_URL) {
            if ((JCVideoPlayerManager.getCurrentJcvdOnSecondFloor() != null && JCVideoPlayerManager.getCurrentJcvdOnSecondFloor().getScreenType() != 2) || JCVideoPlayerManager.getCurrentJcvdOnSecondFloor() == null) {
                JCMediaPlayerListener jcMediaPlayerListener = JCVideoPlayerManager.getCurrentJcvdOnFirtFloor();
                if (jcMediaPlayerListener.getState() != 7 && jcMediaPlayerListener.getState() != 6) {
                    jcMediaPlayerListener.onScrollChange();
                }
            }
        }
    }

    public static void startFullscreen(Context context, Class _class, String url, Object... objects) {
        hideSupportActionBar(context);
        JCUtils.getAppCompActivity(context).setRequestedOrientation(FULLSCREEN_ORIENTATION);
        ViewGroup vp = (ViewGroup) JCUtils.scanForActivity(context).findViewById(16908290);
        View old = vp.findViewById(FULLSCREEN_ID);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            JCVideoPlayer jcVideoPlayer = (JCVideoPlayer) _class.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            jcVideoPlayer.setId(FULLSCREEN_ID);
            vp.addView(jcVideoPlayer, new LayoutParams(-1, -1));
            jcVideoPlayer.setUp(url, 2, objects);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            jcVideoPlayer.startButton.performClick();
            JCVideoPlayerManager.FIRST_FLOOR_LIST.put(url, new WeakReference(jcVideoPlayer));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void hideSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST) {
            ActionBar ab = JCUtils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        if (TOOL_BAR_EXIST) {
            JCUtils.getAppCompActivity(context).getWindow().setFlags(1024, 1024);
        }
    }

    public static void showSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST) {
            ActionBar ab = JCUtils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
        if (TOOL_BAR_EXIST) {
            JCUtils.getAppCompActivity(context).getWindow().clearFlags(1024);
        }
    }

    public void showWifiDialog() {
    }

    public void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
    }

    public void dismissProgressDialog() {
    }

    public void showVolumeDialog(float deltaY, int volumePercent) {
    }

    public void dismissVolumeDialog() {
    }
}
