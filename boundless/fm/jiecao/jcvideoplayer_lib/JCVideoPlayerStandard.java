package fm.jiecao.jcvideoplayer_lib;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

public class JCVideoPlayerStandard extends JCVideoPlayer {
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;
    public ImageView backButton;
    public ProgressBar bottomProgressBar;
    public ProgressBar loadingProgressBar;
    protected ImageView mDialogIcon;
    protected ProgressBar mDialogProgressBar;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ProgressBar mDialogVolumeProgressBar;
    protected DismissControlViewTimerTask mDismissControlViewTimerTask;
    protected Dialog mProgressDialog;
    protected Dialog mVolumeDialog;
    public ImageView thumbImageView;
    public ImageView tinyBackImageView;
    public TextView titleTextView;

    public class DismissControlViewTimerTask extends TimerTask {
        public void run() {
            if (JCVideoPlayerStandard.this.currentState != 0 && JCVideoPlayerStandard.this.currentState != 7 && JCVideoPlayerStandard.this.currentState != 6 && JCVideoPlayerStandard.this.getContext() != null && (JCVideoPlayerStandard.this.getContext() instanceof Activity)) {
                ((Activity) JCVideoPlayerStandard.this.getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        JCVideoPlayerStandard.this.bottomContainer.setVisibility(4);
                        JCVideoPlayerStandard.this.topContainer.setVisibility(4);
                        JCVideoPlayerStandard.this.startButton.setVisibility(4);
                        if (JCVideoPlayerStandard.this.currentScreen != 3) {
                            JCVideoPlayerStandard.this.bottomProgressBar.setVisibility(0);
                        }
                    }
                });
            }
        }
    }

    public JCVideoPlayerStandard(Context context) {
        super(context);
    }

    public JCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context) {
        super.init(context);
        this.bottomProgressBar = (ProgressBar) findViewById(R.id.bottom_progressbar);
        this.titleTextView = (TextView) findViewById(R.id.title);
        this.backButton = (ImageView) findViewById(R.id.back);
        this.thumbImageView = (ImageView) findViewById(R.id.thumb);
        this.loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        this.tinyBackImageView = (ImageView) findViewById(R.id.back_tiny);
        this.thumbImageView.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
        this.tinyBackImageView.setOnClickListener(this);
    }

    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (objects.length != 0) {
            this.titleTextView.setText(objects[0].toString());
            if (this.currentScreen == 2) {
                this.fullscreenButton.setImageResource(R.drawable.jc_shrink);
                this.backButton.setVisibility(0);
                this.tinyBackImageView.setVisibility(4);
            } else if (this.currentScreen == 0 || this.currentScreen == 1) {
                this.fullscreenButton.setImageResource(R.drawable.jc_enlarge);
                this.backButton.setVisibility(8);
                this.tinyBackImageView.setVisibility(4);
            } else if (this.currentScreen == 3) {
                this.tinyBackImageView.setVisibility(0);
                setAllControlsVisible(4, 4, 4, 4, 4, 4, 4);
            }
        }
    }

    public int getLayoutId() {
        return R.layout.jc_layout_standard;
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

    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id != R.id.surface_container) {
            if (id == R.id.progress) {
                switch (event.getAction()) {
                    case 0:
                        cancelDismissControlViewTimer();
                        break;
                    case 1:
                        startDismissControlViewTimer();
                        break;
                    default:
                        break;
                }
            }
        }
        switch (event.getAction()) {
            case 1:
                startDismissControlViewTimer();
                if (this.mChangePosition) {
                    int duration = getDuration();
                    int i = this.mSeekTimePosition * 100;
                    if (duration == 0) {
                        duration = 1;
                    }
                    this.bottomProgressBar.setProgress(i / duration);
                }
                if (!(this.mChangePosition || this.mChangeVolume)) {
                    onEvent(102);
                    onClickUiToggle();
                    break;
                }
        }
        return super.onTouch(v, event);
    }

    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.thumb) {
            if (TextUtils.isEmpty(this.url)) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), 0).show();
            } else if (this.currentState == 0) {
                if (this.url.startsWith("file") || JCUtils.isWifiConnected(getContext()) || WIFI_TIP_DIALOG_SHOWED) {
                    startPlayLogic();
                } else {
                    showWifiDialog();
                }
            } else if (this.currentState == 6) {
                onClickUiToggle();
            }
        } else if (i == R.id.surface_container) {
            startDismissControlViewTimer();
        } else if (i == R.id.back) {
            JCVideoPlayer.backPress();
        } else if (i != R.id.back_tiny) {
        } else {
            if (JCVideoPlayerManager.getCurrentJcvdOnFirtFloor() == null || JCVideoPlayerManager.getCurrentJcvdOnFirtFloor().getUrl() == JCMediaManager.CURRENT_PLAYING_URL) {
                JCVideoPlayer.backPress();
            } else {
                JCVideoPlayer.releaseAllVideos();
            }
        }
    }

    public void showWifiDialog() {
        super.showWifiDialog();
        Builder builder = new Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                JCVideoPlayerStandard.this.startPlayLogic();
                JCVideoPlayer.WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void startPlayLogic() {
        prepareVideo();
        onEvent(101);
    }

    public void onClickUiToggle() {
        if (this.currentState == 1) {
            if (this.bottomContainer.getVisibility() == 0) {
                changeUiToPreparingClear();
            } else {
                changeUiToPreparingShow();
            }
        } else if (this.currentState == 2) {
            if (this.bottomContainer.getVisibility() == 0) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (this.currentState == 5) {
            if (this.bottomContainer.getVisibility() == 0) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        } else if (this.currentState == 6) {
            if (this.bottomContainer.getVisibility() == 0) {
                changeUiToCompleteClear();
            } else {
                changeUiToCompleteShow();
            }
        } else if (this.currentState != 3) {
        } else {
            if (this.bottomContainer.getVisibility() == 0) {
                changeUiToPlayingBufferingClear();
            } else {
                changeUiToPlayingBufferingShow();
            }
        }
    }

    public void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {
        super.setProgressAndTime(progress, secProgress, currentTime, totalTime);
        if (progress != 0) {
            this.bottomProgressBar.setProgress(progress);
        }
        if (secProgress != 0) {
            this.bottomProgressBar.setSecondaryProgress(secProgress);
        }
    }

    public void resetProgressAndTime() {
        super.resetProgressAndTime();
        this.bottomProgressBar.setProgress(0);
        this.bottomProgressBar.setSecondaryProgress(0);
    }

    public void changeUiToNormal() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 4, 0, 4, 0, 0, 4);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(0, 4, 0, 4, 0, 0, 4);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToPreparingShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 4, 4, 0, 0, 0, 4);
                return;
            case 2:
                setAllControlsVisible(0, 4, 4, 0, 0, 0, 4);
                return;
            default:
                return;
        }
    }

    public void changeUiToPreparingClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 4, 4, 0, 0, 0, 4);
                return;
            case 2:
                setAllControlsVisible(0, 4, 4, 0, 0, 0, 4);
                return;
            default:
                return;
        }
    }

    public void onPrepared() {
        super.onPrepared();
        setAllControlsVisible(0, 4, 4, 4, 4, 4, 0);
        startDismissControlViewTimer();
    }

    public void changeUiToPlayingShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 0, 0, 4, 4, 4, 4);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(0, 0, 0, 4, 4, 4, 4);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToPlayingClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(4, 4, 4, 4, 4, 4, 0);
                return;
            case 2:
                setAllControlsVisible(4, 4, 4, 4, 4, 4, 0);
                return;
            default:
                return;
        }
    }

    public void changeUiToPauseShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 0, 0, 4, 4, 4, 4);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(0, 0, 0, 4, 4, 4, 4);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToPauseClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(4, 4, 4, 4, 4, 4, 4);
                return;
            case 2:
                setAllControlsVisible(4, 4, 4, 4, 4, 4, 4);
                return;
            default:
                return;
        }
    }

    public void changeUiToPlayingBufferingShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 0, 4, 0, 4, 4, 4);
                return;
            case 2:
                setAllControlsVisible(0, 0, 4, 0, 4, 4, 4);
                return;
            default:
                return;
        }
    }

    public void changeUiToPlayingBufferingClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(4, 4, 4, 0, 4, 4, 0);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(4, 4, 4, 0, 4, 4, 0);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToCompleteShow() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(0, 0, 0, 4, 0, 4, 4);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(0, 0, 0, 4, 0, 4, 4);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToCompleteClear() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(4, 4, 0, 4, 0, 4, 0);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(4, 4, 0, 4, 0, 4, 0);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void changeUiToError() {
        switch (this.currentScreen) {
            case 0:
            case 1:
                setAllControlsVisible(4, 4, 0, 4, 4, 0, 4);
                updateStartImage();
                return;
            case 2:
                setAllControlsVisible(4, 4, 0, 4, 4, 0, 4);
                updateStartImage();
                return;
            default:
                return;
        }
    }

    public void setAllControlsVisible(int topCon, int bottomCon, int startBtn, int loadingPro, int thumbImg, int coverImg, int bottomPro) {
        this.topContainer.setVisibility(topCon);
        this.bottomContainer.setVisibility(bottomCon);
        this.startButton.setVisibility(startBtn);
        this.loadingProgressBar.setVisibility(loadingPro);
        this.thumbImageView.setVisibility(thumbImg);
        this.bottomProgressBar.setVisibility(bottomPro);
    }

    public void updateStartImage() {
        if (this.currentState == 2) {
            this.startButton.setImageResource(R.drawable.jc_click_pause_selector);
        } else if (this.currentState == 7) {
            this.startButton.setImageResource(R.drawable.jc_click_error_selector);
        } else {
            this.startButton.setImageResource(R.drawable.jc_click_play_selector);
        }
    }

    public void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (this.mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_progress_dialog, null);
            this.mDialogProgressBar = (ProgressBar) localView.findViewById(R.id.duration_progressbar);
            this.mDialogSeekTime = (TextView) localView.findViewById(R.id.tv_current);
            this.mDialogTotalTime = (TextView) localView.findViewById(R.id.tv_duration);
            this.mDialogIcon = (ImageView) localView.findViewById(R.id.duration_image_tip);
            this.mProgressDialog = new Dialog(getContext(), R.style.jc_style_dialog_progress);
            this.mProgressDialog.setContentView(localView);
            this.mProgressDialog.getWindow().addFlags(8);
            this.mProgressDialog.getWindow().addFlags(32);
            this.mProgressDialog.getWindow().addFlags(16);
            this.mProgressDialog.getWindow().setLayout(-2, -2);
            LayoutParams localLayoutParams = this.mProgressDialog.getWindow().getAttributes();
            localLayoutParams.gravity = 49;
            localLayoutParams.y = getResources().getDimensionPixelOffset(R.dimen.jc_progress_dialog_margin_top);
            this.mProgressDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!this.mProgressDialog.isShowing()) {
            this.mProgressDialog.show();
        }
        this.mDialogSeekTime.setText(seekTime);
        this.mDialogTotalTime.setText(" / " + totalTime);
        this.mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (seekTimePosition * 100) / totalTimeDuration);
        if (deltaX > 0.0f) {
            this.mDialogIcon.setBackgroundResource(R.drawable.jc_forward_icon);
        } else {
            this.mDialogIcon.setBackgroundResource(R.drawable.jc_backward_icon);
        }
    }

    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
        }
    }

    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (this.mVolumeDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_volume_dialog, null);
            this.mDialogVolumeProgressBar = (ProgressBar) localView.findViewById(R.id.volume_progressbar);
            this.mVolumeDialog = new Dialog(getContext(), R.style.jc_style_dialog_progress);
            this.mVolumeDialog.setContentView(localView);
            this.mVolumeDialog.getWindow().addFlags(8);
            this.mVolumeDialog.getWindow().addFlags(32);
            this.mVolumeDialog.getWindow().addFlags(16);
            this.mVolumeDialog.getWindow().setLayout(-2, -2);
            LayoutParams localLayoutParams = this.mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = 19;
            localLayoutParams.x = getContext().getResources().getDimensionPixelOffset(R.dimen.jc_volume_dialog_margin_left);
            this.mVolumeDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!this.mVolumeDialog.isShowing()) {
            this.mVolumeDialog.show();
        }
        this.mDialogVolumeProgressBar.setProgress(volumePercent);
    }

    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (this.mVolumeDialog != null) {
            this.mVolumeDialog.dismiss();
        }
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        this.mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(this.mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (this.mDismissControlViewTimerTask != null) {
            this.mDismissControlViewTimerTask.cancel();
        }
    }
}
