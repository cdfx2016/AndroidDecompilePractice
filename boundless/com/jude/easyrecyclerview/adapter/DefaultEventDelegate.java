package com.jude.easyrecyclerview.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.ItemView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnErrorListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnMoreListener;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter.OnNoMoreListener;

public class DefaultEventDelegate implements EventDelegate {
    private static final int STATUS_ERROR = 732;
    private static final int STATUS_INITIAL = 291;
    private static final int STATUS_MORE = 260;
    private static final int STATUS_NOMORE = 408;
    private RecyclerArrayAdapter adapter;
    private EventFooter footer;
    private boolean hasData = false;
    private boolean hasError = false;
    private boolean hasMore = false;
    private boolean hasNoMore = false;
    private boolean isLoadingMore = false;
    private OnErrorListener onErrorListener;
    private OnMoreListener onMoreListener;
    private OnNoMoreListener onNoMoreListener;
    private int status = STATUS_INITIAL;

    private class EventFooter implements ItemView {
        public static final int Hide = 0;
        public static final int ShowError = 2;
        public static final int ShowMore = 1;
        public static final int ShowNoMore = 3;
        private View errorView = null;
        private int errorViewRes = 0;
        private int flag = 0;
        private View moreView = null;
        private int moreViewRes = 0;
        private View noMoreView = null;
        private int noMoreViewRes = 0;
        public boolean skipError = false;
        public boolean skipNoMore = false;

        public View onCreateView(ViewGroup parent) {
            DefaultEventDelegate.log("onCreateView");
            return refreshStatus(parent);
        }

        public void onBindView(View headerView) {
            DefaultEventDelegate.log("onBindView");
            headerView.post(new Runnable() {
                public void run() {
                    switch (EventFooter.this.flag) {
                        case 1:
                            DefaultEventDelegate.this.onMoreViewShowed();
                            return;
                        case 2:
                            if (!EventFooter.this.skipError) {
                                DefaultEventDelegate.this.onErrorViewShowed();
                            }
                            EventFooter.this.skipError = false;
                            return;
                        case 3:
                            if (!EventFooter.this.skipNoMore) {
                                DefaultEventDelegate.this.onNoMoreViewShowed();
                            }
                            EventFooter.this.skipNoMore = false;
                            return;
                        default:
                            return;
                    }
                }
            });
        }

        public View refreshStatus(ViewGroup parent) {
            View view = null;
            switch (this.flag) {
                case 1:
                    if (this.moreView != null) {
                        view = this.moreView;
                    } else if (this.moreViewRes != 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(this.moreViewRes, parent, false);
                    }
                    if (view != null) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                DefaultEventDelegate.this.onMoreViewClicked();
                            }
                        });
                        break;
                    }
                    break;
                case 2:
                    if (this.errorView != null) {
                        view = this.errorView;
                    } else if (this.errorViewRes != 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(this.errorViewRes, parent, false);
                    }
                    if (view != null) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                DefaultEventDelegate.this.onErrorViewClicked();
                            }
                        });
                        break;
                    }
                    break;
                case 3:
                    if (this.noMoreView != null) {
                        view = this.noMoreView;
                    } else if (this.noMoreViewRes != 0) {
                        view = LayoutInflater.from(parent.getContext()).inflate(this.noMoreViewRes, parent, false);
                    }
                    if (view != null) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                DefaultEventDelegate.this.onNoMoreViewClicked();
                            }
                        });
                        break;
                    }
                    break;
            }
            if (view == null) {
                return new FrameLayout(parent.getContext());
            }
            return view;
        }

        public void showError() {
            DefaultEventDelegate.log("footer showError");
            this.skipError = true;
            this.flag = 2;
            if (DefaultEventDelegate.this.adapter.getItemCount() > 0) {
                DefaultEventDelegate.this.adapter.notifyItemChanged(DefaultEventDelegate.this.adapter.getItemCount() - 1);
            }
        }

        public void showMore() {
            DefaultEventDelegate.log("footer showMore");
            this.flag = 1;
            if (DefaultEventDelegate.this.adapter.getItemCount() > 0) {
                DefaultEventDelegate.this.adapter.notifyItemChanged(DefaultEventDelegate.this.adapter.getItemCount() - 1);
            }
        }

        public void showNoMore() {
            DefaultEventDelegate.log("footer showNoMore");
            this.skipNoMore = true;
            this.flag = 3;
            if (DefaultEventDelegate.this.adapter.getItemCount() > 0) {
                DefaultEventDelegate.this.adapter.notifyItemChanged(DefaultEventDelegate.this.adapter.getItemCount() - 1);
            }
        }

        public void hide() {
            DefaultEventDelegate.log("footer hide");
            this.flag = 0;
            if (DefaultEventDelegate.this.adapter.getItemCount() > 0) {
                DefaultEventDelegate.this.adapter.notifyItemChanged(DefaultEventDelegate.this.adapter.getItemCount() - 1);
            }
        }

        public void setMoreView(View moreView) {
            this.moreView = moreView;
            this.moreViewRes = 0;
        }

        public void setNoMoreView(View noMoreView) {
            this.noMoreView = noMoreView;
            this.noMoreViewRes = 0;
        }

        public void setErrorView(View errorView) {
            this.errorView = errorView;
            this.errorViewRes = 0;
        }

        public void setMoreViewRes(int moreViewRes) {
            this.moreView = null;
            this.moreViewRes = moreViewRes;
        }

        public void setNoMoreViewRes(int noMoreViewRes) {
            this.noMoreView = null;
            this.noMoreViewRes = noMoreViewRes;
        }

        public void setErrorViewRes(int errorViewRes) {
            this.errorView = null;
            this.errorViewRes = errorViewRes;
        }

        public int hashCode() {
            return this.flag + 13589;
        }
    }

    public DefaultEventDelegate(RecyclerArrayAdapter adapter) {
        this.adapter = adapter;
        this.footer = new EventFooter();
        adapter.addFooter(this.footer);
    }

    public void onMoreViewShowed() {
        log("onMoreViewShowed");
        if (!this.isLoadingMore && this.onMoreListener != null) {
            this.isLoadingMore = true;
            this.onMoreListener.onMoreShow();
        }
    }

    public void onMoreViewClicked() {
        if (this.onMoreListener != null) {
            this.onMoreListener.onMoreClick();
        }
    }

    public void onErrorViewShowed() {
        if (this.onErrorListener != null) {
            this.onErrorListener.onErrorShow();
        }
    }

    public void onErrorViewClicked() {
        if (this.onErrorListener != null) {
            this.onErrorListener.onErrorClick();
        }
    }

    public void onNoMoreViewShowed() {
        if (this.onNoMoreListener != null) {
            this.onNoMoreListener.onNoMoreShow();
        }
    }

    public void onNoMoreViewClicked() {
        if (this.onNoMoreListener != null) {
            this.onNoMoreListener.onNoMoreClick();
        }
    }

    public void addData(int length) {
        log("addData" + length);
        if (this.hasMore) {
            if (length != 0) {
                this.footer.showMore();
                this.status = STATUS_MORE;
                this.hasData = true;
            } else if (this.status == STATUS_INITIAL || this.status == STATUS_MORE) {
                this.footer.showNoMore();
                this.status = STATUS_NOMORE;
            }
        } else if (this.hasNoMore) {
            this.footer.showNoMore();
            this.status = STATUS_NOMORE;
        }
        this.isLoadingMore = false;
    }

    public void clear() {
        log("clear");
        this.hasData = false;
        this.status = STATUS_INITIAL;
        this.footer.hide();
        this.isLoadingMore = false;
    }

    public void stopLoadMore() {
        log("stopLoadMore");
        this.footer.showNoMore();
        this.status = STATUS_NOMORE;
        this.isLoadingMore = false;
    }

    public void pauseLoadMore() {
        log("pauseLoadMore");
        this.footer.showError();
        this.status = STATUS_ERROR;
        this.isLoadingMore = false;
    }

    public void resumeLoadMore() {
        this.isLoadingMore = false;
        this.footer.showMore();
        this.status = STATUS_MORE;
        onMoreViewShowed();
    }

    public void setMore(View view, OnMoreListener listener) {
        this.footer.setMoreView(view);
        this.onMoreListener = listener;
        this.hasMore = true;
        if (this.adapter.getCount() > 0) {
            addData(this.adapter.getCount());
        }
        log("setMore");
    }

    public void setNoMore(View view, OnNoMoreListener listener) {
        this.footer.setNoMoreView(view);
        this.onNoMoreListener = listener;
        this.hasNoMore = true;
        log("setNoMore");
    }

    public void setErrorMore(View view, OnErrorListener listener) {
        this.footer.setErrorView(view);
        this.onErrorListener = listener;
        this.hasError = true;
        log("setErrorMore");
    }

    public void setMore(int res, OnMoreListener listener) {
        this.footer.setMoreViewRes(res);
        this.onMoreListener = listener;
        this.hasMore = true;
        if (this.adapter.getCount() > 0) {
            addData(this.adapter.getCount());
        }
        log("setMore");
    }

    public void setNoMore(int res, OnNoMoreListener listener) {
        this.footer.setNoMoreViewRes(res);
        this.onNoMoreListener = listener;
        this.hasNoMore = true;
        log("setNoMore");
    }

    public void setErrorMore(int res, OnErrorListener listener) {
        this.footer.setErrorViewRes(res);
        this.onErrorListener = listener;
        this.hasError = true;
        log("setErrorMore");
    }

    private static void log(String content) {
        if (EasyRecyclerView.DEBUG) {
            Log.i(EasyRecyclerView.TAG, content);
        }
    }
}
