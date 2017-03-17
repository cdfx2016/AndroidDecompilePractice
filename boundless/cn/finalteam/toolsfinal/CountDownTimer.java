package cn.finalteam.toolsfinal;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public abstract class CountDownTimer {
    private static final int MSG = 1;
    private boolean mCanceled = false;
    private final long mCountdownInterval;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            synchronized (CountDownTimer.this) {
                long millisLeft = CountDownTimer.this.mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0 || CountDownTimer.this.mCanceled) {
                    CountDownTimer.this.onFinish();
                } else if (millisLeft < CountDownTimer.this.mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(1), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    CountDownTimer.this.onTick(millisLeft);
                    long delay = (CountDownTimer.this.mCountdownInterval + lastTickStart) - SystemClock.elapsedRealtime();
                    while (delay < 0) {
                        delay += CountDownTimer.this.mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(1), delay);
                }
            }
        }
    };
    private final long mMillisInFuture;
    private long mStopTimeInFuture;

    public abstract void onFinish();

    public abstract void onTick(long j);

    public CountDownTimer(long millisInFuture, long countDownInterval) {
        this.mMillisInFuture = millisInFuture;
        this.mCountdownInterval = countDownInterval;
    }

    public final void cancel() {
        this.mHandler.removeMessages(1);
        this.mCanceled = true;
    }

    public final synchronized CountDownTimer start() {
        CountDownTimer this;
        if (this.mMillisInFuture <= 0) {
            onFinish();
            this = this;
        } else {
            this.mStopTimeInFuture = SystemClock.elapsedRealtime() + this.mMillisInFuture;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
            this.mCanceled = false;
            this = this;
        }
        return this;
    }
}
