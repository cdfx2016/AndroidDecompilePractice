package com.fanyu.boundless.widget.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.view.View.MeasureSpec;
import java.lang.reflect.Field;

public class FullyLinearLayoutManager extends LinearLayoutManager {
    private static final int CHILD_HEIGHT = 1;
    private static final int CHILD_WIDTH = 0;
    private static final int DEFAULT_CHILD_SIZE = 100;
    private static boolean canMakeInsetsDirty = true;
    private static Field insetsDirtyField = null;
    private final int[] childDimensions;
    private int childSize;
    private boolean hasChildSize;
    private boolean isScrollEnabled;
    private int overScrollMode;
    private final Rect tmpRect;
    private final RecyclerView view;

    public FullyLinearLayoutManager(Context context) {
        super(context);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.isScrollEnabled = true;
        this.view = null;
    }

    public FullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.isScrollEnabled = true;
        this.view = null;
    }

    public FullyLinearLayoutManager(RecyclerView view) {
        super(view.getContext());
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.isScrollEnabled = true;
        this.view = view;
        this.overScrollMode = ViewCompat.getOverScrollMode(view);
    }

    public FullyLinearLayoutManager(RecyclerView view, int orientation, boolean reverseLayout) {
        super(view.getContext(), orientation, reverseLayout);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.isScrollEnabled = true;
        this.view = view;
        this.overScrollMode = ViewCompat.getOverScrollMode(view);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    public boolean canScrollVertically() {
        return this.isScrollEnabled && super.canScrollVertically();
    }

    public void setOverScrollMode(int overScrollMode) {
        if (overScrollMode < 0 || overScrollMode > 2) {
            throw new IllegalArgumentException("Unknown overscroll mode: " + overScrollMode);
        } else if (this.view == null) {
            throw new IllegalStateException("view == null");
        } else {
            this.overScrollMode = overScrollMode;
            ViewCompat.setOverScrollMode(this.view, overScrollMode);
        }
    }

    public static int makeUnspecifiedSpec() {
        return MeasureSpec.makeMeasureSpec(0, 0);
    }

    public void onMeasure(Recycler recycler, State state, int widthSpec, int heightSpec) {
        int widthMode = MeasureSpec.getMode(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);
        boolean hasWidthSize = widthMode != 0;
        boolean hasHeightSize = heightMode != 0;
        boolean exactWidth = widthMode == 1073741824;
        boolean exactHeight = heightMode == 1073741824;
        int unspecified = makeUnspecifiedSpec();
        if (exactWidth && exactHeight) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }
        boolean vertical = getOrientation() == 1;
        initChildDimensions(widthSize, heightSize, vertical);
        int width = 0;
        int height = 0;
        recycler.clear();
        int stateItemCount = state.getItemCount();
        int adapterItemCount = getItemCount();
        int i = 0;
        while (i < adapterItemCount) {
            if (!vertical) {
                if (!this.hasChildSize && i < stateItemCount) {
                    measureChild(recycler, i, unspecified, heightSize, this.childDimensions);
                }
                width += this.childDimensions[0];
                if (i == 0) {
                    height = this.childDimensions[1];
                }
                if (hasWidthSize && width >= widthSize) {
                    break;
                }
            }
            if (!this.hasChildSize && i < stateItemCount) {
                measureChild(recycler, i, widthSize, unspecified, this.childDimensions);
            }
            height += this.childDimensions[1];
            if (i == 0) {
                width = this.childDimensions[0];
            }
            if (hasHeightSize && height >= heightSize) {
                break;
            }
            i++;
        }
        if (exactWidth) {
            width = widthSize;
        } else {
            width += getPaddingLeft() + getPaddingRight();
            if (hasWidthSize) {
                width = Math.min(width, widthSize);
            }
        }
        if (exactHeight) {
            height = heightSize;
        } else {
            height += getPaddingTop() + getPaddingBottom();
            if (hasHeightSize) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
        if (this.view != null && this.overScrollMode == 1) {
            int i2;
            boolean fit = (vertical && (!hasHeightSize || height < heightSize)) || (!vertical && (!hasWidthSize || width < widthSize));
            View view = this.view;
            if (fit) {
                i2 = 2;
            } else {
                i2 = 0;
            }
            ViewCompat.setOverScrollMode(view, i2);
        }
    }

    private void initChildDimensions(int width, int height, boolean vertical) {
        if (this.childDimensions[0] != 0 || this.childDimensions[1] != 0) {
            return;
        }
        if (vertical) {
            this.childDimensions[0] = width;
            this.childDimensions[1] = this.childSize;
            return;
        }
        this.childDimensions[0] = this.childSize;
        this.childDimensions[1] = height;
    }

    public void setOrientation(int orientation) {
        if (!(this.childDimensions == null || getOrientation() == orientation)) {
            this.childDimensions[0] = 0;
            this.childDimensions[1] = 0;
        }
        super.setOrientation(orientation);
    }

    public void clearChildSize() {
        this.hasChildSize = false;
        setChildSize(100);
    }

    public void setChildSize(int childSize) {
        this.hasChildSize = true;
        if (this.childSize != childSize) {
            this.childSize = childSize;
            requestLayout();
        }
    }

    private void measureChild(Recycler recycler, int position, int widthSize, int heightSize, int[] dimensions) {
        try {
            View child = recycler.getViewForPosition(position);
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();
            int hMargin = p.leftMargin + p.rightMargin;
            int vMargin = p.topMargin + p.bottomMargin;
            makeInsetsDirty(p);
            calculateItemDecorationsForChild(child, this.tmpRect);
            int vDecoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child);
            int i = widthSize;
            child.measure(LayoutManager.getChildMeasureSpec(i, (hPadding + hMargin) + (getRightDecorationWidth(child) + getLeftDecorationWidth(child)), p.width, canScrollHorizontally()), LayoutManager.getChildMeasureSpec(heightSize, (vPadding + vMargin) + vDecoration, p.height, canScrollVertically()));
            dimensions[0] = (getDecoratedMeasuredWidth(child) + p.leftMargin) + p.rightMargin;
            dimensions[1] = (getDecoratedMeasuredHeight(child) + p.bottomMargin) + p.topMargin;
            makeInsetsDirty(p);
            recycler.recycleView(child);
        } catch (IndexOutOfBoundsException e) {
        }
    }

    private static void makeInsetsDirty(LayoutParams p) {
        if (canMakeInsetsDirty) {
            try {
                if (insetsDirtyField == null) {
                    insetsDirtyField = LayoutParams.class.getDeclaredField("mInsetsDirty");
                    insetsDirtyField.setAccessible(true);
                }
                insetsDirtyField.set(p, Boolean.valueOf(true));
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e2) {
            }
        }
    }
}
