package org.jivesoftware.smack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

public class RosterEntry {
    private final Connection connection;
    private String name;
    private final Roster roster;
    private ItemStatus status;
    private ItemType type;
    private String user;

    RosterEntry(String str, String str2, ItemType itemType, ItemStatus itemStatus, Roster roster, Connection connection) {
        this.user = str;
        this.name = str2;
        this.type = itemType;
        this.status = itemStatus;
        this.roster = roster;
        this.connection = connection;
    }

    static Item toRosterItem(RosterEntry rosterEntry) {
        Item item = new Item(rosterEntry.getUser(), rosterEntry.getName());
        item.setItemType(rosterEntry.getType());
        item.setItemStatus(rosterEntry.getStatus());
        for (RosterGroup name : rosterEntry.getGroups()) {
            item.addGroupName(name.getName());
        }
        return item;
    }

    public boolean equals(Object obj) {
        return this == obj ? true : (obj == null || !(obj instanceof RosterEntry)) ? false : this.user.equals(((RosterEntry) obj).getUser());
    }

    public boolean equalsDeep(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RosterEntry rosterEntry = (RosterEntry) obj;
        if (this.name == null) {
            if (rosterEntry.name != null) {
                return false;
            }
        } else if (!this.name.equals(rosterEntry.name)) {
            return false;
        }
        if (this.status == null) {
            if (rosterEntry.status != null) {
                return false;
            }
        } else if (!this.status.equals(rosterEntry.status)) {
            return false;
        }
        if (this.type == null) {
            if (rosterEntry.type != null) {
                return false;
            }
        } else if (!this.type.equals(rosterEntry.type)) {
            return false;
        }
        return this.user == null ? rosterEntry.user == null : this.user.equals(rosterEntry.user);
    }

    public Collection<RosterGroup> getGroups() {
        Collection arrayList = new ArrayList();
        for (RosterGroup rosterGroup : this.roster.getGroups()) {
            if (rosterGroup.contains(this)) {
                arrayList.add(rosterGroup);
            }
        }
        return Collections.unmodifiableCollection(arrayList);
    }

    public String getName() {
        return this.name;
    }

    public ItemStatus getStatus() {
        return this.status;
    }

    public ItemType getType() {
        return this.type;
    }

    public String getUser() {
        return this.user;
    }

    public int hashCode() {
        return this.user.hashCode();
    }

    public void setName(String str) {
        if (str == null || !str.equals(this.name)) {
            this.name = str;
            Packet rosterPacket = new RosterPacket();
            rosterPacket.setType(Type.SET);
            rosterPacket.addRosterItem(toRosterItem(this));
            this.connection.sendPacket(rosterPacket);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.name != null) {
            stringBuilder.append(this.name).append(": ");
        }
        stringBuilder.append(this.user);
        Collection groups = getGroups();
        if (!groups.isEmpty()) {
            stringBuilder.append(" [");
            Iterator it = groups.iterator();
            stringBuilder.append(((RosterGroup) it.next()).getName());
            while (it.hasNext()) {
                stringBuilder.append(", ");
                stringBuilder.append(((RosterGroup) it.next()).getName());
            }
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    void updateState(String str, ItemType itemType, ItemStatus itemStatus) {
        this.name = str;
        this.type = itemType;
        this.status = itemStatus;
    }
}
