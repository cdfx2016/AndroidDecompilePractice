package com.easemob.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import java.util.Hashtable;

public class EMMessage implements Parcelable, Cloneable {
    static final String ATTR_ENCRYPTED = "isencrypted";
    public static final Creator<EMMessage> CREATOR = new Creator<EMMessage>() {
        public EMMessage createFromParcel(Parcel parcel) {
            return new EMMessage(parcel);
        }

        public EMMessage[] newArray(int i) {
            return new EMMessage[i];
        }
    };
    private static final String TAG = "msg";
    Hashtable<String, Object> attributes;
    MessageBody body;
    ChatType chatType;
    public Direct direct;
    EMContact from;
    public boolean isAcked;
    String msgId;
    long msgTime;
    public transient int progress;
    public Status status;
    EMContact to;
    Type type;
    transient boolean unread;

    public enum ChatType {
        Chat,
        GroupChat
    }

    public enum Direct {
        SEND,
        RECEIVE
    }

    public enum Status {
        SUCCESS,
        FAIL,
        INPROGRESS,
        CREATE
    }

    public enum Type {
        TXT,
        IMAGE,
        VIDEO,
        LOCATION,
        VOICE,
        FILE,
        CMD
    }

    private EMMessage(Parcel parcel) {
        this.status = Status.CREATE;
        this.isAcked = false;
        this.chatType = ChatType.Chat;
        this.progress = 0;
        this.attributes = null;
        this.unread = true;
        this.type = Type.valueOf(parcel.readString());
        this.direct = Direct.valueOf(parcel.readString());
        this.msgId = parcel.readString();
        this.msgTime = parcel.readLong();
        this.attributes = new Hashtable();
        parcel.readMap(this.attributes, null);
        this.from = (EMContact) parcel.readParcelable(EMMessage.class.getClassLoader());
        this.to = (EMContact) parcel.readParcelable(EMMessage.class.getClassLoader());
        this.body = (MessageBody) parcel.readParcelable(EMMessage.class.getClassLoader());
    }

    EMMessage(Type type) {
        this.status = Status.CREATE;
        this.isAcked = false;
        this.chatType = ChatType.Chat;
        this.progress = 0;
        this.attributes = null;
        this.unread = true;
        this.type = type;
        this.msgTime = System.currentTimeMillis();
    }

    public static EMMessage createReceiveMessage(Type type) {
        EMMessage eMMessage = new EMMessage(type);
        eMMessage.direct = Direct.RECEIVE;
        eMMessage.to = EMSessionManager.getInstance(null).currentUser;
        return eMMessage;
    }

    public static EMMessage createSendMessage(Type type) {
        EMMessage eMMessage = new EMMessage(type);
        eMMessage.direct = Direct.SEND;
        EMContact eMContact = EMSessionManager.getInstance(null).currentUser;
        if (eMContact == null) {
            eMContact = EMContactManager.getInstance().getContactByUserName(EMSessionManager.getInstance(null).getLastLoginUser());
        }
        eMMessage.from = eMContact;
        return eMMessage;
    }

    public void addBody(MessageBody messageBody) {
        this.body = messageBody;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int describeContents() {
        return 0;
    }

    public MessageBody getBody() {
        return this.body;
    }

    public boolean getBooleanAttribute(String str) throws EaseMobException {
        Boolean bool = null;
        if (this.attributes != null) {
            bool = (Boolean) this.attributes.get(str);
        }
        if (bool != null) {
            return bool.booleanValue();
        }
        throw new EaseMobException("attribute " + str + " not found");
    }

    public boolean getBooleanAttribute(String str, boolean z) {
        if (this.attributes == null) {
            return z;
        }
        Boolean bool = (Boolean) this.attributes.get(str);
        return bool != null ? bool.booleanValue() : z;
    }

    public ChatType getChatType() {
        return this.chatType;
    }

    public String getFrom() {
        return this.from.username;
    }

    public int getIntAttribute(String str) throws EaseMobException {
        Integer num = null;
        if (this.attributes != null) {
            num = (Integer) this.attributes.get(str);
        }
        if (num != null) {
            return num.intValue();
        }
        throw new EaseMobException("attribute " + str + " not found");
    }

    public int getIntAttribute(String str, int i) {
        Integer num = null;
        if (this.attributes != null) {
            num = (Integer) this.attributes.get(str);
        }
        return num == null ? i : num.intValue();
    }

    public String getMsgId() {
        return this.msgId;
    }

    public long getMsgTime() {
        return this.msgTime;
    }

    public String getStringAttribute(String str) throws EaseMobException {
        String str2 = null;
        if (this.attributes != null) {
            str2 = (String) this.attributes.get(str);
        }
        if (str2 != null) {
            return str2;
        }
        throw new EaseMobException("attribute " + str + " not found");
    }

    public String getStringAttribute(String str, String str2) {
        String str3 = null;
        if (this.attributes != null) {
            str3 = (String) this.attributes.get(str);
        }
        return str3 == null ? str2 : str3;
    }

    public String getTo() {
        return this.to.username;
    }

    public Type getType() {
        return this.type;
    }

    public void setAttribute(String str, int i) {
        if (this.attributes == null) {
            this.attributes = new Hashtable();
        }
        this.attributes.put(str, new Integer(i));
    }

    public void setAttribute(String str, String str2) {
        if (this.attributes == null) {
            this.attributes = new Hashtable();
        }
        this.attributes.put(str, str2);
    }

    public void setAttribute(String str, boolean z) {
        if (this.attributes == null) {
            this.attributes = new Hashtable();
        }
        this.attributes.put(str, new Boolean(z));
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public void setFrom(String str) {
        EMContact eMContact = new EMContact();
        eMContact.setUsername(str);
        this.from = eMContact;
    }

    public void setMsgId(String str) {
        this.msgId = str;
    }

    public void setMsgTime(long j) {
        this.msgTime = j;
    }

    public void setReceipt(String str) {
        EMContactManager instance = EMContactManager.getInstance();
        EMContact eMContact = null;
        if (str.contains("@")) {
            EMLog.e("msg", "error wrong uesrname format:" + str);
        } else {
            eMContact = instance.getContactByUserName(str);
        }
        if (eMContact == null) {
            eMContact = new EMContact(str);
            instance.addContactInternal(eMContact);
        }
        this.to = eMContact;
    }

    public void setTo(String str) {
        EMContact eMContact = new EMContact();
        eMContact.setUsername(str);
        this.to = eMContact;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("msg{from:" + this.from.username);
        stringBuffer.append(", to:" + this.to.username);
        stringBuffer.append(" body:" + this.body.toString());
        return stringBuffer.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.type.name());
        parcel.writeString(this.direct.name());
        parcel.writeString(this.msgId);
        parcel.writeLong(this.msgTime);
        parcel.writeMap(this.attributes);
        parcel.writeParcelable(this.from, i);
        parcel.writeParcelable(this.to, i);
        parcel.writeParcelable(this.body, i);
    }
}
