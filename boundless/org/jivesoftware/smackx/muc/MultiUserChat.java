package org.jivesoftware.smackx.muc;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.NodeInformationProvider;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.packet.MUCAdmin;
import org.jivesoftware.smackx.packet.MUCInitialPresence;
import org.jivesoftware.smackx.packet.MUCOwner;
import org.jivesoftware.smackx.packet.MUCOwner.Destroy;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Decline;
import org.jivesoftware.smackx.packet.MUCUser.Invite;

public class MultiUserChat {
    private static final String discoNamespace = "http://jabber.org/protocol/muc";
    private static final String discoNode = "http://jabber.org/protocol/muc#rooms";
    private static Map<Connection, List<String>> joinedRooms = new WeakHashMap();
    private Connection connection;
    private List<PacketListener> connectionListeners = new ArrayList();
    private final List<InvitationRejectionListener> invitationRejectionListeners = new ArrayList();
    private boolean joined = false;
    private ConnectionDetachedPacketCollector messageCollector;
    private PacketFilter messageFilter;
    private String nickname = null;
    private Map<String, Presence> occupantsMap = new ConcurrentHashMap();
    private final List<ParticipantStatusListener> participantStatusListeners = new ArrayList();
    private PacketFilter presenceFilter;
    private List<PacketInterceptor> presenceInterceptors = new ArrayList();
    private String room;
    private RoomListenerMultiplexor roomListenerMultiplexor;
    private String subject;
    private final List<SubjectUpdatedListener> subjectUpdatedListeners = new ArrayList();
    private final List<UserStatusListener> userStatusListeners = new ArrayList();

    private static class InvitationsMonitor implements ConnectionListener {
        private static final Map<Connection, WeakReference<InvitationsMonitor>> monitors = new WeakHashMap();
        private Connection connection;
        private PacketFilter invitationFilter;
        private PacketListener invitationPacketListener;
        private final List<InvitationListener> invitationsListeners = new ArrayList();

        private InvitationsMonitor(Connection connection) {
            this.connection = connection;
        }

        private void cancel() {
            this.connection.removePacketListener(this.invitationPacketListener);
            this.connection.removeConnectionListener(this);
        }

        private void fireInvitationListeners(String str, String str2, String str3, String str4, Message message) {
            synchronized (this.invitationsListeners) {
                InvitationListener[] invitationListenerArr = new InvitationListener[this.invitationsListeners.size()];
                this.invitationsListeners.toArray(invitationListenerArr);
            }
            for (InvitationListener invitationReceived : invitationListenerArr) {
                invitationReceived.invitationReceived(this.connection, str, str2, str3, str4, message);
            }
        }

        public static InvitationsMonitor getInvitationsMonitor(Connection connection) {
            InvitationsMonitor invitationsMonitor;
            synchronized (monitors) {
                if (!monitors.containsKey(connection) || ((WeakReference) monitors.get(connection)).get() == null) {
                    invitationsMonitor = new InvitationsMonitor(connection);
                    monitors.put(connection, new WeakReference(invitationsMonitor));
                } else {
                    invitationsMonitor = (InvitationsMonitor) ((WeakReference) monitors.get(connection)).get();
                }
            }
            return invitationsMonitor;
        }

        private void init() {
            this.invitationFilter = new PacketExtensionFilter("x", "http://jabber.org/protocol/muc#user");
            this.invitationPacketListener = new PacketListener() {
                public void processPacket(Packet packet) {
                    MUCUser mUCUser = (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user");
                    if (mUCUser.getInvite() != null && ((Message) packet).getType() != Type.error) {
                        InvitationsMonitor.this.fireInvitationListeners(packet.getFrom(), mUCUser.getInvite().getFrom(), mUCUser.getInvite().getReason(), mUCUser.getPassword(), (Message) packet);
                    }
                }
            };
            this.connection.addPacketListener(this.invitationPacketListener, this.invitationFilter);
            this.connection.addConnectionListener(this);
        }

        public void addInvitationListener(InvitationListener invitationListener) {
            synchronized (this.invitationsListeners) {
                if (this.invitationsListeners.size() == 0) {
                    init();
                }
                if (!this.invitationsListeners.contains(invitationListener)) {
                    this.invitationsListeners.add(invitationListener);
                }
            }
        }

        public void connectionClosed() {
            cancel();
        }

        public void connectionClosedOnError(Exception exception) {
        }

        public void reconnectingIn(int i) {
        }

        public void reconnectionFailed(Exception exception) {
        }

        public void reconnectionSuccessful() {
        }

        public void removeInvitationListener(InvitationListener invitationListener) {
            synchronized (this.invitationsListeners) {
                if (this.invitationsListeners.contains(invitationListener)) {
                    this.invitationsListeners.remove(invitationListener);
                }
                if (this.invitationsListeners.size() == 0) {
                    cancel();
                }
            }
        }
    }

    static {
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(final Connection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection).addFeature(MultiUserChat.discoNamespace);
                ServiceDiscoveryManager.getInstanceFor(connection).setNodeInformationProvider(MultiUserChat.discoNode, new NodeInformationProvider() {
                    public List<String> getNodeFeatures() {
                        return null;
                    }

                    public List<Identity> getNodeIdentities() {
                        return null;
                    }

                    public List<Item> getNodeItems() {
                        List<Item> arrayList = new ArrayList();
                        Iterator access$000 = MultiUserChat.getJoinedRooms(connection);
                        while (access$000.hasNext()) {
                            arrayList.add(new Item((String) access$000.next()));
                        }
                        return arrayList;
                    }

                    public List<PacketExtension> getNodePacketExtensions() {
                        return null;
                    }
                });
            }
        });
    }

    public MultiUserChat(Connection connection, String str) {
        this.connection = connection;
        this.room = str.toLowerCase();
        init();
    }

    public static void addInvitationListener(Connection connection, InvitationListener invitationListener) {
        InvitationsMonitor.getInvitationsMonitor(connection).addInvitationListener(invitationListener);
    }

    private void changeAffiliationByAdmin(String str, String str2, String str3) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.SET);
        MUCAdmin.Item item = new MUCAdmin.Item(str2, null);
        item.setJid(str);
        if (str3 != null) {
            item.setReason(str3);
        }
        mUCAdmin.addItem(item);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void changeAffiliationByAdmin(Collection<String> collection, String str) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.SET);
        for (String str2 : collection) {
            MUCAdmin.Item item = new MUCAdmin.Item(str, null);
            item.setJid(str2);
            mUCAdmin.addItem(item);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void changeAffiliationByOwner(String str, String str2) throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.SET);
        MUCOwner.Item item = new MUCOwner.Item(str2);
        item.setJid(str);
        mUCOwner.addItem(item);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void changeAffiliationByOwner(Collection<String> collection, String str) throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.SET);
        for (String str2 : collection) {
            MUCOwner.Item item = new MUCOwner.Item(str);
            item.setJid(str2);
            mUCOwner.addItem(item);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void changeRole(String str, String str2, String str3) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.SET);
        MUCAdmin.Item item = new MUCAdmin.Item(null, str2);
        item.setNick(str);
        item.setReason(str3);
        mUCAdmin.addItem(item);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void changeRole(Collection<String> collection, String str) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.SET);
        for (String str2 : collection) {
            MUCAdmin.Item item = new MUCAdmin.Item(null, str);
            item.setNick(str2);
            mUCAdmin.addItem(item);
        }
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    private void checkAffiliationModifications(String str, String str2, boolean z, String str3) {
        List arrayList;
        if (!"owner".equals(str) || "owner".equals(str2)) {
            if (!"admin".equals(str) || "admin".equals(str2)) {
                if ("member".equals(str) && !"member".equals(str2)) {
                    if (z) {
                        fireUserStatusListeners("membershipRevoked", new Object[0]);
                    } else {
                        arrayList = new ArrayList();
                        arrayList.add(str3);
                        fireParticipantStatusListeners("membershipRevoked", arrayList);
                    }
                }
            } else if (z) {
                fireUserStatusListeners("adminRevoked", new Object[0]);
            } else {
                arrayList = new ArrayList();
                arrayList.add(str3);
                fireParticipantStatusListeners("adminRevoked", arrayList);
            }
        } else if (z) {
            fireUserStatusListeners("ownershipRevoked", new Object[0]);
        } else {
            arrayList = new ArrayList();
            arrayList.add(str3);
            fireParticipantStatusListeners("ownershipRevoked", arrayList);
        }
        if ("owner".equals(str) || !"owner".equals(str2)) {
            if ("admin".equals(str) || !"admin".equals(str2)) {
                if (!"member".equals(str) && "member".equals(str2)) {
                    if (z) {
                        fireUserStatusListeners("membershipGranted", new Object[0]);
                        return;
                    }
                    arrayList = new ArrayList();
                    arrayList.add(str3);
                    fireParticipantStatusListeners("membershipGranted", arrayList);
                }
            } else if (z) {
                fireUserStatusListeners("adminGranted", new Object[0]);
            } else {
                arrayList = new ArrayList();
                arrayList.add(str3);
                fireParticipantStatusListeners("adminGranted", arrayList);
            }
        } else if (z) {
            fireUserStatusListeners("ownershipGranted", new Object[0]);
        } else {
            arrayList = new ArrayList();
            arrayList.add(str3);
            fireParticipantStatusListeners("ownershipGranted", arrayList);
        }
    }

    private void checkPresenceCode(String str, boolean z, MUCUser mUCUser, String str2) {
        List arrayList;
        if ("307".equals(str)) {
            if (z) {
                this.joined = false;
                fireUserStatusListeners("kicked", new Object[]{mUCUser.getItem().getActor(), mUCUser.getItem().getReason()});
                this.occupantsMap.clear();
                this.nickname = null;
                userHasLeft();
                return;
            }
            arrayList = new ArrayList();
            arrayList.add(str2);
            arrayList.add(mUCUser.getItem().getActor());
            arrayList.add(mUCUser.getItem().getReason());
            fireParticipantStatusListeners("kicked", arrayList);
        } else if ("301".equals(str)) {
            if (z) {
                this.joined = false;
                fireUserStatusListeners("banned", new Object[]{mUCUser.getItem().getActor(), mUCUser.getItem().getReason()});
                this.occupantsMap.clear();
                this.nickname = null;
                userHasLeft();
                return;
            }
            arrayList = new ArrayList();
            arrayList.add(str2);
            arrayList.add(mUCUser.getItem().getActor());
            arrayList.add(mUCUser.getItem().getReason());
            fireParticipantStatusListeners("banned", arrayList);
        } else if ("321".equals(str)) {
            if (z) {
                this.joined = false;
                fireUserStatusListeners("membershipRevoked", new Object[0]);
                this.occupantsMap.clear();
                this.nickname = null;
                userHasLeft();
            }
        } else if ("303".equals(str)) {
            arrayList = new ArrayList();
            arrayList.add(str2);
            arrayList.add(mUCUser.getItem().getNick());
            fireParticipantStatusListeners("nicknameChanged", arrayList);
        }
    }

    private void checkRoleModifications(String str, String str2, boolean z, String str3) {
        List arrayList;
        if (("visitor".equals(str) || PrivacyRule.SUBSCRIPTION_NONE.equals(str)) && "participant".equals(str2)) {
            if (z) {
                fireUserStatusListeners("voiceGranted", new Object[0]);
            } else {
                arrayList = new ArrayList();
                arrayList.add(str3);
                fireParticipantStatusListeners("voiceGranted", arrayList);
            }
        } else if ("participant".equals(str) && ("visitor".equals(str2) || PrivacyRule.SUBSCRIPTION_NONE.equals(str2))) {
            if (z) {
                fireUserStatusListeners("voiceRevoked", new Object[0]);
            } else {
                arrayList = new ArrayList();
                arrayList.add(str3);
                fireParticipantStatusListeners("voiceRevoked", arrayList);
            }
        }
        if (!"moderator".equals(str) && "moderator".equals(str2)) {
            if ("visitor".equals(str) || PrivacyRule.SUBSCRIPTION_NONE.equals(str)) {
                if (z) {
                    fireUserStatusListeners("voiceGranted", new Object[0]);
                } else {
                    arrayList = new ArrayList();
                    arrayList.add(str3);
                    fireParticipantStatusListeners("voiceGranted", arrayList);
                }
            }
            if (z) {
                fireUserStatusListeners("moderatorGranted", new Object[0]);
                return;
            }
            arrayList = new ArrayList();
            arrayList.add(str3);
            fireParticipantStatusListeners("moderatorGranted", arrayList);
        } else if ("moderator".equals(str) && !"moderator".equals(str2)) {
            if ("visitor".equals(str2) || PrivacyRule.SUBSCRIPTION_NONE.equals(str2)) {
                if (z) {
                    fireUserStatusListeners("voiceRevoked", new Object[0]);
                } else {
                    arrayList = new ArrayList();
                    arrayList.add(str3);
                    fireParticipantStatusListeners("voiceRevoked", arrayList);
                }
            }
            if (z) {
                fireUserStatusListeners("moderatorRevoked", new Object[0]);
                return;
            }
            arrayList = new ArrayList();
            arrayList.add(str3);
            fireParticipantStatusListeners("moderatorRevoked", arrayList);
        }
    }

    private void cleanup() {
        try {
            if (this.connection != null) {
                this.roomListenerMultiplexor.removeRoom(this.room);
                for (PacketListener removePacketListener : this.connectionListeners) {
                    this.connection.removePacketListener(removePacketListener);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void decline(Connection connection, String str, String str2, String str3) {
        Packet message = new Message(str);
        PacketExtension mUCUser = new MUCUser();
        Decline decline = new Decline();
        decline.setTo(str2);
        decline.setReason(str3);
        mUCUser.setDecline(decline);
        message.addExtension(mUCUser);
        connection.sendPacket(message);
    }

    private void fireInvitationRejectionListeners(String str, String str2) {
        synchronized (this.invitationRejectionListeners) {
            InvitationRejectionListener[] invitationRejectionListenerArr = new InvitationRejectionListener[this.invitationRejectionListeners.size()];
            this.invitationRejectionListeners.toArray(invitationRejectionListenerArr);
        }
        for (InvitationRejectionListener invitationDeclined : invitationRejectionListenerArr) {
            invitationDeclined.invitationDeclined(str, str2);
        }
    }

    private void fireParticipantStatusListeners(String str, List<String> list) {
        int i = 0;
        synchronized (this.participantStatusListeners) {
            ParticipantStatusListener[] participantStatusListenerArr = new ParticipantStatusListener[this.participantStatusListeners.size()];
            this.participantStatusListeners.toArray(participantStatusListenerArr);
        }
        try {
            Class[] clsArr = new Class[list.size()];
            for (int i2 = 0; i2 < list.size(); i2++) {
                clsArr[i2] = String.class;
            }
            Method declaredMethod = ParticipantStatusListener.class.getDeclaredMethod(str, clsArr);
            int length = participantStatusListenerArr.length;
            while (i < length) {
                declaredMethod.invoke(participantStatusListenerArr[i], list.toArray());
                i++;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    private void fireSubjectUpdatedListeners(String str, String str2) {
        synchronized (this.subjectUpdatedListeners) {
            SubjectUpdatedListener[] subjectUpdatedListenerArr = new SubjectUpdatedListener[this.subjectUpdatedListeners.size()];
            this.subjectUpdatedListeners.toArray(subjectUpdatedListenerArr);
        }
        for (SubjectUpdatedListener subjectUpdated : subjectUpdatedListenerArr) {
            subjectUpdated.subjectUpdated(str, str2);
        }
    }

    private void fireUserStatusListeners(String str, Object[] objArr) {
        int i = 0;
        synchronized (this.userStatusListeners) {
            UserStatusListener[] userStatusListenerArr = new UserStatusListener[this.userStatusListeners.size()];
            this.userStatusListeners.toArray(userStatusListenerArr);
        }
        Class[] clsArr = new Class[objArr.length];
        for (int i2 = 0; i2 < objArr.length; i2++) {
            clsArr[i2] = objArr[i2].getClass();
        }
        try {
            Method declaredMethod = UserStatusListener.class.getDeclaredMethod(str, clsArr);
            int length = userStatusListenerArr.length;
            while (i < length) {
                declaredMethod.invoke(userStatusListenerArr[i], objArr);
                i++;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    private Collection<Affiliate> getAffiliatesByAdmin(String str) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.GET);
        mUCAdmin.addItem(new MUCAdmin.Item(str, null));
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        MUCAdmin mUCAdmin2 = (MUCAdmin) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (mUCAdmin2 == null) {
            throw new XMPPException("No response from server.");
        } else if (mUCAdmin2.getError() != null) {
            throw new XMPPException(mUCAdmin2.getError());
        } else {
            Collection arrayList = new ArrayList();
            Iterator items = mUCAdmin2.getItems();
            while (items.hasNext()) {
                arrayList.add(new Affiliate((MUCAdmin.Item) items.next()));
            }
            return arrayList;
        }
    }

    private Collection<Affiliate> getAffiliatesByOwner(String str) throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.GET);
        mUCOwner.addItem(new MUCOwner.Item(str));
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        MUCOwner mUCOwner2 = (MUCOwner) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (mUCOwner2 == null) {
            throw new XMPPException("No response from server.");
        } else if (mUCOwner2.getError() != null) {
            throw new XMPPException(mUCOwner2.getError());
        } else {
            Collection arrayList = new ArrayList();
            Iterator items = mUCOwner2.getItems();
            while (items.hasNext()) {
                arrayList.add(new Affiliate((MUCOwner.Item) items.next()));
            }
            return arrayList;
        }
    }

    public static Collection<HostedRoom> getHostedRooms(Connection connection, String str) throws XMPPException {
        Collection arrayList = new ArrayList();
        Iterator items = ServiceDiscoveryManager.getInstanceFor(connection).discoverItems(str).getItems();
        while (items.hasNext()) {
            arrayList.add(new HostedRoom((Item) items.next()));
        }
        return arrayList;
    }

    private static Iterator<String> getJoinedRooms(Connection connection) {
        List list = (List) joinedRooms.get(connection);
        return list != null ? list.iterator() : new ArrayList().iterator();
    }

    public static Iterator<String> getJoinedRooms(Connection connection, String str) {
        try {
            ArrayList arrayList = new ArrayList();
            Iterator items = ServiceDiscoveryManager.getInstanceFor(connection).discoverItems(str, discoNode).getItems();
            while (items.hasNext()) {
                arrayList.add(((Item) items.next()).getEntityID());
            }
            return arrayList.iterator();
        } catch (XMPPException e) {
            e.printStackTrace();
            return new ArrayList().iterator();
        }
    }

    private MUCUser getMUCUserExtension(Packet packet) {
        return packet != null ? (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user") : null;
    }

    private Collection<Occupant> getOccupants(String str) throws XMPPException {
        Packet mUCAdmin = new MUCAdmin();
        mUCAdmin.setTo(this.room);
        mUCAdmin.setType(IQ.Type.GET);
        mUCAdmin.addItem(new MUCAdmin.Item(null, str));
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCAdmin.getPacketID()));
        this.connection.sendPacket(mUCAdmin);
        MUCAdmin mUCAdmin2 = (MUCAdmin) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (mUCAdmin2 == null) {
            throw new XMPPException("No response from server.");
        } else if (mUCAdmin2.getError() != null) {
            throw new XMPPException(mUCAdmin2.getError());
        } else {
            Collection arrayList = new ArrayList();
            Iterator items = mUCAdmin2.getItems();
            while (items.hasNext()) {
                arrayList.add(new Occupant((MUCAdmin.Item) items.next()));
            }
            return arrayList;
        }
    }

    public static Collection<HostedRoom> getPublicRooms(Connection connection, String str, String str2) throws XMPPException {
        Collection arrayList = new ArrayList();
        Iterator items = ServiceDiscoveryManager.getInstanceFor(connection).discoverItems(str, str2).getItems();
        while (items.hasNext()) {
            arrayList.add(new HostedRoom((Item) items.next()));
        }
        return arrayList;
    }

    public static RoomInfo getRoomInfo(Connection connection, String str) throws XMPPException {
        return new RoomInfo(ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(str));
    }

    public static Collection<String> getServiceNames(Connection connection) throws XMPPException {
        Collection arrayList = new ArrayList();
        ServiceDiscoveryManager instanceFor = ServiceDiscoveryManager.getInstanceFor(connection);
        Iterator items = instanceFor.discoverItems(connection.getServiceName()).getItems();
        while (items.hasNext()) {
            Item item = (Item) items.next();
            try {
                if (instanceFor.discoverInfo(item.getEntityID()).containsFeature(discoNamespace)) {
                    arrayList.add(item.getEntityID());
                }
            } catch (XMPPException e) {
            }
        }
        return arrayList;
    }

    private void init() {
        this.messageFilter = new AndFilter(new FromMatchesFilter(this.room), new MessageTypeFilter(Type.groupchat));
        this.messageFilter = new AndFilter(this.messageFilter, new PacketFilter() {
            public boolean accept(Packet packet) {
                return ((Message) packet).getBody() != null;
            }
        });
        this.presenceFilter = new AndFilter(new FromMatchesFilter(this.room), new PacketTypeFilter(Presence.class));
        this.messageCollector = new ConnectionDetachedPacketCollector();
        PacketListener anonymousClass4 = new PacketListener() {
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                MultiUserChat.this.subject = message.getSubject();
                MultiUserChat.this.fireSubjectUpdatedListeners(message.getSubject(), message.getFrom());
            }
        };
        PacketMultiplexListener packetMultiplexListener = new PacketMultiplexListener(this.messageCollector, new PacketListener() {
            public void processPacket(Packet packet) {
                Presence presence = (Presence) packet;
                String from = presence.getFrom();
                String str = MultiUserChat.this.room + "/" + MultiUserChat.this.nickname;
                boolean equals = presence.getFrom().equals(str);
                List arrayList;
                if (presence.getType() == Presence.Type.available) {
                    Presence presence2 = (Presence) MultiUserChat.this.occupantsMap.put(from, presence);
                    if (presence2 != null) {
                        MUCUser access$600 = MultiUserChat.this.getMUCUserExtension(presence2);
                        String affiliation = access$600.getItem().getAffiliation();
                        str = access$600.getItem().getRole();
                        MUCUser access$6002 = MultiUserChat.this.getMUCUserExtension(presence);
                        String affiliation2 = access$6002.getItem().getAffiliation();
                        MultiUserChat.this.checkRoleModifications(str, access$6002.getItem().getRole(), equals, from);
                        MultiUserChat.this.checkAffiliationModifications(affiliation, affiliation2, equals, from);
                    } else if (!equals) {
                        arrayList = new ArrayList();
                        arrayList.add(from);
                        MultiUserChat.this.fireParticipantStatusListeners("joined", arrayList);
                    }
                } else if (presence.getType() == Presence.Type.unavailable) {
                    MultiUserChat.this.occupantsMap.remove(from);
                    MUCUser access$6003 = MultiUserChat.this.getMUCUserExtension(presence);
                    if (access$6003 != null && access$6003.getStatus() != null) {
                        MultiUserChat.this.checkPresenceCode(access$6003.getStatus().getCode(), presence.getFrom().equals(str), access$6003, from);
                    } else if (!equals) {
                        arrayList = new ArrayList();
                        arrayList.add(from);
                        MultiUserChat.this.fireParticipantStatusListeners(TtmlNode.LEFT, arrayList);
                    }
                }
            }
        }, anonymousClass4, new PacketListener() {
            public void processPacket(Packet packet) {
                MUCUser access$600 = MultiUserChat.this.getMUCUserExtension(packet);
                if (access$600.getDecline() != null && ((Message) packet).getType() != Type.error) {
                    MultiUserChat.this.fireInvitationRejectionListeners(access$600.getDecline().getFrom(), access$600.getDecline().getReason());
                }
            }
        });
        this.roomListenerMultiplexor = RoomListenerMultiplexor.getRoomMultiplexor(this.connection);
        this.roomListenerMultiplexor.addRoom(this.room, packetMultiplexListener);
    }

    public static boolean isServiceEnabled(Connection connection, String str) {
        try {
            return ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo(str).containsFeature(discoNamespace);
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeInvitationListener(Connection connection, InvitationListener invitationListener) {
        InvitationsMonitor.getInvitationsMonitor(connection).removeInvitationListener(invitationListener);
    }

    private synchronized void userHasJoined() {
        List list = (List) joinedRooms.get(this.connection);
        if (list == null) {
            list = new ArrayList();
            joinedRooms.put(this.connection, list);
        }
        list.add(this.room);
    }

    private synchronized void userHasLeft() {
        List list = (List) joinedRooms.get(this.connection);
        if (list != null) {
            list.remove(this.room);
            cleanup();
        }
    }

    public void addInvitationRejectionListener(InvitationRejectionListener invitationRejectionListener) {
        synchronized (this.invitationRejectionListeners) {
            if (!this.invitationRejectionListeners.contains(invitationRejectionListener)) {
                this.invitationRejectionListeners.add(invitationRejectionListener);
            }
        }
    }

    public void addMessageListener(PacketListener packetListener) {
        this.connection.addPacketListener(packetListener, this.messageFilter);
        this.connectionListeners.add(packetListener);
    }

    public void addParticipantListener(PacketListener packetListener) {
        this.connection.addPacketListener(packetListener, this.presenceFilter);
        this.connectionListeners.add(packetListener);
    }

    public void addParticipantStatusListener(ParticipantStatusListener participantStatusListener) {
        synchronized (this.participantStatusListeners) {
            if (!this.participantStatusListeners.contains(participantStatusListener)) {
                this.participantStatusListeners.add(participantStatusListener);
            }
        }
    }

    public void addPresenceInterceptor(PacketInterceptor packetInterceptor) {
        this.presenceInterceptors.add(packetInterceptor);
    }

    public void addSubjectUpdatedListener(SubjectUpdatedListener subjectUpdatedListener) {
        synchronized (this.subjectUpdatedListeners) {
            if (!this.subjectUpdatedListeners.contains(subjectUpdatedListener)) {
                this.subjectUpdatedListeners.add(subjectUpdatedListener);
            }
        }
    }

    public void addUserStatusListener(UserStatusListener userStatusListener) {
        synchronized (this.userStatusListeners) {
            if (!this.userStatusListeners.contains(userStatusListener)) {
                this.userStatusListeners.add(userStatusListener);
            }
        }
    }

    public void banUser(String str, String str2) throws XMPPException {
        changeAffiliationByAdmin(str, "outcast", str2);
    }

    public void banUsers(Collection<String> collection) throws XMPPException {
        changeAffiliationByAdmin(collection, "outcast");
    }

    public void changeAvailabilityStatus(String str, Mode mode) {
        if (this.nickname == null || this.nickname.equals("")) {
            throw new IllegalArgumentException("Nickname must not be null or blank.");
        } else if (this.joined) {
            Packet presence = new Presence(Presence.Type.available);
            presence.setStatus(str);
            presence.setMode(mode);
            presence.setTo(this.room + "/" + this.nickname);
            for (PacketInterceptor interceptPacket : this.presenceInterceptors) {
                interceptPacket.interceptPacket(presence);
            }
            this.connection.sendPacket(presence);
        } else {
            throw new IllegalStateException("Must be logged into the room to change the availability status.");
        }
    }

    public void changeNickname(String str) throws XMPPException {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Nickname must not be null or blank.");
        } else if (this.joined) {
            Packet presence = new Presence(Presence.Type.available);
            presence.setTo(this.room + "/" + str);
            for (PacketInterceptor interceptPacket : this.presenceInterceptors) {
                interceptPacket.interceptPacket(presence);
            }
            PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new FromMatchesFilter(this.room + "/" + str), new PacketTypeFilter(Presence.class)));
            this.connection.sendPacket(presence);
            Presence presence2 = (Presence) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            createPacketCollector.cancel();
            if (presence2 == null) {
                throw new XMPPException("No response from server.");
            } else if (presence2.getError() != null) {
                throw new XMPPException(presence2.getError());
            } else {
                this.nickname = str;
            }
        } else {
            throw new IllegalStateException("Must be logged into the room to change nickname.");
        }
    }

    public void changeSubject(final String str) throws XMPPException {
        Packet message = new Message(this.room, Type.groupchat);
        message.setSubject(str);
        PacketFilter[] packetFilterArr = new PacketFilter[]{new FromMatchesFilter(this.room), new PacketTypeFilter(Message.class)};
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new AndFilter(packetFilterArr), new PacketFilter() {
            public boolean accept(Packet packet) {
                return str.equals(((Message) packet).getSubject());
            }
        }));
        this.connection.sendPacket(message);
        Message message2 = (Message) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (message2 == null) {
            throw new XMPPException("No response from server.");
        } else if (message2.getError() != null) {
            throw new XMPPException(message2.getError());
        }
    }

    public synchronized void create(String str) throws XMPPException {
        if (str != null) {
            if (!str.equals("")) {
                if (this.joined) {
                    throw new IllegalStateException("Creation failed - User already joined the room.");
                }
                Packet presence = new Presence(Presence.Type.available);
                presence.setTo(this.room + "/" + str);
                presence.addExtension(new MUCInitialPresence());
                for (PacketInterceptor interceptPacket : this.presenceInterceptors) {
                    interceptPacket.interceptPacket(presence);
                }
                PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new FromMatchesFilter(this.room + "/" + str), new PacketTypeFilter(Presence.class)));
                this.connection.sendPacket(presence);
                Presence presence2 = (Presence) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
                createPacketCollector.cancel();
                if (presence2 == null) {
                    throw new XMPPException("No response from server.");
                } else if (presence2.getError() != null) {
                    throw new XMPPException(presence2.getError());
                } else {
                    this.nickname = str;
                    this.joined = true;
                    userHasJoined();
                    MUCUser mUCUserExtension = getMUCUserExtension(presence2);
                    if (mUCUserExtension == null || mUCUserExtension.getStatus() == null || !"201".equals(mUCUserExtension.getStatus().getCode())) {
                        leave();
                        throw new XMPPException("Creation failed - Missing acknowledge of room creation.");
                    }
                }
            }
        }
        throw new IllegalArgumentException("Nickname must not be null or blank.");
    }

    public Message createMessage() {
        return new Message(this.room, Type.groupchat);
    }

    public Chat createPrivateChat(String str, MessageListener messageListener) {
        return this.connection.getChatManager().createChat(str, messageListener);
    }

    public void destroy(String str, String str2) throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.SET);
        Destroy destroy = new Destroy();
        destroy.setReason(str);
        destroy.setJid(str2);
        mUCOwner.setDestroy(destroy);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        } else {
            this.occupantsMap.clear();
            this.nickname = null;
            this.joined = false;
            userHasLeft();
        }
    }

    protected void finalize() throws Throwable {
        cleanup();
        super.finalize();
    }

    public Collection<Affiliate> getAdmins() throws XMPPException {
        return getAffiliatesByOwner("admin");
    }

    public Form getConfigurationForm() throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.GET);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() == null) {
            return Form.getFormFrom(iq);
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public Collection<Affiliate> getMembers() throws XMPPException {
        return getAffiliatesByAdmin("member");
    }

    public Collection<Occupant> getModerators() throws XMPPException {
        return getOccupants("moderator");
    }

    public String getNickname() {
        return this.nickname;
    }

    public Occupant getOccupant(String str) {
        Presence presence = (Presence) this.occupantsMap.get(str);
        return presence != null ? new Occupant(presence) : null;
    }

    public Presence getOccupantPresence(String str) {
        return (Presence) this.occupantsMap.get(str);
    }

    public Iterator<String> getOccupants() {
        return Collections.unmodifiableList(new ArrayList(this.occupantsMap.keySet())).iterator();
    }

    public int getOccupantsCount() {
        return this.occupantsMap.size();
    }

    public Collection<Affiliate> getOutcasts() throws XMPPException {
        return getAffiliatesByAdmin("outcast");
    }

    public Collection<Affiliate> getOwners() throws XMPPException {
        return getAffiliatesByAdmin("owner");
    }

    public Collection<Occupant> getParticipants() throws XMPPException {
        return getOccupants("participant");
    }

    public Form getRegistrationForm() throws XMPPException {
        Packet registration = new Registration();
        registration.setType(IQ.Type.GET);
        registration.setTo(this.room);
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
        this.connection.sendPacket(registration);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getType() != IQ.Type.ERROR) {
            return Form.getFormFrom(iq);
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public String getReservedNickname() {
        try {
            Iterator identities = ServiceDiscoveryManager.getInstanceFor(this.connection).discoverInfo(this.room, "x-roomuser-item").getIdentities();
            return identities.hasNext() ? ((Identity) identities.next()).getName() : null;
        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRoom() {
        return this.room;
    }

    public String getSubject() {
        return this.subject;
    }

    public void grantAdmin(String str) throws XMPPException {
        changeAffiliationByOwner(str, "admin");
    }

    public void grantAdmin(Collection<String> collection) throws XMPPException {
        changeAffiliationByOwner((Collection) collection, "admin");
    }

    public void grantMembership(String str) throws XMPPException {
        changeAffiliationByAdmin(str, "member", null);
    }

    public void grantMembership(Collection<String> collection) throws XMPPException {
        changeAffiliationByAdmin(collection, "member");
    }

    public void grantModerator(String str) throws XMPPException {
        changeRole(str, "moderator", null);
    }

    public void grantModerator(Collection<String> collection) throws XMPPException {
        changeRole(collection, "moderator");
    }

    public void grantOwnership(String str) throws XMPPException {
        changeAffiliationByAdmin(str, "owner", null);
    }

    public void grantOwnership(Collection<String> collection) throws XMPPException {
        changeAffiliationByAdmin(collection, "owner");
    }

    public void grantVoice(String str) throws XMPPException {
        changeRole(str, "participant", null);
    }

    public void grantVoice(Collection<String> collection) throws XMPPException {
        changeRole(collection, "participant");
    }

    public void invite(String str, String str2) {
        invite(new Message(), str, str2);
    }

    public void invite(Message message, String str, String str2) {
        message.setTo(this.room);
        PacketExtension mUCUser = new MUCUser();
        Invite invite = new Invite();
        invite.setTo(str);
        invite.setReason(str2);
        mUCUser.setInvite(invite);
        message.addExtension(mUCUser);
        this.connection.sendPacket(message);
    }

    public boolean isJoined() {
        return this.joined;
    }

    public void join(String str) throws XMPPException {
        join(str, null, null, (long) SmackConfiguration.getPacketReplyTimeout());
    }

    public void join(String str, String str2) throws XMPPException {
        join(str, str2, null, (long) SmackConfiguration.getPacketReplyTimeout());
    }

    public synchronized void join(String str, String str2, DiscussionHistory discussionHistory, long j) throws XMPPException {
        if (str != null) {
            if (!str.equals("")) {
                if (this.joined) {
                    leave();
                }
                Packet presence = new Presence(Presence.Type.available);
                presence.setTo(this.room + "/" + str);
                PacketExtension mUCInitialPresence = new MUCInitialPresence();
                if (str2 != null) {
                    mUCInitialPresence.setPassword(str2);
                }
                if (discussionHistory != null) {
                    mUCInitialPresence.setHistory(discussionHistory.getMUCHistory());
                }
                presence.addExtension(mUCInitialPresence);
                for (PacketInterceptor interceptPacket : this.presenceInterceptors) {
                    interceptPacket.interceptPacket(presence);
                }
                PacketCollector packetCollector = null;
                try {
                    packetCollector = this.connection.createPacketCollector(new AndFilter(new FromMatchesFilter(this.room + "/" + str), new PacketTypeFilter(Presence.class)));
                    this.connection.sendPacket(presence);
                    Presence presence2 = (Presence) packetCollector.nextResult(j);
                    if (packetCollector != null) {
                        packetCollector.cancel();
                    }
                    if (presence2 == null) {
                        throw new XMPPException("No response from server.");
                    } else if (presence2.getError() != null) {
                        throw new XMPPException(presence2.getError());
                    } else {
                        this.nickname = str;
                        this.joined = true;
                        userHasJoined();
                    }
                } catch (Throwable th) {
                    if (packetCollector != null) {
                        packetCollector.cancel();
                    }
                }
            }
        }
        throw new IllegalArgumentException("Nickname must not be null or blank.");
    }

    public void kickParticipant(String str, String str2) throws XMPPException {
        changeRole(str, PrivacyRule.SUBSCRIPTION_NONE, str2);
    }

    public synchronized void leave() {
        if (this.joined) {
            Packet presence = new Presence(Presence.Type.unavailable);
            presence.setTo(this.room + "/" + this.nickname);
            for (PacketInterceptor interceptPacket : this.presenceInterceptors) {
                interceptPacket.interceptPacket(presence);
            }
            this.connection.sendPacket(presence);
            this.occupantsMap.clear();
            this.nickname = null;
            this.joined = false;
            userHasLeft();
        }
    }

    public Message nextMessage() {
        return (Message) this.messageCollector.nextResult();
    }

    public Message nextMessage(long j) {
        return (Message) this.messageCollector.nextResult(j);
    }

    public Message pollMessage() {
        return (Message) this.messageCollector.pollResult();
    }

    public void removeInvitationRejectionListener(InvitationRejectionListener invitationRejectionListener) {
        synchronized (this.invitationRejectionListeners) {
            this.invitationRejectionListeners.remove(invitationRejectionListener);
        }
    }

    public void removeMessageListener(PacketListener packetListener) {
        this.connection.removePacketListener(packetListener);
        this.connectionListeners.remove(packetListener);
    }

    public void removeParticipantListener(PacketListener packetListener) {
        this.connection.removePacketListener(packetListener);
        this.connectionListeners.remove(packetListener);
    }

    public void removeParticipantStatusListener(ParticipantStatusListener participantStatusListener) {
        synchronized (this.participantStatusListeners) {
            this.participantStatusListeners.remove(participantStatusListener);
        }
    }

    public void removePresenceInterceptor(PacketInterceptor packetInterceptor) {
        this.presenceInterceptors.remove(packetInterceptor);
    }

    public void removeSubjectUpdatedListener(SubjectUpdatedListener subjectUpdatedListener) {
        synchronized (this.subjectUpdatedListeners) {
            this.subjectUpdatedListeners.remove(subjectUpdatedListener);
        }
    }

    public void removeUserStatusListener(UserStatusListener userStatusListener) {
        synchronized (this.userStatusListeners) {
            this.userStatusListeners.remove(userStatusListener);
        }
    }

    public void revokeAdmin(String str) throws XMPPException {
        changeAffiliationByOwner(str, "member");
    }

    public void revokeAdmin(Collection<String> collection) throws XMPPException {
        changeAffiliationByOwner((Collection) collection, "member");
    }

    public void revokeMembership(String str) throws XMPPException {
        changeAffiliationByAdmin(str, PrivacyRule.SUBSCRIPTION_NONE, null);
    }

    public void revokeMembership(Collection<String> collection) throws XMPPException {
        changeAffiliationByAdmin(collection, PrivacyRule.SUBSCRIPTION_NONE);
    }

    public void revokeModerator(String str) throws XMPPException {
        changeRole(str, "participant", null);
    }

    public void revokeModerator(Collection<String> collection) throws XMPPException {
        changeRole(collection, "participant");
    }

    public void revokeOwnership(String str) throws XMPPException {
        changeAffiliationByAdmin(str, "admin", null);
    }

    public void revokeOwnership(Collection<String> collection) throws XMPPException {
        changeAffiliationByAdmin(collection, "admin");
    }

    public void revokeVoice(String str) throws XMPPException {
        changeRole(str, "visitor", null);
    }

    public void revokeVoice(Collection<String> collection) throws XMPPException {
        changeRole(collection, "visitor");
    }

    public void sendConfigurationForm(Form form) throws XMPPException {
        Packet mUCOwner = new MUCOwner();
        mUCOwner.setTo(this.room);
        mUCOwner.setType(IQ.Type.SET);
        mUCOwner.addExtension(form.getDataFormToSend());
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(mUCOwner.getPacketID()));
        this.connection.sendPacket(mUCOwner);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getError() != null) {
            throw new XMPPException(iq.getError());
        }
    }

    public void sendMessage(String str) throws XMPPException {
        Packet message = new Message(this.room, Type.groupchat);
        message.setBody(str);
        this.connection.sendPacket(message);
    }

    public void sendMessage(Message message) throws XMPPException {
        this.connection.sendPacket(message);
    }

    public void sendRegistrationForm(Form form) throws XMPPException {
        Packet registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(this.room);
        registration.addExtension(form.getDataFormToSend());
        PacketCollector createPacketCollector = this.connection.createPacketCollector(new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class)));
        this.connection.sendPacket(registration);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server.");
        } else if (iq.getType() == IQ.Type.ERROR) {
            throw new XMPPException(iq.getError());
        }
    }
}
