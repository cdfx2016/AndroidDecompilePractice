package com.wzgiceman.rxretrofitlibrary.retrofit_rx.downlaod;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

public class DownInfoDao extends AbstractDao<DownInfo, Long> {
    public static final String TABLENAME = "DOWN_INFO";

    public static class Properties {
        public static final Property ConnectonTime = new Property(4, Integer.TYPE, "connectonTime", false, "CONNECTON_TIME");
        public static final Property CountLength = new Property(2, Long.TYPE, "countLength", false, "COUNT_LENGTH");
        public static final Property Id = new Property(0, Long.TYPE, "id", true, "_id");
        public static final Property ReadLength = new Property(3, Long.TYPE, "readLength", false, "READ_LENGTH");
        public static final Property SavePath = new Property(1, String.class, "savePath", false, "SAVE_PATH");
        public static final Property StateInte = new Property(5, Integer.TYPE, "stateInte", false, "STATE_INTE");
        public static final Property Url = new Property(6, String.class, MessageEncoder.ATTR_URL, false, "URL");
    }

    public DownInfoDao(DaoConfig config) {
        super(config);
    }

    public DownInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : "") + "\"DOWN_INFO\" (" + "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + "\"SAVE_PATH\" TEXT," + "\"COUNT_LENGTH\" INTEGER NOT NULL ," + "\"READ_LENGTH\" INTEGER NOT NULL ," + "\"CONNECTON_TIME\" INTEGER NOT NULL ," + "\"STATE_INTE\" INTEGER NOT NULL ," + "\"URL\" TEXT);");
    }

    public static void dropTable(Database db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DOWN_INFO\"");
    }

    protected final void bindValues(DatabaseStatement stmt, DownInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        String savePath = entity.getSavePath();
        if (savePath != null) {
            stmt.bindString(2, savePath);
        }
        stmt.bindLong(3, entity.getCountLength());
        stmt.bindLong(4, entity.getReadLength());
        stmt.bindLong(5, (long) entity.getConnectonTime());
        stmt.bindLong(6, (long) entity.getStateInte());
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(7, url);
        }
    }

    protected final void bindValues(SQLiteStatement stmt, DownInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        String savePath = entity.getSavePath();
        if (savePath != null) {
            stmt.bindString(2, savePath);
        }
        stmt.bindLong(3, entity.getCountLength());
        stmt.bindLong(4, entity.getReadLength());
        stmt.bindLong(5, (long) entity.getConnectonTime());
        stmt.bindLong(6, (long) entity.getStateInte());
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(7, url);
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        return Long.valueOf(cursor.getLong(offset + 0));
    }

    public DownInfo readEntity(Cursor cursor, int offset) {
        String str = null;
        long j = cursor.getLong(offset + 0);
        String string = cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
        long j2 = cursor.getLong(offset + 2);
        long j3 = cursor.getLong(offset + 3);
        int i = cursor.getInt(offset + 4);
        int i2 = cursor.getInt(offset + 5);
        if (!cursor.isNull(offset + 6)) {
            str = cursor.getString(offset + 6);
        }
        return new DownInfo(j, string, j2, j3, i, i2, str);
    }

    public void readEntity(Cursor cursor, DownInfo entity, int offset) {
        String str = null;
        entity.setId(cursor.getLong(offset + 0));
        entity.setSavePath(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCountLength(cursor.getLong(offset + 2));
        entity.setReadLength(cursor.getLong(offset + 3));
        entity.setConnectonTime(cursor.getInt(offset + 4));
        entity.setStateInte(cursor.getInt(offset + 5));
        if (!cursor.isNull(offset + 6)) {
            str = cursor.getString(offset + 6);
        }
        entity.setUrl(str);
    }

    protected final Long updateKeyAfterInsert(DownInfo entity, long rowId) {
        entity.setId(rowId);
        return Long.valueOf(rowId);
    }

    public Long getKey(DownInfo entity) {
        if (entity != null) {
            return Long.valueOf(entity.getId());
        }
        return null;
    }

    public boolean hasKey(DownInfo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }
}
