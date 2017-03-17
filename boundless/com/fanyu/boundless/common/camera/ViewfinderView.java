package com.fanyu.boundless.common.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.alibaba.fastjson.asm.Opcodes;
import com.fanyu.boundless.R;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 80;
    private static final int CURRENT_POINT_OPACITY = 160;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private final int SCAN_VELOCITY = 15;
    private CameraManager cameraManager;
    private final int laserColor;
    private List<ResultPoint> lastPossibleResultPoints;
    private final int maskColor;
    private final Paint paint = new Paint(1);
    private List<ResultPoint> possibleResultPoints;
    private Bitmap resultBitmap;
    private final int resultColor;
    private final int resultPointColor;
    Bitmap scanLight;
    private int scanLineTop;
    private int scannerAlpha;
    private final int statusColor;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        this.maskColor = resources.getColor(R.color.viewfinder_mask);
        this.resultColor = resources.getColor(R.color.result_view);
        this.laserColor = resources.getColor(R.color.viewfinder_laser);
        this.resultPointColor = resources.getColor(R.color.possible_result_points);
        this.statusColor = resources.getColor(R.color.status_text);
        this.scannerAlpha = 0;
        this.possibleResultPoints = new ArrayList(5);
        this.lastPossibleResultPoints = null;
        this.scanLight = BitmapFactory.decodeResource(resources, R.mipmap.scan_light);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        if (this.cameraManager != null) {
            Rect frame = this.cameraManager.getFramingRect();
            Rect previewFrame = this.cameraManager.getFramingRectInPreview();
            if (frame != null && previewFrame != null) {
                int i;
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                Paint paint = this.paint;
                if (this.resultBitmap != null) {
                    i = this.resultColor;
                } else {
                    i = this.maskColor;
                }
                paint.setColor(i);
                canvas.drawRect(0.0f, 0.0f, (float) width, (float) frame.top, this.paint);
                canvas.drawRect(0.0f, (float) frame.top, (float) frame.left, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect((float) (frame.right + 1), (float) frame.top, (float) width, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect(0.0f, (float) (frame.bottom + 1), (float) width, (float) height, this.paint);
                if (this.resultBitmap != null) {
                    this.paint.setAlpha(160);
                    canvas.drawBitmap(this.resultBitmap, null, frame, this.paint);
                    return;
                }
                drawFrameBounds(canvas, frame);
                drawStatusText(canvas, frame, width);
                drawScanLight(canvas, frame);
                float scaleX = ((float) frame.width()) / ((float) previewFrame.width());
                float scaleY = ((float) frame.height()) / ((float) previewFrame.height());
                List<ResultPoint> currentPossible = this.possibleResultPoints;
                List<ResultPoint> currentLast = this.lastPossibleResultPoints;
                int frameLeft = frame.left;
                int frameTop = frame.top;
                if (currentPossible.isEmpty()) {
                    this.lastPossibleResultPoints = null;
                } else {
                    this.possibleResultPoints = new ArrayList(5);
                    this.lastPossibleResultPoints = currentPossible;
                    this.paint.setAlpha(160);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentPossible) {
                        for (ResultPoint point : currentPossible) {
                            canvas.drawCircle((float) (((int) (point.getX() * scaleX)) + frameLeft), (float) (((int) (point.getY() * scaleY)) + frameTop), 6.0f, this.paint);
                        }
                    }
                }
                if (currentLast != null) {
                    this.paint.setAlpha(80);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentLast) {
                        for (ResultPoint point2 : currentLast) {
                            canvas.drawCircle((float) (((int) (point2.getX() * scaleX)) + frameLeft), (float) (((int) (point2.getY() * scaleY)) + frameTop), 3.0f, this.paint);
                        }
                    }
                }
                postInvalidateDelayed(ANIMATION_DELAY, frame.left - 6, frame.top - 6, frame.right + 6, frame.bottom + 6);
            }
        }
    }

    private void drawFrameBounds(Canvas canvas, Rect frame) {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth(2.0f);
        this.paint.setStyle(Style.STROKE);
        canvas.drawRect(frame, this.paint);
        this.paint.setColor(Color.parseColor("#ff945700"));
        this.paint.setStyle(Style.FILL);
        canvas.drawRect((float) (frame.left - 20), (float) frame.top, (float) frame.left, (float) (frame.top + 20), this.paint);
        canvas.drawRect((float) (frame.left - 20), (float) (frame.top - 20), (float) (frame.left + 20), (float) frame.top, this.paint);
        canvas.drawRect((float) frame.right, (float) frame.top, (float) (frame.right + 20), (float) (frame.top + 20), this.paint);
        canvas.drawRect((float) (frame.right - 20), (float) (frame.top - 20), (float) (frame.right + 20), (float) frame.top, this.paint);
        canvas.drawRect((float) (frame.left - 20), (float) (frame.bottom - 20), (float) frame.left, (float) frame.bottom, this.paint);
        canvas.drawRect((float) (frame.left - 20), (float) frame.bottom, (float) (frame.left + 20), (float) (frame.bottom + 20), this.paint);
        canvas.drawRect((float) frame.right, (float) (frame.bottom - 20), (float) (frame.right + 20), (float) frame.bottom, this.paint);
        canvas.drawRect((float) (frame.right - 20), (float) frame.bottom, (float) (frame.right + 20), (float) (frame.bottom + 20), this.paint);
    }

    private void drawStatusText(Canvas canvas, Rect frame, int width) {
        String statusText1 = getResources().getString(R.string.viewfinderview_status_text1);
        String statusText2 = getResources().getString(R.string.viewfinderview_status_text2);
        this.paint.setColor(this.statusColor);
        this.paint.setTextSize((float) 45);
        canvas.drawText(statusText1, (float) ((width - ((int) this.paint.measureText(statusText1))) / 2), (float) (frame.top - Opcodes.GETFIELD), this.paint);
        canvas.drawText(statusText2, (float) ((width - ((int) this.paint.measureText(statusText2))) / 2), (float) ((frame.top - Opcodes.GETFIELD) + 60), this.paint);
    }

    private void drawScanLight(Canvas canvas, Rect frame) {
        if (this.scanLineTop == 0) {
            this.scanLineTop = frame.top;
        }
        if (this.scanLineTop >= frame.bottom) {
            this.scanLineTop = frame.top;
        } else {
            this.scanLineTop += 15;
        }
        canvas.drawBitmap(this.scanLight, null, new Rect(frame.left, this.scanLineTop, frame.right, this.scanLineTop + 30), this.paint);
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = this.possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > 20) {
                points.subList(0, size - 10).clear();
            }
        }
    }
}
