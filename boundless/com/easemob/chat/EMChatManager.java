package com.easemob.chat;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatService.LocalBinder;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.EMMessage.Status;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.chat.core.a;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.CryptoUtils;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.fanyu.boundless.util.FileUtil;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.packet.VCard;

public class EMChatManager {
    private static final String ACK_MSG_BROADCAST = "easemob.ackmsg.";
    private static final String CONTACT_INVITE_EVENT_BROADCAST = "easemob.contact.invite.";
    private static final int DEFAULT_LOAD_MESSAGE_COUNT = 20;
    private static final String NEW_MSG_BROADCAST = "easemob.newmsg.";
    private static final String TAG = "chat";
    private static EMChatManager instance = null;
    private Hashtable<String, EMMessage> allMessages = new Hashtable();
    private Context applicationContext;
    private EMChatService boundService = null;
    private final ChatListener chatListener = new ChatListener(this);
    private EMChatManagerListener chatManagerListener = new EMChatManagerListener();
    private EMChatOptions chatOptions;
    private Map<String, Chat> chats = new HashMap();
    private EMCmdManager cmdManager = null;
    private final List<ConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList());
    private Hashtable<String, EMConversation> conversations = new Hashtable();
    private CryptoUtils cryptoUtils = new CryptoUtils();
    private EncryptProvider encryptProvider = null;
    private final GroupChatListener groupChatListener = new GroupChatListener(this);
    private Handler handler = new Handler();
    private boolean isBound = false;
    private EMNotifier notifier;
    private ArrayList<Presence> offlineRosterPresenceList = new ArrayList();
    private final RecvAckListener recvAckListener = new RecvAckListener();
    ExecutorService sendThreadPool = null;
    private ChatServiceConnection serviceConnection = null;
    private ChatManager xmppChatManager;
    private final XmppConnectionListener xmppConnectionListener = new XmppConnectionListener();
    private XmppConnectionManager xmppConnectionManager;

    private class ChatServiceConnection implements ServiceConnection {
        private ChatServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            EMChatManager.this.boundService = ((LocalBinder) iBinder).getService();
            EMLog.d(EMChatManager.TAG, "service connected");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            EMLog.d(EMChatManager.TAG, "EaseMobService is disconnected");
            EMChatManager.this.boundService = null;
            EMLog.d(EMChatManager.TAG, "service disconnected");
        }
    }

    private class EMChatManagerListener implements ChatManagerListener {
        private EMChatManagerListener() {
        }

        public void chatCreated(Chat chat, boolean z) {
            String participant = chat.getParticipant();
            EMLog.d(EMChatManager.TAG, "xmpp chat created for: " + participant);
            EMChatManager.this.chats.put(participant, chat);
        }
    }

    private class SingleInvitationListener implements PacketListener {
        private SingleInvitationListener() {
        }

        public void processPacket(Packet packet) {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                if (EMChat.getInstance().appInited) {
                    EMChatManager.this.processRosterPresence(presence);
                    return;
                }
                EMLog.d(EMChatManager.TAG, "received roster presence, but app is not ready");
                EMChatManager.this.offlineRosterPresenceList.add(presence);
            }
        }
    }

    private class XmppConnectionListener implements ConnectionListener {
        private XmppConnectionListener() {
        }

        public void connectionClosed() {
            EMLog.d(EMChatManager.TAG, "closing connection");
            EMChatManager.this.handler.post(new Runnable() {
                public void run() {
                    for (ConnectionListener connectionListener : EMChatManager.this.connectionListeners) {
                        if (connectionListener != null) {
                            connectionListener.onDisConnected("connectionClosed");
                        }
                    }
                }
            });
        }

        public void connectionClosedOnError(final Exception exception) {
            EMLog.d(EMChatManager.TAG, "connectionClosedOnError");
            EMGroupManager.getInstance().removeMucs();
            EMChatManager.this.handler.post(new Runnable() {
                public void run() {
                    for (ConnectionListener connectionListener : EMChatManager.this.connectionListeners) {
                        if (connectionListener != null) {
                            connectionListener.onDisConnected("connectionClosedOnError:" + exception.getMessage());
                        }
                    }
                }
            });
        }

        public void reconnectingIn(int i) {
            EMLog.d(EMChatManager.TAG, "reconnectingIn in " + i);
        }

        public void reconnectionFailed(final Exception exception) {
            EMLog.d(EMChatManager.TAG, "reconnectionFailed");
            EMChatManager.this.handler.post(new Runnable() {
                public void run() {
                    for (ConnectionListener connectionListener : EMChatManager.this.connectionListeners) {
                        if (connectionListener != null) {
                            connectionListener.onDisConnected(exception.getMessage());
                        }
                    }
                }
            });
        }

        public void reconnectionSuccessful() {
            EMLog.d(EMChatManager.TAG, "reconnectionSuccessful");
            EMChatManager.this.handler.post(new Runnable() {
                public void run() {
                    for (ConnectionListener connectionListener : EMChatManager.this.connectionListeners) {
                        if (connectionListener != null) {
                            connectionListener.onReConnected();
                        }
                    }
                }
            });
        }
    }

    private EMChatManager() {
        this.cryptoUtils.init(1);
        this.cmdManager = EMCmdManager.getInstance();
        this.sendThreadPool = Executors.newCachedThreadPool();
        this.chatOptions = new EMChatOptions();
        this.serviceConnection = new ChatServiceConnection();
    }

    private void acceptInvitation(String str, boolean z) throws EaseMobException {
        try {
            checkConnection();
            Packet presence = new Presence(Type.subscribed);
            presence.setMode(Mode.available);
            presence.setPriority(24);
            presence.setTo(str);
            presence.setStatus("[resp:" + z + "]");
            XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
            if (z) {
                presence = new Presence(Type.subscribe);
                presence.setStatus("[resp:true]");
                presence.setTo(str);
                XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
            }
        } catch (Exception e) {
            throw new EaseMobException(e.getMessage());
        }
    }

    public static EMChatManager getInstance() {
        if (instance == null) {
            instance = new EMChatManager();
        }
        return instance;
    }

    static String getUniqueMessageId() {
        return Packet.nextID() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + Long.toHexString(System.currentTimeMillis()).substring(6);
    }

    private void processRosterPresence(Presence presence) {
        boolean z = false;
        String str = null;
        String status;
        if (presence.getType().equals(Type.subscribe)) {
            String substring;
            boolean z2;
            status = presence.getStatus() != null ? presence.getStatus() : null;
            String str2 = "[resp:";
            if (status.startsWith(str2)) {
                z = Boolean.parseBoolean(status.substring(str2.length(), status.indexOf("]")));
                if (status.length() > status.indexOf("]") + 1) {
                    boolean z3 = z;
                    substring = status.substring(status.indexOf("]1"), status.length());
                    z2 = z3;
                } else {
                    z2 = z;
                    substring = null;
                }
            } else {
                substring = status;
                z2 = false;
            }
            EMLog.d(TAG, "isresp:" + z2 + " reason:" + substring);
            if (z2) {
                try {
                    acceptInvitation(presence.getFrom(), false);
                    if (z2) {
                        Intent intent = new Intent(getInstance().getContactInviteEventBroadcastAction());
                        intent.putExtra("username", EMContactManager.getUserNameFromEid(presence.getFrom()));
                        intent.putExtra("isResponse", z2);
                        this.applicationContext.sendBroadcast(intent);
                        EMContactManager.getInstance().contactListener.onContactAgreed(EMContactManager.getUserNameFromEid(presence.getFrom()));
                    }
                } catch (EaseMobException e) {
                    EMLog.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            } else if (getInstance().getChatOptions().getAcceptInvitationAlways()) {
                try {
                    acceptInvitation(presence.getFrom(), true);
                } catch (EaseMobException e2) {
                    EMLog.e(TAG, e2.getMessage());
                    e2.printStackTrace();
                }
            } else {
                Intent intent2 = new Intent(getInstance().getContactInviteEventBroadcastAction());
                intent2.putExtra("username", EMContactManager.getUserNameFromEid(presence.getFrom()));
                intent2.putExtra("reason", substring);
                intent2.putExtra("isResponse", z2);
                this.applicationContext.sendOrderedBroadcast(intent2, null);
                EMContactManager.getInstance().contactListener.onContactInvited(EMContactManager.getUserNameFromEid(presence.getFrom()), substring);
            }
        } else if (presence.getType().equals(Type.unsubscribe)) {
            EMContactManager.getInstance().deleteContactsSet.add(presence.getFrom());
            Packet presence2 = new Presence(Type.unsubscribed);
            presence2.setMode(Mode.available);
            presence2.setPriority(24);
            presence2.setTo(presence.getFrom());
            XmppConnectionManager.getInstance().getConnection().sendPacket(presence2);
        } else if (presence.getType().equals(Type.subscribed)) {
            if (presence.getStatus() != null) {
                str = presence.getStatus();
            }
            status = "[resp:";
            if (str.startsWith(status)) {
                z = Boolean.parseBoolean(str.substring(status.length(), str.indexOf("]")));
            }
            if (z) {
                Intent intent3 = new Intent(getInstance().getContactInviteEventBroadcastAction());
                intent3.putExtra("username", EMContactManager.getUserNameFromEid(presence.getFrom()));
                intent3.putExtra("isResponse", z);
                this.applicationContext.sendBroadcast(intent3);
                EMContactManager.getInstance().contactListener.onContactAgreed(EMContactManager.getUserNameFromEid(presence.getFrom()));
            }
        }
    }

    private void sendVCard() {
        try {
            VCard vCard = new VCard();
            vCard.setField("os", "android");
            vCard.save(this.xmppConnectionManager.getConnection());
            EMLog.d(TAG, "sent user vcard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptInvitation(String str) throws EaseMobException {
        acceptInvitation(EMContactManager.getEidFromUserName(str), true);
    }

    public void ackMessageRead(String str, String str2) throws EaseMobException {
        if (this.chatOptions.getRequireAck()) {
            checkConnection();
            String eidFromUserName = EMContactManager.getEidFromUserName(str);
            if (((Chat) this.chats.get(eidFromUserName)) == null) {
                this.chats.put(eidFromUserName, this.xmppChatManager.createChat(eidFromUserName, null));
            }
            Packet message = new Message();
            try {
                PacketExtension aVar = new a(a.b);
                aVar.setValue("id", str2);
                message.addExtension(aVar);
                message.setBody(str2);
                EMLog.d(TAG, "send ack msg to:" + str + " for msg:" + str2);
                message.setType(Message.Type.normal);
                message.setTo(eidFromUserName);
                message.setFrom(EMContactManager.getEidFromUserName(getCurrentUser()));
                XmppConnectionManager.getInstance().getConnection().sendPacket(message);
                EMChatDB.getInstance().updateMessageAck(str2, true);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                throw new EaseMobException(e.getMessage());
            }
        }
        EMLog.d(TAG, "chat option reqire ack set to false. skip send out ask msg read");
    }

    public void activityResumed() {
        if (this.notifier != null) {
            this.notifier.resetNotificationCount();
        }
    }

    public void addConnectionListener(final ConnectionListener connectionListener) {
        if (connectionListener != null) {
            this.connectionListeners.add(connectionListener);
            if (this.xmppConnectionManager == null || this.xmppConnectionManager.getConnection() == null || !this.xmppConnectionManager.getConnection().isConnected()) {
                this.xmppConnectionManager.registerConnectivityReceiver();
            } else {
                this.handler.post(new Runnable() {
                    public void run() {
                        for (ConnectionListener connectionListener : EMChatManager.this.connectionListeners) {
                            if (connectionListener != null && connectionListener.equals(connectionListener)) {
                                connectionListener.onConnected();
                            }
                        }
                    }
                });
            }
        }
    }

    void addMessage(EMMessage eMMessage) {
        this.allMessages.put(eMMessage.msgId, eMMessage);
        if (eMMessage.getChatType() == ChatType.GroupChat) {
            getConversation(eMMessage.getTo()).addMessage(eMMessage);
        } else {
            getConversation(eMMessage.direct == Direct.RECEIVE ? eMMessage.from.username : eMMessage.to.username).addMessage(eMMessage);
        }
    }

    void addPacketListeners(XMPPConnection xMPPConnection) {
        if (!xMPPConnection.isConnected() || !xMPPConnection.isAuthenticated()) {
            xMPPConnection.addPacketListener(this.chatListener, new MessageTypeFilter(Message.Type.chat));
            xMPPConnection.addPacketListener(this.groupChatListener, new MessageTypeFilter(Message.Type.groupchat));
            xMPPConnection.addPacketListener(this.recvAckListener, new MessageTypeFilter(Message.Type.normal));
            this.xmppConnectionManager.getConnection().addPacketListener(new SingleInvitationListener(), new PacketTypeFilter(Presence.class) {
                public boolean accept(Packet packet) {
                    if (packet instanceof Presence) {
                        Presence presence = (Presence) packet;
                        if (presence.getType().equals(Type.subscribed) || presence.getType().equals(Type.subscribe) || presence.getType().equals(Type.unsubscribed) || presence.getType().equals(Type.unsubscribe)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void changePasswordOnServer(String str) throws EaseMobException {
        EMSessionManager.getInstance(this.applicationContext).changePasswordXMPP(str);
    }

    void checkConnection() throws EaseMobException {
        if (this.xmppConnectionManager == null) {
            throw new EMNetworkUnconnectedException("xmppConnectionManager is null");
        } else if (this.xmppConnectionManager.getConnection() == null) {
            throw new EMNetworkUnconnectedException("connection is null");
        } else if (!this.xmppConnectionManager.getConnection().isConnected() || !this.xmppConnectionManager.getConnection().isAuthenticated()) {
            EMLog.e(TAG, "network unconnected");
            if (NetUtils.hasDataConnection(EMChatConfig.getInstance().applicationContext)) {
                EMLog.d(TAG, "try to reconnect after check connection failed");
                this.xmppConnectionManager.reconnectASync();
            }
            throw new EMNetworkUnconnectedException("connection is not connected");
        }
    }

    public boolean clearConversation(String str) {
        EMLog.d(TAG, "clear conversation for user: " + str);
        EMConversation eMConversation = (EMConversation) this.conversations.get(str);
        if (eMConversation == null) {
            return false;
        }
        EMChatDB.getInstance().deleteConversions(str);
        eMConversation.clear();
        return true;
    }

    public void createAccountOnServer(String str, String str2) throws EaseMobException {
        if (!str.startsWith(EMChatConfig.getInstance().APPKEY)) {
            str = new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").append(str).toString();
        }
        EMSessionManager.getInstance(this.applicationContext).createAccountXMPP(str, str2);
    }

    public boolean deleteConversation(String str) {
        EMLog.d(TAG, "remove conversation for user: " + str);
        EMConversation eMConversation = (EMConversation) this.conversations.get(str);
        if (eMConversation == null) {
            return false;
        }
        EMChatDB.getInstance().deleteConversions(str);
        eMConversation.clear();
        this.conversations.remove(str);
        return true;
    }

    void doBindService() {
        if (!this.isBound) {
            EMLog.d(TAG, "do bind service");
            this.applicationContext.bindService(new Intent(this.applicationContext, EMChatService.class), this.serviceConnection, 1);
            this.isBound = true;
        }
    }

    void doUnbindService() {
        if (this.isBound) {
            EMLog.d(TAG, "do unbind service");
            this.applicationContext.unbindService(this.serviceConnection);
            this.isBound = false;
        }
    }

    public String getAckMessageBroadcastAction() {
        return new StringBuilder(ACK_MSG_BROADCAST).append(EMChatConfig.getInstance().APPKEY.replaceAll("#", FileUtil.FILE_EXTENSION_SEPARATOR).replaceAll(Constants.ACCEPT_TIME_SEPARATOR_SERVER, FileUtil.FILE_EXTENSION_SEPARATOR)).toString();
    }

    public EMChatOptions getChatOptions() {
        return this.chatOptions;
    }

    public String getContactInviteEventBroadcastAction() {
        return new StringBuilder(CONTACT_INVITE_EVENT_BROADCAST).append(EMChatConfig.getInstance().APPKEY.replaceAll("#", FileUtil.FILE_EXTENSION_SEPARATOR).replaceAll(Constants.ACCEPT_TIME_SEPARATOR_SERVER, FileUtil.FILE_EXTENSION_SEPARATOR)).toString();
    }

    public List<String> getContactUserNames() throws EaseMobException {
        return EMContactManager.getInstance().getRosterUserNames();
    }

    public EMConversation getConversation(String str) {
        EMLog.d(TAG, "get conversation for user:" + str);
        EMConversation eMConversation = (EMConversation) this.conversations.get(str);
        if (eMConversation != null) {
            return eMConversation;
        }
        eMConversation = new EMConversation(str);
        this.conversations.put(str, eMConversation);
        return eMConversation;
    }

    CryptoUtils getCryptoUtils() {
        return this.cryptoUtils;
    }

    public String getCurrentUser() {
        return EMSessionManager.getInstance(this.applicationContext).currentUser.username;
    }

    public EncryptProvider getEncryptProvider() {
        if (this.encryptProvider == null) {
            EMLog.d(TAG, "encrypt provider is not set, create default");
            this.encryptProvider = new EncryptProvider() {
                public byte[] decrypt(byte[] bArr, String str) {
                    try {
                        bArr = EMChatManager.this.cryptoUtils.decrypt(bArr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bArr;
                }

                public byte[] encrypt(byte[] bArr, String str) {
                    try {
                        bArr = EMChatManager.this.cryptoUtils.encrypt(bArr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bArr;
                }
            };
        }
        return this.encryptProvider;
    }

    public EMMessage getMessage(String str) {
        return (EMMessage) this.allMessages.get(str);
    }

    public String getNewMessageBroadcastAction() {
        return new StringBuilder(NEW_MSG_BROADCAST).append(EMChatConfig.getInstance().APPKEY.replaceAll("#", FileUtil.FILE_EXTENSION_SEPARATOR).replaceAll(Constants.ACCEPT_TIME_SEPARATOR_SERVER, FileUtil.FILE_EXTENSION_SEPARATOR)).toString();
    }

    public int getUnreadMsgsCount() {
        int i = 0;
        for (EMConversation unreadMsgCount : this.conversations.values()) {
            i = unreadMsgCount.getUnreadMsgCount() + i;
        }
        EMLog.d(TAG, "getunreadmsgcount return:" + i);
        return i;
    }

    void init(Context context, XmppConnectionManager xmppConnectionManager) {
        EMLog.d(TAG, "init chat manager");
        this.applicationContext = context;
        this.notifier = EMNotifier.getInstance(context);
        if (xmppConnectionManager == null || xmppConnectionManager.getConnection() == null) {
            EMLog.e(TAG, "error in Chat Manage init. connection is null");
            return;
        }
        try {
            this.xmppChatManager = xmppConnectionManager.getConnection().getChatManager();
            this.xmppConnectionManager = xmppConnectionManager;
            this.xmppChatManager.addChatListener(this.chatManagerListener);
            xmppConnectionManager.setChatConnectionListener(this.xmppConnectionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return EMSessionManager.getInstance(this.applicationContext).isConnected();
    }

    void loadConversations() {
        for (String str : EMChatDB.getInstance().findAllParticipantsWithMsg()) {
            List findMessages = EMChatDB.getInstance().findMessages(str, null, 20);
            this.conversations.put(str, new EMConversation(str, findMessages));
            EMLog.d(TAG, "loaded user " + str + " history msg:" + findMessages.size());
        }
        for (String str2 : EMChatDB.getInstance().findAllGroupsWithMsg()) {
            findMessages = EMChatDB.getInstance().findGroupMessages(str2, null, 20);
            this.conversations.put(str2, new EMConversation(str2, findMessages));
            EMLog.d(TAG, "loaded group " + str2 + " history msg:" + findMessages.size());
        }
        EMLog.d(TAG, "total history conversations:" + this.conversations.size());
        for (EMConversation eMConversation : this.conversations.values()) {
            for (EMMessage eMMessage : eMConversation.messages) {
                this.allMessages.put(eMMessage.msgId, eMMessage);
            }
        }
        EMLog.d(TAG, "add msgs to allMessages for search by id. size:" + this.allMessages.size());
    }

    public void login(String str, String str2, EMCallBack eMCallBack) {
        EMSessionManager.getInstance(this.applicationContext).login(str, str2, eMCallBack);
        try {
            doBindService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        EMSessionManager.getInstance(this.applicationContext).logout();
        this.allMessages.clear();
        this.conversations.clear();
        this.chats.clear();
        EMGroupManager.getInstance().logout();
        EMChat.getInstance().appInited = false;
        if (EMChatConfig.debugMode) {
            com.easemob.a.a.c();
        }
        try {
            doUnbindService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void notifiyAckMessage(String str, String str2) {
        this.notifier.sendAckMsgBroadcast(str, str2);
    }

    void notifyMessage(EMMessage eMMessage) {
        this.notifier.notifyChatMsg(eMMessage);
    }

    void processOfflineMessages() {
    }

    void processOfflinePresenceMessages() {
        EMLog.d(TAG, "process offline RosterPresence msg start");
        Iterator it = this.offlineRosterPresenceList.iterator();
        while (it.hasNext()) {
            processRosterPresence((Presence) it.next());
        }
        this.offlineRosterPresenceList.clear();
        EMLog.d(TAG, "proess offline RosterPresence msg finish");
    }

    public void refuseInvitation(String str) throws EaseMobException {
        try {
            checkConnection();
            Packet presence = new Presence(Type.unsubscribed);
            EMContactManager.getInstance();
            presence.setTo(EMContactManager.getEidFromUserName(str));
            this.xmppConnectionManager.getConnection().sendPacket(presence);
        } catch (Exception e) {
            throw new EaseMobException(e.getMessage());
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener != null) {
            this.connectionListeners.remove(connectionListener);
        }
    }

    public void saveMessage(EMMessage eMMessage) {
        EMLog.d(TAG, "save message:" + eMMessage.getMsgId());
        try {
            addMessage(eMMessage);
            EMChatDB.getInstance().saveMessage(eMMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGroupMessage(EMMessage eMMessage, EMCallBack eMCallBack) {
        try {
            eMMessage.setChatType(ChatType.GroupChat);
            if (eMMessage.msgId == null) {
                eMMessage.msgId = getUniqueMessageId();
            }
            if (!this.allMessages.containsKey(eMMessage.msgId)) {
                EMChatDB.getInstance().saveMessage(eMMessage);
                addMessage(eMMessage);
            }
            checkConnection();
            eMMessage.status = Status.INPROGRESS;
            eMMessage.from = EMSessionManager.getInstance(null).currentUser;
            String to = eMMessage.getTo();
            EMLog.d(TAG, "start send group message:" + to + " message:" + eMMessage.toString());
            this.sendThreadPool.execute(new SendMessageThread(EMGroupManager.getInstance().getMUC(EMContactManager.getEidFromGroupId(to)), eMMessage, eMCallBack));
        } catch (Exception e) {
            eMMessage.status = Status.FAIL;
            ContentValues contentValues = new ContentValues();
            contentValues.put("status", new StringBuilder(String.valueOf(eMMessage.status.ordinal())).toString());
            EMChatDB.getInstance().updateMessage(eMMessage.msgId, contentValues);
            e.printStackTrace();
            if (eMCallBack != null) {
                eMCallBack.onError(-2, e.getLocalizedMessage());
            }
        }
    }

    public void sendMessage(EMMessage eMMessage) throws EaseMobException {
        sendMessage(eMMessage, null);
    }

    public void sendMessage(EMMessage eMMessage, EMCallBack eMCallBack) {
        if (eMMessage.getChatType() == ChatType.GroupChat) {
            sendGroupMessage(eMMessage, eMCallBack);
            return;
        }
        try {
            String str;
            if (eMMessage.msgId == null) {
                eMMessage.msgId = getUniqueMessageId();
            }
            if (!this.allMessages.containsKey(eMMessage.msgId)) {
                EMChatDB.getInstance().saveMessage(eMMessage);
                addMessage(eMMessage);
            }
            checkConnection();
            eMMessage.status = Status.INPROGRESS;
            eMMessage.from = EMSessionManager.getInstance(null).currentUser;
            String str2 = eMMessage.to.eid;
            if (str2.contains("@")) {
                str = str2;
            } else {
                StringBuilder append = new StringBuilder(String.valueOf(str2)).append("@");
                EMChatConfig.getInstance();
                str = append.append(EMChatConfig.DOMAIN).toString();
            }
            Chat chat = (Chat) this.chats.get(str);
            if (chat == null) {
                chat = this.xmppChatManager.createChat(str, null);
                this.chats.put(str, chat);
            }
            this.sendThreadPool.execute(new SendMessageThread(chat, eMMessage, eMCallBack));
        } catch (Exception e) {
            eMMessage.status = Status.FAIL;
            ContentValues contentValues = new ContentValues();
            contentValues.put("status", new StringBuilder(String.valueOf(eMMessage.status.ordinal())).toString());
            EMChatDB.getInstance().updateMessage(eMMessage.msgId, contentValues);
            e.printStackTrace();
            if (eMCallBack != null) {
                eMCallBack.onError(-2, e.getLocalizedMessage());
            }
        }
    }

    public void setChatOptions(EMChatOptions eMChatOptions) {
        this.chatOptions = eMChatOptions;
    }

    public void setEncryptProvider(EncryptProvider encryptProvider) {
        this.encryptProvider = encryptProvider;
    }
}
