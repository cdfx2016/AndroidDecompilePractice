package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownLoadListener.DownloadInterceptor;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.HttpTimeException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.RetryWhenNetworkException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.subscribers.ProgressDownSubscriber;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.DbDwonUtil;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient.Builder;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpDownManager {
    private static volatile HttpDownManager INSTANCE;
    private DbDwonUtil db = DbDwonUtil.getInstance();
    private Set<DownInfo> downInfos = new HashSet();
    private HashMap<String, ProgressDownSubscriber> subMap = new HashMap();

    private HttpDownManager() {
    }

    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }

    public void startDown(final DownInfo info) {
        if (info == null || this.subMap.get(info.getUrl()) != null) {
            ((ProgressDownSubscriber) this.subMap.get(info.getUrl())).setDownInfo(info);
            return;
        }
        HttpDownService httpService;
        Subscriber subscriber = new ProgressDownSubscriber(info);
        this.subMap.put(info.getUrl(), subscriber);
        if (this.downInfos.contains(info)) {
            httpService = info.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            Builder builder = new Builder();
            builder.connectTimeout((long) info.getConnectonTime(), TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);
            httpService = (HttpDownService) new Retrofit.Builder().client(builder.build()).addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(AppUtil.getBasUrl(info.getUrl())).build().create(HttpDownService.class);
            info.setService(httpService);
            this.downInfos.add(info);
        }
        httpService.download("bytes=" + info.getReadLength() + Constants.ACCEPT_TIME_SEPARATOR_SERVER, info.getUrl()).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).retryWhen(new RetryWhenNetworkException()).map(new Func1<ResponseBody, DownInfo>() {
            public DownInfo call(ResponseBody responseBody) {
                try {
                    AppUtil.writeCache(responseBody, new File(info.getSavePath()), info);
                    return info;
                } catch (IOException e) {
                    throw new HttpTimeException(e.getMessage());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public void stopDown(DownInfo info) {
        if (info != null) {
            info.setState(DownState.STOP);
            info.getListener().onStop();
            if (this.subMap.containsKey(info.getUrl())) {
                ((ProgressDownSubscriber) this.subMap.get(info.getUrl())).unsubscribe();
                this.subMap.remove(info.getUrl());
            }
            this.db.save(info);
        }
    }

    public void pause(DownInfo info) {
        if (info != null) {
            info.setState(DownState.PAUSE);
            info.getListener().onPuase();
            if (this.subMap.containsKey(info.getUrl())) {
                ((ProgressDownSubscriber) this.subMap.get(info.getUrl())).unsubscribe();
                this.subMap.remove(info.getUrl());
            }
            this.db.update(info);
        }
    }

    public void stopAllDown() {
        for (DownInfo downInfo : this.downInfos) {
            stopDown(downInfo);
        }
        this.subMap.clear();
        this.downInfos.clear();
    }

    public void pauseAll() {
        for (DownInfo downInfo : this.downInfos) {
            pause(downInfo);
        }
        this.subMap.clear();
        this.downInfos.clear();
    }

    public Set<DownInfo> getDownInfos() {
        return this.downInfos;
    }

    public void remove(DownInfo info) {
        this.subMap.remove(info.getUrl());
        this.downInfos.remove(info);
    }
}
