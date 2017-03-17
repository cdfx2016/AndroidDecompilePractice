package com.easemob.chat;

import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.core.b;
import com.easemob.util.EMLog;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.util.StringUtils;

class GroupChatListener extends ChatListener {
    private static final String TAG = "groupchatlistener";

    public GroupChatListener(EMChatManager eMChatManager) {
        super(eMChatManager);
    }

    private void processGroupMessage(Message message, EMMessage eMMessage) {
        String substring;
        String from = message.getFrom();
        int indexOf = from.indexOf("/");
        if (indexOf > 0) {
            substring = from.substring(indexOf + 1);
            from = from.substring(0, indexOf - 1);
        } else {
            EMLog.d(TAG, "the message is from muc itself");
            substring = "EaseMobGroup";
        }
        String groupIdFromEid = EMContactManager.getGroupIdFromEid(from);
        EMLog.d(TAG, "group msg groupjid:" + from + " groupid:" + groupIdFromEid + " usrname:" + substring);
        eMMessage.setChatType(ChatType.GroupChat);
        eMMessage.setTo(groupIdFromEid);
    }

    protected boolean processMessage(Message message) {
        ackMessage(message);
        if (message.getBody() == null || message.getBody().equals("")) {
            return true;
        }
        if (isDuplicateMsg(message)) {
            EMLog.d(TAG, "ignore duplicate msg");
            return true;
        }
        EMLog.d(TAG, "groupchat listener receive msg from:" + StringUtils.parseBareAddress(message.getFrom()) + " body:" + message.getBody());
        if (message.getType() != Type.groupchat) {
            return false;
        }
        EMMessage parseXmppMsg = MessageEncoder.parseXmppMsg(message);
        if (parseXmppMsg == null) {
            return false;
        }
        if (parseXmppMsg.getFrom().equals(EMChatManager.getInstance().getCurrentUser())) {
            EMLog.d(TAG, "igore group msg sent from myself:" + parseXmppMsg.toString());
            return false;
        }
        processGroupMessage(message, parseXmppMsg);
        if (message.getExtension(b.a, b.b) != null) {
            parseXmppMsg.setAttribute("isencrypted", true);
        }
        return processEMMessage(parseXmppMsg);
    }
}
