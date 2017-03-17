package com.fanyu.boundless.util;

import android.app.Activity;
import android.util.Log;
import android.view.OrientationEventListener;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;

public class ScreenOrientationUtil {
    private static ScreenOrientationUtil instance = new ScreenOrientationUtil();
    private Activity mActivity;
    private OrientationEventListener mOrEventListener;
    private OrientationEventListener mOrEventListener1;
    private int mOrientation;
    private int mOrientation1;

    public static ScreenOrientationUtil getInstance() {
        return instance;
    }

    public void start(Activity activity) {
        this.mActivity = activity;
        if (this.mOrEventListener == null) {
            initListener();
        }
        this.mOrEventListener.enable();
    }

    public void stop() {
        if (this.mOrEventListener != null) {
            this.mOrEventListener.disable();
        }
        if (this.mOrEventListener1 != null) {
            this.mOrEventListener1.disable();
        }
    }

    private void initListener() {
        this.mOrEventListener = new OrientationEventListener(this.mActivity) {
            public void onOrientationChanged(int rotation) {
                Log.e("test", "" + rotation);
                if (rotation != -1) {
                    int orientation = ScreenOrientationUtil.this.convert2Orientation(rotation);
                    if (orientation != ScreenOrientationUtil.this.mOrientation) {
                        ScreenOrientationUtil.this.mOrientation = orientation;
                        ScreenOrientationUtil.this.mActivity.setRequestedOrientation(ScreenOrientationUtil.this.mOrientation);
                    }
                }
            }
        };
        this.mOrEventListener1 = new OrientationEventListener(this.mActivity) {
            public void onOrientationChanged(int rotation) {
                if (rotation != -1) {
                    int orientation = ScreenOrientationUtil.this.convert2Orientation(rotation);
                    if (orientation != ScreenOrientationUtil.this.mOrientation1) {
                        ScreenOrientationUtil.this.mOrientation1 = orientation;
                        if (ScreenOrientationUtil.this.mOrientation1 == ScreenOrientationUtil.this.mOrientation) {
                            ScreenOrientationUtil.this.mOrEventListener1.disable();
                            ScreenOrientationUtil.this.mOrEventListener.enable();
                        }
                    }
                }
            }
        };
    }

    public boolean isPortrait() {
        if (this.mOrientation == 1 || this.mOrientation == 9) {
            return true;
        }
        return false;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void toggleScreen() {
        this.mOrEventListener.disable();
        this.mOrEventListener1.enable();
        int orientation = 0;
        if (this.mOrientation == 1) {
            orientation = 8;
        } else if (this.mOrientation == 0) {
            orientation = 1;
        } else if (this.mOrientation == 8) {
            orientation = 1;
        } else if (this.mOrientation == 9) {
            orientation = 8;
        }
        this.mOrientation = orientation;
        this.mActivity.setRequestedOrientation(this.mOrientation);
    }

    private int convert2Orientation(int rotation) {
        if ((rotation >= 0 && rotation <= 45) || rotation > 315) {
            return 1;
        }
        if (rotation > 45 && rotation <= TsExtractor.TS_STREAM_TYPE_E_AC3) {
            return 8;
        }
        if (rotation > TsExtractor.TS_STREAM_TYPE_E_AC3 && rotation <= 225) {
            return 9;
        }
        if (rotation <= 225 || rotation > 315) {
            return 1;
        }
        return 0;
    }
}
