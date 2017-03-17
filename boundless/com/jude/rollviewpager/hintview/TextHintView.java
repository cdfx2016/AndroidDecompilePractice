package com.jude.rollviewpager.hintview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.jude.rollviewpager.HintView;

public class TextHintView extends TextView implements HintView {
    private int length;

    public TextHintView(Context context) {
        super(context);
    }

    public TextHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(int length, int gravity) {
        this.length = length;
        setTextColor(-1);
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
        setCurrent(0);
    }

    public void setCurrent(int current) {
        setText((current + 1) + "/" + this.length);
    }
}
