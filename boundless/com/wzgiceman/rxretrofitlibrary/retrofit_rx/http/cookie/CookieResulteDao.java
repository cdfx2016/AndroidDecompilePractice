package com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod.DaoSession;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class CookieResulteDao extends AbstractDao<CookieResulte, Long> {
    public static final String TABLENAME = "COOKIE_RESULTE";

    public static class Properties {
        public static final Property Id = new Property(0, Long.TYPE, "id", true, "_id");
        public static final Property Resulte = new Property(2, String.class, "resulte", false, "RESULTE");
        public static final Property Time = new Property(3, Long.TYPE, "time", false, "TIME");
        public static final Property Url = new Property(1, String.class, MessageEncoder.ATTR_URL, false, "URL");
    }

    public CookieResulteDao(DaoConfig config) {
        super(config);
    }

    public CookieResulteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : "") + "\"COOKIE_RESULTE\" (" + "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + "\"URL\" TEXT," + "\"RESULTE\" TEXT," + "\"TIME\" INTEGER NOT NULL );");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COOKIE_RESULTE\"");
    }

    protected final void bindValues(DatabaseStatement stmt, CookieResulte entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(2, url);
        }
        String resulte = entity.getResulte();
        if (resulte != null) {
            stmt.bindString(3, resulte);
        }
        stmt.bindLong(4, entity.getTime());
    }

    protected final void bindValues(SQLiteStatement stmt, CookieResulte entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(2, url);
        }
        String resulte = entity.getResulte();
        if (resulte != null) {
            stmt.bindString(3, resulte);
        }
        stmt.bindLong(4, entity.getTime());
    }

    public Long readKey(Cursor cursor, int offset) {
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public CookieResulte readEntity(Cursor cursor, int offset) {
        return new CookieResulte(cursor.getLong(offset + 0), cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), cursor.getLong(offset + 3));
    }

    public void readEntity(Cursor cursor, CookieResulte entity, int offset) {
        String str = null;
        entity.setId(cursor.getLong(offset + 0));
        entity.setUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        if (!cursor.isNull(offset + 2)) {
            str = cursor.getString(offset + 2);
        }
        entity.setResulte(str);
        entity.setTime(cursor.getLong(offset + 3));
    }

    protected final Long updateKeyAfterInsert(CookieResulte entity, long rowId) {
        entity.setId(rowId);
        return Long.valueOf(rowId);
    }

    public Long getKey(CookieResulte entity) {
        if (entity != null) {
            return Long.valueOf(entity.getId());
        }
        return null;
    }

    public boolean hasKey(CookieResulte entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }
}
