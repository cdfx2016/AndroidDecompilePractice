package com.jude.easyrecyclerview.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams;
import android.view.View;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class SpaceDecoration extends ItemDecoration {
    private int footerCount = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    private int headerCount = -1;
    private boolean mPaddingEdgeSide = true;
    private boolean mPaddingHeaderFooter = false;
    private boolean mPaddingStart = true;
    private int space;

    public SpaceDecoration(int space) {
        this.space = space;
    }

    public void setPaddingEdgeSide(boolean mPaddingEdgeSide) {
        this.mPaddingEdgeSide = mPaddingEdgeSide;
    }

    public void setPaddingStart(boolean mPaddingStart) {
        this.mPaddingStart = mPaddingStart;
    }

    public void setPaddingHeaderFooter(boolean mPaddingHeaderFooter) {
        this.mPaddingHeaderFooter = mPaddingHeaderFooter;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanCount = 0;
        int orientation = 0;
        int spanIndex = 0;
        int headerCount = 0;
        int footerCount = 0;
        if (parent.getAdapter() instanceof RecyclerArrayAdapter) {
            headerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getHeaderCount();
            footerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getFooterCount();
        }
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            spanIndex = ((LayoutParams) view.getLayoutParams()).getSpanIndex();
        } else if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            spanIndex = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
        } else if (layoutManager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            spanCount = 1;
            spanIndex = 0;
        }
        if (position < headerCount || position >= parent.getAdapter().getItemCount() - footerCount) {
            if (!this.mPaddingHeaderFooter) {
                return;
            }
            int i;
            if (orientation == 1) {
                i = this.mPaddingEdgeSide ? this.space : 0;
                outRect.left = i;
                outRect.right = i;
                outRect.top = this.mPaddingStart ? this.space : 0;
                return;
            }
            i = this.mPaddingEdgeSide ? this.space : 0;
            outRect.bottom = i;
            outRect.top = i;
            outRect.left = this.mPaddingStart ? this.space : 0;
        } else if (orientation == 1) {
            float expectedWidth = ((float) (parent.getWidth() - (((this.mPaddingEdgeSide ? 1 : -1) + spanCount) * this.space))) / ((float) spanCount);
            float originWidth = ((float) parent.getWidth()) / ((float) spanCount);
            outRect.left = (int) ((((float) (this.mPaddingEdgeSide ? this.space : 0)) + ((((float) this.space) + expectedWidth) * ((float) spanIndex))) - (originWidth * ((float) spanIndex)));
            outRect.right = (int) ((originWidth - ((float) outRect.left)) - expectedWidth);
            if (position - headerCount < spanCount && this.mPaddingStart) {
                outRect.top = this.space;
            }
            outRect.bottom = this.space;
        } else {
            float expectedHeight = ((float) (parent.getHeight() - (((this.mPaddingEdgeSide ? 1 : -1) + spanCount) * this.space))) / ((float) spanCount);
            float originHeight = ((float) parent.getHeight()) / ((float) spanCount);
            outRect.bottom = (int) ((((float) (this.mPaddingEdgeSide ? this.space : 0)) + ((((float) this.space) + expectedHeight) * ((float) spanIndex))) - (originHeight * ((float) spanIndex)));
            outRect.top = (int) ((originHeight - ((float) outRect.bottom)) - expectedHeight);
            if (position - headerCount < spanCount && this.mPaddingStart) {
                outRect.left = this.space;
            }
            outRect.right = this.space;
        }
    }
}
