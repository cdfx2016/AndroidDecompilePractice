package com.easemob.chat;

import android.content.Context;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterStorage;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

public class EMContactManager {
    private static final String BROADCAST_CONTACT_CHANGED_ACTION = "com.easemob.contact.changed";
    private static final String TAG = "contact";
    private static EMContactManager instance = null;
    EMContactListener contactListener = null;
    Map<String, EMContact> contactTable = new Hashtable(100);
    private Context context;
    Set<String> deleteContactsSet = null;
    boolean enableRosterVersion = false;
    private Roster roster;
    private EMRosterListener rosterListener = null;
    RosterStorageImpl rosterStorage;
    private XmppConnectionManager xmppConnectionManager;

    private EMContactManager() {
    }

    static String getBareEidFromUserName(String str) {
        return new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").append(str).toString();
    }

    public static String getContactChangeAction() {
        return "com.easemob.contact.changed_" + EMChatConfig.getInstance().APPKEY;
    }

    static String getEidFromGroupId(String str) {
        return str.contains("@") ? str : new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").append(str).append(EMChatConfig.MUC_DOMAIN_SUFFIX).toString();
    }

    static String getEidFromUserName(String str) {
        if (str.contains("@")) {
            return str;
        }
        if (str.equals("bot")) {
            return "bot@echo.easemob.com";
        }
        StringBuilder append = new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").append(str).append("@");
        EMChatConfig.getInstance();
        return append.append(EMChatConfig.DOMAIN).toString();
    }

    static String getGroupIdFromEid(String str) {
        String substring = str.contains("@") ? str.substring(0, str.indexOf("@")) : str;
        if (!(substring == null || "".equals(substring))) {
            str = substring;
        }
        return str.startsWith(EMChatConfig.getInstance().APPKEY) ? str.substring(new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").toString().length()) : str;
    }

    public static EMContactManager getInstance() {
        if (instance == null) {
            instance = new EMContactManager();
        }
        return instance;
    }

    static String getUserNameFromEid(String str) {
        String substring = str.contains("@") ? str.substring(0, str.indexOf("@")) : str;
        if (!(substring == null || "".equals(substring))) {
            str = substring;
        }
        return str.startsWith(EMChatConfig.getInstance().APPKEY) ? str.substring(new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").toString().length()) : str;
    }

    public void addContact(String str, String str2) throws EaseMobException {
        addContactToRosterThroughPresence(str, str2);
    }

    void addContactFromDb(EMContact eMContact) {
        EMLog.d(TAG, "add contact from db jid:" + eMContact.eid);
        this.contactTable.put(eMContact.username, eMContact);
    }

    void addContactInternal(EMContact eMContact) {
        EMLog.d(TAG, "try to add contact:" + eMContact.eid);
        this.contactTable.put(eMContact.username, eMContact);
    }

    void addContactToRosterThroughPresence(String str, String str2) throws EaseMobException {
        try {
            EMChatManager.getInstance().checkConnection();
            Packet presence = new Presence(Type.subscribe);
            presence.setTo(getEidFromUserName(str));
            if (!(str2 == null || "".equals(str2))) {
                presence.setStatus(str2);
            }
            XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
        } catch (Exception e) {
            throw new EaseMobException(e.getMessage());
        }
    }

    void checkConnection() throws EaseMobException {
        if (this.xmppConnectionManager != null && this.xmppConnectionManager.getConnection() != null) {
            if (!this.xmppConnectionManager.getConnection().isConnected() || !this.xmppConnectionManager.getConnection().isAuthenticated()) {
                EMLog.e(TAG, "network unconnected");
                if (NetUtils.hasDataConnection(EMChatConfig.getInstance().applicationContext)) {
                    EMLog.d(TAG, "try to reconnect after check connection failed");
                    this.xmppConnectionManager.reconnectASync();
                }
            }
        }
    }

    public void deleteContact(String str) throws EaseMobException {
        removeContactFromRoster(str);
    }

    EMContact getContactByUserName(String str) {
        EMContact eMContact = (EMContact) this.contactTable.get(str);
        if (eMContact != null) {
            return eMContact;
        }
        eMContact = new EMContact(str);
        addContactInternal(eMContact);
        return eMContact;
    }

    String getCurrentUserFullJid() {
        String str = EMSessionManager.getInstance(this.context).currentUser.username;
        return getEidFromUserName(str) + "/" + XmppConnectionManager.getXmppResource(this.context);
    }

    RosterStorage getRosterStorage(Context context) {
        if (this.rosterStorage == null) {
            this.rosterStorage = new RosterStorageImpl(context, this);
        }
        return this.rosterStorage;
    }

    List<String> getRosterUserNames() throws EaseMobException {
        EMChatManager.getInstance().checkConnection();
        EMLog.d(TAG, "start to get roster for user:" + EMSessionManager.getInstance(null).getLoginUserName());
        Collection<RosterEntry> entries = this.roster.getEntries();
        EMLog.d(TAG, "get roster return size:" + entries.size());
        List<String> arrayList = new ArrayList();
        for (RosterEntry rosterEntry : entries) {
            EMLog.d(TAG, "entry name:" + rosterEntry.getName() + " user:" + rosterEntry.getUser());
            if (rosterEntry.getType() == ItemType.both || rosterEntry.getType() == ItemType.from) {
                String name = rosterEntry.getName();
                String userNameFromEid = (name == null || name.equals("")) ? getUserNameFromEid(rosterEntry.getUser()) : name;
                if (userNameFromEid.startsWith(EMChatConfig.getInstance().APPKEY)) {
                    userNameFromEid = userNameFromEid.substring(new StringBuilder(String.valueOf(EMChatConfig.getInstance().APPKEY)).append("_").toString().length());
                }
                EMLog.d(TAG, "get roster contact:" + userNameFromEid);
                arrayList.add(userNameFromEid);
            }
        }
        return arrayList;
    }

    void init(Context context, XmppConnectionManager xmppConnectionManager) {
        EMLog.d(TAG, "try to init contact manager");
        this.context = context;
        this.xmppConnectionManager = xmppConnectionManager;
        this.deleteContactsSet = Collections.synchronizedSet(new HashSet());
        this.roster = xmppConnectionManager.getConnection().getRoster();
        this.rosterListener = new EMRosterListener(this, this.roster);
        this.roster.addRosterListener(this.rosterListener);
        EMLog.d(TAG, "created contact manager");
    }

    void removeContactByUsername(String str) {
        EMLog.d(TAG, "removed contact:" + ((EMContact) this.contactTable.remove(str)));
    }

    void removeContactFromRoster(String str) throws EaseMobException {
        try {
            this.roster.removeEntry(this.roster.getEntry(getEidFromUserName(str)));
        } catch (Throwable e) {
            EMLog.e(TAG, "Failed to delete contact:", e);
            throw new EaseMobException("Failed to delete contact:" + e);
        }
    }

    public void removeContactListener() {
        this.contactListener = null;
    }

    public void reset() {
        this.contactTable.clear();
        this.roster = null;
    }

    public void setContactListener(EMContactListener eMContactListener) {
        this.contactListener = eMContactListener;
    }
}
