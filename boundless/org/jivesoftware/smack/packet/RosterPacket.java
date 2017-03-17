package org.jivesoftware.smack.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jivesoftware.smack.util.StringUtils;

public class RosterPacket extends IQ {
    private final List<Item> rosterItems = new ArrayList();
    private String version;

    public static class Item {
        private final Set<String> groupNames = new CopyOnWriteArraySet();
        private ItemStatus itemStatus = null;
        private ItemType itemType = null;
        private String name;
        private String user;

        public Item(String str, String str2) {
            this.user = str.toLowerCase();
            this.name = str2;
        }

        public void addGroupName(String str) {
            this.groupNames.add(str);
        }

        public Set<String> getGroupNames() {
            return Collections.unmodifiableSet(this.groupNames);
        }

        public ItemStatus getItemStatus() {
            return this.itemStatus;
        }

        public ItemType getItemType() {
            return this.itemType;
        }

        public String getName() {
            return this.name;
        }

        public String getUser() {
            return this.user;
        }

        public void removeGroupName(String str) {
            this.groupNames.remove(str);
        }

        public void setItemStatus(ItemStatus itemStatus) {
            this.itemStatus = itemStatus;
        }

        public void setItemType(ItemType itemType) {
            this.itemType = itemType;
        }

        public void setName(String str) {
            this.name = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<item jid=\"").append(this.user).append("\"");
            if (this.name != null) {
                stringBuilder.append(" name=\"").append(StringUtils.escapeForXML(this.name)).append("\"");
            }
            if (this.itemType != null) {
                stringBuilder.append(" subscription=\"").append(this.itemType).append("\"");
            }
            if (this.itemStatus != null) {
                stringBuilder.append(" ask=\"").append(this.itemStatus).append("\"");
            }
            stringBuilder.append(">");
            for (String escapeForXML : this.groupNames) {
                stringBuilder.append("<group>").append(StringUtils.escapeForXML(escapeForXML)).append("</group>");
            }
            stringBuilder.append("</item>");
            return stringBuilder.toString();
        }
    }

    public static class ItemStatus {
        public static final ItemStatus SUBSCRIPTION_PENDING = new ItemStatus("subscribe");
        public static final ItemStatus UNSUBSCRIPTION_PENDING = new ItemStatus("unsubscribe");
        private String value;

        private ItemStatus(String str) {
            this.value = str;
        }

        public static ItemStatus fromString(String str) {
            if (str == null) {
                return null;
            }
            String toLowerCase = str.toLowerCase();
            return "unsubscribe".equals(toLowerCase) ? UNSUBSCRIPTION_PENDING : "subscribe".equals(toLowerCase) ? SUBSCRIPTION_PENDING : null;
        }

        public String toString() {
            return this.value;
        }
    }

    public enum ItemType {
        none,
        to,
        from,
        both,
        remove
    }

    public void addRosterItem(Item item) {
        synchronized (this.rosterItems) {
            this.rosterItems.add(item);
        }
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:roster\" ");
        if (this.version != null) {
            stringBuilder.append(" ver=\"" + this.version + "\" ");
        }
        stringBuilder.append(">");
        synchronized (this.rosterItems) {
            for (Item toXML : this.rosterItems) {
                stringBuilder.append(toXML.toXML());
            }
        }
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public int getRosterItemCount() {
        int size;
        synchronized (this.rosterItems) {
            size = this.rosterItems.size();
        }
        return size;
    }

    public Collection<Item> getRosterItems() {
        Collection unmodifiableList;
        synchronized (this.rosterItems) {
            unmodifiableList = Collections.unmodifiableList(new ArrayList(this.rosterItems));
        }
        return unmodifiableList;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String str) {
        this.version = str;
    }
}
