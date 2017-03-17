package com.fanyu.boundless.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;

public class ScreenSwitchUtils {
    private static final String TAG = ScreenSwitchUtils.class.getSimpleName();
    private static volatile ScreenSwitchUtils mInstance;
    private boolean isPortrait = true;
    private OrientationSensorListener listener;
    private OrientationSensorListener1 listener1;
    private Activity mActivity;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 888:
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < TsExtractor.TS_STREAM_TYPE_E_AC3) {
                        return;
                    }
                    if (orientation > TsExtractor.TS_STREAM_TYPE_E_AC3 && orientation < 225) {
                        return;
                    }
                    if (orientation <= 225 || orientation >= 315) {
                        if (((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) && !ScreenSwitchUtils.this.isPortrait) {
                            Log.e("test", "切换成竖屏");
                            ScreenSwitchUtils.this.mActivity.setRequestedOrientation(1);
                            ScreenSwitchUtils.this.isPortrait = true;
                            return;
                        }
                        return;
                    } else if (ScreenSwitchUtils.this.isPortrait) {
                        Log.e("test", "切换成横屏");
                        ScreenSwitchUtils.this.mActivity.setRequestedOrientation(0);
                        ScreenSwitchUtils.this.isPortrait = false;
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    };
    private Sensor sensor;
    private Sensor sensor1;
    private SensorManager sm;
    private SensorManager sm1;

    public class OrientationSensorListener1 implements SensorEventListener {
        public static final int ORIENTATION_UNKNOWN = -1;
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = -1;
            float X = -values[0];
            float Y = -values[1];
            float Z = -values[2];
            if (4.0f * ((X * X) + (Y * Y)) >= Z * Z) {
                orientation = 90 - Math.round(((float) Math.atan2((double) (-Y), (double) X)) * 57.29578f);
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation <= 225 || orientation >= 315) {
                if (((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) && ScreenSwitchUtils.this.isPortrait) {
                    ScreenSwitchUtils.this.sm.registerListener(ScreenSwitchUtils.this.listener, ScreenSwitchUtils.this.sensor, 2);
                    ScreenSwitchUtils.this.sm1.unregisterListener(ScreenSwitchUtils.this.listener1);
                }
            } else if (!ScreenSwitchUtils.this.isPortrait) {
                ScreenSwitchUtils.this.sm.registerListener(ScreenSwitchUtils.this.listener, ScreenSwitchUtils.this.sensor, 2);
                ScreenSwitchUtils.this.sm1.unregisterListener(ScreenSwitchUtils.this.listener1);
            }
        }
    }

    public class OrientationSensorListener implements SensorEventListener {
        public static final int ORIENTATION_UNKNOWN = -1;
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;
        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            this.rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = -1;
            float X = -values[0];
            float Y = -values[1];
            float Z = -values[2];
            if (4.0f * ((X * X) + (Y * Y)) >= Z * Z) {
                orientation = 90 - Math.round(((float) Math.atan2((double) (-Y), (double) X)) * 57.29578f);
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (this.rotateHandler != null) {
                this.rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
    }

    public static ScreenSwitchUtils init(Context context) {
        if (mInstance == null) {
            synchronized (ScreenSwitchUtils.class) {
                if (mInstance == null) {
                    mInstance = new ScreenSwitchUtils(context);
                }
            }
        }
        return mInstance;
    }

    private ScreenSwitchUtils(Context context) {
        Log.d(TAG, "init orientation listener.");
        this.sm = (SensorManager) context.getSystemService("sensor");
        this.sensor = this.sm.getDefaultSensor(1);
        this.listener = new OrientationSensorListener(this.mHandler);
        this.sm1 = (SensorManager) context.getSystemService("sensor");
        this.sensor1 = this.sm1.getDefaultSensor(1);
        this.listener1 = new OrientationSensorListener1();
    }

    public void start(Activity activity) {
        Log.d(TAG, "start orientation listener.");
        this.mActivity = activity;
        this.sm.registerListener(this.listener, this.sensor, 2);
    }

    public void stop() {
        Log.d(TAG, "stop orientation listener.");
        this.sm.unregisterListener(this.listener);
        this.sm1.unregisterListener(this.listener1);
    }

    public void toggleScreen() {
        this.sm.unregisterListener(this.listener);
        this.sm1.registerListener(this.listener1, this.sensor1, 2);
        if (this.isPortrait) {
            this.isPortrait = false;
            this.mActivity.setRequestedOrientation(0);
            return;
        }
        this.isPortrait = true;
        this.mActivity.setRequestedOrientation(1);
    }

    public boolean isPortrait() {
        return this.isPortrait;
    }
}
