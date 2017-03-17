package com.jude.easyrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class RecyclerArrayAdapter<T> extends Adapter<BaseViewHolder> {
    protected ArrayList<ItemView> footers = new ArrayList();
    protected ArrayList<ItemView> headers = new ArrayList();
    private Context mContext;
    protected EventDelegate mEventDelegate;
    protected OnItemClickListener mItemClickListener;
    protected OnItemLongClickListener mItemLongClickListener;
    private final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    protected List<T> mObjects;
    protected RecyclerView mRecyclerView;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface ItemView {
        void onBindView(View view);

        View onCreateView(ViewGroup viewGroup);
    }

    public interface OnMoreListener {
        void onMoreClick();

        void onMoreShow();
    }

    public class GridSpanSizeLookup extends SpanSizeLookup {
        private int mMaxCount;

        public GridSpanSizeLookup(int maxCount) {
            this.mMaxCount = maxCount;
        }

        public int getSpanSize(int position) {
            if (RecyclerArrayAdapter.this.headers.size() != 0 && position < RecyclerArrayAdapter.this.headers.size()) {
                return this.mMaxCount;
            }
            if (RecyclerArrayAdapter.this.footers.size() == 0 || (position - RecyclerArrayAdapter.this.headers.size()) - RecyclerArrayAdapter.this.mObjects.size() < 0) {
                return 1;
            }
            return this.mMaxCount;
        }
    }

    public interface OnErrorListener {
        void onErrorClick();

        void onErrorShow();
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int i);
    }

    public interface OnNoMoreListener {
        void onNoMoreClick();

        void onNoMoreShow();
    }

    private class StateViewHolder extends BaseViewHolder {
        public StateViewHolder(View itemView) {
            super(itemView);
        }
    }

    public abstract BaseViewHolder OnCreateViewHolder(ViewGroup viewGroup, int i);

    public GridSpanSizeLookup obtainGridSpanSizeLookUp(int maxCount) {
        return new GridSpanSizeLookup(maxCount);
    }

    public RecyclerArrayAdapter(Context context) {
        init(context, new ArrayList());
    }

    public RecyclerArrayAdapter(Context context, T[] objects) {
        init(context, Arrays.asList(objects));
    }

    public RecyclerArrayAdapter(Context context, List<T> objects) {
        init(context, objects);
    }

    private void init(Context context, List<T> objects) {
        this.mContext = context;
        this.mObjects = new ArrayList(objects);
    }

    public void stopMore() {
        if (this.mEventDelegate == null) {
            throw new NullPointerException("You should invoking setLoadMore() first");
        }
        this.mEventDelegate.stopLoadMore();
    }

    public void pauseMore() {
        if (this.mEventDelegate == null) {
            throw new NullPointerException("You should invoking setLoadMore() first");
        }
        this.mEventDelegate.pauseLoadMore();
    }

    public void resumeMore() {
        if (this.mEventDelegate == null) {
            throw new NullPointerException("You should invoking setLoadMore() first");
        }
        this.mEventDelegate.resumeLoadMore();
    }

    public void addHeader(ItemView view) {
        if (view == null) {
            throw new NullPointerException("ItemView can't be null");
        }
        this.headers.add(view);
        notifyItemInserted(this.headers.size() - 1);
    }

    public void addFooter(ItemView view) {
        if (view == null) {
            throw new NullPointerException("ItemView can't be null");
        }
        this.footers.add(view);
        notifyItemInserted(((this.headers.size() + getCount()) + this.footers.size()) - 1);
    }

    public void removeAllHeader() {
        int count = this.headers.size();
        this.headers.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void removeAllFooter() {
        int count = this.footers.size();
        this.footers.clear();
        notifyItemRangeRemoved(this.headers.size() + getCount(), count);
    }

    public ItemView getHeader(int index) {
        return (ItemView) this.headers.get(index);
    }

    public ItemView getFooter(int index) {
        return (ItemView) this.footers.get(index);
    }

    public int getHeaderCount() {
        return this.headers.size();
    }

    public int getFooterCount() {
        return this.footers.size();
    }

    public void removeHeader(ItemView view) {
        int position = this.headers.indexOf(view);
        this.headers.remove(view);
        notifyItemRemoved(position);
    }

    public void removeFooter(ItemView view) {
        int position = (this.headers.size() + getCount()) + this.footers.indexOf(view);
        this.footers.remove(view);
        notifyItemRemoved(position);
    }

    EventDelegate getEventDelegate() {
        if (this.mEventDelegate == null) {
            this.mEventDelegate = new DefaultEventDelegate(this);
        }
        return this.mEventDelegate;
    }

    @Deprecated
    public void setMore(int res, final OnLoadMoreListener listener) {
        getEventDelegate().setMore(res, new OnMoreListener() {
            public void onMoreShow() {
                listener.onLoadMore();
            }

            public void onMoreClick() {
            }
        });
    }

    public void setMore(View view, final OnLoadMoreListener listener) {
        getEventDelegate().setMore(view, new OnMoreListener() {
            public void onMoreShow() {
                listener.onLoadMore();
            }

            public void onMoreClick() {
            }
        });
    }

    public void setMore(int res, OnMoreListener listener) {
        getEventDelegate().setMore(res, listener);
    }

    public void setMore(View view, OnMoreListener listener) {
        getEventDelegate().setMore(view, listener);
    }

    public void setNoMore(int res) {
        getEventDelegate().setNoMore(res, null);
    }

    public void setNoMore(View view) {
        getEventDelegate().setNoMore(view, null);
    }

    public void setNoMore(View view, OnNoMoreListener listener) {
        getEventDelegate().setNoMore(view, listener);
    }

    public void setNoMore(int res, OnNoMoreListener listener) {
        getEventDelegate().setNoMore(res, listener);
    }

    public void setError(int res) {
        getEventDelegate().setErrorMore(res, null);
    }

    public void setError(View view) {
        getEventDelegate().setErrorMore(view, null);
    }

    public void setError(int res, OnErrorListener listener) {
        getEventDelegate().setErrorMore(res, listener);
    }

    public void setError(View view, OnErrorListener listener) {
        getEventDelegate().setErrorMore(view, listener);
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        registerAdapterDataObserver(new FixDataObserver(this.mRecyclerView));
    }

    public void add(T object) {
        if (this.mEventDelegate != null) {
            this.mEventDelegate.addData(object == null ? 0 : 1);
        }
        if (object != null) {
            synchronized (this.mLock) {
                this.mObjects.add(object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyItemInserted(this.headers.size() + getCount());
        }
        log("add notifyItemInserted " + (this.headers.size() + getCount()));
    }

    public void addAll(Collection<? extends T> collection) {
        if (this.mEventDelegate != null) {
            this.mEventDelegate.addData(collection == null ? 0 : collection.size());
        }
        if (!(collection == null || collection.size() == 0)) {
            synchronized (this.mLock) {
                this.mObjects.addAll(collection);
            }
        }
        int dataCount = collection == null ? 0 : collection.size();
        if (this.mNotifyOnChange) {
            notifyItemRangeInserted((this.headers.size() + getCount()) - dataCount, dataCount);
        }
        log("addAll notifyItemRangeInserted " + ((this.headers.size() + getCount()) - dataCount) + Constants.ACCEPT_TIME_SEPARATOR_SP + dataCount);
    }

    public void addAll(T[] items) {
        if (this.mEventDelegate != null) {
            this.mEventDelegate.addData(items == null ? 0 : items.length);
        }
        if (!(items == null || items.length == 0)) {
            synchronized (this.mLock) {
                Collections.addAll(this.mObjects, items);
            }
        }
        int dataCount = items == null ? 0 : items.length;
        if (this.mNotifyOnChange) {
            notifyItemRangeInserted((this.headers.size() + getCount()) - dataCount, dataCount);
        }
        log("addAll notifyItemRangeInserted " + ((this.headers.size() + getCount()) - dataCount) + Constants.ACCEPT_TIME_SEPARATOR_SP + dataCount);
    }

    public void insert(T object, int index) {
        synchronized (this.mLock) {
            this.mObjects.add(index, object);
        }
        if (this.mNotifyOnChange) {
            notifyItemInserted(this.headers.size() + index);
        }
        log("insert notifyItemRangeInserted " + (this.headers.size() + index));
    }

    public void insertAll(T[] object, int index) {
        synchronized (this.mLock) {
            this.mObjects.addAll(index, Arrays.asList(object));
        }
        int dataCount = object == null ? 0 : object.length;
        if (this.mNotifyOnChange) {
            notifyItemRangeInserted(this.headers.size() + index, dataCount);
        }
        log("insertAll notifyItemRangeInserted " + (this.headers.size() + index) + Constants.ACCEPT_TIME_SEPARATOR_SP + dataCount);
    }

    public void insertAll(Collection<? extends T> object, int index) {
        synchronized (this.mLock) {
            this.mObjects.addAll(index, object);
        }
        int dataCount = object == null ? 0 : object.size();
        if (this.mNotifyOnChange) {
            notifyItemRangeInserted(this.headers.size() + index, dataCount);
        }
        log("insertAll notifyItemRangeInserted " + (this.headers.size() + index) + Constants.ACCEPT_TIME_SEPARATOR_SP + dataCount);
    }

    public void update(T object, int pos) {
        synchronized (this.mLock) {
            this.mObjects.set(pos, object);
        }
        if (this.mNotifyOnChange) {
            notifyItemChanged(pos);
        }
        log("insertAll notifyItemChanged " + pos);
    }

    public void remove(T object) {
        int position = this.mObjects.indexOf(object);
        synchronized (this.mLock) {
            if (this.mObjects.remove(object)) {
                if (this.mNotifyOnChange) {
                    notifyItemRemoved(this.headers.size() + position);
                }
                log("remove notifyItemRemoved " + (this.headers.size() + position));
            }
        }
    }

    public void remove(int position) {
        synchronized (this.mLock) {
            this.mObjects.remove(position);
        }
        if (this.mNotifyOnChange) {
            notifyItemRemoved(this.headers.size() + position);
        }
        log("remove notifyItemRemoved " + (this.headers.size() + position));
    }

    public void removeAll() {
        int count = this.mObjects.size();
        if (this.mEventDelegate != null) {
            this.mEventDelegate.clear();
        }
        synchronized (this.mLock) {
            this.mObjects.clear();
        }
        if (this.mNotifyOnChange) {
            notifyItemRangeRemoved(this.headers.size(), count);
        }
        log("clear notifyItemRangeRemoved " + this.headers.size() + Constants.ACCEPT_TIME_SEPARATOR_SP + count);
    }

    public void clear() {
        int count = this.mObjects.size();
        if (this.mEventDelegate != null) {
            this.mEventDelegate.clear();
        }
        synchronized (this.mLock) {
            this.mObjects.clear();
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
        log("clear notifyItemRangeRemoved " + this.headers.size() + Constants.ACCEPT_TIME_SEPARATOR_SP + count);
    }

    public void sort(Comparator<? super T> comparator) {
        synchronized (this.mLock) {
            Collections.sort(this.mObjects, comparator);
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.mNotifyOnChange = notifyOnChange;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void setContext(Context ctx) {
        this.mContext = ctx;
    }

    @Deprecated
    public final int getItemCount() {
        return (this.mObjects.size() + this.headers.size()) + this.footers.size();
    }

    public int getCount() {
        return this.mObjects.size();
    }

    private View createSpViewByType(ViewGroup parent, int viewType) {
        Iterator it = this.headers.iterator();
        while (it.hasNext()) {
            LayoutParams layoutParams;
            ItemView headerView = (ItemView) it.next();
            if (headerView.hashCode() == viewType) {
                View view;
                view = headerView.onCreateView(parent);
                if (view.getLayoutParams() != null) {
                    layoutParams = new LayoutParams(view.getLayoutParams());
                } else {
                    layoutParams = new LayoutParams(-1, -2);
                }
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        it = this.footers.iterator();
        while (it.hasNext()) {
            ItemView footerview = (ItemView) it.next();
            if (footerview.hashCode() == viewType) {
                view = footerview.onCreateView(parent);
                if (view.getLayoutParams() != null) {
                    layoutParams = new LayoutParams(view.getLayoutParams());
                } else {
                    layoutParams = new LayoutParams(-1, -2);
                }
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        return null;
    }

    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createSpViewByType(parent, viewType);
        if (view != null) {
            return new StateViewHolder(view);
        }
        final BaseViewHolder viewHolder = OnCreateViewHolder(parent, viewType);
        if (this.mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    RecyclerArrayAdapter.this.mItemClickListener.onItemClick(viewHolder.getAdapterPosition() - RecyclerArrayAdapter.this.headers.size());
                }
            });
        }
        if (this.mItemLongClickListener == null) {
            return viewHolder;
        }
        viewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                return RecyclerArrayAdapter.this.mItemLongClickListener.onItemLongClick(viewHolder.getAdapterPosition() - RecyclerArrayAdapter.this.headers.size());
            }
        });
        return viewHolder;
    }

    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setId(position);
        if (this.headers.size() == 0 || position >= this.headers.size()) {
            int i = (position - this.headers.size()) - this.mObjects.size();
            if (this.footers.size() == 0 || i < 0) {
                OnBindViewHolder(holder, position - this.headers.size());
                return;
            } else {
                ((ItemView) this.footers.get(i)).onBindView(holder.itemView);
                return;
            }
        }
        ((ItemView) this.headers.get(position)).onBindView(holder.itemView);
    }

    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    @Deprecated
    public final int getItemViewType(int position) {
        if (this.headers.size() != 0 && position < this.headers.size()) {
            return ((ItemView) this.headers.get(position)).hashCode();
        }
        if (this.footers.size() != 0) {
            int i = (position - this.headers.size()) - this.mObjects.size();
            if (i >= 0) {
                return ((ItemView) this.footers.get(i)).hashCode();
            }
        }
        return getViewType(position - this.headers.size());
    }

    public int getViewType(int position) {
        return 0;
    }

    public List<T> getAllData() {
        return new ArrayList(this.mObjects);
    }

    public T getItem(int position) {
        return this.mObjects.get(position);
    }

    public int getPosition(T item) {
        return this.mObjects.indexOf(item);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    private static void log(String content) {
        if (EasyRecyclerView.DEBUG) {
            Log.i(EasyRecyclerView.TAG, content);
        }
    }
}
