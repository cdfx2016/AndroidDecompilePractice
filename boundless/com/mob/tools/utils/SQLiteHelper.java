package com.mob.tools.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.easemob.util.HanziToPinyin.Token;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class SQLiteHelper {

    public static class SingleTableDB {
        private SQLiteDatabase db;
        private HashMap<String, Boolean> fieldLimits;
        private LinkedHashMap<String, String> fieldTypes;
        private String name;
        private String path;
        private String primary;
        private boolean primaryAutoincrement;

        private SingleTableDB(String path, String name) {
            this.path = path;
            this.name = name;
            this.fieldTypes = new LinkedHashMap();
            this.fieldLimits = new HashMap();
        }

        public void addField(String fieldName, String fieldType, boolean notNull) {
            if (this.db == null) {
                this.fieldTypes.put(fieldName, fieldType);
                this.fieldLimits.put(fieldName, Boolean.valueOf(notNull));
            }
        }

        public void setPrimary(String fieldName, boolean autoincrement) {
            this.primary = fieldName;
            this.primaryAutoincrement = autoincrement;
        }

        private void open() {
            if (this.db == null) {
                this.db = SQLiteDatabase.openOrCreateDatabase(new File(this.path), null);
                Cursor cursor = this.db.rawQuery("select count(*) from sqlite_master where type ='table' and name ='" + this.name + "' ", null);
                boolean shouldCreate = true;
                if (cursor != null) {
                    if (cursor.moveToNext() && cursor.getInt(0) > 0) {
                        shouldCreate = false;
                    }
                    cursor.close();
                }
                if (shouldCreate) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("create table  ").append(this.name).append("(");
                    for (Entry<String, String> ent : this.fieldTypes.entrySet()) {
                        String field = (String) ent.getKey();
                        String type = (String) ent.getValue();
                        boolean notNull = ((Boolean) this.fieldLimits.get(field)).booleanValue();
                        boolean primary = field.equals(this.primary);
                        boolean autoincrement = primary ? this.primaryAutoincrement : false;
                        sb.append(field).append(Token.SEPARATOR).append(type);
                        sb.append(notNull ? " not null" : "");
                        sb.append(primary ? " primary key" : "");
                        sb.append(autoincrement ? " autoincrement," : Constants.ACCEPT_TIME_SEPARATOR_SP);
                    }
                    sb.replace(sb.length() - 1, sb.length(), ");");
                    this.db.execSQL(sb.toString());
                }
            }
        }

        private void close() {
            if (this.db != null) {
                this.db.close();
                this.db = null;
            }
        }

        private String getName() {
            return this.name;
        }
    }

    public static SingleTableDB getDatabase(Context context, String name) {
        return getDatabase(context.getDatabasePath(name).getPath(), name);
    }

    public static SingleTableDB getDatabase(String path, String name) {
        return new SingleTableDB(path, name);
    }

    public static long insert(SingleTableDB db, ContentValues values) throws Throwable {
        db.open();
        return db.db.replace(db.getName(), null, values);
    }

    public static int delete(SingleTableDB db, String selection, String[] selectionArgs) throws Throwable {
        db.open();
        return db.db.delete(db.getName(), selection, selectionArgs);
    }

    public static int update(SingleTableDB db, ContentValues values, String selection, String[] selectionArgs) throws Throwable {
        db.open();
        return db.db.update(db.getName(), values, selection, selectionArgs);
    }

    public static Cursor query(SingleTableDB db, String[] columns, String selection, String[] selectionArgs, String sortOrder) throws Throwable {
        db.open();
        return db.db.query(db.getName(), columns, selection, selectionArgs, null, null, sortOrder);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void execSQL(com.mob.tools.utils.SQLiteHelper.SingleTableDB r3, java.lang.String r4) throws java.lang.Throwable {
        /*
        r3.open();
        r1 = r3.db;
        r1.beginTransaction();
        r1 = r3.db;	 Catch:{ Throwable -> 0x0020 }
        r1.execSQL(r4);	 Catch:{ Throwable -> 0x0020 }
        r1 = r3.db;	 Catch:{ Throwable -> 0x0020 }
        r1.setTransactionSuccessful();	 Catch:{ Throwable -> 0x0020 }
        r1 = r3.db;
        r1.endTransaction();
        return;
    L_0x0020:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0022 }
    L_0x0022:
        r1 = move-exception;
        r2 = r3.db;
        r2.endTransaction();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.utils.SQLiteHelper.execSQL(com.mob.tools.utils.SQLiteHelper$SingleTableDB, java.lang.String):void");
    }

    public static Cursor rawQuery(SingleTableDB db, String script, String[] selectionArgs) throws Throwable {
        db.open();
        return db.db.rawQuery(script, selectionArgs);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getSize(com.mob.tools.utils.SQLiteHelper.SingleTableDB r6) throws java.lang.Throwable {
        /*
        r6.open();
        r1 = 0;
        r0 = 0;
        r3 = r6.db;	 Catch:{ Throwable -> 0x0034 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0034 }
        r4.<init>();	 Catch:{ Throwable -> 0x0034 }
        r5 = "select count(*) from ";
        r4 = r4.append(r5);	 Catch:{ Throwable -> 0x0034 }
        r5 = r6.getName();	 Catch:{ Throwable -> 0x0034 }
        r4 = r4.append(r5);	 Catch:{ Throwable -> 0x0034 }
        r4 = r4.toString();	 Catch:{ Throwable -> 0x0034 }
        r5 = 0;
        r0 = r3.rawQuery(r4, r5);	 Catch:{ Throwable -> 0x0034 }
        r3 = r0.moveToNext();	 Catch:{ Throwable -> 0x0034 }
        if (r3 == 0) goto L_0x0030;
    L_0x002b:
        r3 = 0;
        r1 = r0.getInt(r3);	 Catch:{ Throwable -> 0x0034 }
    L_0x0030:
        r0.close();
        return r1;
    L_0x0034:
        r2 = move-exception;
        throw r2;	 Catch:{ all -> 0x0036 }
    L_0x0036:
        r3 = move-exception;
        r0.close();
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.utils.SQLiteHelper.getSize(com.mob.tools.utils.SQLiteHelper$SingleTableDB):int");
    }

    public static void close(SingleTableDB db) {
        db.close();
    }
}
