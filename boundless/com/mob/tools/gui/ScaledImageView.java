package com.mob.tools.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import com.mob.tools.MobLog;
import com.mob.tools.utils.BitmapHelper;

public class ScaledImageView extends ImageView implements OnTouchListener {
    private static final int DRAG_1 = 1;
    private static final int DRAG_2 = 2;
    private static final int NONE = 0;
    private static final int ZOOM = 3;
    private Bitmap bitmap;
    private float distSquare;
    private float[] downPoint;
    private int dragScrollMinDistSquare;
    private OnMatrixChangedListener listener;
    private Matrix matrix;
    private int mode;
    private Matrix savedMatrix;

    public interface OnMatrixChangedListener {
        void onMactrixChage(Matrix matrix);
    }

    public ScaledImageView(Context context) {
        super(context);
        init(context);
    }

    public ScaledImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ScaledImageView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.dragScrollMinDistSquare = ViewConfiguration.get(context).getScaledTouchSlop();
        this.dragScrollMinDistSquare *= this.dragScrollMinDistSquare;
        setOnTouchListener(this);
    }

    public void setBitmap(Bitmap bm) {
        this.bitmap = bm;
        setImageBitmap(bm);
        int[] src = new int[]{this.bitmap.getWidth(), this.bitmap.getHeight()};
        int[] dst = BitmapHelper.fixRect(src, new int[]{getWidth(), getHeight()});
        int[] centerDel = new int[]{(target[0] - dst[0]) / 2, (target[1] - dst[1]) / 2};
        float[] factor = new float[]{((float) dst[0]) / ((float) src[0]), ((float) dst[1]) / ((float) src[1])};
        this.matrix = new Matrix();
        this.matrix.set(getImageMatrix());
        this.matrix.postScale(factor[0], factor[1]);
        this.matrix.postTranslate((float) centerDel[0], (float) centerDel[1]);
        if (this.listener != null) {
            this.listener.onMactrixChage(this.matrix);
        }
        setImageMatrix(this.matrix);
    }

    public void setOnMatrixChangedListener(OnMatrixChangedListener l) {
        this.listener = l;
        if (this.matrix != null) {
            if (this.listener != null) {
                this.listener.onMactrixChage(this.matrix);
            }
            setImageMatrix(this.matrix);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        try {
            float dx;
            float dy;
            float[] start;
            float[] end;
            switch (event.getAction()) {
                case 0:
                    this.matrix = new Matrix();
                    this.matrix.set(getImageMatrix());
                    this.savedMatrix = new Matrix();
                    this.savedMatrix.set(this.matrix);
                    this.downPoint = new float[]{event.getX(0), event.getY(0)};
                    this.mode = 1;
                    break;
                case 1:
                    if (this.listener != null) {
                        this.listener.onMactrixChage(this.matrix);
                    }
                    dx = event.getX(0) - this.downPoint[0];
                    dy = event.getY(0) - this.downPoint[1];
                    if (this.mode == 1 && (dx * dx) + (dy * dy) <= ((float) this.dragScrollMinDistSquare)) {
                        performClick();
                    }
                    this.mode = 0;
                    break;
                case 2:
                    if (this.mode != 1) {
                        if (this.mode != 2) {
                            if (this.mode == 3) {
                                start = new float[]{event.getX(0), event.getY(0)};
                                end = new float[]{event.getX(1), event.getY(1)};
                                dx = start[0] - end[0];
                                dy = start[1] - end[1];
                                float newDistSquare = (dx * dx) + (dy * dy);
                                this.matrix.set(this.savedMatrix);
                                float scale = (float) Math.sqrt((double) (newDistSquare / this.distSquare));
                                float[] middle = new float[]{(start[0] + end[0]) / 2.0f, (start[1] + end[1]) / 2.0f};
                                this.matrix.postScale(scale, scale, middle[0], middle[1]);
                                break;
                            }
                        }
                        end = new float[]{event.getX(1), event.getY(1)};
                        this.matrix.set(this.savedMatrix);
                        this.matrix.postTranslate(end[0] - this.downPoint[0], end[1] - this.downPoint[1]);
                        break;
                    }
                    end = new float[]{event.getX(0), event.getY(0)};
                    this.matrix.set(this.savedMatrix);
                    this.matrix.postTranslate(end[0] - this.downPoint[0], end[1] - this.downPoint[1]);
                    break;
                    break;
                case 5:
                    start = new float[]{event.getX(0), event.getY(0)};
                    end = new float[]{event.getX(1), event.getY(1)};
                    dx = start[0] - end[0];
                    dy = start[1] - end[1];
                    this.distSquare = (dx * dx) + (dy * dy);
                    this.mode = 3;
                    break;
                case 6:
                    this.downPoint = new float[]{event.getX(1), event.getY(1)};
                    this.savedMatrix.set(this.matrix);
                    this.mode = 2;
                    break;
                case 261:
                    start = new float[]{event.getX(0), event.getY(0)};
                    end = new float[]{event.getX(1), event.getY(1)};
                    dx = start[0] - end[0];
                    dy = start[1] - end[1];
                    this.distSquare = (dx * dx) + (dy * dy);
                    this.mode = 3;
                    break;
                case 262:
                    this.downPoint = new float[]{event.getX(0), event.getY(0)};
                    this.savedMatrix.set(this.matrix);
                    this.mode = 1;
                    break;
                default:
                    return false;
            }
            setImageMatrix(this.matrix);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
        return true;
    }

    public void rotateLeft() {
        try {
            this.matrix = new Matrix();
            float[] matrixValue = new float[]{0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
            this.matrix.setValues(matrixValue);
            Bitmap resizedBitmap = Bitmap.createBitmap(this.bitmap, 0, 0, this.bitmap.getWidth(), this.bitmap.getHeight(), this.matrix, true);
            if (!(resizedBitmap == null || resizedBitmap.isRecycled())) {
                this.bitmap.recycle();
                this.bitmap = resizedBitmap;
            }
            setImageBitmap(this.bitmap);
            this.matrix = new Matrix();
            this.matrix.set(getImageMatrix());
            this.matrix.getValues(matrixValue);
            int[] target = new int[]{getWidth(), getHeight()};
            float[] src = new float[]{matrixValue[0] * ((float) this.bitmap.getWidth()), matrixValue[4] * ((float) this.bitmap.getHeight())};
            float[] centerDel = new float[]{(((float) target[0]) - src[0]) / 2.0f, (((float) target[1]) - src[1]) / 2.0f};
            matrixValue[2] = centerDel[0];
            matrixValue[5] = centerDel[1];
            this.matrix.setValues(matrixValue);
            if (this.listener != null) {
                this.listener.onMactrixChage(this.matrix);
            }
            setImageMatrix(this.matrix);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
    }

    public void rotateRight() {
        try {
            this.matrix = new Matrix();
            float[] matrixValue = new float[]{0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
            this.matrix.setValues(matrixValue);
            Bitmap resizedBitmap = Bitmap.createBitmap(this.bitmap, 0, 0, this.bitmap.getWidth(), this.bitmap.getHeight(), this.matrix, true);
            if (!(resizedBitmap == null || resizedBitmap.isRecycled())) {
                this.bitmap.recycle();
                this.bitmap = resizedBitmap;
            }
            setImageBitmap(this.bitmap);
            this.matrix = new Matrix();
            this.matrix.set(getImageMatrix());
            this.matrix.getValues(matrixValue);
            int[] target = new int[]{getWidth(), getHeight()};
            float[] src = new float[]{matrixValue[0] * ((float) this.bitmap.getWidth()), matrixValue[4] * ((float) this.bitmap.getHeight())};
            float[] centerDel = new float[]{(((float) target[0]) - src[0]) / 2.0f, (((float) target[1]) - src[1]) / 2.0f};
            matrixValue[2] = centerDel[0];
            matrixValue[5] = centerDel[1];
            this.matrix.setValues(matrixValue);
            if (this.listener != null) {
                this.listener.onMactrixChage(this.matrix);
            }
            setImageMatrix(this.matrix);
        } catch (Throwable t) {
            MobLog.getInstance().w(t);
        }
    }

    public void zoomIn() {
        this.matrix = new Matrix();
        this.matrix.set(getImageMatrix());
        this.matrix.postScale(1.072f, 1.072f);
        if (this.listener != null) {
            this.listener.onMactrixChage(this.matrix);
        }
        setImageMatrix(this.matrix);
    }

    public void zoomOut() {
        this.matrix = new Matrix();
        this.matrix.set(getImageMatrix());
        this.matrix.postScale(0.933f, 0.933f);
        if (this.listener != null) {
            this.listener.onMactrixChage(this.matrix);
        }
        setImageMatrix(this.matrix);
    }

    public Bitmap getCropedBitmap(Rect cropRect) {
        try {
            Bitmap bmTmp = BitmapHelper.captureView(this, getWidth(), getHeight());
            if (bmTmp == null) {
                MobLog.getInstance().w("ivPhoto.getDrawingCache() returns null", new Object[0]);
                return null;
            }
            Bitmap scaledBm = Bitmap.createBitmap(bmTmp, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
            bmTmp.recycle();
            return scaledBm;
        } catch (Throwable e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }
}
