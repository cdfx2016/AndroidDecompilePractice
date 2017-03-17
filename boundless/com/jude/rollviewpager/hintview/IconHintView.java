package com.jude.rollviewpager.hintview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import com.jude.rollviewpager.Util;

public class IconHintView extends ShapeHintView {
    private int focusResId;
    private int normalResId;
    private int size;

    public IconHintView(Context context, @DrawableRes int focusResId, @DrawableRes int normalResId) {
        this(context, focusResId, normalResId, Util.dip2px(context, 32.0f));
    }

    public IconHintView(Context context, @DrawableRes int focusResId, @DrawableRes int normalResId, int size) {
        super(context);
        this.focusResId = focusResId;
        this.normalResId = normalResId;
        this.size = size;
    }

    public Drawable makeFocusDrawable() {
        Drawable drawable = getContext().getResources().getDrawable(this.focusResId);
        if (this.size > 0) {
            return zoomDrawable(drawable, this.size, this.size);
        }
        return drawable;
    }

    public Drawable makeNormalDrawable() {
        Drawable drawable = getContext().getResources().getDrawable(this.normalResId);
        if (this.size > 0) {
            return zoomDrawable(drawable, this.size, this.size);
        }
        return drawable;
    }

    private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        matrix.postScale(((float) w) / ((float) width), ((float) h) / ((float) height));
        return new BitmapDrawable(null, Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true));
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != -1 ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
