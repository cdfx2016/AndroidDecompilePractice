package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.util.StringUtils;

public class Roster {
    private static SubscriptionMode defaultSubscriptionMode = SubscriptionMode.accept_all;
    private Connection connection;
    private final Map<String, RosterEntry> entries;
    private final Map<String, RosterGroup> groups;
    private RosterStorage persistentStorage;
    private Map<String, Map<String, Presence>> presenceMap;
    private PresencePacketListener presencePacketListener;
    private String requestPacketId;
    boolean rosterInitialized;
    private final List<RosterListener> rosterListeners;
    private SubscriptionMode subscriptionMode;
    private final List<RosterEntry> unfiledEntries;

    private class PresencePacketListener implements PacketListener {
        private PresencePacketListener() {
        }

        public void processPacket(Packet packet) {
            Presence presence = (Presence) packet;
            String from = presence.getFrom();
            String access$500 = Roster.this.getPresenceMapKey(from);
            Map concurrentHashMap;
            if (presence.getType() == Type.available) {
                if (Roster.this.presenceMap.get(access$500) == null) {
                    concurrentHashMap = new ConcurrentHashMap();
                    Roster.this.presenceMap.put(access$500, concurrentHashMap);
                } else {
                    concurrentHashMap = (Map) Roster.this.presenceMap.get(access$500);
                }
                concurrentHashMap.remove("");
                concurrentHashMap.put(StringUtils.parseResource(from), presence);
                if (((RosterEntry) Roster.this.entries.get(access$500)) != null) {
                    Roster.this.fireRosterPresenceEvent(presence);
                }
            } else if (presence.getType() == Type.unavailable) {
                if ("".equals(StringUtils.parseResource(from))) {
                    if (Roster.this.presenceMap.get(access$500) == null) {
                        concurrentHashMap = new ConcurrentHashMap();
                        Roster.this.presenceMap.put(access$500, concurrentHashMap);
                    } else {
                        concurrentHashMap = (Map) Roster.this.presenceMap.get(access$500);
                    }
                    concurrentHashMap.put("", presence);
                } else if (Roster.this.presenceMap.get(access$500) != null) {
                    ((Map) Roster.this.presenceMap.get(access$500)).put(StringUtils.parseResource(from), presence);
                }
                if (((RosterEntry) Roster.this.entries.get(access$500)) != null) {
                    Roster.this.fireRosterPresenceEvent(presence);
                }
            } else if (presence.getType() == Type.subscribe) {
                if (Roster.this.subscriptionMode == SubscriptionMode.accept_all) {
                    r0 = new Presence(Type.subscribed);
                    r0.setTo(presence.getFrom());
                    Roster.this.connection.sendPacket(r0);
                } else if (Roster.this.subscriptionMode == SubscriptionMode.reject_all) {
                    r0 = new Presence(Type.unsubscribed);
                    r0.setTo(presence.getFrom());
                    Roster.this.connection.sendPacket(r0);
                }
            } else if (presence.getType() == Type.unsubscribe) {
                if (Roster.this.subscriptionMode != SubscriptionMode.manual) {
                    r0 = new Presence(Type.unsubscribed);
                    r0.setTo(presence.getFrom());
                    Roster.this.connection.sendPacket(r0);
                }
            } else if (presence.getType() == Type.error && "".equals(StringUtils.parseResource(from))) {
                if (Roster.this.presenceMap.containsKey(access$500)) {
                    concurrentHashMap = (Map) Roster.this.presenceMap.get(access$500);
                    concurrentHashMap.clear();
                } else {
                    concurrentHashMap = new ConcurrentHashMap();
                    Roster.this.presenceMap.put(access$500, concurrentHashMap);
                }
                concurrentHashMap.put("", presence);
                if (((RosterEntry) Roster.this.entries.get(access$500)) != null) {
                    Roster.this.fireRosterPresenceEvent(presence);
                }
            }
        }
    }

    private class RosterPacketListener implements PacketListener {
        private RosterPacketListener() {
        }

        public void processPacket(Packet packet) {
            String str = null;
            Collection arrayList = new ArrayList();
            Collection arrayList2 = new ArrayList();
            Collection arrayList3 = new ArrayList();
            RosterPacket rosterPacket = (RosterPacket) packet;
            List<Item> arrayList4 = new ArrayList();
            for (Item add : rosterPacket.getRosterItems()) {
                arrayList4.add(add);
            }
            if (rosterPacket.getVersion() == null) {
                Roster.this.persistentStorage = null;
            } else {
                str = rosterPacket.getVersion();
            }
            if (!(Roster.this.persistentStorage == null || Roster.this.rosterInitialized)) {
                for (Item add2 : Roster.this.persistentStorage.getEntries()) {
                    arrayList4.add(add2);
                }
            }
            for (Item add22 : arrayList4) {
                Roster.this.insertRosterItem(add22, arrayList, arrayList2, arrayList3);
            }
            if (Roster.this.persistentStorage != null) {
                for (Item add222 : rosterPacket.getRosterItems()) {
                    if (add222.getItemType().equals(ItemType.remove)) {
                        Roster.this.persistentStorage.removeEntry(add222.getUser());
                    } else {
                        Roster.this.persistentStorage.addEntry(add222, str);
                    }
                }
            }
            synchronized (Roster.this) {
                Roster.this.rosterInitialized = true;
                Roster.this.notifyAll();
            }
            Roster.this.fireRosterChangedEvent(arrayList, arrayList2, arrayList3);
        }
    }

    private class RosterResultListener implements PacketListener {
        private RosterResultListener() {
        }

        public void processPacket(Packet packet) {
            if (packet instanceof IQ) {
                IQ iq = (IQ) packet;
                if (iq.getType().equals(IQ.Type.RESULT) && iq.getExtensions().isEmpty()) {
                    Collection arrayList = new ArrayList();
                    Collection arrayList2 = new ArrayList();
                    Collection arrayList3 = new ArrayList();
                    if (Roster.this.persistentStorage != null) {
                        for (Item access$1100 : Roster.this.persistentStorage.getEntries()) {
                            Roster.this.insertRosterItem(access$1100, arrayList, arrayList2, arrayList3);
                        }
                    }
                    Roster.this.fireRosterChangedEvent(arrayList, arrayList2, arrayList3);
                }
            }
            Roster.this.connection.removePacketListener(this);
        }
    }

    public enum SubscriptionMode {
        accept_all,
        reject_all,
        manual
    }

    Roster(Connection connection) {
        this.rosterInitialized = false;
        this.subscriptionMode = getDefaultSubscriptionMode();
        this.connection = connection;
        if (!connection.getConfiguration().isRosterVersioningAvailable()) {
            this.persistentStorage = null;
        }
        this.groups = new ConcurrentHashMap();
        this.unfiledEntries = new CopyOnWriteArrayList();
        this.entries = new ConcurrentHashMap();
        this.rosterListeners = new CopyOnWriteArrayList();
        this.presenceMap = new ConcurrentHashMap();
        connection.addPacketListener(new RosterPacketListener(), new PacketTypeFilter(RosterPacket.class));
        PacketFilter packetTypeFilter = new PacketTypeFilter(Presence.class);
        this.presencePacketListener = new PresencePacketListener();
        connection.addPacketListener(this.presencePacketListener, packetTypeFilter);
        final ConnectionListener anonymousClass1 = new AbstractConnectionListener() {
            public void connectionClosed() {
                Roster.this.setOfflinePresences();
            }

            public void connectionClosedOnError(Exception exception) {
                Roster.this.setOfflinePresences();
            }
        };
        if (this.connection.isConnected()) {
            connection.addConnectionListener(anonymousClass1);
        } else {
            Connection.addConnectionCreationListener(new ConnectionCreationListener() {
                public void connectionCreated(Connection connection) {
                    if (connection.equals(Roster.this.connection)) {
                        Roster.this.connection.addConnectionListener(anonymousClass1);
                    }
                }
            });
        }
    }

    Roster(Connection connection, RosterStorage rosterStorage) {
        this(connection);
        this.persistentStorage = rosterStorage;
    }

    private void fireRosterChangedEvent(Collection<String> collection, Collection<String> collection2, Collection<String> collection3) {
        for (RosterListener rosterListener : this.rosterListeners) {
            if (!collection.isEmpty()) {
                rosterListener.entriesAdded(collection);
            }
            if (!collection2.isEmpty()) {
                rosterListener.entriesUpdated(collection2);
            }
            if (!collection3.isEmpty()) {
                rosterListener.entriesDeleted(collection3);
            }
        }
    }

    private void fireRosterPresenceEvent(Presence presence) {
        for (RosterListener presenceChanged : this.rosterListeners) {
            presenceChanged.presenceChanged(presence);
        }
    }

    public static SubscriptionMode getDefaultSubscriptionMode() {
        return defaultSubscriptionMode;
    }

    private String getPresenceMapKey(String str) {
        if (str == null) {
            return null;
        }
        if (!contains(str)) {
            str = StringUtils.parseBareAddress(str);
        }
        return str.toLowerCase();
    }

    private void insertRosterItem(Item item, Collection<String> collection, Collection<String> collection2, Collection<String> collection3) {
        RosterEntry rosterEntry = new RosterEntry(item.getUser(), item.getName(), item.getItemType(), item.getItemStatus(), this, this.connection);
        if (ItemType.remove.equals(item.getItemType())) {
            if (this.entries.containsKey(item.getUser())) {
                this.entries.remove(item.getUser());
            }
            if (this.unfiledEntries.contains(rosterEntry)) {
                this.unfiledEntries.remove(rosterEntry);
            }
            this.presenceMap.remove(StringUtils.parseName(item.getUser()) + "@" + StringUtils.parseServer(item.getUser()));
            if (collection3 != null) {
                collection3.add(item.getUser());
            }
        } else {
            if (this.entries.containsKey(item.getUser())) {
                this.entries.put(item.getUser(), rosterEntry);
                if (collection2 != null) {
                    collection2.add(item.getUser());
                }
            } else {
                this.entries.put(item.getUser(), rosterEntry);
                if (collection != null) {
                    collection.add(item.getUser());
                }
            }
            if (!item.getGroupNames().isEmpty()) {
                this.unfiledEntries.remove(rosterEntry);
            } else if (!this.unfiledEntries.contains(rosterEntry)) {
                this.unfiledEntries.add(rosterEntry);
            }
        }
        List<String> arrayList = new ArrayList();
        for (RosterGroup rosterGroup : getGroups()) {
            if (rosterGroup.contains(rosterEntry)) {
                arrayList.add(rosterGroup.getName());
            }
        }
        if (!ItemType.remove.equals(item.getItemType())) {
            List<String> arrayList2 = new ArrayList();
            for (String str : item.getGroupNames()) {
                arrayList2.add(str);
                RosterGroup group = getGroup(str);
                if (group == null) {
                    group = createGroup(str);
                    this.groups.put(str, group);
                }
                group.addEntryLocal(rosterEntry);
            }
            for (String str2 : arrayList2) {
                arrayList.remove(str2);
            }
        }
        for (String str22 : arrayList) {
            RosterGroup group2 = getGroup(str22);
            group2.removeEntryLocal(rosterEntry);
            if (group2.getEntryCount() == 0) {
                this.groups.remove(str22);
            }
        }
        for (RosterGroup rosterGroup2 : getGroups()) {
            if (rosterGroup2.getEntryCount() == 0) {
                this.groups.remove(rosterGroup2.getName());
            }
        }
    }

    private void insertRosterItems(List<Item> list) {
        Collection arrayList = new ArrayList();
        Collection arrayList2 = new ArrayList();
        Collection arrayList3 = new ArrayList();
        for (Item insertRosterItem : list) {
            insertRosterItem(insertRosterItem, arrayList, arrayList2, arrayList3);
        }
        fireRosterChangedEvent(arrayList, arrayList2, arrayList3);
    }

    public static void setDefaultSubscriptionMode(SubscriptionMode subscriptionMode) {
        defaultSubscriptionMode = subscriptionMode;
    }

    private void setOfflinePresences() {
        for (String str : this.presenceMap.keySet()) {
            Map map = (Map) this.presenceMap.get(str);
            if (map != null) {
                for (String str2 : map.keySet()) {
                    Packet presence = new Presence(Type.unavailable);
                    presence.setFrom(str + "/" + str2);
                    this.presencePacketListener.processPacket(presence);
                }
            }
        }
    }

    public void addRosterListener(RosterListener rosterListener) {
        if (!this.rosterListeners.contains(rosterListener)) {
            this.rosterListeners.add(rosterListener);
        }
    }

    public boolean contains(String str) {
        return getEntry(str) != null;
    }

    public void createEntry(String str, String str2, String[] strArr) throws XMPPException {
        if (!this.connection.isAuthenticated()) {
            throw new IllegalStateException("Not logged in to server.");
        } else if (this.connection.isAnonymous()) {
            throw new IllegalStateException("Anonymous users can't have a roster.");
        } else {
            Packet rosterPacket = new RosterPacket();
            rosterPacket.setType(IQ.Type.SET);
            Item item = new Item(str, str2);
            if (strArr != null) {
                for (String str3 : strArr) {
                    if (str3 != null && str3.trim().length() > 0) {
                        item.addGroupName(str3);
                    }
                }
            }
            rosterPacket.addRosterItem(item);
            PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
            this.connection.sendPacket(rosterPacket);
            IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            createPacketCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from the server.");
            } else if (iq.getType() == IQ.Type.ERROR) {
                throw new XMPPException(iq.getError());
            } else {
                Packet presence = new Presence(Type.subscribe);
                presence.setTo(str);
                this.connection.sendPacket(presence);
            }
        }
    }

    public RosterGroup createGroup(String str) {
        if (!this.connection.isAuthenticated()) {
            throw new IllegalStateException("Not logged in to server.");
        } else if (this.connection.isAnonymous()) {
            throw new IllegalStateException("Anonymous users can't have a roster.");
        } else if (this.groups.containsKey(str)) {
            throw new IllegalArgumentException("Group with name " + str + " alread exists.");
        } else {
            RosterGroup rosterGroup = new RosterGroup(str, this.connection);
            this.groups.put(str, rosterGroup);
            return rosterGroup;
        }
    }

    public Collection<RosterEntry> getEntries() {
        Collection hashSet = new HashSet();
        for (RosterGroup entries : getGroups()) {
            hashSet.addAll(entries.getEntries());
        }
        hashSet.addAll(this.unfiledEntries);
        return Collections.unmodifiableCollection(hashSet);
    }

    public RosterEntry getEntry(String str) {
        return str == null ? null : (RosterEntry) this.entries.get(str.toLowerCase());
    }

    public int getEntryCount() {
        return getEntries().size();
    }

    public RosterGroup getGroup(String str) {
        return (RosterGroup) this.groups.get(str);
    }

    public int getGroupCount() {
        return this.groups.size();
    }

    public Collection<RosterGroup> getGroups() {
        return Collections.unmodifiableCollection(this.groups.values());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.jivesoftware.smack.packet.Presence getPresence(java.lang.String r7) {
        /*
        r6 = this;
        r0 = org.jivesoftware.smack.util.StringUtils.parseBareAddress(r7);
        r0 = r6.getPresenceMapKey(r0);
        r1 = r6.presenceMap;
        r0 = r1.get(r0);
        r0 = (java.util.Map) r0;
        if (r0 != 0) goto L_0x001d;
    L_0x0012:
        r2 = new org.jivesoftware.smack.packet.Presence;
        r0 = org.jivesoftware.smack.packet.Presence.Type.unavailable;
        r2.<init>(r0);
        r2.setFrom(r7);
    L_0x001c:
        return r2;
    L_0x001d:
        r2 = 0;
        r1 = r0.keySet();
        r5 = r1.iterator();
    L_0x0026:
        r1 = r5.hasNext();
        if (r1 == 0) goto L_0x006e;
    L_0x002c:
        r1 = r5.next();
        r1 = (java.lang.String) r1;
        r1 = r0.get(r1);
        r1 = (org.jivesoftware.smack.packet.Presence) r1;
        r3 = r1.isAvailable();
        if (r3 == 0) goto L_0x0026;
    L_0x003e:
        if (r2 == 0) goto L_0x004a;
    L_0x0040:
        r3 = r1.getPriority();
        r4 = r2.getPriority();
        if (r3 <= r4) goto L_0x004c;
    L_0x004a:
        r2 = r1;
        goto L_0x0026;
    L_0x004c:
        r3 = r1.getPriority();
        r4 = r2.getPriority();
        if (r3 != r4) goto L_0x006c;
    L_0x0056:
        r3 = r1.getMode();
        if (r3 != 0) goto L_0x005e;
    L_0x005c:
        r3 = org.jivesoftware.smack.packet.Presence.Mode.available;
    L_0x005e:
        r4 = r2.getMode();
        if (r4 != 0) goto L_0x0066;
    L_0x0064:
        r4 = org.jivesoftware.smack.packet.Presence.Mode.available;
    L_0x0066:
        r3 = r3.compareTo(r4);
        if (r3 < 0) goto L_0x004a;
    L_0x006c:
        r1 = r2;
        goto L_0x004a;
    L_0x006e:
        if (r2 != 0) goto L_0x001c;
    L_0x0070:
        r2 = new org.jivesoftware.smack.packet.Presence;
        r0 = org.jivesoftware.smack.packet.Presence.Type.unavailable;
        r2.<init>(r0);
        r2.setFrom(r7);
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.Roster.getPresence(java.lang.String):org.jivesoftware.smack.packet.Presence");
    }

    public Presence getPresenceResource(String str) {
        String presenceMapKey = getPresenceMapKey(str);
        String parseResource = StringUtils.parseResource(str);
        Map map = (Map) this.presenceMap.get(presenceMapKey);
        if (map == null) {
            Presence presence = new Presence(Type.unavailable);
            presence.setFrom(str);
            return presence;
        }
        presence = (Presence) map.get(parseResource);
        if (presence != null) {
            return presence;
        }
        presence = new Presence(Type.unavailable);
        presence.setFrom(str);
        return presence;
    }

    public Iterator<Presence> getPresences(String str) {
        Map map = (Map) this.presenceMap.get(getPresenceMapKey(str));
        if (map == null) {
            new Presence(Type.unavailable).setFrom(str);
            return Arrays.asList(new Presence[]{r0}).iterator();
        }
        Collection arrayList = new ArrayList();
        for (Presence presence : map.values()) {
            if (presence.isAvailable()) {
                arrayList.add(presence);
            }
        }
        if (!arrayList.isEmpty()) {
            return arrayList.iterator();
        }
        new Presence(Type.unavailable).setFrom(str);
        return Arrays.asList(new Presence[]{presence}).iterator();
    }

    public SubscriptionMode getSubscriptionMode() {
        return this.subscriptionMode;
    }

    public Collection<RosterEntry> getUnfiledEntries() {
        return Collections.unmodifiableList(this.unfiledEntries);
    }

    public int getUnfiledEntryCount() {
        return this.unfiledEntries.size();
    }

    public void reload() {
        if (!this.connection.isAuthenticated()) {
            throw new IllegalStateException("Not logged in to server.");
        } else if (this.connection.isAnonymous()) {
            throw new IllegalStateException("Anonymous users can't have a roster.");
        } else {
            Packet rosterPacket = new RosterPacket();
            if (this.persistentStorage != null) {
                rosterPacket.setVersion(this.persistentStorage.getRosterVersion());
            }
            this.requestPacketId = rosterPacket.getPacketID();
            this.connection.addPacketListener(new RosterResultListener(), new PacketIDFilter(this.requestPacketId));
            this.connection.sendPacket(rosterPacket);
        }
    }

    public void removeEntry(RosterEntry rosterEntry) throws XMPPException {
        if (!this.connection.isAuthenticated()) {
            throw new IllegalStateException("Not logged in to server.");
        } else if (this.connection.isAnonymous()) {
            throw new IllegalStateException("Anonymous users can't have a roster.");
        } else if (this.entries.containsKey(rosterEntry.getUser())) {
            Packet rosterPacket = new RosterPacket();
            rosterPacket.setType(IQ.Type.SET);
            Item toRosterItem = RosterEntry.toRosterItem(rosterEntry);
            toRosterItem.setItemType(ItemType.remove);
            rosterPacket.addRosterItem(toRosterItem);
            PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
            this.connection.sendPacket(rosterPacket);
            IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            createPacketCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from the server.");
            } else if (iq.getType() == IQ.Type.ERROR) {
                throw new XMPPException(iq.getError());
            }
        }
    }

    public void removeRosterListener(RosterListener rosterListener) {
        this.rosterListeners.remove(rosterListener);
    }

    public void setSubscriptionMode(SubscriptionMode subscriptionMode) {
        this.subscriptionMode = subscriptionMode;
    }
}
