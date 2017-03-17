package com.mob.tools.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RoundRectLayout extends RelativeLayout {
    private Path path;
    private float[] rect;

    public RoundRectLayout(Context context) {
        super(context);
    }

    public RoundRectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundRectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRound(float radius) {
        setRound(radius, radius, radius, radius);
    }

    public void setRound(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        this.rect = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.rect != null) {
            if (this.path == null) {
                int width = getWidth();
                int height = getHeight();
                this.path = new Path();
                this.path.addRoundRect(new RectF(0.0f, 0.0f, (float) width, (float) height), this.rect, Direction.CW);
            }
            canvas.clipPath(this.path);
        }
        super.dispatchDraw(canvas);
    }
}
