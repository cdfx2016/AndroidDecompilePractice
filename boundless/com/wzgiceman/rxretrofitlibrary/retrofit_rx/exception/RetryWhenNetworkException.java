package com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class RetryWhenNetworkException implements Func1<Observable<? extends Throwable>, Observable<?>> {
    private int count = 3;
    private long delay = 3000;
    private long increaseDelay = 3000;

    private class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }

    public RetryWhenNetworkException(int count, long delay) {
        this.count = count;
        this.delay = delay;
    }

    public RetryWhenNetworkException(int count, long delay, long increaseDelay) {
        this.count = count;
        this.delay = delay;
        this.increaseDelay = increaseDelay;
    }

    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.zipWith(Observable.range(1, this.count + 1), new Func2<Throwable, Integer, Wrapper>() {
            public Wrapper call(Throwable throwable, Integer integer) {
                return new Wrapper(throwable, integer.intValue());
            }
        }).flatMap(new Func1<Wrapper, Observable<?>>() {
            public Observable<?> call(Wrapper wrapper) {
                if (((wrapper.throwable instanceof ConnectException) || (wrapper.throwable instanceof SocketTimeoutException) || (wrapper.throwable instanceof TimeoutException)) && wrapper.index < RetryWhenNetworkException.this.count + 1) {
                    return Observable.timer(RetryWhenNetworkException.this.delay + (((long) (wrapper.index - 1)) * RetryWhenNetworkException.this.increaseDelay), TimeUnit.MILLISECONDS);
                }
                return Observable.error(wrapper.throwable);
            }
        });
    }
}
