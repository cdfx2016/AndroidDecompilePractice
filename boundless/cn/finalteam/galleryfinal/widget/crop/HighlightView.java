package cn.finalteam.galleryfinal.widget.crop;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.view.View;

class HighlightView {
    private static final int DEFAULT_HIGHLIGHT_COLOR = -13388315;
    public static final int GROW_BOTTOM_EDGE = 16;
    public static final int GROW_LEFT_EDGE = 2;
    public static final int GROW_NONE = 1;
    public static final int GROW_RIGHT_EDGE = 4;
    public static final int GROW_TOP_EDGE = 8;
    private static final float HANDLE_RADIUS_DP = 12.0f;
    public static final int MOVE = 32;
    private static final float OUTLINE_DP = 2.0f;
    RectF cropRect;
    Rect drawRect;
    private HandleMode handleMode = HandleMode.Changing;
    private final Paint handlePaint = new Paint();
    private float handleRadius;
    private int highlightColor;
    private RectF imageRect;
    private float initialAspectRatio;
    private boolean isFocused;
    private boolean maintainAspectRatio;
    Matrix matrix;
    private ModifyMode modifyMode = ModifyMode.None;
    private final Paint outlinePaint = new Paint();
    private float outlineWidth;
    private final Paint outsidePaint = new Paint();
    private boolean showCircle;
    private boolean showThirds;
    private View viewContext;

    enum HandleMode {
        Changing,
        Always,
        Never
    }

    enum ModifyMode {
        None,
        Move,
        Grow
    }

    public HighlightView(View view, int highlightColor) {
        this.viewContext = view;
        initStyles(highlightColor);
    }

    private void initStyles(int highlightColor) {
        this.showThirds = true;
        this.showCircle = false;
        this.handleMode = HandleMode.Always;
        this.highlightColor = highlightColor;
    }

    public void setup(Matrix m, Rect imageRect, RectF cropRect, boolean maintainAspectRatio) {
        this.matrix = new Matrix(m);
        this.cropRect = cropRect;
        this.imageRect = new RectF(imageRect);
        this.maintainAspectRatio = maintainAspectRatio;
        this.initialAspectRatio = this.cropRect.width() / this.cropRect.height();
        this.drawRect = computeLayout();
        this.outsidePaint.setARGB(125, 50, 50, 50);
        this.outlinePaint.setStyle(Style.STROKE);
        this.outlinePaint.setAntiAlias(true);
        this.outlineWidth = dpToPx(OUTLINE_DP);
        this.handlePaint.setColor(this.highlightColor);
        this.handlePaint.setStyle(Style.FILL);
        this.handlePaint.setAntiAlias(true);
        this.handleRadius = dpToPx(HANDLE_RADIUS_DP);
        this.modifyMode = ModifyMode.None;
    }

    private float dpToPx(float dp) {
        return this.viewContext.getResources().getDisplayMetrics().density * dp;
    }

    protected void draw(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        this.outlinePaint.setStrokeWidth(this.outlineWidth);
        if (hasFocus()) {
            Rect viewDrawingRect = new Rect();
            this.viewContext.getDrawingRect(viewDrawingRect);
            path.addRect(new RectF(this.drawRect), Direction.CW);
            this.outlinePaint.setColor(this.highlightColor);
            if (isClipPathSupported(canvas)) {
                canvas.clipPath(path, Op.DIFFERENCE);
                canvas.drawRect(viewDrawingRect, this.outsidePaint);
            } else {
                drawOutsideFallback(canvas);
            }
            canvas.restore();
            canvas.drawPath(path, this.outlinePaint);
            if (this.showThirds) {
                drawThirds(canvas);
            }
            if (this.showCircle) {
                drawCircle(canvas);
            }
            if (this.handleMode == HandleMode.Always || (this.handleMode == HandleMode.Changing && this.modifyMode == ModifyMode.Grow)) {
                drawHandles(canvas);
                return;
            }
            return;
        }
        this.outlinePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        canvas.drawRect(this.drawRect, this.outlinePaint);
    }

    private void drawOutsideFallback(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) this.drawRect.top, this.outsidePaint);
        canvas.drawRect(0.0f, (float) this.drawRect.bottom, (float) canvas.getWidth(), (float) canvas.getHeight(), this.outsidePaint);
        canvas.drawRect(0.0f, (float) this.drawRect.top, (float) this.drawRect.left, (float) this.drawRect.bottom, this.outsidePaint);
        canvas.drawRect((float) this.drawRect.right, (float) this.drawRect.top, (float) canvas.getWidth(), (float) this.drawRect.bottom, this.outsidePaint);
    }

    @SuppressLint({"NewApi"})
    private boolean isClipPathSupported(Canvas canvas) {
        if (VERSION.SDK_INT == 17) {
            return false;
        }
        if (VERSION.SDK_INT < 14 || VERSION.SDK_INT > 15 || !canvas.isHardwareAccelerated()) {
            return true;
        }
        return false;
    }

    private void drawHandles(Canvas canvas) {
        int xMiddle = this.drawRect.left + ((this.drawRect.right - this.drawRect.left) / 2);
        int yMiddle = this.drawRect.top + ((this.drawRect.bottom - this.drawRect.top) / 2);
        canvas.drawCircle((float) this.drawRect.left, (float) yMiddle, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float) xMiddle, (float) this.drawRect.top, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float) this.drawRect.right, (float) yMiddle, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float) xMiddle, (float) this.drawRect.bottom, this.handleRadius, this.handlePaint);
    }

    private void drawThirds(Canvas canvas) {
        this.outlinePaint.setStrokeWidth(1.0f);
        float xThird = (float) ((this.drawRect.right - this.drawRect.left) / 3);
        float yThird = (float) ((this.drawRect.bottom - this.drawRect.top) / 3);
        canvas.drawLine(((float) this.drawRect.left) + xThird, (float) this.drawRect.top, ((float) this.drawRect.left) + xThird, (float) this.drawRect.bottom, this.outlinePaint);
        Canvas canvas2 = canvas;
        canvas2.drawLine((xThird * OUTLINE_DP) + ((float) this.drawRect.left), (float) this.drawRect.top, (xThird * OUTLINE_DP) + ((float) this.drawRect.left), (float) this.drawRect.bottom, this.outlinePaint);
        canvas.drawLine((float) this.drawRect.left, ((float) this.drawRect.top) + yThird, (float) this.drawRect.right, ((float) this.drawRect.top) + yThird, this.outlinePaint);
        canvas2 = canvas;
        canvas2.drawLine((float) this.drawRect.left, (yThird * OUTLINE_DP) + ((float) this.drawRect.top), (float) this.drawRect.right, (yThird * OUTLINE_DP) + ((float) this.drawRect.top), this.outlinePaint);
    }

    private void drawCircle(Canvas canvas) {
        this.outlinePaint.setStrokeWidth(1.0f);
        canvas.drawOval(new RectF(this.drawRect), this.outlinePaint);
    }

    public void setMode(ModifyMode mode) {
        if (mode != this.modifyMode) {
            this.modifyMode = mode;
            this.viewContext.invalidate();
        }
    }

    public int getHit(float x, float y) {
        Rect r = computeLayout();
        int retval = 1;
        boolean verticalCheck;
        if (y < ((float) r.top) - 20.0f || y >= ((float) r.bottom) + 20.0f) {
            verticalCheck = false;
        } else {
            verticalCheck = true;
        }
        boolean horizCheck;
        if (x < ((float) r.left) - 20.0f || x >= ((float) r.right) + 20.0f) {
            horizCheck = false;
        } else {
            horizCheck = true;
        }
        if (Math.abs(((float) r.left) - x) < 20.0f && verticalCheck) {
            retval = 1 | 2;
        }
        if (Math.abs(((float) r.right) - x) < 20.0f && verticalCheck) {
            retval |= 4;
        }
        if (Math.abs(((float) r.top) - y) < 20.0f && horizCheck) {
            retval |= 8;
        }
        if (Math.abs(((float) r.bottom) - y) < 20.0f && horizCheck) {
            retval |= 16;
        }
        if (retval == 1 && r.contains((int) x, (int) y)) {
            return 32;
        }
        return retval;
    }

    void handleMotion(int edge, float dx, float dy) {
        int i = -1;
        Rect r = computeLayout();
        if (edge == 32) {
            moveBy((this.cropRect.width() / ((float) r.width())) * dx, (this.cropRect.height() / ((float) r.height())) * dy);
            return;
        }
        if ((edge & 6) == 0) {
            dx = 0.0f;
        }
        if ((edge & 24) == 0) {
            dy = 0.0f;
        }
        float yDelta = dy * (this.cropRect.height() / ((float) r.height()));
        float width = ((float) ((edge & 2) != 0 ? -1 : 1)) * (dx * (this.cropRect.width() / ((float) r.width())));
        if ((edge & 8) == 0) {
            i = 1;
        }
        growBy(width, ((float) i) * yDelta);
    }

    void moveBy(float dx, float dy) {
        Rect invalRect = new Rect(this.drawRect);
        this.cropRect.offset(dx, dy);
        this.cropRect.offset(Math.max(0.0f, this.imageRect.left - this.cropRect.left), Math.max(0.0f, this.imageRect.top - this.cropRect.top));
        this.cropRect.offset(Math.min(0.0f, this.imageRect.right - this.cropRect.right), Math.min(0.0f, this.imageRect.bottom - this.cropRect.bottom));
        this.drawRect = computeLayout();
        invalRect.union(this.drawRect);
        invalRect.inset(-((int) this.handleRadius), -((int) this.handleRadius));
        this.viewContext.invalidate(invalRect);
    }

    void growBy(float dx, float dy) {
        float heightCap = 25.0f;
        if (this.maintainAspectRatio) {
            if (dx != 0.0f) {
                dy = dx / this.initialAspectRatio;
            } else if (dy != 0.0f) {
                dx = dy * this.initialAspectRatio;
            }
        }
        RectF r = new RectF(this.cropRect);
        if (dx > 0.0f && r.width() + (OUTLINE_DP * dx) > this.imageRect.width()) {
            dx = (this.imageRect.width() - r.width()) / OUTLINE_DP;
            if (this.maintainAspectRatio) {
                dy = dx / this.initialAspectRatio;
            }
        }
        if (dy > 0.0f && r.height() + (OUTLINE_DP * dy) > this.imageRect.height()) {
            dy = (this.imageRect.height() - r.height()) / OUTLINE_DP;
            if (this.maintainAspectRatio) {
                dx = dy * this.initialAspectRatio;
            }
        }
        r.inset(-dx, -dy);
        if (r.width() < 25.0f) {
            r.inset((-(25.0f - r.width())) / OUTLINE_DP, 0.0f);
        }
        if (this.maintainAspectRatio) {
            heightCap = 25.0f / this.initialAspectRatio;
        }
        if (r.height() < heightCap) {
            r.inset(0.0f, (-(heightCap - r.height())) / OUTLINE_DP);
        }
        if (r.left < this.imageRect.left) {
            r.offset(this.imageRect.left - r.left, 0.0f);
        } else if (r.right > this.imageRect.right) {
            r.offset(-(r.right - this.imageRect.right), 0.0f);
        }
        if (r.top < this.imageRect.top) {
            r.offset(0.0f, this.imageRect.top - r.top);
        } else if (r.bottom > this.imageRect.bottom) {
            r.offset(0.0f, -(r.bottom - this.imageRect.bottom));
        }
        this.cropRect.set(r);
        this.drawRect = computeLayout();
        this.viewContext.invalidate();
    }

    public Rect getScaledCropRect(float scale) {
        return new Rect((int) (this.cropRect.left * scale), (int) (this.cropRect.top * scale), (int) (this.cropRect.right * scale), (int) (this.cropRect.bottom * scale));
    }

    private Rect computeLayout() {
        RectF r = new RectF(this.cropRect.left, this.cropRect.top, this.cropRect.right, this.cropRect.bottom);
        this.matrix.mapRect(r);
        return new Rect(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    public void invalidate() {
        this.drawRect = computeLayout();
    }

    public boolean hasFocus() {
        return this.isFocused;
    }

    public void setFocus(boolean isFocused) {
        this.isFocused = isFocused;
    }
}
