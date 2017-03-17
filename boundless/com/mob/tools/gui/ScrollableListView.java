package com.mob.tools.gui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ListView;
import com.mob.tools.gui.Scrollable.OnScrollListener;

public class ScrollableListView extends ListView implements Scrollable {
    private OnScrollListener osListener;
    private boolean pullEnable;

    public ScrollableListView(Context context) {
        super(context);
        init(context);
    }

    public ScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setCacheColorHint(0);
        setSelector(new ColorDrawable());
        setDividerHeight(0);
        this.osListener = new OnScrollListener() {
            public void onScrollChanged(Scrollable scrollable, int l, int t, int oldl, int oldt) {
                ScrollableListView scrollableListView = ScrollableListView.this;
                boolean z = t <= 0 && oldt <= 0;
                scrollableListView.pullEnable = z;
            }
        };
    }

    public boolean isReadyToPull() {
        return this.pullEnable;
    }

    protected int computeVerticalScrollOffset() {
        int offset = super.computeVerticalScrollOffset();
        if (this.osListener != null) {
            this.osListener.onScrollChanged(this, 0, offset, 0, 0);
        }
        return offset;
    }
}
