package cn.finalteam.galleryfinal.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.finalteam.galleryfinal.R;

public class FloatingActionButton extends ImageButton {
    private float mCircleSize;
    int mColorNormal;
    int mColorPressed;
    private int mDrawableSize;
    @DrawableRes
    private int mIcon;
    private float mShadowOffset;
    private float mShadowRadius;
    String mTitle;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attributeSet) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.GFFloatingActionButton, 0, 0);
        this.mColorNormal = attr.getColor(R.styleable.GFFloatingActionButton_fabColorNormal, ViewCompat.MEASURED_STATE_MASK);
        this.mColorPressed = attr.getColor(R.styleable.GFFloatingActionButton_fabColorPressed, ViewCompat.MEASURED_STATE_MASK);
        this.mIcon = attr.getResourceId(R.styleable.GFFloatingActionButton_fabIcon, 0);
        this.mTitle = attr.getString(R.styleable.GFFloatingActionButton_fabTitle);
        attr.recycle();
        updateCircleSize();
        this.mShadowRadius = getDimension(R.dimen.fab_shadow_radius);
        this.mShadowOffset = getDimension(R.dimen.fab_shadow_offset);
        updateDrawableSize();
        updateBackground();
    }

    private void updateDrawableSize() {
        this.mDrawableSize = (int) (this.mCircleSize + (2.0f * this.mShadowRadius));
    }

    private void updateCircleSize() {
        this.mCircleSize = getDimension(R.dimen.fab_size_normal);
    }

    public void setIcon(@DrawableRes int icon) {
        if (this.mIcon != icon) {
            this.mIcon = icon;
            updateBackground();
        }
    }

    public int getColorNormal() {
        return this.mColorNormal;
    }

    public void setColorNormalResId(@ColorRes int colorNormal) {
        setColorNormal(getColor(colorNormal));
    }

    public void setColorNormal(int color) {
        if (this.mColorNormal != color) {
            this.mColorNormal = color;
            updateBackground();
        }
    }

    public int getColorPressed() {
        return this.mColorPressed;
    }

    public void setColorPressedResId(@ColorRes int colorPressed) {
        setColorPressed(getColor(colorPressed));
    }

    public void setColorPressed(int color) {
        if (this.mColorPressed != color) {
            this.mColorPressed = color;
            updateBackground();
        }
    }

    int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    public void setTitle(String title) {
        this.mTitle = title;
        TextView label = (TextView) getTag(R.id.fab_label);
        if (label != null) {
            label.setText(title);
        }
    }

    public String getTitle() {
        return this.mTitle;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(this.mDrawableSize, this.mDrawableSize);
    }

    void updateBackground() {
        float circleLeft = this.mShadowRadius;
        float circleTop = this.mShadowRadius - this.mShadowOffset;
        RectF circleRect = new RectF(circleLeft, circleTop, this.mCircleSize + circleLeft, this.mCircleSize + circleTop);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{new BitmapDrawable(getResources()), createFillDrawable(circleRect), new BitmapDrawable(getResources()), getIconDrawable()});
        float iconOffset = (this.mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2.0f;
        int iconInsetHorizontal = (int) (this.mShadowRadius + iconOffset);
        layerDrawable.setLayerInset(3, iconInsetHorizontal, (int) (circleTop + iconOffset), iconInsetHorizontal, (int) ((this.mShadowRadius + this.mShadowOffset) + iconOffset));
        setBackgroundCompat(layerDrawable);
    }

    Drawable getIconDrawable() {
        if (this.mIcon != 0) {
            return getResources().getDrawable(this.mIcon);
        }
        return new ColorDrawable(0);
    }

    private StateListDrawable createFillDrawable(RectF circleRect) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{16842919}, createCircleDrawable(circleRect, this.mColorPressed));
        drawable.addState(new int[0], createCircleDrawable(circleRect, this.mColorNormal));
        return drawable;
    }

    private Drawable createCircleDrawable(RectF circleRect, int color) {
        Bitmap bitmap = Bitmap.createBitmap(this.mDrawableSize, this.mDrawableSize, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawOval(circleRect, paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    @SuppressLint({"NewApi"})
    private void setBackgroundCompat(Drawable drawable) {
        if (VERSION.SDK_INT >= 16) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }
}
