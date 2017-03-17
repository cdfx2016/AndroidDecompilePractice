package com.fanyu.boundless.widget.ImagPagerUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {
    public static final int STATUS_INIT = 1;
    public static final int STATUS_MOVE = 4;
    public static final int STATUS_ZOOM_IN = 3;
    public static final int STATUS_ZOOM_OUT = 2;
    private float centerPointX;
    private float centerPointY;
    private float currentBitmapHeight;
    private float currentBitmapWidth;
    private int currentStatus = 1;
    private int height;
    private float initRatio;
    boolean isInitCenter;
    private double lastFingerDis;
    private float lastXMove = -1.0f;
    private float lastYMove = -1.0f;
    private Matrix matrix = new Matrix();
    private float movedDistanceX;
    private float movedDistanceY;
    private float scaledRatio;
    private Bitmap sourceBitmap;
    private float totalRatio;
    private float totalTranslateX;
    private float totalTranslateY;
    private int width;
    float x1 = 0.0f;
    float y1 = 0.0f;

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomImageView(Context context) {
        super(context);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.sourceBitmap = bitmap;
        this.isInitCenter = false;
        invalidate();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.width = getWidth();
            this.height = getHeight();
        }
    }

    @TargetApi(15)
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case 0:
                this.x1 = event.getX();
                this.y1 = event.getY();
                break;
            case 1:
                float v = event.getX() - this.x1;
                float v1 = event.getY() - this.y1;
                this.x1 = 0.0f;
                this.y1 = 0.0f;
                if (Math.abs(v) + Math.abs(v1) < 10.0f) {
                    callOnClick();
                }
                this.lastXMove = -1.0f;
                this.lastYMove = -1.0f;
                break;
            case 2:
                if (event.getPointerCount() != 1) {
                    if (event.getPointerCount() == 2) {
                        centerPointBetweenFingers(event);
                        double fingerDis = distanceBetweenFingers(event);
                        if (fingerDis > this.lastFingerDis) {
                            this.currentStatus = 2;
                        } else {
                            this.currentStatus = 3;
                        }
                        if ((this.currentStatus == 2 && this.totalRatio < this.initRatio * 4.0f) || (this.currentStatus == 3 && this.totalRatio > this.initRatio)) {
                            this.scaledRatio = (float) (fingerDis / this.lastFingerDis);
                            this.totalRatio *= this.scaledRatio;
                            if (this.totalRatio > this.initRatio * 4.0f) {
                                this.totalRatio = this.initRatio * 4.0f;
                            } else if (this.totalRatio < this.initRatio) {
                                this.totalRatio = this.initRatio;
                            }
                            invalidate();
                            this.lastFingerDis = fingerDis;
                            break;
                        }
                    }
                }
                float xMove = event.getX();
                float yMove = event.getY();
                if (this.lastXMove == -1.0f && this.lastYMove == -1.0f) {
                    this.lastXMove = xMove;
                    this.lastYMove = yMove;
                }
                this.currentStatus = 4;
                this.movedDistanceX = xMove - this.lastXMove;
                this.movedDistanceY = yMove - this.lastYMove;
                if (this.totalTranslateX + this.movedDistanceX >= 50.0f || ((float) this.width) - (this.totalTranslateX + this.movedDistanceX) >= this.currentBitmapWidth) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (this.totalTranslateX + this.movedDistanceX > 0.0f) {
                    this.movedDistanceX = 0.0f;
                } else if (((float) this.width) - (this.totalTranslateX + this.movedDistanceX) > this.currentBitmapWidth) {
                    this.movedDistanceX = 0.0f;
                }
                if (this.totalTranslateY + this.movedDistanceY > 0.0f) {
                    this.movedDistanceY = 0.0f;
                } else if (((float) this.height) - (this.totalTranslateY + this.movedDistanceY) > this.currentBitmapHeight) {
                    this.movedDistanceY = 0.0f;
                }
                invalidate();
                this.lastXMove = xMove;
                this.lastYMove = yMove;
                break;
                break;
            case 5:
                if (event.getPointerCount() == 2) {
                    this.lastFingerDis = distanceBetweenFingers(event);
                    break;
                }
                break;
            case 6:
                if (event.getPointerCount() == 2) {
                    this.lastXMove = -1.0f;
                    this.lastYMove = -1.0f;
                    break;
                }
                break;
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.isInitCenter) {
            this.currentStatus = 1;
            this.isInitCenter = true;
        }
        switch (this.currentStatus) {
            case 1:
                initBitmap(canvas);
                break;
            case 2:
            case 3:
                zoom(canvas);
                return;
            case 4:
                move(canvas);
                return;
        }
        if (this.sourceBitmap != null) {
            this.currentStatus = 1;
            canvas.drawBitmap(this.sourceBitmap, this.matrix, null);
        }
    }

    private void zoom(Canvas canvas) {
        this.matrix.reset();
        this.matrix.postScale(this.totalRatio, this.totalRatio);
        if (this.sourceBitmap != null) {
            float translateX;
            float translateY;
            float scaledWidth = ((float) this.sourceBitmap.getWidth()) * this.totalRatio;
            float scaledHeight = ((float) this.sourceBitmap.getHeight()) * this.totalRatio;
            if (this.currentBitmapWidth < ((float) this.width)) {
                translateX = (((float) this.width) - scaledWidth) / 2.0f;
            } else {
                translateX = (this.totalTranslateX * this.scaledRatio) + (this.centerPointX * (1.0f - this.scaledRatio));
                if (translateX > 0.0f) {
                    translateX = 0.0f;
                } else if (((float) this.width) - translateX > scaledWidth) {
                    translateX = ((float) this.width) - scaledWidth;
                }
            }
            if (this.currentBitmapHeight < ((float) this.height)) {
                translateY = (((float) this.height) - scaledHeight) / 2.0f;
            } else {
                translateY = (this.totalTranslateY * this.scaledRatio) + (this.centerPointY * (1.0f - this.scaledRatio));
                if (translateY > 0.0f) {
                    translateY = 0.0f;
                } else if (((float) this.height) - translateY > scaledHeight) {
                    translateY = ((float) this.height) - scaledHeight;
                }
            }
            this.matrix.postTranslate(translateX, translateY);
            this.totalTranslateX = translateX;
            this.totalTranslateY = translateY;
            this.currentBitmapWidth = scaledWidth;
            this.currentBitmapHeight = scaledHeight;
            canvas.drawBitmap(this.sourceBitmap, this.matrix, null);
        }
    }

    private void move(Canvas canvas) {
        if (this.sourceBitmap != null) {
            this.matrix.reset();
            float translateX = this.totalTranslateX + this.movedDistanceX;
            float translateY = this.totalTranslateY + this.movedDistanceY;
            this.matrix.postScale(this.totalRatio, this.totalRatio);
            this.matrix.postTranslate(translateX, translateY);
            this.totalTranslateX = translateX;
            this.totalTranslateY = translateY;
            canvas.drawBitmap(this.sourceBitmap, this.matrix, null);
        }
    }

    private void initBitmap(Canvas canvas) {
        if (this.sourceBitmap != null) {
            this.matrix.reset();
            int bitmapWidth = this.sourceBitmap.getWidth();
            int bitmapHeight = this.sourceBitmap.getHeight();
            float translateY;
            float translateX;
            if (bitmapWidth > this.width || bitmapHeight > this.height) {
                float ratio;
                if (bitmapWidth - this.width > bitmapHeight - this.height) {
                    ratio = ((float) this.width) / (((float) bitmapWidth) * 1.0f);
                    this.matrix.postScale(ratio, ratio);
                    translateY = (((float) this.height) - (((float) bitmapHeight) * ratio)) / 2.0f;
                    this.matrix.postTranslate(0.0f, translateY);
                    this.totalTranslateY = translateY;
                    this.initRatio = ratio;
                    this.totalRatio = ratio;
                } else {
                    ratio = ((float) this.height) / (((float) bitmapHeight) * 1.0f);
                    this.matrix.postScale(ratio, ratio);
                    translateX = (((float) this.width) - (((float) bitmapWidth) * ratio)) / 2.0f;
                    this.matrix.postTranslate(translateX, 0.0f);
                    this.totalTranslateX = translateX;
                    this.initRatio = ratio;
                    this.totalRatio = ratio;
                }
                this.currentBitmapWidth = ((float) bitmapWidth) * this.initRatio;
                this.currentBitmapHeight = ((float) bitmapHeight) * this.initRatio;
            } else {
                translateX = ((float) (this.width - this.sourceBitmap.getWidth())) / 2.0f;
                translateY = ((float) (this.height - this.sourceBitmap.getHeight())) / 2.0f;
                this.matrix.postTranslate(translateX, translateY);
                this.totalTranslateX = translateX;
                this.totalTranslateY = translateY;
                this.initRatio = 1.0f;
                this.totalRatio = 1.0f;
                this.currentBitmapWidth = (float) bitmapWidth;
                this.currentBitmapHeight = (float) bitmapHeight;
            }
            canvas.drawBitmap(this.sourceBitmap, this.matrix, null);
        }
    }

    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt((double) ((disX * disX) + (disY * disY)));
    }

    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        this.centerPointX = (xPoint0 + xPoint1) / 2.0f;
        this.centerPointY = (yPoint0 + yPoint1) / 2.0f;
    }
}
