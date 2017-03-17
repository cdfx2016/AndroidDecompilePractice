package com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownInfo;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownLoadListener.DownloadProgressListener;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownState;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.HttpDownManager;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.HttpDownOnNextListener;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DbDwonUtil;
import java.lang.ref.SoftReference;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ProgressDownSubscriber<T> extends Subscriber<T> implements DownloadProgressListener {
    private DownInfo downInfo;
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;

    public ProgressDownSubscriber(DownInfo downInfo) {
        this.mSubscriberOnNextListener = new SoftReference(downInfo.getListener());
        this.downInfo = downInfo;
    }

    public void setDownInfo(DownInfo downInfo) {
        this.mSubscriberOnNextListener = new SoftReference(downInfo.getListener());
        this.downInfo = downInfo;
    }

    public void onStart() {
        if (this.mSubscriberOnNextListener.get() != null) {
            ((HttpDownOnNextListener) this.mSubscriberOnNextListener.get()).onStart();
        }
        this.downInfo.setState(DownState.START);
    }

    public void onCompleted() {
        if (this.mSubscriberOnNextListener.get() != null) {
            ((HttpDownOnNextListener) this.mSubscriberOnNextListener.get()).onComplete();
        }
        HttpDownManager.getInstance().remove(this.downInfo);
        this.downInfo.setState(DownState.FINISH);
        DbDwonUtil.getInstance().update(this.downInfo);
    }

    public void onError(Throwable e) {
        if (this.mSubscriberOnNextListener.get() != null) {
            ((HttpDownOnNextListener) this.mSubscriberOnNextListener.get()).onError(e);
        }
        HttpDownManager.getInstance().remove(this.downInfo);
        this.downInfo.setState(DownState.ERROR);
        DbDwonUtil.getInstance().update(this.downInfo);
    }

    public void onNext(T t) {
        if (this.mSubscriberOnNextListener.get() != null) {
            ((HttpDownOnNextListener) this.mSubscriberOnNextListener.get()).onNext(t);
        }
    }

    public void update(long read, long count, boolean done) {
        if (this.downInfo.getCountLength() > count) {
            read += this.downInfo.getCountLength() - count;
        } else {
            this.downInfo.setCountLength(count);
        }
        this.downInfo.setReadLength(read);
        if (this.mSubscriberOnNextListener.get() != null) {
            Observable.just(Long.valueOf(read)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                public void call(Long aLong) {
                    if (ProgressDownSubscriber.this.downInfo.getState() != DownState.PAUSE && ProgressDownSubscriber.this.downInfo.getState() != DownState.STOP) {
                        ProgressDownSubscriber.this.downInfo.setState(DownState.DOWN);
                        ((HttpDownOnNextListener) ProgressDownSubscriber.this.mSubscriberOnNextListener.get()).updateProgress(aLong.longValue(), ProgressDownSubscriber.this.downInfo.getCountLength());
                    }
                }
            });
        }
    }
}
