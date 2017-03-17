package cn.finalteam.galleryfinal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GFImageView extends ImageView {
    private OnImageViewListener mOnImageViewListener;

    public interface OnImageViewListener {
        void onAttach();

        void onDetach();

        void onDraw(Canvas canvas);

        boolean verifyDrawable(Drawable drawable);
    }

    public GFImageView(Context context) {
        super(context);
    }

    public GFImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GFImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnImageViewListener(OnImageViewListener listener) {
        this.mOnImageViewListener = listener;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mOnImageViewListener != null) {
            this.mOnImageViewListener.onDetach();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mOnImageViewListener != null) {
            this.mOnImageViewListener.onAttach();
        }
    }

    protected boolean verifyDrawable(Drawable dr) {
        if (this.mOnImageViewListener == null || !this.mOnImageViewListener.verifyDrawable(dr)) {
            return super.verifyDrawable(dr);
        }
        return true;
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.mOnImageViewListener != null) {
            this.mOnImageViewListener.onDetach();
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.mOnImageViewListener != null) {
            this.mOnImageViewListener.onAttach();
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mOnImageViewListener != null) {
            this.mOnImageViewListener.onDraw(canvas);
        }
    }
}
