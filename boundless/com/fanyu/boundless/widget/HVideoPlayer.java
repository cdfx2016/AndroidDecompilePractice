package com.fanyu.boundless.widget;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanyu.boundless.util.ScreenOrientationUtil;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class HVideoPlayer extends JCVideoPlayerStandard {
    private Context context;
    private ScreenOrientationUtil instance;
    private PlayCountListener playCountListener;
    private boolean playScreen = true;

    public interface PlayCountListener {
        void playCount();
    }

    public class JCAutoFullscreenListener implements SensorEventListener {
        public static final int ORIENTATION_UNKNOWN = -1;
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = -1;
            float X = -values[0];
            float Y = -values[1];
            float Z = -values[2];
            float magnitude = (X * X) + (Y * Y);
            if (Math.abs(X) > 17.0f || Math.abs(Y) > 17.0f || Math.abs(Z) > 17.0f) {
                HVideoPlayer.this.setPlayScreen(true);
            }
            if (HVideoPlayer.this.playScreen) {
                if (4.0f * magnitude >= Z * Z) {
                    orientation = 90 - Math.round(((float) Math.atan2((double) (-Y), (double) X)) * 57.29578f);
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                Log.d(JCVideoPlayer.TAG, "onSensorChanged: " + orientation);
                if ((orientation <= 315 || orientation >= 360) && (orientation <= 0 || orientation >= 45)) {
                    if (orientation <= 150 || orientation >= 225) {
                        if (orientation <= 225 || orientation >= 315) {
                            if (orientation > 0 && orientation < 120 && JCVideoPlayerManager.getCurrentJcvd() != null) {
                                JCVideoPlayerManager.getCurrentJcvd().autoFullscreen(1.6842924E7f);
                                JCVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
                            }
                        } else if (!HVideoPlayer.this.instance.isPortrait() && JCVideoPlayerManager.getCurrentJcvd() != null) {
                            JCVideoPlayerManager.getCurrentJcvd().autoFullscreen(1.6842924E7f);
                            JCVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
                        }
                    } else if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                        JCVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
                        JCVideoPlayer.backPress();
                    }
                } else if (HVideoPlayer.this.instance.isPortrait() && JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
                    JCVideoPlayer.backPress();
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public boolean isPlayScreen() {
        return this.playScreen;
    }

    public void setPlayScreen(boolean playScreen) {
        this.playScreen = playScreen;
    }

    public HVideoPlayer(Context context) {
        super(context);
    }

    public HVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context) {
        super.init(context);
        this.context = context;
        this.instance = ScreenOrientationUtil.getInstance();
    }

    public int getLayoutId() {
        return super.getLayoutId();
    }

    public void setUp(String url, int screen, Object... objects) {
        FULLSCREEN_ORIENTATION = 6;
        super.setUp(url, screen, objects[0]);
        this.backButton.setVisibility(8);
        this.fullscreenButton.setVisibility(8);
        this.fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HVideoPlayer.this.currentState != 6) {
                    if (HVideoPlayer.this.currentScreen == 2) {
                        JCVideoPlayer.backPress();
                    } else {
                        HVideoPlayer.this.startWindowFullscreen();
                    }
                }
            }
        });
    }

    public void prepareVideo() {
        super.prepareVideo();
        this.playCountListener.playCount();
    }

    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (this.currentState) {
            case 0:
                changeUiToNormal();
                return;
            case 1:
                changeUiToPreparingShow();
                startDismissControlViewTimer();
                return;
            case 2:
                changeUiToPlayingShow();
                startDismissControlViewTimer();
                this.instance.start((Activity) this.context);
                return;
            case 3:
                changeUiToPlayingBufferingShow();
                return;
            case 5:
                changeUiToPauseShow();
                cancelDismissControlViewTimer();
                return;
            case 6:
                changeUiToCompleteShow();
                cancelDismissControlViewTimer();
                this.bottomProgressBar.setProgress(100);
                return;
            case 7:
                changeUiToError();
                return;
            default:
                return;
        }
    }

    public void setPlayCountListener(PlayCountListener playCountListener) {
        this.playCountListener = playCountListener;
    }
}
