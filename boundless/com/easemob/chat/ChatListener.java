package com.easemob.chat;

import com.easemob.chat.EMMessage.Status;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.chat.core.a;
import com.easemob.chat.core.b;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;

class ChatListener implements PacketListener {
    protected static final int RECENT_QUEUE_SIZE = 20;
    private static final String TAG = "chat";
    protected EMChatManager chatManager = null;
    protected String previousBody = "";
    protected String previousFrom = "";
    protected long previousTime = System.currentTimeMillis();
    protected ArrayBlockingQueue<String> recentMsgIdQueue;
    protected ExecutorService recvThreadPool = null;

    public ChatListener(EMChatManager eMChatManager) {
        this.chatManager = eMChatManager;
        this.recvThreadPool = Executors.newCachedThreadPool();
        this.recentMsgIdQueue = new ArrayBlockingQueue(20);
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

    protected void ackMessage(Message message) {
        String packetID = message.getPacketID();
        if (packetID != null && !packetID.equals("")) {
            Packet message2 = new Message();
            message2.setPacketID(packetID);
            message2.setTo(EMChatConfig.DOMAIN);
            message2.setFrom(message.getTo());
            PacketExtension aVar = new a("received");
            aVar.setValue("id", packetID);
            message2.addExtension(aVar);
            XmppConnectionManager.getInstance().getConnection().sendPacket(message2);
            EMLog.d(TAG, "send ack message back to server:" + message2);
        }
    }

    protected boolean isDuplicateMsg(Message message) {
        boolean z;
        if (message.getFrom().equals(this.previousFrom) && message.getBody().equals(this.previousBody) && System.currentTimeMillis() - this.previousTime < 1000) {
            EMLog.d(TAG, "ignore duplicate msg with same from and body:" + this.previousFrom);
            z = true;
        } else {
            z = false;
        }
        this.previousFrom = message.getFrom();
        this.previousBody = message.getBody();
        this.previousTime = System.currentTimeMillis();
        String packetID = message.getPacketID();
        if (packetID == null) {
            return z;
        }
        Iterator it = this.recentMsgIdQueue.iterator();
        while (it.hasNext()) {
            if (packetID.equals((String) it.next())) {
                EMLog.d(TAG, "ignore duplicate msg:" + message);
                return true;
            }
        }
        if (this.recentMsgIdQueue.size() == 20) {
            try {
                this.recentMsgIdQueue.poll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.recentMsgIdQueue.add(message.getPacketID());
        return z;
    }

    protected boolean processEMMessage(EMMessage eMMessage) {
        if (eMMessage == null) {
            return false;
        }
        if (eMMessage.type == Type.CMD) {
            EMCmdManager.getInstance().processCmd(eMMessage);
            return true;
        }
        if (eMMessage.getMsgId() == null) {
            eMMessage.msgId = DateUtils.getTimestampStr();
        }
        if (eMMessage.body instanceof FileMessageBody) {
            setLocalUrl(eMMessage);
            this.recvThreadPool.execute(new ReceiveMessageThread(eMMessage, eMMessage.getBooleanAttribute("isencrypted", false)));
        } else {
            eMMessage.status = Status.SUCCESS;
        }
        EMChatDB.getInstance().saveMessage(eMMessage);
        this.chatManager.addMessage(eMMessage);
        if (EMChat.getInstance().appInited) {
            this.chatManager.notifyMessage(eMMessage);
        }
        return true;
    }

    protected boolean processMessage(Message message) {
        if (processClientAckMessage(message)) {
            return true;
        }
        ackMessage(message);
        if (message.getBody() == null || message.getBody().equals("")) {
            return true;
        }
        if (isDuplicateMsg(message)) {
            EMLog.d(TAG, "ignore duplicate msg");
            return true;
        }
        EMLog.d(TAG, "chat listener receive msg from:" + StringUtils.parseBareAddress(message.getFrom()) + " body:" + message.getBody());
        if (message.getType() != Message.Type.chat) {
            return false;
        }
        EMMessage parseXmppMsg = MessageEncoder.parseXmppMsg(message);
        if (message.getExtension(b.a, b.b) != null) {
            parseXmppMsg.setAttribute("isencrypted", true);
        }
        return processEMMessage(parseXmppMsg);
    }

    public synchronized void processPacket(Packet packet) {
        if (packet instanceof Message) {
            processMessage((Message) packet);
        } else {
            EMLog.d(TAG, "packet is not message, skip");
        }
    }

    protected void setLocalUrl(EMMessage eMMessage) {
        FileMessageBody fileMessageBody = (FileMessageBody) eMMessage.body;
        String substring = fileMessageBody.remoteUrl.substring(fileMessageBody.remoteUrl.lastIndexOf("/") + 1);
        if (eMMessage.type == Type.IMAGE) {
            fileMessageBody.localUrl = PathUtil.getInstance().getImagePath() + "/" + substring;
        } else if (eMMessage.type == Type.VOICE) {
            fileMessageBody.localUrl = PathUtil.getInstance().getVoicePath() + "/" + substring;
        } else if (eMMessage.type == Type.VIDEO) {
            fileMessageBody.localUrl = PathUtil.getInstance().getVideoPath() + "/" + substring;
        } else {
            fileMessageBody.localUrl = PathUtil.getInstance().getVideoPath() + "/" + substring;
        }
    }
}
