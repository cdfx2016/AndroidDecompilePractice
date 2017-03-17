package com.easemob.chat;

import com.easemob.chat.core.a;
import com.easemob.util.EMLog;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;

class RecvAckListener implements PacketListener {
    private static final String TAG = "acklistener";

    RecvAckListener() {
    }

    private synchronized boolean processClientAckMessage(Message message) {
        boolean z = true;
        synchronized (this) {
            PacketExtension extension = message.getExtension("urn:xmpp:receipts");
            if (extension == null || !extension.getElementName().equals(a.b)) {
                z = false;
            } else if (EMChatManager.getInstance().getChatOptions().getRequireAck()) {
                String body = message.getBody();
                EMMessage message2 = EMChatManager.getInstance().getMessage(body);
                if (message2 != null) {
                    message2.isAcked = true;
                    EMChatDB.getInstance().updateMessageAck(body, true);
                    EMChatManager.getInstance().notifiyAckMessage(EMContactManager.getUserNameFromEid(message.getFrom()), body);
                }
            } else {
                EMLog.d(TAG, "msg read ack is not enabled. skip ack msg received");
            }
        }
        return z;
    }

    public void processPacket(Packet packet) {
        Message message = (Message) packet;
        if (!processClientAckMessage(message)) {
            PacketExtension extension = message.getExtension("urn:xmpp:receipts");
            if (extension != null && extension.getElementName().equals("received")) {
                String body = message.getBody();
                EMLog.d(TAG, "received server ack for msg:" + body);
                SendMessageThread.notifySendLock(body);
            }
        }
    }
}
