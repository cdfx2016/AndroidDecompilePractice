package com.fanyu.boundless.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class MyLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG = MyLinearLayoutManager.class.getSimpleName();
    private boolean isScrollEnabled;
    private int[] mMeasuredDimension;

    public MyLinearLayoutManager(Context context) {
        super(context);
        this.isScrollEnabled = true;
        this.mMeasuredDimension = new int[2];
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.isScrollEnabled = true;
        this.mMeasuredDimension = new int[2];
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    public boolean canScrollVertically() {
        return this.isScrollEnabled && super.canScrollVertically();
    }

    public void onMeasure(Recycler recycler, State state, int widthSpec, int heightSpec) {
        int widthMode = MeasureSpec.getMode(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i, MeasureSpec.makeMeasureSpec(i, 0), MeasureSpec.makeMeasureSpec(i, 0), this.mMeasuredDimension);
            if (getOrientation() == 0) {
                width += this.mMeasuredDimension[0];
                if (i == 0) {
                    height = this.mMeasuredDimension[1];
                }
            } else {
                height += this.mMeasuredDimension[1];
                if (i == 0) {
                    width = this.mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode) {
            case 1073741824:
                width = widthSize;
                break;
        }
        switch (heightMode) {
            case 1073741824:
                height = heightSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        try {
            View view = recycler.getViewForPosition(0);
            if (view != null) {
                LayoutParams p = (LayoutParams) view.getLayoutParams();
                view.measure(ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width), ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height));
                measuredDimension[0] = (view.getMeasuredWidth() + p.leftMargin) + p.rightMargin;
                measuredDimension[1] = (view.getMeasuredHeight() + p.bottomMargin) + p.topMargin;
                recycler.recycleView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
