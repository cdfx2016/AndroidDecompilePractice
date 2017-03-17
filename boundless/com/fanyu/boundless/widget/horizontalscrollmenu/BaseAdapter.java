package com.fanyu.boundless.widget.horizontalscrollmenu;

import android.view.View;
import java.util.List;

public abstract class BaseAdapter {
    private HorizontalScrollMenu mHorizontalScrollMenu;

    public abstract List<View> getContentViews();

    public abstract List<String> getMenuItems();

    public abstract void onPageChanged(int i, boolean z);

    public void setHorizontalScrollMenu(HorizontalScrollMenu horizontalScrollMenu) {
        this.mHorizontalScrollMenu = horizontalScrollMenu;
    }

    public void notifyDataSetChanged() {
        this.mHorizontalScrollMenu.notifyDataSetChanged(this);
    }
}
