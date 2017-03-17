package com.fanyu.boundless.widget.tagview;

import android.view.View;

public interface OnTagClickListener {
    void onTagClick(Tag tag, int i);

    void onTagClick(Tag tag, View view, int i);
}
