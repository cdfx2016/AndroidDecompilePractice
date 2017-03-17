package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;
import org.jivesoftware.smack.util.StringUtils;

public class RosterGroup {
    private Connection connection;
    private final List<RosterEntry> entries = new ArrayList();
    private String name;

    RosterGroup(String str, Connection connection) {
        this.name = str;
        this.connection = connection;
    }

    public void addEntry(RosterEntry rosterEntry) throws XMPPException {
        PacketCollector packetCollector;
        synchronized (this.entries) {
            if (this.entries.contains(rosterEntry)) {
                packetCollector = null;
            } else {
                Packet rosterPacket = new RosterPacket();
                rosterPacket.setType(Type.SET);
                Item toRosterItem = RosterEntry.toRosterItem(rosterEntry);
                toRosterItem.addGroupName(getName());
                rosterPacket.addRosterItem(toRosterItem);
                PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
                this.connection.sendPacket(rosterPacket);
                packetCollector = createPacketCollector;
            }
        }
        if (packetCollector != null) {
            IQ iq = (IQ) packetCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            packetCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from the server.");
            } else if (iq.getType() == Type.ERROR) {
                throw new XMPPException(iq.getError());
            }
        }
    }

    public void addEntryLocal(RosterEntry rosterEntry) {
        synchronized (this.entries) {
            this.entries.remove(rosterEntry);
            this.entries.add(rosterEntry);
        }
    }

    public boolean contains(String str) {
        return getEntry(str) != null;
    }

    public boolean contains(RosterEntry rosterEntry) {
        boolean contains;
        synchronized (this.entries) {
            contains = this.entries.contains(rosterEntry);
        }
        return contains;
    }

    public Collection<RosterEntry> getEntries() {
        Collection unmodifiableList;
        synchronized (this.entries) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.entries));
        }
        return unmodifiableList;
    }

    public RosterEntry getEntry(String str) {
        if (str == null) {
            return null;
        }
        String toLowerCase = StringUtils.parseBareAddress(str).toLowerCase();
        synchronized (this.entries) {
            for (RosterEntry rosterEntry : this.entries) {
                if (rosterEntry.getUser().equals(toLowerCase)) {
                    return rosterEntry;
                }
            }
            return null;
        }
    }

    public int getEntryCount() {
        int size;
        synchronized (this.entries) {
            size = this.entries.size();
        }
        return size;
    }

    public String getName() {
        return this.name;
    }

    public void removeEntry(RosterEntry rosterEntry) throws XMPPException {
        PacketCollector packetCollector;
        synchronized (this.entries) {
            if (this.entries.contains(rosterEntry)) {
                Packet rosterPacket = new RosterPacket();
                rosterPacket.setType(Type.SET);
                Item toRosterItem = RosterEntry.toRosterItem(rosterEntry);
                toRosterItem.removeGroupName(getName());
                rosterPacket.addRosterItem(toRosterItem);
                PacketCollector createPacketCollector = this.connection.createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
                this.connection.sendPacket(rosterPacket);
                packetCollector = createPacketCollector;
            } else {
                packetCollector = null;
            }
        }
        if (packetCollector != null) {
            IQ iq = (IQ) packetCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
            packetCollector.cancel();
            if (iq == null) {
                throw new XMPPException("No response from the server.");
            } else if (iq.getType() == Type.ERROR) {
                throw new XMPPException(iq.getError());
            }
        }
    }

    void removeEntryLocal(RosterEntry rosterEntry) {
        synchronized (this.entries) {
            if (this.entries.contains(rosterEntry)) {
                this.entries.remove(rosterEntry);
            }
        }
    }

    public void setName(String str) {
        synchronized (this.entries) {
            for (RosterEntry rosterEntry : this.entries) {
                Packet rosterPacket = new RosterPacket();
                rosterPacket.setType(Type.SET);
                Item toRosterItem = RosterEntry.toRosterItem(rosterEntry);
                toRosterItem.removeGroupName(this.name);
                toRosterItem.addGroupName(str);
                rosterPacket.addRosterItem(toRosterItem);
                this.connection.sendPacket(rosterPacket);
            }
        }
    }
}
