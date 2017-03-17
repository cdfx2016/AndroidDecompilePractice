package com.jude.easyrecyclerview.adapter;

import android.view.View;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnErrorListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnMoreListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnNoMoreListener;

public interface EventDelegate {
    void addData(int i);

    void clear();

    void pauseLoadMore();

    void resumeLoadMore();

    void setErrorMore(int i, OnErrorListener onErrorListener);

    void setErrorMore(View view, OnErrorListener onErrorListener);

    void setMore(int i, OnMoreListener onMoreListener);

    void setMore(View view, OnMoreListener onMoreListener);

    void setNoMore(int i, OnNoMoreListener onNoMoreListener);

    void setNoMore(View view, OnNoMoreListener onNoMoreListener);

    void stopLoadMore();
}
