package com.easemob.chat;

import com.easemob.chat.EMMessage.Direct;
import com.easemob.util.EMLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EMConversation {
    private static final String TAG = "conversation";
    private boolean isGroup = false;
    List<EMMessage> messages;
    private EMContact opposite = null;
    private int unreadMsgCount = 0;
    private String username;

    public EMConversation(String str) {
        this.username = str;
        if (this.messages == null) {
            this.messages = Collections.synchronizedList(new ArrayList());
        }
    }

    EMConversation(String str, List<EMMessage> list) {
        this.username = str;
        if (this.messages == null) {
            this.messages = Collections.synchronizedList(list);
        }
    }

    public void addMessage(EMMessage eMMessage) {
        if (this.messages.size() > 0) {
            EMMessage eMMessage2 = (EMMessage) this.messages.get(this.messages.size() - 1);
            if (!(eMMessage.getMsgId() == null || eMMessage2.getMsgId() == null || !eMMessage.getMsgId().equals(eMMessage2.getMsgId()))) {
                return;
            }
        }
        this.messages.add(eMMessage);
        if (eMMessage.direct == Direct.RECEIVE && eMMessage.unread) {
            this.unreadMsgCount++;
        }
    }

    public void clear() {
        this.messages.clear();
        this.unreadMsgCount = 0;
    }

    public List<EMMessage> getAllMessages() {
        return this.messages;
    }

    public boolean getIsGroup() {
        return this.isGroup;
    }

    public EMMessage getLastMessage() {
        return this.messages.size() == 0 ? null : (EMMessage) this.messages.get(this.messages.size() - 1);
    }

    public EMMessage getMessage(int i) {
        if (i >= this.messages.size()) {
            EMLog.e(TAG, "outofbound, messages.size:" + this.messages.size());
            return null;
        }
        EMMessage eMMessage = (EMMessage) this.messages.get(i);
        if (eMMessage == null || !eMMessage.unread) {
            return eMMessage;
        }
        eMMessage.unread = false;
        if (this.unreadMsgCount <= 0) {
            return eMMessage;
        }
        this.unreadMsgCount--;
        return eMMessage;
    }

    public EMMessage getMessage(String str) {
        int size = this.messages.size() - 1;
        while (size >= 0) {
            EMMessage eMMessage = (EMMessage) this.messages.get(size);
            if (!eMMessage.msgId.equals(str)) {
                size--;
            } else if (!eMMessage.unread) {
                return eMMessage;
            } else {
                eMMessage.unread = false;
                if (this.unreadMsgCount <= 0) {
                    return eMMessage;
                }
                this.unreadMsgCount--;
                return eMMessage;
            }
        }
        return null;
    }

    public int getMessagePosition(EMMessage eMMessage) {
        try {
            for (EMMessage eMMessage2 : this.messages) {
                if (eMMessage.getMsgId().equals(eMMessage2.getMsgId())) {
                    return this.messages.indexOf(eMMessage2);
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public int getMsgCount() {
        return this.messages.size();
    }

    public EMContact getOpposite() {
        return this.opposite;
    }

    public int getUnreadMsgCount() {
        return this.unreadMsgCount;
    }

    public String getUserName() {
        return this.username;
    }

    public List<EMMessage> loadMoreGroupMsgFromDB(String str, int i) {
        Object findGroupMessages = EMChatDB.getInstance().findGroupMessages(this.username, str, i);
        this.messages.addAll(0, findGroupMessages);
        return findGroupMessages;
    }

    public List<EMMessage> loadMoreMsgFromDB(String str, int i) {
        ArrayList arrayList = new ArrayList();
        Object findMessages = EMChatDB.getInstance().findMessages(this.username, str, i);
        this.messages.addAll(0, findMessages);
        return findMessages;
    }

    public void removeMessage(String str) {
        EMLog.d(TAG, "remove msg from conversation:" + str);
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            if (((EMMessage) this.messages.get(size)).msgId.equals(str)) {
                this.messages.remove(size);
                EMChatDB.getInstance().deleteMessage(str);
            }
        }
    }

    public void resetUnsetMsgCount() {
        this.unreadMsgCount = 0;
    }

    public void setGroup(boolean z) {
        this.isGroup = z;
    }

    public void setOpposite(EMContact eMContact) {
        this.opposite = eMContact;
    }
}
