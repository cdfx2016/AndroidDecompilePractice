package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster.DevOpenHelper;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.HttpDownManager;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulte;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao.Properties;
import java.util.List;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

public class CookieDbUtil {
    private static CookieDbUtil db = null;
    private static final String dbName = "tests_db";
    private Context context = RxRetrofitApp.getApplication();
    private DevOpenHelper openHelper = new DevOpenHelper(this.context, dbName);

    public static CookieDbUtil getInstance() {
        if (db == null) {
            synchronized (HttpDownManager.class) {
                if (db == null) {
                    db = new CookieDbUtil();
                }
            }
        }
        return db;
    }

    private SQLiteDatabase getReadableDatabase() {
        if (this.openHelper == null) {
            this.openHelper = new DevOpenHelper(this.context, dbName);
        }
        return this.openHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (this.openHelper == null) {
            this.openHelper = new DevOpenHelper(this.context, dbName);
        }
        return this.openHelper.getWritableDatabase();
    }

    public void saveCookie(CookieResulte info) {
        new DaoMaster(getWritableDatabase()).newSession().getCookieResulteDao().insert(info);
    }

    public void updateCookie(CookieResulte info) {
        new DaoMaster(getWritableDatabase()).newSession().getCookieResulteDao().update(info);
    }

    public void deleteCookie(CookieResulte info) {
        new DaoMaster(getWritableDatabase()).newSession().getCookieResulteDao().delete(info);
    }

    public CookieResulte queryCookieBy(String url) {
        QueryBuilder<CookieResulte> qb = new DaoMaster(getReadableDatabase()).newSession().getCookieResulteDao().queryBuilder();
        qb.where(Properties.Url.eq(url), new WhereCondition[0]);
        List<CookieResulte> list = qb.list();
        if (list.isEmpty()) {
            return null;
        }
        return (CookieResulte) list.get(0);
    }

    public List<CookieResulte> queryCookieAll() {
        return new DaoMaster(getReadableDatabase()).newSession().getCookieResulteDao().queryBuilder().list();
    }
}
