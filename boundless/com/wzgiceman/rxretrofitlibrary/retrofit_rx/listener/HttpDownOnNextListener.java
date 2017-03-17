package com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener;

public abstract class HttpDownOnNextListener<T> {
    public abstract void onComplete();

    public abstract void onNext(T t);

    public abstract void onStart();

    public abstract void updateProgress(long j, long j2);

    public void onError(Throwable e) {
    }

    public void onPuase() {
    }

    public void onStop() {
    }
}
