package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulte;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.CookieResulteDao;
import java.util.Map;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

public class DaoSession extends AbstractDaoSession {
    private final CookieResulteDao cookieResulteDao = new CookieResulteDao(this.cookieResulteDaoConfig, this);
    private final DaoConfig cookieResulteDaoConfig;
    private final DownInfoDao downInfoDao = new DownInfoDao(this.downInfoDaoConfig, this);
    private final DaoConfig downInfoDaoConfig;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);
        this.downInfoDaoConfig = ((DaoConfig) daoConfigMap.get(DownInfoDao.class)).clone();
        this.downInfoDaoConfig.initIdentityScope(type);
        this.cookieResulteDaoConfig = ((DaoConfig) daoConfigMap.get(CookieResulteDao.class)).clone();
        this.cookieResulteDaoConfig.initIdentityScope(type);
        registerDao(DownInfo.class, this.downInfoDao);
        registerDao(CookieResulte.class, this.cookieResulteDao);
    }

    public void clear() {
        this.downInfoDaoConfig.clearIdentityScope();
        this.cookieResulteDaoConfig.clearIdentityScope();
    }

    public DownInfoDao getDownInfoDao() {
        return this.downInfoDao;
    }

    public CookieResulteDao getCookieResulteDao() {
        return this.cookieResulteDao;
    }
}
