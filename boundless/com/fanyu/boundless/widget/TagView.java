package com.fanyu.boundless.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.util.ArrayList;
import java.util.List;

public class TagView extends RelativeLayout {
    int lineMargin;
    private OnTagClickListener mClickListener;
    private OnTagDeleteListener mDeleteListener;
    private LayoutInflater mInflater;
    private boolean mInitialized = false;
    private List<Tag> mTags = new ArrayList();
    private ViewTreeObserver mViewTreeObserber;
    private int mWidth;
    int tagMargin;
    int texPaddingBottom;
    int textPaddingLeft;
    int textPaddingRight;
    int textPaddingTop;

    public TagView(Context ctx) {
        super(ctx, null);
        initialize(ctx, null, 0);
    }

    public TagView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initialize(ctx, attrs, 0);
    }

    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initialize(ctx, attrs, defStyle);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        this.mInflater = (LayoutInflater) ctx.getSystemService("layout_inflater");
        this.mViewTreeObserber = getViewTreeObserver();
        this.mViewTreeObserber.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (!TagView.this.mInitialized) {
                    TagView.this.mInitialized = true;
                    TagView.this.drawTags();
                }
            }
        });
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);
        this.lineMargin = (int) typeArray.getDimension(0, (float) Utils.dipToPx(getContext(), 5.0f));
        this.tagMargin = (int) typeArray.getDimension(1, (float) Utils.dipToPx(getContext(), 5.0f));
        this.textPaddingLeft = (int) typeArray.getDimension(2, (float) Utils.dipToPx(getContext(), 6.0f));
        this.textPaddingRight = (int) typeArray.getDimension(3, (float) Utils.dipToPx(getContext(), 6.0f));
        this.textPaddingTop = (int) typeArray.getDimension(4, (float) Utils.dipToPx(getContext(), 5.0f));
        this.texPaddingBottom = (int) typeArray.getDimension(5, (float) Utils.dipToPx(getContext(), 5.0f));
        typeArray.recycle();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mWidth = w;
    }

    @SuppressLint({"InflateParams"})
    private void drawTags() {
        if (this.mInitialized) {
            removeAllViews();
            float total = (float) (getPaddingLeft() + getPaddingRight());
            int listIndex = 1;
            int index_bottom = 1;
            int index_header = 1;
            Tag tag_pre = null;
            for (Tag item : this.mTags) {
                float tagWidth;
                final int position = listIndex - 1;
                final Tag tag = item;
                View tagLayout = this.mInflater.inflate(R.layout.tagview, null);
                tagLayout.setId(listIndex);
                TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
                tagView.setText(tag.text);
                tagLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (TagView.this.mClickListener != null) {
                            TagView.this.mClickListener.onTagClick(tag, position);
                        }
                    }
                });
                if (item.isChecked()) {
                    tagView.setTextColor(Color.parseColor("#ffffff"));
                    tagView.setBackgroundResource(R.drawable.bianse);
                } else {
                    tagView.setTextColor(Color.parseColor("#d29846"));
                    tagView.setBackgroundResource(R.drawable.miaobian);
                }
                if (this.mWidth < 700) {
                    tagWidth = (float) ((this.textPaddingLeft + 150) + this.textPaddingRight);
                } else {
                    tagWidth = (float) ((this.textPaddingLeft + PsExtractor.VIDEO_STREAM_MASK) + this.textPaddingRight);
                }
                LayoutParams tagParams = new LayoutParams(-2, -2);
                tagParams.bottomMargin = this.lineMargin;
                if (((float) this.mWidth) <= (total + tagWidth) + ((float) Utils.dipToPx(getContext(), 2.0f))) {
                    tagParams.addRule(3, index_bottom);
                    total = (float) (getPaddingLeft() + getPaddingRight());
                    index_bottom = listIndex;
                    index_header = listIndex;
                } else {
                    tagParams.addRule(6, index_header);
                    if (listIndex != index_header) {
                        tagParams.addRule(1, listIndex - 1);
                        tagParams.leftMargin = this.tagMargin;
                        total += (float) this.tagMargin;
                        if (tag_pre != null && tag_pre.tagTextSize < tag.tagTextSize) {
                            index_bottom = listIndex;
                        }
                    }
                }
                total += tagWidth;
                addView(tagLayout, tagParams);
                tag_pre = tag;
                listIndex++;
            }
        }
    }

    private StateListDrawable getSelector(Tag tag) {
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gd_normal = new GradientDrawable();
        gd_normal.setColor(tag.layoutColor);
        gd_normal.setCornerRadius(tag.radius);
        GradientDrawable gd_press = new GradientDrawable();
        gd_press.setColor(tag.layoutColorPress);
        gd_press.setCornerRadius(tag.radius);
        states.addState(new int[]{16842919}, gd_press);
        states.addState(new int[0], gd_normal);
        return states;
    }

    public void add(Tag tag) {
        this.mTags.add(tag);
        drawTags();
    }

    public void clear() {
        this.mTags.clear();
    }

    public void addone(Tag tag) {
        this.mTags.add(this.mTags.size() - 1, tag);
        drawTags();
    }

    public void refresh(Tag tag) {
        drawTags();
    }

    public List<Tag> getTags() {
        return this.mTags;
    }

    public void remove(int position) {
        this.mTags.remove(position);
        drawTags();
    }

    public int getLineMargin() {
        return this.lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = Utils.dipToPx(getContext(), lineMargin);
    }

    public int getTagMargin() {
        return this.tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = Utils.dipToPx(getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return this.textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = Utils.dipToPx(getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return this.textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = Utils.dipToPx(getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return this.textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = Utils.dipToPx(getContext(), textPaddingTop);
    }

    public int getTexPaddingBottom() {
        return this.texPaddingBottom;
    }

    public void setTexPaddingBottom(float texPaddingBottom) {
        this.texPaddingBottom = Utils.dipToPx(getContext(), texPaddingBottom);
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        this.mDeleteListener = deleteListener;
    }
}
