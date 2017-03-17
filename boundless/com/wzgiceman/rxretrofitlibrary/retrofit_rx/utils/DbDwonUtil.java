package com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.RxRetrofitApp;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoMaster.DevOpenHelper;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownInfo;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DownInfoDao.Properties;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.HttpDownManager;
import java.util.List;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

public class DbDwonUtil {
    private static DbDwonUtil db = null;
    private static final String dbName = "tests_db";
    private Context context = RxRetrofitApp.getApplication();
    private DevOpenHelper openHelper = new DevOpenHelper(this.context, dbName, null);

    public static DbDwonUtil getInstance() {
        if (db == null) {
            synchronized (HttpDownManager.class) {
                if (db == null) {
                    db = new DbDwonUtil();
                }
            }
        }
        return db;
    }

    private SQLiteDatabase getReadableDatabase() {
        if (this.openHelper == null) {
            this.openHelper = new DevOpenHelper(this.context, dbName, null);
        }
        return this.openHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (this.openHelper == null) {
            this.openHelper = new DevOpenHelper(this.context, dbName, null);
        }
        return this.openHelper.getWritableDatabase();
    }

    public void save(DownInfo info) {
        new DaoMaster(getWritableDatabase()).newSession().getDownInfoDao().insert(info);
    }

    public void update(DownInfo info) {
        new DaoMaster(getWritableDatabase()).newSession().getDownInfoDao().update(info);
    }

    public void deleteDowninfo(DownInfo info) {
        new DaoMaster(getWritableDatabase()).newSession().getDownInfoDao().delete(info);
    }

    public DownInfo queryDownBy(long Id) {
        QueryBuilder<DownInfo> qb = new DaoMaster(getReadableDatabase()).newSession().getDownInfoDao().queryBuilder();
        qb.where(Properties.Id.eq(Long.valueOf(Id)), new WhereCondition[0]);
        List<DownInfo> list = qb.list();
        if (list.isEmpty()) {
            return null;
        }
        return (DownInfo) list.get(0);
    }

    public List<DownInfo> queryDownAll() {
        return new DaoMaster(getReadableDatabase()).newSession().getDownInfoDao().queryBuilder().list();
    }
}
