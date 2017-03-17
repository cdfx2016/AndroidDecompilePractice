package com.jude.easyrecyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import java.util.HashMap;
import java.util.Map;

public class StickyHeaderDecoration extends ItemDecoration {
    public static final long NO_HEADER_ID = -1;
    private IStickyHeaderAdapter mAdapter;
    private Map<Long, ViewHolder> mHeaderCache;
    private boolean mIncludeHeader;
    private boolean mRenderInline;

    public interface IStickyHeaderAdapter<T extends ViewHolder> {
        long getHeaderId(int i);

        void onBindHeaderViewHolder(T t, int i);

        T onCreateHeaderViewHolder(ViewGroup viewGroup);
    }

    public StickyHeaderDecoration(IStickyHeaderAdapter adapter) {
        this(adapter, false);
    }

    public StickyHeaderDecoration(IStickyHeaderAdapter adapter, boolean renderInline) {
        this.mIncludeHeader = false;
        this.mAdapter = adapter;
        this.mHeaderCache = new HashMap();
        this.mRenderInline = renderInline;
    }

    public void setIncludeHeader(boolean mIncludeHeader) {
        this.mIncludeHeader = mIncludeHeader;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = 0;
        if (!this.mIncludeHeader && (parent.getAdapter() instanceof RecyclerArrayAdapter)) {
            int headerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getHeaderCount();
            int footerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getFooterCount();
            int dataCount = ((RecyclerArrayAdapter) parent.getAdapter()).getCount();
            if (position < headerCount || position >= headerCount + dataCount) {
                return;
            }
            if (position >= headerCount) {
                position -= headerCount;
            }
        }
        if (position != -1 && hasHeader(position) && showHeaderAboveItem(position)) {
            headerHeight = getHeaderHeightForLayout(getHeader(parent, position).itemView);
        }
        outRect.set(0, headerHeight, 0, 0);
    }

    private boolean showHeaderAboveItem(int itemAdapterPosition) {
        if (itemAdapterPosition != 0 && this.mAdapter.getHeaderId(itemAdapterPosition - 1) == this.mAdapter.getHeaderId(itemAdapterPosition)) {
            return false;
        }
        return true;
    }

    public void clearHeaderCache() {
        this.mHeaderCache.clear();
    }

    public View findHeaderViewUnder(float x, float y) {
        for (ViewHolder holder : this.mHeaderCache.values()) {
            View child = holder.itemView;
            float translationX = ViewCompat.getTranslationX(child);
            float translationY = ViewCompat.getTranslationY(child);
            if (x >= ((float) child.getLeft()) + translationX && x <= ((float) child.getRight()) + translationX && y >= ((float) child.getTop()) + translationY && y <= ((float) child.getBottom()) + translationY) {
                return child;
            }
        }
        return null;
    }

    private boolean hasHeader(int position) {
        return this.mAdapter.getHeaderId(position) != -1;
    }

    private ViewHolder getHeader(RecyclerView parent, int position) {
        long key = this.mAdapter.getHeaderId(position);
        if (this.mHeaderCache.containsKey(Long.valueOf(key))) {
            return (ViewHolder) this.mHeaderCache.get(Long.valueOf(key));
        }
        ViewHolder holder = this.mAdapter.onCreateHeaderViewHolder(parent);
        View header = holder.itemView;
        this.mAdapter.onBindHeaderViewHolder(holder, position);
        header.measure(ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), 1073741824), parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width), ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), 0), parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height));
        header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        this.mHeaderCache.put(Long.valueOf(key), holder);
        return holder;
    }

    public void onDrawOver(Canvas canvas, RecyclerView parent, State state) {
        if (parent.getAdapter() != null) {
            int count = parent.getChildCount();
            long previousHeaderId = -1;
            for (int layoutPos = 0; layoutPos < count; layoutPos++) {
                View child = parent.getChildAt(layoutPos);
                int adapterPos = parent.getChildAdapterPosition(child);
                if (!this.mIncludeHeader && (parent.getAdapter() instanceof RecyclerArrayAdapter)) {
                    int headerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getHeaderCount();
                    int footerCount = ((RecyclerArrayAdapter) parent.getAdapter()).getFooterCount();
                    int dataCount = ((RecyclerArrayAdapter) parent.getAdapter()).getCount();
                    if (adapterPos >= headerCount && adapterPos < headerCount + dataCount) {
                        if (adapterPos >= headerCount) {
                            adapterPos -= headerCount;
                        }
                    }
                }
                if (adapterPos != -1 && hasHeader(adapterPos)) {
                    long headerId = this.mAdapter.getHeaderId(adapterPos);
                    if (headerId != previousHeaderId) {
                        previousHeaderId = headerId;
                        View header = getHeader(parent, adapterPos).itemView;
                        canvas.save();
                        int left = child.getLeft();
                        int top = getHeaderTop(parent, child, header, adapterPos, layoutPos);
                        canvas.translate((float) left, (float) top);
                        header.setTranslationX((float) left);
                        header.setTranslationY((float) top);
                        header.draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
    }

    private int getHeaderTop(RecyclerView parent, View child, View header, int adapterPos, int layoutPos) {
        int headerHeight = getHeaderHeightForLayout(header);
        int top = ((int) child.getY()) - headerHeight;
        if (layoutPos == 0) {
            int count = parent.getChildCount();
            long currentId = this.mAdapter.getHeaderId(adapterPos);
            int i = 1;
            while (i < count) {
                int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i));
                if (adapterPosHere == -1 || this.mAdapter.getHeaderId(adapterPosHere) == currentId) {
                    i++;
                } else {
                    int offset = ((int) parent.getChildAt(i).getY()) - (getHeader(parent, adapterPosHere).itemView.getHeight() + headerHeight);
                    if (offset < 0) {
                        return offset;
                    }
                    top = Math.max(0, top);
                }
            }
            top = Math.max(0, top);
        }
        return top;
    }

    private int getHeaderHeightForLayout(View header) {
        return this.mRenderInline ? 0 : header.getHeight();
    }
}
