package com.easemob.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.EMMessage.Status;
import com.easemob.util.EMLog;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class EMChatDB {
    private static final String CHAT_TABLE_NAME = "chat";
    private static final String COLUMN_GROUP_DESC = "desc";
    private static final String COLUMN_GROUP_JID = "jid";
    private static final String COLUMN_GROUP_MEMBERS = "members";
    private static final String COLUMN_GROUP_NAME = "name";
    private static final String COLUMN_GROUP_NICK = "nick";
    private static final String COLUMN_GROUP_OWNER = "owner";
    private static final String COLUMN_GROUP_PUBLIC = "ispublic";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MODIFIED_TIME = "modifiedtime";
    private static final String COLUMN_MSG_BODY = "msgbody";
    private static final String COLUMN_MSG_DIR = "msgdir";
    private static final String COLUMN_MSG_GROUP = "groupname";
    private static final String COLUMN_MSG_ID = "msgid";
    private static final String COLUMN_MSG_ISACKED = "isacked";
    public static final String COLUMN_MSG_STATUS = "status";
    private static final String COLUMN_MSG_TIME = "msgtime";
    private static final String COLUMN_PARTICIPANT = "participant";
    private static final String CREATE_CHAT_TABLE = "create table chat (_id integer primary key autoincrement, msgid text, msgtime integer, msgdir integer, isacked integer, status integer,participant text not null, msgbody text not null,groupname text);";
    private static final String CREATE_GROUP_TABLE = "create table emgroup (name text primary key, jid text not null, nick text not null, owner text not null, modifiedtime integer, ispublic integer, desc text, members text);";
    static final String DATABASE_NAME = "_emmsg.db";
    private static final int DATABASE_VERSION = 1;
    private static final String GROUP_TABLE_NAME = "emgroup";
    private static String TAG = "chatdb";
    private static EMChatDB instance = null;
    private Context appContext;
    private String currentUserName;

    private static class EMChatDBOpenHelper extends SQLiteOpenHelper {
        private static EMChatDBOpenHelper instance = null;

        private EMChatDBOpenHelper(Context context, String str) {
            super(context, new StringBuilder(String.valueOf(str)).append(EMChatDB.DATABASE_NAME).toString(), null, 1);
            EMLog.d(EMChatDB.TAG, "created chatdb for :" + str);
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

        public static EMChatDBOpenHelper getInstance(Context context, String str) {
            if (instance == null) {
                instance = new EMChatDBOpenHelper(context, str);
            }
            return instance;
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(EMChatDB.CREATE_CHAT_TABLE);
            sQLiteDatabase.execSQL(EMChatDB.CREATE_GROUP_TABLE);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Log.w(EMChatDB.TAG, "Upgrading from version " + i + " to " + i2 + ", which will destroy all old data");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS chat");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS emgroup");
            onCreate(sQLiteDatabase);
        }
    }

    private EMChatDB(Context context, String str) {
        try {
            this.appContext = context;
            this.currentUserName = str;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertListToString(List<String> list) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String append : list) {
            stringBuffer.append(append);
            stringBuffer.append(Constants.ACCEPT_TIME_SEPARATOR_SP);
        }
        return stringBuffer.toString();
    }

    public static EMChatDB getInstance() {
        if (instance == null) {
            new Exception().printStackTrace();
        }
        return instance;
    }

    static void initDB(Context context, String str) {
        if (instance != null) {
            instance.closeDatabase();
        }
        instance = new EMChatDB(context, str);
        EMLog.d(TAG, "start to load groups");
        EMGroupManager.getInstance().loadAllGroups();
        EMLog.d(TAG, "loaded groups:" + EMGroupManager.getInstance().allGroups.size());
        new Thread() {
            public void run() {
                EMLog.d(EMChatDB.TAG, "load conversations in thread...");
                EMChatManager.getInstance().loadConversations();
                EMLog.d(EMChatDB.TAG, "loaded conversations:");
            }
        }.start();
    }

    private EMGroup loadGroupFromCursor(Cursor cursor) throws Exception {
        boolean z = false;
        EMGroup eMGroup = new EMGroup(cursor.getString(0));
        eMGroup.eid = cursor.getString(1);
        eMGroup.setGroupName(cursor.getString(2));
        eMGroup.owner = cursor.getString(3);
        eMGroup.lastModifiedTime = cursor.getLong(4);
        if (cursor.getInt(5) != 0) {
            z = true;
        }
        eMGroup.isPublic = z;
        eMGroup.description = cursor.getString(6);
        StringTokenizer stringTokenizer = new StringTokenizer(cursor.getString(7), Constants.ACCEPT_TIME_SEPARATOR_SP);
        while (stringTokenizer.hasMoreTokens()) {
            eMGroup.addMember(stringTokenizer.nextToken());
        }
        return eMGroup;
    }

    private EMMessage loadMsgFromCursor(Cursor cursor) {
        EMMessage msgFromJson = MessageEncoder.getMsgFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_MSG_BODY)));
        msgFromJson.msgId = cursor.getString(cursor.getColumnIndex(COLUMN_MSG_ID));
        msgFromJson.msgTime = cursor.getLong(cursor.getColumnIndex(COLUMN_MSG_TIME));
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_MSG_DIR)) == Direct.SEND.ordinal()) {
            msgFromJson.direct = Direct.SEND;
        } else {
            msgFromJson.direct = Direct.RECEIVE;
        }
        int i = cursor.getInt(cursor.getColumnIndex("status"));
        if (i == Status.CREATE.ordinal()) {
            msgFromJson.status = Status.CREATE;
        } else if (i == Status.INPROGRESS.ordinal()) {
            msgFromJson.status = Status.INPROGRESS;
        } else if (i == Status.SUCCESS.ordinal()) {
            msgFromJson.status = Status.SUCCESS;
        } else if (i == Status.FAIL.ordinal()) {
            msgFromJson.status = Status.FAIL;
        }
        if (cursor.getInt(cursor.getColumnIndex(COLUMN_MSG_ISACKED)) == 0) {
            msgFromJson.isAcked = false;
        } else {
            msgFromJson.isAcked = true;
        }
        msgFromJson.unread = false;
        String string = cursor.getString(cursor.getColumnIndex(COLUMN_MSG_GROUP));
        if (string == null) {
            msgFromJson.setChatType(ChatType.Chat);
        } else {
            msgFromJson.setChatType(ChatType.GroupChat);
            msgFromJson.setTo(string);
        }
        return msgFromJson;
    }

    void closeDatabase() {
        EMChatDBOpenHelper.closeDB();
        EMLog.d(TAG, "close msg db");
    }

    public void deleteConversions(String str) {
        try {
            EMLog.d(TAG, "delete converstion with:" + str + " return:" + EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().delete(CHAT_TABLE_NAME, "participant = ?", new String[]{str}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(String str) {
        try {
            EMLog.d(TAG, "delete converstion with:" + str + " return:" + EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().delete(GROUP_TABLE_NAME, "name = ?", new String[]{str}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroupConversions(String str) {
        try {
            EMLog.d(TAG, "delete converstion with:" + str + " return:" + EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().delete(CHAT_TABLE_NAME, "groupname = ?", new String[]{str}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String str) {
        try {
            EMLog.d(TAG, "delete msg:" + str + " return:" + EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().delete(CHAT_TABLE_NAME, "msgid = ?", new String[]{str}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> findAllGroupsWithMsg() {
        List arrayList = new ArrayList();
        try {
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select distinct groupname from chat where groupname is not null", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(rawQuery.getString(0));
                } while (rawQuery.moveToNext());
                rawQuery.close();
                EMLog.d(TAG, "load msg groups size:" + arrayList.size());
            } else {
                rawQuery.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public List<String> findAllParticipantsWithMsg() {
        List arrayList = new ArrayList();
        try {
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select distinct participant from chat where groupname is null", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(rawQuery.getString(0));
                } while (rawQuery.moveToNext());
                rawQuery.close();
                EMLog.d(TAG, "load participants size:" + arrayList.size());
            } else {
                rawQuery.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public List<EMMessage> findGroupMessages(String str) {
        Object arrayList = new ArrayList();
        try {
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select * from chat where groupname = ? order by msgtime", new String[]{str});
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(loadMsgFromCursor(rawQuery));
                } while (rawQuery.moveToNext());
                rawQuery.close();
                EMLog.d(TAG, "load msgs size:" + arrayList.size() + " for group:" + str);
                return arrayList;
            }
            rawQuery.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EMMessage> findGroupMessages(String str, String str2, int i) {
        Object arrayList = new ArrayList();
        try {
            Cursor rawQuery;
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            if (str2 != null) {
                rawQuery = writableDatabase.rawQuery("select _id from chat where msgid = ?", new String[]{str2});
                if (rawQuery != null && rawQuery.moveToFirst()) {
                    int i2 = rawQuery.getInt(rawQuery.getColumnIndex(COLUMN_ID));
                    rawQuery = writableDatabase.rawQuery("select * from chat where groupname = ? and _id < ? order by _id desc limit ?", new String[]{str, new StringBuilder(String.valueOf(i2)).toString(), new StringBuilder(String.valueOf(i)).toString()});
                }
            } else {
                rawQuery = writableDatabase.rawQuery("select * from chat where groupname = ? order by _id desc limit ?", new String[]{str, new StringBuilder(String.valueOf(i)).toString()});
            }
            if (rawQuery.moveToLast()) {
                do {
                    arrayList.add(loadMsgFromCursor(rawQuery));
                } while (rawQuery.moveToPrevious());
                rawQuery.close();
                EMLog.d(TAG, "load msgs size:" + arrayList.size() + " for groupid:" + str);
                return arrayList;
            }
            rawQuery.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EMMessage> findMessages(String str) {
        Object arrayList = new ArrayList();
        try {
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select * from chat where participant = ? and groupname = null order by msgtime", new String[]{str});
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(loadMsgFromCursor(rawQuery));
                } while (rawQuery.moveToNext());
                rawQuery.close();
                EMLog.d(TAG, "load msgs size:" + arrayList.size() + " for participate:" + str);
                return arrayList;
            }
            rawQuery.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EMMessage> findMessages(String str, String str2, int i) {
        Object arrayList = new ArrayList();
        try {
            Cursor rawQuery;
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            if (str2 != null) {
                rawQuery = writableDatabase.rawQuery("select _id from chat where msgid = ?", new String[]{str2});
                if (rawQuery != null && rawQuery.moveToFirst()) {
                    int i2 = rawQuery.getInt(rawQuery.getColumnIndex(COLUMN_ID));
                    rawQuery = writableDatabase.rawQuery("select * from chat where participant = ? and _id < ? and groupname is null order by _id desc limit ?", new String[]{str, new StringBuilder(String.valueOf(i2)).toString(), new StringBuilder(String.valueOf(i)).toString()});
                }
            } else {
                rawQuery = writableDatabase.rawQuery("select * from chat where participant = ? and groupname is null order by _id desc limit ?", new String[]{str, new StringBuilder(String.valueOf(i)).toString()});
            }
            if (rawQuery.moveToLast()) {
                do {
                    arrayList.add(loadMsgFromCursor(rawQuery));
                } while (rawQuery.moveToPrevious());
                rawQuery.close();
                EMLog.d(TAG, "load msgs size:" + arrayList.size() + " for participate:" + str);
                return arrayList;
            }
            rawQuery.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, EMGroup> loadAllGroups() {
        Map<String, EMGroup> hashtable = new Hashtable();
        try {
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select * from emgroup", new String[0]);
            if (rawQuery.moveToFirst()) {
                do {
                    EMGroup loadGroupFromCursor = loadGroupFromCursor(rawQuery);
                    hashtable.put(loadGroupFromCursor.getGroupId(), loadGroupFromCursor);
                } while (rawQuery.moveToNext());
                rawQuery.close();
                EMLog.d(TAG, "load groups from db:" + hashtable.size());
                return hashtable;
            }
            rawQuery.close();
            return hashtable;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EMGroup loadGroup(String str) {
        try {
            EMGroup loadGroupFromCursor;
            Cursor rawQuery = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().rawQuery("select * from emgroup where name  =?", new String[]{str});
            if (rawQuery != null) {
                loadGroupFromCursor = rawQuery.moveToFirst() ? loadGroupFromCursor(rawQuery) : null;
                rawQuery.close();
            } else {
                loadGroupFromCursor = null;
            }
            EMLog.d(TAG, "db load group:" + loadGroupFromCursor);
            return loadGroupFromCursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void saveGroup(EMGroup eMGroup) {
        try {
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", eMGroup.getGroupId());
            contentValues.put(COLUMN_GROUP_JID, eMGroup.eid);
            contentValues.put("nick", eMGroup.getGroupName());
            contentValues.put(COLUMN_GROUP_DESC, eMGroup.description);
            contentValues.put(COLUMN_GROUP_OWNER, eMGroup.owner);
            contentValues.put(COLUMN_GROUP_MEMBERS, convertListToString(eMGroup.getMembers()));
            contentValues.put(COLUMN_MODIFIED_TIME, Long.valueOf(eMGroup.lastModifiedTime));
            contentValues.put(COLUMN_GROUP_PUBLIC, Boolean.valueOf(eMGroup.isPublic));
            writableDatabase.insert(GROUP_TABLE_NAME, null, contentValues);
            EMLog.d(TAG, "save group to db groupname:" + eMGroup.getGroupName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveMessage(EMMessage eMMessage) {
        try {
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_MSG_ID, eMMessage.msgId);
            contentValues.put(COLUMN_MSG_TIME, Long.valueOf(eMMessage.msgTime));
            contentValues.put(COLUMN_MSG_ISACKED, Boolean.valueOf(eMMessage.isAcked));
            contentValues.put(COLUMN_MSG_DIR, Integer.valueOf(eMMessage.direct.ordinal()));
            contentValues.put("status", Integer.valueOf(eMMessage.status.ordinal()));
            String str = eMMessage.from.username.equals(this.currentUserName) ? eMMessage.to.username : eMMessage.from.username;
            contentValues.put(COLUMN_PARTICIPANT, str);
            contentValues.put(COLUMN_MSG_BODY, MessageEncoder.getJSONMsg(eMMessage, true));
            if (eMMessage.getChatType() == ChatType.GroupChat) {
                contentValues.put(COLUMN_MSG_GROUP, eMMessage.getTo());
            } else {
                contentValues.putNull(COLUMN_MSG_GROUP);
            }
            if (!str.equals("bot")) {
                writableDatabase.insert(CHAT_TABLE_NAME, null, contentValues);
            }
            EMLog.d(TAG, "save msg to db msgid:" + eMMessage.msgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGroup(EMGroup eMGroup) {
        try {
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_GROUP_JID, eMGroup.eid);
            contentValues.put("nick", eMGroup.getGroupName());
            contentValues.put(COLUMN_GROUP_DESC, eMGroup.description);
            contentValues.put(COLUMN_GROUP_OWNER, eMGroup.owner);
            contentValues.put(COLUMN_GROUP_MEMBERS, convertListToString(eMGroup.getMembers()));
            contentValues.put(COLUMN_MODIFIED_TIME, Long.valueOf(eMGroup.lastModifiedTime));
            contentValues.put(COLUMN_GROUP_PUBLIC, Boolean.valueOf(eMGroup.isPublic));
            writableDatabase.update(GROUP_TABLE_NAME, contentValues, "name = ?", new String[]{eMGroup.getGroupId()});
            EMLog.d(TAG, "updated group groupname:" + eMGroup.getGroupName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMessage(String str, ContentValues contentValues) {
        EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase().update(CHAT_TABLE_NAME, contentValues, "msgid = ?", new String[]{str});
    }

    public void updateMessageAck(String str, boolean z) {
        try {
            SQLiteDatabase writableDatabase = EMChatDBOpenHelper.getInstance(this.appContext, this.currentUserName).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_MSG_ISACKED, Boolean.valueOf(z));
            writableDatabase.update(CHAT_TABLE_NAME, contentValues, "msgid = ?", new String[]{str});
            EMLog.d(TAG, "update msg:" + str + " ack:" + z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
