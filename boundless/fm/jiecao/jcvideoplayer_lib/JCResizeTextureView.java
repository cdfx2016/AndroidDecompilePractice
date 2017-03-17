package fm.jiecao.jcvideoplayer_lib;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View.MeasureSpec;

public class JCResizeTextureView extends TextureView {
    protected static final String TAG = "JCResizeTextureView";
    protected Point mVideoSize;

    public JCResizeTextureView(Context context) {
        super(context);
        init();
    }

    public JCResizeTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.mVideoSize = new Point(0, 0);
    }

    public void setVideoSize(Point videoSize) {
        if (videoSize != null && !this.mVideoSize.equals(videoSize)) {
            this.mVideoSize = videoSize;
            requestLayout();
        }
    }

    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewRotation = (int) getRotation();
        if (viewRotation == 90 || viewRotation == 270) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }
        Log.i(TAG, "onMeasure  [" + hashCode() + "] ");
        Log.i(TAG, "viewRotation = " + viewRotation);
        int videoWidth = this.mVideoSize.x;
        int videoHeight = this.mVideoSize.y;
        Log.i(TAG, "videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);
        if (videoWidth > 0 && videoHeight > 0) {
            Log.i(TAG, "videoWidth / videoHeight = " + (videoWidth / videoHeight));
        }
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            Log.i(TAG, "widthMeasureSpec  [" + MeasureSpec.toString(widthMeasureSpec) + "]");
            Log.i(TAG, "heightMeasureSpec [" + MeasureSpec.toString(heightMeasureSpec) + "]");
            if (widthSpecMode == 1073741824 && heightSpecMode == 1073741824) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (videoWidth * height < width * videoHeight) {
                    width = (height * videoWidth) / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    height = (width * videoHeight) / videoWidth;
                }
            } else if (widthSpecMode == 1073741824) {
                width = widthSpecSize;
                height = (width * videoHeight) / videoWidth;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = (height * videoWidth) / videoHeight;
                }
            } else if (heightSpecMode == 1073741824) {
                height = heightSpecSize;
                width = (height * videoWidth) / videoHeight;
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = (width * videoHeight) / videoWidth;
                }
            } else {
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = (height * videoWidth) / videoHeight;
                }
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = (width * videoHeight) / videoWidth;
                }
            }
        }
        Log.i(TAG, "viewWidth = " + width + ", viewHeight = " + height);
        Log.i(TAG, "viewWidth / viewHeight = " + (width / height));
        setMeasuredDimension(width, height);
    }
}
