package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.FactoryException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.RetryWhenNetworkException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.HttpOnNextListener;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers.ProgressSubscriber;
import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpManager {
    private SoftReference<RxAppCompatActivity> appCompatActivity;
    Func1 funcException = new Func1<Throwable, Observable>() {
        public Observable call(Throwable throwable) {
            return Observable.error(FactoryException.analysisExcetpion(throwable));
        }
    };
    private SoftReference<HttpOnNextListener> onNextListener;

    public HttpManager(HttpOnNextListener onNextListener, RxAppCompatActivity appCompatActivity) {
        this.onNextListener = new SoftReference(onNextListener);
        this.appCompatActivity = new SoftReference(appCompatActivity);
    }

    public void doHttpDeal(BaseApi basePar) {
        Builder builder = new Builder();
        builder.connectTimeout((long) basePar.getConnectionTime(), TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder().client(builder.build()).addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(basePar.getBaseUrl()).build();
        basePar.getObservable(retrofit).retryWhen(new RetryWhenNetworkException()).onErrorResumeNext(this.funcException).compose(((RxAppCompatActivity) this.appCompatActivity.get()).bindUntilEvent(ActivityEvent.DESTROY)).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(basePar).subscribe(new ProgressSubscriber(basePar, this.onNextListener));
    }
}
