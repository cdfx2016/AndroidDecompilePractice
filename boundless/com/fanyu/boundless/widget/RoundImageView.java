package com.fanyu.boundless.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import com.fanyu.boundless.R;

public class RoundImageView extends ImageView {
    private static final int BODER_RADIUS_DEFAULT = 6;
    private static final String STATE_BORDER_RADIUS = "state_border_radius";
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    private Paint mBitmapPaint;
    private BitmapShader mBitmapShader;
    private int mBorderRadius;
    private Matrix mMatrix;
    private int mRadius;
    private RectF mRoundRect;
    private int mWidth;
    private int type;

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        this.mBorderRadius = a.getDimensionPixelSize(0, (int) TypedValue.applyDimension(1, 6.0f, getResources().getDisplayMetrics()));
        this.type = a.getInt(1, 1);
        a.recycle();
    }

    public RoundImageView(Context context) {
        this(context, null);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.type == 0) {
            this.mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            this.mRadius = this.mWidth / 2;
            setMeasuredDimension(this.mWidth, this.mWidth);
        }
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap bmp = drawableToBitamp(drawable);
            this.mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
            float scale = 1.0f;
            if (this.type == 0) {
                scale = (((float) this.mWidth) * 1.0f) / ((float) Math.min(bmp.getWidth(), bmp.getHeight()));
            } else if (this.type == 1) {
                Log.e("TAG", "b'w = " + bmp.getWidth() + " , " + "b'h = " + bmp.getHeight());
                if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())) {
                    scale = Math.max((((float) getWidth()) * 1.0f) / ((float) bmp.getWidth()), (((float) getHeight()) * 1.0f) / ((float) bmp.getHeight()));
                }
            }
            this.mMatrix.setScale(scale, scale);
            this.mBitmapShader.setLocalMatrix(this.mMatrix);
            this.mBitmapPaint.setShader(this.mBitmapShader);
        }
    }

    protected void onDraw(Canvas canvas) {
        Log.e("TAG", "onDraw");
        if (getDrawable() != null) {
            setUpShader();
            if (this.type == 1) {
                canvas.drawRoundRect(this.mRoundRect, (float) this.mBorderRadius, (float) this.mBorderRadius, this.mBitmapPaint);
            } else {
                canvas.drawCircle((float) this.mRadius, (float) this.mRadius, (float) this.mRadius, this.mBitmapPaint);
            }
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.type == 1) {
            this.mRoundRect = new RectF(0.0f, 0.0f, (float) w, (float) h);
        }
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, this.type);
        bundle.putInt(STATE_BORDER_RADIUS, this.mBorderRadius);
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void setBorderRadius(int borderRadius) {
        int pxVal = dp2px(borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (!(this.type == 1 || this.type == 0)) {
                this.type = 0;
            }
            requestLayout();
        }
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(1, (float) dpVal, getResources().getDisplayMetrics());
    }
}
