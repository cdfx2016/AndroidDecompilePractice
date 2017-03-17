package com.jude.rollviewpager.hintview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.jude.rollviewpager.HintView;

public abstract class ShapeHintView extends LinearLayout implements HintView {
    private Drawable dot_focus;
    private Drawable dot_normal;
    private int lastPosition = 0;
    private int length = 0;
    private ImageView[] mDots;

    public abstract Drawable makeFocusDrawable();

    public abstract Drawable makeNormalDrawable();

    public ShapeHintView(Context context) {
        super(context);
    }

    public ShapeHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(int length, int gravity) {
        removeAllViews();
        this.lastPosition = 0;
        setOrientation(0);
        switch (gravity) {
            case 0:
                setGravity(19);
                break;
            case 1:
                setGravity(17);
                break;
            case 2:
                setGravity(21);
                break;
        }
        this.length = length;
        this.mDots = new ImageView[length];
        this.dot_focus = makeFocusDrawable();
        this.dot_normal = makeNormalDrawable();
        for (int i = 0; i < length; i++) {
            this.mDots[i] = new ImageView(getContext());
            LayoutParams dotlp = new LayoutParams(-2, -2);
            dotlp.setMargins(10, 0, 10, 0);
            this.mDots[i].setLayoutParams(dotlp);
            this.mDots[i].setBackgroundDrawable(this.dot_normal);
            addView(this.mDots[i]);
        }
        setCurrent(0);
    }

    public void setCurrent(int current) {
        if (current >= 0 && current <= this.length - 1) {
            this.mDots[this.lastPosition].setBackgroundDrawable(this.dot_normal);
            this.mDots[current].setBackgroundDrawable(this.dot_focus);
            this.lastPosition = current;
        }
    }
}
