package com.wzgiceman.rxretrofitlibrary.retrofit_rx;

import android.app.Application;

public class RxRetrofitApp {
    private static Application application;

    public static void init(Application app) {
        setApplication(app);
    }

    public static Application getApplication() {
        return application;
    }

    private static void setApplication(Application application) {
        application = application;
    }
}
