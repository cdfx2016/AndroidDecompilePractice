package com.jude.easyrecyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class DividerDecoration extends ItemDecoration {
    private ColorDrawable mColorDrawable;
    private boolean mDrawHeaderFooter = false;
    private boolean mDrawLastItem = true;
    private int mHeight;
    private int mPaddingLeft;
    private int mPaddingRight;

    public DividerDecoration(int color, int height) {
        this.mColorDrawable = new ColorDrawable(color);
        this.mHeight = height;
    }

    public DividerDecoration(int color, int height, int paddingLeft, int paddingRight) {
        this.mColorDrawable = new ColorDrawable(color);
        this.mHeight = height;
        this.mPaddingLeft = paddingLeft;
        this.mPaddingRight = paddingRight;
    }

    public void setDrawLastItem(boolean mDrawLastItem) {
        this.mDrawLastItem = mDrawLastItem;
    }

    public void setDrawHeaderFooter(boolean mDrawHeaderFooter) {
        this.mDrawHeaderFooter = mDrawHeaderFooter;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int position = parent.getChildAdapterPosition(view);
        int orientation = 0;
        int headerCount = 0;
        int footerCount = 0;
        if (parent.getAdapter() instanceof RecyclerArrayAdapter) {
            headerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getHeaderCount();
            footerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getFooterCount();
        }
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
        }
        if ((position >= headerCount && position < parent.getAdapter().getItemCount() - footerCount) || this.mDrawHeaderFooter) {
            if (orientation == 1) {
                outRect.bottom = this.mHeight;
            } else {
                outRect.right = this.mHeight;
            }
        }
    }

    public void onDrawOver(Canvas c, RecyclerView parent, State state) {
        if (parent.getAdapter() != null) {
            int dataCount;
            int start;
            int end;
            int orientation = 0;
            int headerCount = 0;
            if (parent.getAdapter() instanceof RecyclerArrayAdapter) {
                headerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getHeaderCount();
                int footerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getFooterCount();
                dataCount = ((RecyclerArrayAdapter) parent.getAdapter()).getCount();
            } else {
                dataCount = parent.getAdapter().getItemCount();
            }
            int dataStartPosition = headerCount;
            int dataEndPosition = headerCount + dataCount;
            LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            } else if (layoutManager instanceof GridLayoutManager) {
                orientation = ((GridLayoutManager) layoutManager).getOrientation();
            } else if (layoutManager instanceof LinearLayoutManager) {
                orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            }
            if (orientation == 1) {
                start = parent.getPaddingLeft() + this.mPaddingLeft;
                end = (parent.getWidth() - parent.getPaddingRight()) - this.mPaddingRight;
            } else {
                start = parent.getPaddingTop() + this.mPaddingLeft;
                end = (parent.getHeight() - parent.getPaddingBottom()) - this.mPaddingRight;
            }
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if ((position >= dataStartPosition && position < dataEndPosition - 1) || ((position == dataEndPosition - 1 && this.mDrawLastItem) || ((position < dataStartPosition || position >= dataEndPosition) && this.mDrawHeaderFooter))) {
                    if (orientation == 1) {
                        int top = child.getBottom() + ((LayoutParams) child.getLayoutParams()).bottomMargin;
                        this.mColorDrawable.setBounds(start, top, end, top + this.mHeight);
                        this.mColorDrawable.draw(c);
                    } else {
                        int left = child.getRight() + ((LayoutParams) child.getLayoutParams()).rightMargin;
                        this.mColorDrawable.setBounds(left, start, left + this.mHeight, end);
                        this.mColorDrawable.draw(c);
                    }
                }
            }
        }
    }
}
