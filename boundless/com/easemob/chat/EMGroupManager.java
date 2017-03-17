package com.easemob.chat;

import android.content.Context;
import com.easemob.chat.core.XmppConnectionManager;
import com.easemob.exceptions.EMPermissionException;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.UserStatusListener;

public class EMGroupManager {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$easemob$chat$EMGroupManager$GroupEventType = null;
    private static final String PERMISSION_ERROR_ADD = "only group owner can add member";
    private static final String PERMISSION_ERROR_DELETE = "only group owner can delete group";
    private static final String PERMISSION_ERROR_REMOVE = "only group owner can remove member";
    private static String TAG = "group";
    private static EMGroupManager instance = null;
    Map<String, EMGroup> allGroups = null;
    private Context appContext;
    private boolean autoAcceptInvitation = true;
    private ArrayList<GroupChangeListener> groupChangeListeners = new ArrayList();
    private MUCInvitationListener invitationListener;
    private Map<String, MultiUserChat> multiUserChats = new HashMap();
    private Object mutex = new Object();
    private ArrayList<GroupChangeEvent> offlineGroupEvents = new ArrayList();
    private boolean receivedQuery = false;
    private XmppConnectionManager xmppConnectionManager;

    private class GroupChangeEvent {
        String groupId;
        String groupName;
        String inviterUserName;
        String reason;
        GroupEventType type;

        public GroupChangeEvent(GroupEventType groupEventType, String str, String str2, String str3, String str4) {
            this.type = groupEventType;
            this.groupId = str;
            this.groupName = str2;
            this.inviterUserName = str3;
            this.reason = str4;
        }
    }

    private enum GroupEventType {
        Invitate
    }

    private class MUCInvitationListener implements InvitationListener {
        private MUCInvitationListener() {
        }

        public void invitationReceived(Connection connection, String str, String str2, String str3, String str4, Message message) {
            String roomName;
            Iterator it;
            EMLog.d(EMGroupManager.TAG, "invitation received room:" + str + " inviter:" + str2 + " reason:" + str3 + " message:" + message.getBody());
            String userNameFromEid = EMContactManager.getUserNameFromEid(str2);
            String groupIdFromEid = EMContactManager.getGroupIdFromEid(str);
            try {
                RoomInfo roomInfo = MultiUserChat.getRoomInfo(XmppConnectionManager.getInstance().getConnection(), str);
                if (roomInfo != null) {
                    roomName = roomInfo.getRoomName();
                    if (EMGroupManager.this.autoAcceptInvitation) {
                        try {
                            EMLog.d(EMGroupManager.TAG, "auto accept group invitation for group:" + roomName);
                            EMGroupManager.this.acceptInvitation(groupIdFromEid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (EMChat.getInstance().appInited) {
                        EMLog.d(EMGroupManager.TAG, "aff offline group inviatation received event for group:" + roomName);
                        EMGroupManager.this.offlineGroupEvents.add(new GroupChangeEvent(GroupEventType.Invitate, groupIdFromEid, roomName, userNameFromEid, str3));
                        return;
                    }
                    it = EMGroupManager.this.groupChangeListeners.iterator();
                    while (it.hasNext()) {
                        GroupChangeListener groupChangeListener = (GroupChangeListener) it.next();
                        EMLog.d(EMGroupManager.TAG, "fire group inviatation received event for group:" + roomName);
                        groupChangeListener.onInvitationReceived(groupIdFromEid, roomName, userNameFromEid, str3);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            roomName = groupIdFromEid;
            if (EMGroupManager.this.autoAcceptInvitation) {
                EMLog.d(EMGroupManager.TAG, "auto accept group invitation for group:" + roomName);
                EMGroupManager.this.acceptInvitation(groupIdFromEid);
            }
            if (EMChat.getInstance().appInited) {
                EMLog.d(EMGroupManager.TAG, "aff offline group inviatation received event for group:" + roomName);
                EMGroupManager.this.offlineGroupEvents.add(new GroupChangeEvent(GroupEventType.Invitate, groupIdFromEid, roomName, userNameFromEid, str3));
                return;
            }
            it = EMGroupManager.this.groupChangeListeners.iterator();
            while (it.hasNext()) {
                GroupChangeListener groupChangeListener2 = (GroupChangeListener) it.next();
                EMLog.d(EMGroupManager.TAG, "fire group inviatation received event for group:" + roomName);
                groupChangeListener2.onInvitationReceived(groupIdFromEid, roomName, userNameFromEid, str3);
            }
        }
    }

    private class MUCPresenceListener implements PacketListener {
        private static final String ITEM_DESTROY = "destroy";
        private static final String ITEM_EXITMUC = "<item affiliation=\"none\" role=\"none\">";
        private static final String MUC_ELEMENT_NAME = "x";
        private static final String MUC_NS_USER = "http://jabber.org/protocol/muc#user";

        private void handleRoomDestroy(String str) {
            String groupIdFromEid = EMContactManager.getGroupIdFromEid(str);
            EMGroup eMGroup = (EMGroup) EMGroupManager.this.allGroups.get(groupIdFromEid);
            String str2 = "";
            if (eMGroup != null) {
                str2 = eMGroup.getGroupName();
            }
            EMLog.d(EMGroupManager.TAG, "group has been destroy on server:" + groupIdFromEid + " name:" + str2);
            EMGroupManager.this.deleteLocalGroup(groupIdFromEid);
            Iterator it = EMGroupManager.this.groupChangeListeners.iterator();
            while (it.hasNext()) {
                ((GroupChangeListener) it.next()).onGroupDestroy(groupIdFromEid, str2);
            }
        }

        private void handleUserRemove(String str) {
            int indexOf = str.indexOf("/");
            if (indexOf > 0) {
                String substring = str.substring(indexOf + 1);
                String groupIdFromEid = EMContactManager.getGroupIdFromEid(str.substring(0, indexOf - 1));
                if (substring.equals(EMChatManager.getInstance().getCurrentUser())) {
                    EMLog.d(EMGroupManager.TAG, "user " + substring + " has been removed from group:" + groupIdFromEid);
                    substring = "";
                    EMGroup eMGroup = (EMGroup) EMGroupManager.this.allGroups.get(groupIdFromEid);
                    if (eMGroup != null) {
                        substring = eMGroup.getGroupName();
                    }
                    EMGroupManager.this.deleteLocalGroup(groupIdFromEid);
                    Iterator it = EMGroupManager.this.groupChangeListeners.iterator();
                    while (it.hasNext()) {
                        ((GroupChangeListener) it.next()).onUserRemoved(groupIdFromEid, substring);
                    }
                }
            }
        }

        public synchronized void processPacket(Packet packet) {
            try {
                Presence presence = (Presence) packet;
                if (presence.getType() == Type.unavailable) {
                    PacketExtension extension = presence.getExtension("x", MUC_NS_USER);
                    if (extension != null) {
                        String toXML = extension.toXML();
                        if (toXML.contains(ITEM_DESTROY)) {
                            handleRoomDestroy(packet.getFrom());
                        } else if (toXML.contains(ITEM_EXITMUC)) {
                            handleUserRemove(packet.getFrom());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MUCSearchIQ extends IQ {
        public MUCSearchIQ(String str, String str2) {
            setType(IQ.Type.GET);
            setFrom(str);
            setTo(str2);
        }

        public String getChildElementXML() {
            return "<query xmlns='http://jabber.org/protocol/disco#items' node='http://jabber.org/protocol/muc#rooms'/>";
        }
    }

    private class MucUserStatusListener implements UserStatusListener {
        private String roomJid;

        public MucUserStatusListener(String str) {
            this.roomJid = str;
        }

        public void adminGranted() {
            System.out.println("admin granted");
        }

        public void adminRevoked() {
            System.out.println("admin revoked");
        }

        public void banned(String str, String str2) {
            System.out.println("banned actor:" + str + " reason:" + str2);
        }

        public void kicked(String str, String str2) {
            try {
                EMLog.d(EMGroupManager.TAG, "kicked actor:" + EMContactManager.getUserNameFromEid(str) + " reason:" + str2);
                String groupIdFromEid = EMContactManager.getGroupIdFromEid(this.roomJid);
                EMLog.d(EMGroupManager.TAG, "current user has been revoked membership. delete local group:" + groupIdFromEid);
                EMGroupManager.this.deleteLocalGroup(groupIdFromEid);
                Iterator it = EMGroupManager.this.groupChangeListeners.iterator();
                while (it.hasNext()) {
                    ((GroupChangeListener) it.next()).onUserRemoved(groupIdFromEid, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void membershipGranted() {
            System.out.println("membership granted");
        }

        public void membershipRevoked() {
            EMLog.d(EMGroupManager.TAG, "membership revoked");
            String groupIdFromEid = EMContactManager.getGroupIdFromEid(this.roomJid);
            EMLog.d(EMGroupManager.TAG, "current user has been revoked membership. delete local group:" + groupIdFromEid);
            EMGroupManager.this.deleteLocalGroup(groupIdFromEid);
            Iterator it = EMGroupManager.this.groupChangeListeners.iterator();
            while (it.hasNext()) {
                ((GroupChangeListener) it.next()).onUserRemoved(groupIdFromEid, "");
            }
        }

        public void moderatorGranted() {
            System.out.println("moderator granted");
        }

        public void moderatorRevoked() {
            System.out.println("moderator revoked");
        }

        public void ownershipGranted() {
            System.out.println("ownership granted");
        }

        public void ownershipRevoked() {
            System.out.println("ownership revoked");
        }

        public void voiceGranted() {
            System.out.println("voice granted");
        }

        public void voiceRevoked() {
            System.out.println("voice revoked");
        }
    }

    private class RoomQueryIQ extends IQ {
        private RoomQueryIQ() {
        }

        public String getChildElementXML() {
            return "<query xmlns=\"http://jabber.org/protocol/disco#items\" node=\"http://jabber.org/protocol/muc#rooms\"></query>";
        }
    }

    private class SearchPacketListener implements PacketListener {
        private SearchPacketListener() {
        }

        public void processPacket(Packet packet) {
            synchronized (EMGroupManager.this.mutex) {
                EMGroupManager.this.receivedQuery = true;
                EMGroupManager.this.mutex.notify();
            }
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$easemob$chat$EMGroupManager$GroupEventType() {
        int[] iArr = $SWITCH_TABLE$com$easemob$chat$EMGroupManager$GroupEventType;
        if (iArr == null) {
            iArr = new int[GroupEventType.values().length];
            try {
                iArr[GroupEventType.Invitate.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            $SWITCH_TABLE$com$easemob$chat$EMGroupManager$GroupEventType = iArr;
        }
        return iArr;
    }

    private EMGroupManager() {
    }

    private void addMuc(String str, MultiUserChat multiUserChat) {
        this.multiUserChats.put(str, multiUserChat);
        multiUserChat.addUserStatusListener(new MucUserStatusListener(str));
        EMGroup eMGroup = (EMGroup) this.allGroups.get(EMContactManager.getGroupIdFromEid(str));
        String str2 = "";
        if (eMGroup != null) {
            eMGroup.getGroupName();
        }
    }

    private void addUserToMUC(String str, String str2) throws XMPPException {
        EMLog.d(TAG, "muc add user:" + str2 + " to chat room:" + str);
        MultiUserChat muc = getMUC(str);
        muc.invite(str2, "EaseMob-Group");
        muc.grantMembership(str2);
    }

    private void checkGroupOwner(EMGroup eMGroup, String str) throws EMPermissionException {
        String owner = eMGroup.getOwner();
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        if (owner == null || !currentUser.equals(owner)) {
            throw new EMPermissionException(str);
        }
    }

    private void createPrivateXmppMUC(String str, String str2, String str3, String str4, boolean z) throws Exception {
        MultiUserChat multiUserChat = new MultiUserChat(this.xmppConnectionManager.getConnection(), str);
        EMLog.d(TAG, "create muc room jid:" + str + " roomName:" + str2 + " owner:" + str4 + " allowInvites:" + z);
        try {
            multiUserChat.create(str4);
            Form configurationForm = multiUserChat.getConfigurationForm();
            Form createAnswerForm = configurationForm.createAnswerForm();
            Iterator fields = configurationForm.getFields();
            while (fields.hasNext()) {
                FormField formField = (FormField) fields.next();
                if (!(FormField.TYPE_HIDDEN.equals(formField.getType()) || formField.getVariable() == null)) {
                    createAnswerForm.setDefaultAnswer(formField.getVariable());
                }
            }
            createAnswerForm.setAnswer("muc#roomconfig_persistentroom", true);
            createAnswerForm.setAnswer("muc#roomconfig_membersonly", true);
            createAnswerForm.setAnswer("muc#roomconfig_moderatedroom", true);
            createAnswerForm.setAnswer("muc#roomconfig_publicroom", false);
            createAnswerForm.setAnswer("members_by_default", true);
            createAnswerForm.setAnswer("muc#roomconfig_allowinvites", z);
            createAnswerForm.setAnswer("muc#roomconfig_roomname", str2);
            createAnswerForm.setAnswer("muc#roomconfig_roomdesc", str3);
            multiUserChat.sendConfigurationForm(createAnswerForm);
            multiUserChat.join(str4);
            addMuc(str, multiUserChat);
            EMLog.d(TAG, "muc created:" + multiUserChat.getRoom());
        } catch (XMPPException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createPublicXmppMUC(String str, String str2, String str3, String str4) throws Exception {
        MultiUserChat multiUserChat = new MultiUserChat(this.xmppConnectionManager.getConnection(), str);
        EMLog.d(TAG, "create muc room jid:" + str + " roomName:" + str2 + " owner:" + str4);
        try {
            multiUserChat.create(str4);
            Form configurationForm = multiUserChat.getConfigurationForm();
            Form createAnswerForm = configurationForm.createAnswerForm();
            Iterator fields = configurationForm.getFields();
            while (fields.hasNext()) {
                FormField formField = (FormField) fields.next();
                if (!(FormField.TYPE_HIDDEN.equals(formField.getType()) || formField.getVariable() == null)) {
                    createAnswerForm.setDefaultAnswer(formField.getVariable());
                }
            }
            createAnswerForm.setAnswer("muc#roomconfig_persistentroom", true);
            createAnswerForm.setAnswer("muc#roomconfig_membersonly", false);
            createAnswerForm.setAnswer("muc#roomconfig_moderatedroom", false);
            createAnswerForm.setAnswer("muc#roomconfig_publicroom", true);
            createAnswerForm.setAnswer("members_by_default", true);
            createAnswerForm.setAnswer("muc#roomconfig_roomname", str2);
            createAnswerForm.setAnswer("muc#roomconfig_roomdesc", str3);
            multiUserChat.sendConfigurationForm(createAnswerForm);
            multiUserChat.join(str4);
            addMuc(str, multiUserChat);
            EMLog.d(TAG, "muc created:" + multiUserChat.getRoom());
        } catch (XMPPException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private synchronized void deleteAllLocalGroups() {
        for (String str : this.allGroups.keySet()) {
            EMChatDB.getInstance().deleteGroup(str);
            EMChatDB.getInstance().deleteGroupConversions(str);
        }
        this.allGroups.clear();
    }

    private void deleteMUC(String str) throws XMPPException {
        getMUC(str).destroy("delete-group", null);
    }

    private String formatGroupName(String str) {
        int lastIndexOf = str.lastIndexOf(Token.SEPARATOR);
        return (!str.endsWith(")") || lastIndexOf <= 0) ? str : str.substring(0, lastIndexOf);
    }

    private String generateGroupId() {
        return DateUtils.getTimestampStr();
    }

    public static EMGroupManager getInstance() {
        if (instance == null) {
            instance = new EMGroupManager();
        }
        return instance;
    }

    private List<EMGroup> getJoinedMUCs(String str) throws EaseMobException, XMPPException {
        List arrayList = new ArrayList();
        Collection<HostedRoom> hostedRooms = MultiUserChat.getHostedRooms(this.xmppConnectionManager.getConnection(), "conference.easemob.com");
        EMLog.d(TAG, "joined room size:" + hostedRooms.size());
        for (HostedRoom hostedRoom : hostedRooms) {
            EMLog.d(TAG, "joined room room jid:" + hostedRoom.getJid() + " name:" + hostedRoom.getName());
            try {
                EMGroup muc = getMUC(hostedRoom.getJid(), EMChatManager.getInstance().getCurrentUser(), false, true);
                if (muc != null) {
                    EMLog.d(TAG, "  get group detail:" + muc.getGroupName());
                    arrayList.add(muc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EMLog.d(TAG, " retrieved groups from server:" + arrayList.size());
        return arrayList;
    }

    private List<EMGroupInfo> getPublicMUCs(String str, String str2) throws EaseMobException, XMPPException {
        List arrayList = new ArrayList();
        Collection<HostedRoom> publicRooms = MultiUserChat.getPublicRooms(this.xmppConnectionManager.getConnection(), "conference.easemob.com", str2);
        EMLog.d(TAG, "public room size:" + publicRooms.size());
        for (HostedRoom hostedRoom : publicRooms) {
            String formatGroupName = formatGroupName(hostedRoom.getName());
            EMLog.d(TAG, "joined room room jid:" + hostedRoom.getJid() + " name:" + formatGroupName);
            arrayList.add(new EMGroupInfo(EMContactManager.getGroupIdFromEid(hostedRoom.getJid()), formatGroupName));
        }
        EMLog.d(TAG, " retrieved public groups from server:" + arrayList.size());
        return arrayList;
    }

    private void inviteUserMUC(String str, List<String> list, String str2) throws XMPPException {
        MultiUserChat muc = getMUC(str);
        EMGroupManager instance = getInstance();
        EMContactManager.getInstance();
        EMGroup group = instance.getGroup(EMContactManager.getGroupIdFromEid(str));
        if (list != null && list.size() != 0) {
            for (String str3 : list) {
                muc.invite(str3, str2);
                if (group.isAllowInvites()) {
                    EMContactManager.getInstance();
                    group.addMember(EMContactManager.getUserNameFromEid(str3));
                }
            }
        }
    }

    private void leaveMUC(String str, String str2) throws XMPPException {
        getMUC(str).leave();
    }

    private void leaveMUCRemoveMember(String str, String str2) throws XMPPException {
        MultiUserChat muc = getMUC(str);
        muc.leave();
        try {
            muc.revokeMembership(str2);
        } catch (Exception e) {
        }
    }

    private void removeMuc(String str) {
        if (((MultiUserChat) this.multiUserChats.remove(str)) == null) {
        }
    }

    private void removeUserFromMUC(String str, String str2) throws Exception {
        EMLog.d(TAG, "muc remove user:" + str2 + " from chat room:" + str);
        MultiUserChat muc = getMUC(str);
        muc.revokeMembership(str2);
        String userNameFromEid = EMContactManager.getUserNameFromEid(str2);
        try {
            EMLog.d(TAG, "try to kick user if already joined");
            muc.kickParticipant(userNameFromEid, "RemoveFromGroup");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void retrieveUserMucsOnServer(String str) throws Exception {
        Packet mUCSearchIQ = new MUCSearchIQ(str, str);
        XMPPConnection connection = this.xmppConnectionManager.getConnection();
        connection.addPacketListener(new SearchPacketListener(), new PacketTypeFilter(IQ.class) {
            public boolean accept(Packet packet) {
                if (packet instanceof IQ) {
                    IQ iq = (IQ) packet;
                    if (iq.getType().equals(IQ.Type.RESULT)) {
                        System.err.println("childXML:" + iq.getChildElementXML());
                        new Exception().printStackTrace();
                        return true;
                    }
                }
                return false;
            }
        });
        this.receivedQuery = true;
        connection.sendPacket(mUCSearchIQ);
        synchronized (this.mutex) {
            this.mutex.wait(10000);
        }
        if (!this.receivedQuery) {
            EMLog.e(TAG, "server no response for group search");
            throw new EaseMobException("server timeout");
        }
    }

    private void syncGroupsWithRemoteGroupList(List<EMGroup> list) {
        for (EMGroup eMGroup : list) {
            if (this.allGroups.containsKey(eMGroup.getGroupId())) {
                EMLog.d(TAG, " group sync. local already exists:" + eMGroup.getGroupId());
            } else {
                createOrUpdateLocalGroup(eMGroup);
            }
        }
        Set<String> keySet = this.allGroups.keySet();
        ArrayList arrayList = new ArrayList();
        for (String str : keySet) {
            Object obj;
            for (EMGroup groupId : list) {
                String str2;
                if (groupId.getGroupId().equals(str2)) {
                    obj = 1;
                    break;
                }
            }
            obj = null;
            if (obj == null) {
                arrayList.add(str2);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            str2 = (String) it.next();
            EMLog.d(TAG, "delete local group which not exists on server:" + str2);
            deleteLocalGroup(str2);
        }
    }

    public void acceptInvitation(String str) throws EaseMobException {
        try {
            String eidFromGroupId = EMContactManager.getEidFromGroupId(str);
            createOrUpdateLocalGroup(getGroupFromServer(str));
            MultiUserChat muc = getMUC(eidFromGroupId);
            if (!muc.isJoined()) {
                muc.join(EMChatManager.getInstance().getCurrentUser());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public void addGroupChangeListener(GroupChangeListener groupChangeListener) {
        EMLog.d(TAG, "add group change listener:" + groupChangeListener.getClass().getName());
        if (!this.groupChangeListeners.contains(groupChangeListener)) {
            this.groupChangeListeners.add(groupChangeListener);
        }
    }

    public void addUsersToGroup(String str, String[] strArr) throws EaseMobException {
        int i = 0;
        EMGroup eMGroup = (EMGroup) this.allGroups.get(str);
        if (eMGroup == null) {
            throw new EaseMobException("group doesn't exist:" + str);
        }
        checkGroupOwner(eMGroup, PERMISSION_ERROR_ADD);
        try {
            String eidFromGroupId = EMContactManager.getEidFromGroupId(str);
            for (String eidFromUserName : strArr) {
                addUserToMUC(eidFromGroupId, EMContactManager.getEidFromUserName(eidFromUserName));
            }
            int length = strArr.length;
            while (i < length) {
                eMGroup.addMember(strArr[i]);
                i++;
            }
            EMChatDB.getInstance().updateGroup(eMGroup);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public EMGroup createGroup(String str, String str2, String[] strArr) throws EaseMobException {
        return createPrivateGroup(str, str2, strArr);
    }

    public EMGroup createOrUpdateLocalGroup(EMGroup eMGroup) {
        if (EMChatDB.getInstance().loadGroup(eMGroup.getGroupId()) == null) {
            EMChatDB.getInstance().saveGroup(eMGroup);
        } else {
            EMChatDB.getInstance().updateGroup(eMGroup);
        }
        EMGroup eMGroup2 = (EMGroup) this.allGroups.get(eMGroup.getGroupId());
        if (eMGroup2 != null) {
            eMGroup2.copyGroup(eMGroup);
            return eMGroup2;
        }
        this.allGroups.put(eMGroup.getGroupId(), eMGroup);
        return eMGroup;
    }

    public EMGroup createPrivateGroup(String str, String str2, String[] strArr) throws EaseMobException {
        return createPrivateGroup(str, str2, strArr, false);
    }

    public EMGroup createPrivateGroup(String str, String str2, String[] strArr, boolean z) throws EaseMobException {
        String generateGroupId = generateGroupId();
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        String eidFromGroupId = EMContactManager.getEidFromGroupId(generateGroupId);
        try {
            createPrivateXmppMUC(eidFromGroupId, str, str2, currentUser, z);
            for (String eidFromUserName : strArr) {
                addUserToMUC(eidFromGroupId, EMContactManager.getEidFromUserName(eidFromUserName));
            }
            EMGroup eMGroup = new EMGroup(generateGroupId);
            eMGroup.setGroupName(str);
            eMGroup.setDescription(str2);
            eMGroup.setOwner(EMChatManager.getInstance().getCurrentUser());
            List arrayList = new ArrayList();
            arrayList.add(eMGroup.getOwner());
            for (Object add : strArr) {
                arrayList.add(add);
            }
            eMGroup.setMembers(arrayList);
            EMChatDB.getInstance().saveGroup(eMGroup);
            this.allGroups.put(eMGroup.getGroupId(), eMGroup);
            return eMGroup;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public EMGroup createPublicGroup(String str, String str2, String[] strArr, boolean z) throws EaseMobException {
        int i = 0;
        String generateGroupId = generateGroupId();
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        String eidFromGroupId = EMContactManager.getEidFromGroupId(generateGroupId);
        try {
            createPublicXmppMUC(eidFromGroupId, str, str2, currentUser);
            for (String eidFromUserName : strArr) {
                addUserToMUC(eidFromGroupId, EMContactManager.getEidFromUserName(eidFromUserName));
            }
            EMGroup eMGroup = new EMGroup(generateGroupId);
            eMGroup.setGroupName(str);
            eMGroup.setDescription(str2);
            eMGroup.setOwner(EMChatManager.getInstance().getCurrentUser());
            List arrayList = new ArrayList();
            arrayList.add(eMGroup.getOwner());
            int length = strArr.length;
            while (i < length) {
                arrayList.add(strArr[i]);
                i++;
            }
            eMGroup.setMembers(arrayList);
            EMChatDB.getInstance().saveGroup(eMGroup);
            this.allGroups.put(eMGroup.getGroupId(), eMGroup);
            return eMGroup;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public void declineInvitation(String str, String str2, String str3) {
        EMLog.d(TAG, "decline invitation:" + str + " inviter:" + str2 + " reason" + str3);
        try {
            MultiUserChat.decline(XmppConnectionManager.getInstance().getConnection(), EMContactManager.getEidFromGroupId(str), EMContactManager.getEidFromUserName(str2), str3);
            deleteLocalGroup(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLocalGroup(String str) {
        EMLog.d(TAG, "delete local group:" + str);
        EMChatDB.getInstance().deleteGroup(str);
        this.allGroups.remove(str);
        EMChatManager.getInstance().deleteConversation(str);
    }

    public void exitAndDeleteGroup(String str) throws EaseMobException {
        EMGroup eMGroup = (EMGroup) this.allGroups.get(str);
        if (eMGroup == null) {
            throw new EaseMobException("group doesn't exist:" + str);
        }
        checkGroupOwner(eMGroup, PERMISSION_ERROR_DELETE);
        try {
            deleteMUC(EMContactManager.getEidFromGroupId(str));
            if (((EMGroup) this.allGroups.get(str)) != null) {
                deleteLocalGroup(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public void exitFromGroup(String str) throws EaseMobException {
        try {
            EMGroup eMGroup = (EMGroup) this.allGroups.get(str);
            String eidFromGroupId = EMContactManager.getEidFromGroupId(str);
            String eidFromUserName = EMContactManager.getEidFromUserName(EMChatManager.getInstance().getCurrentUser());
            if (eMGroup.getIsPublic()) {
                leaveMUCRemoveMember(eidFromGroupId, eidFromUserName);
            } else {
                leaveMUCRemoveMember(eidFromGroupId, eidFromUserName);
            }
            deleteLocalGroup(str);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public List<EMGroup> getAllGroups() {
        return Collections.unmodifiableList(new ArrayList(this.allGroups.values()));
    }

    public List<EMGroupInfo> getAllPublicGroupsFromServer() throws EaseMobException {
        try {
            return getPublicMUCs(EMContactManager.getEidFromUserName(EMChatManager.getInstance().getCurrentUser()), EMChatConfig.getInstance().APPKEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public EMGroup getGroup(String str) {
        return (EMGroup) this.allGroups.get(str);
    }

    public EMGroup getGroupFromServer(String str) throws EaseMobException {
        try {
            EMGroup muc = getMUC(EMContactManager.getEidFromGroupId(str), EMChatManager.getInstance().getCurrentUser(), true, false);
            if (muc != null) {
                return muc;
            }
            EMLog.d(TAG, "no group on server or meet error with groupid:" + str);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public synchronized List<EMGroup> getGroupsFromServer() throws EaseMobException {
        List<EMGroup> joinedMUCs;
        try {
            joinedMUCs = getJoinedMUCs(EMContactManager.getEidFromUserName(EMChatManager.getInstance().getCurrentUser()));
            syncGroupsWithRemoteGroupList(joinedMUCs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
        return joinedMUCs;
    }

    EMGroup getMUC(String str, String str2, boolean z, boolean z2) throws XMPPException {
        RoomInfo roomInfo = MultiUserChat.getRoomInfo(XmppConnectionManager.getInstance().getConnection(), str);
        if (roomInfo == null) {
            return null;
        }
        String roomName = roomInfo.getRoomName();
        String description = roomInfo.getDescription();
        EMGroup eMGroup = new EMGroup(EMContactManager.getUserNameFromEid(str));
        eMGroup.setGroupName(roomName);
        eMGroup.setDescription(description);
        eMGroup.membersOnly = roomInfo.isMembersOnly();
        eMGroup.isPublic = roomInfo.isPublic();
        eMGroup.allowInvites = roomInfo.isAllowInvites();
        EMLog.d(TAG, "get room info for roomjid:" + str + " name:" + roomName + " desc:" + description + " ispublic:" + eMGroup.getIsPublic() + " ismemberonly:" + eMGroup.isMembersOnly() + " isallowinvites:" + eMGroup.isAllowInvites() + " isjoin:" + z2);
        MultiUserChat muc = getMUC(str);
        if (z2) {
            muc.join(str2);
        }
        if (!z) {
            return eMGroup;
        }
        try {
            String userNameFromEid;
            Iterator it = muc.getOwners().iterator();
            if (it.hasNext()) {
                userNameFromEid = EMContactManager.getUserNameFromEid(((Affiliate) it.next()).getJid());
                eMGroup.setOwner(userNameFromEid);
                EMLog.d(TAG, " room owner:" + userNameFromEid);
            }
            eMGroup.addMember(eMGroup.getOwner());
            for (Affiliate jid : muc.getMembers()) {
                userNameFromEid = EMContactManager.getUserNameFromEid(jid.getJid());
                eMGroup.addMember(userNameFromEid);
                EMLog.d(TAG, "  room member:" + userNameFromEid);
            }
            if (eMGroup.getIsPublic()) {
                for (Occupant jid2 : muc.getParticipants()) {
                    userNameFromEid = EMContactManager.getUserNameFromEid(jid2.getJid());
                    if (!eMGroup.members.contains(userNameFromEid)) {
                        eMGroup.addMember(userNameFromEid);
                        EMLog.d(TAG, "  room participant member:" + userNameFromEid);
                    }
                }
            }
            return eMGroup;
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.d(TAG, "error when retrieve group info from server:" + e.toString());
            removeMuc(str);
            return null;
        }
    }

    synchronized MultiUserChat getMUC(String str) throws XMPPException {
        MultiUserChat multiUserChat;
        if (!str.contains("@")) {
            str = new StringBuilder(String.valueOf(str)).append(EMChatConfig.MUC_DOMAIN_SUFFIX).toString();
        }
        multiUserChat = (MultiUserChat) this.multiUserChats.get(str);
        if (multiUserChat == null) {
            multiUserChat = new MultiUserChat(XmppConnectionManager.getInstance().getConnection(), str);
            addMuc(str, multiUserChat);
        }
        if (!multiUserChat.isJoined()) {
            String currentUser = EMChatManager.getInstance().getCurrentUser();
            multiUserChat.join(currentUser);
            EMLog.d(TAG, "joined muc:" + multiUserChat.getRoom() + " with eid:" + currentUser);
        }
        return multiUserChat;
    }

    void init(Context context, XmppConnectionManager xmppConnectionManager) {
        EMLog.d(TAG, "init group manager");
        this.appContext = context;
        this.xmppConnectionManager = xmppConnectionManager;
        this.invitationListener = new MUCInvitationListener();
        MultiUserChat.addInvitationListener(xmppConnectionManager.getConnection(), this.invitationListener);
        this.multiUserChats.clear();
        PacketFilter packetTypeFilter = new PacketTypeFilter(Presence.class);
        XmppConnectionManager.getInstance().getConnection().addPacketListener(new MUCPresenceListener(), packetTypeFilter);
    }

    public void inviteUser(String str, String[] strArr, String str2) throws EaseMobException {
        try {
            EMLog.d(TAG, "invite usernames:" + strArr + " to group:" + str + " reason:" + str2);
            if (str2 == null) {
                str2 = "";
            }
            String eidFromGroupId = EMContactManager.getEidFromGroupId(str);
            List arrayList = new ArrayList();
            for (String eidFromUserName : strArr) {
                arrayList.add(EMContactManager.getEidFromUserName(eidFromUserName));
            }
            inviteUserMUC(eidFromGroupId, arrayList, str2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    public void joinGroup(String str) throws EaseMobException {
        try {
            EMLog.d(TAG, "try to joinPublicGroup, current user:" + EMChatManager.getInstance().getCurrentUser() + " groupId:" + str);
            String eidFromGroupId = EMContactManager.getEidFromGroupId(str);
            createOrUpdateLocalGroup(getGroupFromServer(str));
            MultiUserChat muc = getMUC(eidFromGroupId);
            if (!(muc == null || muc.isJoined())) {
                muc.join(EMChatManager.getInstance().getCurrentUser());
            }
            EMContactManager.getEidFromUserName(EMChatManager.getInstance().getCurrentUser());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EaseMobException(e.toString());
        }
    }

    void joinGroupsAfterLogin() {
        new Thread() {
            public void run() {
                List<EMGroup> allGroups = EMGroupManager.this.getAllGroups();
                EMLog.d(EMGroupManager.TAG, "join groups. size:" + allGroups.size());
                for (EMGroup groupId : allGroups) {
                    try {
                        EMGroupManager.this.getMUC(EMContactManager.getEidFromGroupId(groupId.getGroupId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                EMLog.d(EMGroupManager.TAG, "join groups thread finished.");
            }
        }.start();
    }

    void joinMUC(String str, String str2) throws XMPPException {
        MultiUserChat multiUserChat = (MultiUserChat) this.multiUserChats.get(str);
        if (multiUserChat == null) {
            multiUserChat = new MultiUserChat(XmppConnectionManager.getInstance().getConnection(), str);
        }
        multiUserChat.join(str2);
        EMLog.d(TAG, "joined muc:" + str);
        try {
            Collection<Affiliate> members = multiUserChat.getMembers();
            EMLog.d(TAG, "  room members size:" + members.size());
            for (Affiliate affiliate : members) {
                EMLog.d(TAG, "  member jid:" + affiliate.getJid() + " role:" + affiliate.getRole());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadAllGroups() {
        this.allGroups = EMChatDB.getInstance().loadAllGroups();
        EMLog.d(TAG, "load all groups from db. size:" + this.allGroups.values().size());
    }

    void logout() {
        EMLog.d(TAG, "group manager logout");
        this.allGroups.clear();
        this.multiUserChats.clear();
        this.groupChangeListeners.clear();
        try {
            MultiUserChat.removeInvitationListener(this.xmppConnectionManager.getConnection(), this.invitationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.offlineGroupEvents.clear();
    }

    void processOfflineMessages() {
        EMLog.d(TAG, "process offline group event start: " + this.offlineGroupEvents.size());
        Iterator it = this.offlineGroupEvents.iterator();
        while (it.hasNext()) {
            GroupChangeEvent groupChangeEvent = (GroupChangeEvent) it.next();
            switch ($SWITCH_TABLE$com$easemob$chat$EMGroupManager$GroupEventType()[groupChangeEvent.type.ordinal()]) {
                case 1:
                    Iterator it2 = this.groupChangeListeners.iterator();
                    while (it2.hasNext()) {
                        GroupChangeListener groupChangeListener = (GroupChangeListener) it2.next();
                        EMLog.d(TAG, "fire group inviatation received event for group:" + groupChangeEvent.groupName + " listener:" + groupChangeListener.hashCode());
                        groupChangeListener.onInvitationReceived(groupChangeEvent.groupId, groupChangeEvent.groupName, groupChangeEvent.inviterUserName, groupChangeEvent.reason);
                    }
                    break;
                default:
                    break;
            }
        }
        this.offlineGroupEvents.clear();
        EMLog.d(TAG, "proess offline group event finish");
    }

    public void removeGroupChangeListener(GroupChangeListener groupChangeListener) {
        EMLog.d(TAG, "remove group change listener:" + groupChangeListener.getClass().getName());
        this.groupChangeListeners.remove(groupChangeListener);
    }

    void removeMucs() {
        this.multiUserChats.clear();
    }

    public void removeUserFromGroup(String str, String str2) throws EaseMobException {
        EMGroup eMGroup = (EMGroup) this.allGroups.get(str);
        if (eMGroup == null) {
            throw new EaseMobException("group doesn't exist:" + str);
        }
        checkGroupOwner(eMGroup, PERMISSION_ERROR_REMOVE);
        try {
            removeUserFromMUC(EMContactManager.getEidFromGroupId(str), EMContactManager.getEidFromUserName(str2));
            eMGroup.removeMember(str2);
            EMChatDB.getInstance().updateGroup(eMGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoAcceptInvitation(boolean z) {
        this.autoAcceptInvitation = z;
    }
}
