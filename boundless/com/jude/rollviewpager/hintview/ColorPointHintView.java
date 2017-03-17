package com.jude.rollviewpager.hintview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import com.jude.rollviewpager.Util;

public class ColorPointHintView extends ShapeHintView {
    private int focusColor;
    private int normalColor;

    public ColorPointHintView(Context context, int focusColor, int normalColor) {
        super(context);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
    }

    public Drawable makeFocusDrawable() {
        GradientDrawable dot_focus = new GradientDrawable();
        dot_focus.setColor(this.focusColor);
        dot_focus.setCornerRadius((float) Util.dip2px(getContext(), 4.0f));
        dot_focus.setSize(Util.dip2px(getContext(), 8.0f), Util.dip2px(getContext(), 8.0f));
        return dot_focus;
    }

    public Drawable makeNormalDrawable() {
        GradientDrawable dot_normal = new GradientDrawable();
        dot_normal.setColor(this.normalColor);
        dot_normal.setCornerRadius((float) Util.dip2px(getContext(), 4.0f));
        dot_normal.setSize(Util.dip2px(getContext(), 8.0f), Util.dip2px(getContext(), 8.0f));
        return dot_normal;
    }
}
