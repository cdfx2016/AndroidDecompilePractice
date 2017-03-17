package com.easemob.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.easemob.util.EMLog;

class ChatDB {
    private static final String CONTACT_COLUMN_JID = "jid";
    private static final String CONTACT_COLUMN_USERNAME = "username";
    private static final String CONTACT_TABLE = "contact";
    private static final String CREATE_CONTACT_TABLE = "create table contact (jid text primary key, username text not null);";
    static final String DATABASE_NAME = "_emchat.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "chatdb";
    private EMContactManager contactManager;
    private Context context;

    private static class ChatDBOpenHelper extends SQLiteOpenHelper {
        private static ChatDBOpenHelper instance = null;

        private ChatDBOpenHelper(Context context) {
            super(context, new StringBuilder(String.valueOf(EMSessionManager.getInstance(context).currentUser.eid)).append(ChatDB.DATABASE_NAME).toString(), null, 1);
        }

        public static void closeDB() {
            if (instance != null) {
                try {
                    instance.getWritableDatabase().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                instance = null;
            }
        }

        public static ChatDBOpenHelper getInstance(Context context) {
            if (instance == null) {
                instance = new ChatDBOpenHelper(context);
            }
            return instance;
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(ChatDB.CREATE_CONTACT_TABLE);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Log.w(ChatDB.TAG, "Upgrading database from version " + i + " to " + i2 + ", which will destroy all old data");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS contact");
            onCreate(sQLiteDatabase);
        }
    }

    ChatDB() {
    }

    public void addContact(String str, String str2) {
        try {
            SQLiteDatabase writableDatabase = ChatDBOpenHelper.getInstance(this.context).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACT_COLUMN_JID, str);
            contentValues.put(CONTACT_COLUMN_USERNAME, str2);
            writableDatabase.insert(CONTACT_TABLE, null, contentValues);
            EMLog.d(TAG, "add contact to db jid:" + str + " username:" + str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDatabase() {
        ChatDBOpenHelper.closeDB();
        EMLog.d(TAG, "close chat db");
    }

    public void deleteContact(String str) {
        try {
            ChatDBOpenHelper.getInstance(this.context).getWritableDatabase().delete(CONTACT_TABLE, "jid = ?", new String[]{str});
            EMLog.d(TAG, "delete contact jid:" + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadContacts() {
        try {
            Cursor rawQuery = ChatDBOpenHelper.getInstance(this.context).getWritableDatabase().rawQuery("select * from contact", null);
            if (rawQuery.moveToFirst()) {
                do {
                    EMContact eMContact = new EMContact(rawQuery.getString(0), rawQuery.getString(1));
                    EMLog.d(TAG, "load contact from db:" + eMContact);
                    this.contactManager.addContactFromDb(eMContact);
                } while (rawQuery.moveToNext());
                rawQuery.close();
                return;
            }
            rawQuery.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
