package com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.HttpTimeException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulte;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.HttpOnNextListener;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.CookieDbUtil;
import java.lang.ref.SoftReference;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class ProgressSubscriber<T> extends Subscriber<T> implements Subscription {
    private BaseApi api;
    private SoftReference<HttpOnNextListener> mSubscriberOnNextListener;

    public ProgressSubscriber(BaseApi api, SoftReference<HttpOnNextListener> listenerSoftReference) {
        this.api = api;
        this.mSubscriberOnNextListener = listenerSoftReference;
    }

    public void onStart() {
        if (this.api.isCache() && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
            CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(this.api.getUrl());
            if (cookieResulte != null && (System.currentTimeMillis() - cookieResulte.getTime()) / 1000 < ((long) this.api.getCookieNetWorkTime())) {
                if (this.mSubscriberOnNextListener.get() != null) {
                    ((HttpOnNextListener) this.mSubscriberOnNextListener.get()).onNext(cookieResulte.getResulte(), this.api.getMothed());
                }
                onCompleted();
                unsubscribe();
            }
        }
    }

    public void onCompleted() {
    }

    public void onError(Throwable e) {
        if (this.api.isCache()) {
            getCache();
        } else {
            errorDo(e);
        }
    }

    private void getCache() {
        Observable.just(this.api.getUrl()).subscribe(new Subscriber<String>() {
            public void onCompleted() {
            }

            public void onError(Throwable e) {
                ProgressSubscriber.this.errorDo(e);
            }

            public void onNext(String s) {
                CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(s);
                if (cookieResulte == null) {
                    throw new HttpTimeException(4099);
                } else if ((System.currentTimeMillis() - cookieResulte.getTime()) / 1000 >= ((long) ProgressSubscriber.this.api.getCookieNoNetWorkTime())) {
                    CookieDbUtil.getInstance().deleteCookie(cookieResulte);
                    throw new HttpTimeException((int) HttpTimeException.CHACHE_TIMEOUT_ERROR);
                } else if (ProgressSubscriber.this.mSubscriberOnNextListener.get() != null) {
                    ((HttpOnNextListener) ProgressSubscriber.this.mSubscriberOnNextListener.get()).onNext(cookieResulte.getResulte(), ProgressSubscriber.this.api.getMothed());
                }
            }
        });
    }

    private void errorDo(Throwable e) {
        HttpOnNextListener httpOnNextListener = (HttpOnNextListener) this.mSubscriberOnNextListener.get();
        if (httpOnNextListener != null) {
            if (e instanceof ApiException) {
                httpOnNextListener.onError((ApiException) e);
            } else if (e instanceof HttpTimeException) {
                HttpTimeException exception = (HttpTimeException) e;
                httpOnNextListener.onError(new ApiException(exception, 5, exception.getMessage()));
            } else {
                httpOnNextListener.onError(new ApiException(e, 4, e.getMessage()));
            }
        }
    }

    public void onNext(T t) {
        if (this.api.isCache()) {
            CookieResulte resulte = CookieDbUtil.getInstance().queryCookieBy(this.api.getUrl());
            long time = System.currentTimeMillis();
            if (resulte == null) {
                CookieDbUtil.getInstance().saveCookie(new CookieResulte(this.api.getUrl(), t.toString(), time));
            } else {
                resulte.setResulte(t.toString());
                resulte.setTime(time);
                CookieDbUtil.getInstance().updateCookie(resulte);
            }
        }
        if (this.mSubscriberOnNextListener.get() != null) {
            ((HttpOnNextListener) this.mSubscriberOnNextListener.get()).onNext((String) t, this.api.getMothed());
        }
    }
}
