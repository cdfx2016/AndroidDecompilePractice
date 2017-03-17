package com.fanyu.boundless.widget.tagview;

import android.graphics.Color;

public class Tag {
    public String deleteIcon;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public int id;
    private boolean isChecked = false;
    public boolean isDeletable;
    public int layoutColor;
    public int layoutColorPress;
    public float radius;
    public int tagTextColor;
    public float tagTextSize;
    public String text;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean paramBoolean) {
        this.isChecked = paramBoolean;
    }

    public Tag(String text) {
        init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    public Tag(String text, int color) {
        init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, color, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    public Tag(String text, String color) {
        init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, Color.parseColor(color), Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    public Tag(int id, String text) {
        init(id, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    public Tag(int id, String text, int color) {
        init(id, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, color, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    public Tag(int id, String text, String color) {
        init(id, text, Constants.DEFAULT_TAG_TEXT_COLOR, 5.0f, Color.parseColor(color), Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, true, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 7.0f, 7.0f, "×");
    }

    private void init(int id, String text, int tagTextColor, float tagTextSize, int layout_color, int layout_color_press, boolean isDeletable, int deleteIndicatorColor, float deleteIndicatorSize, float radius, String deleteIcon) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layout_color;
        this.layoutColorPress = layout_color_press;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
    }
}
