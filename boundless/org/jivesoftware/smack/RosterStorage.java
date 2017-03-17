package org.jivesoftware.smack;

import java.util.List;
import org.jivesoftware.smack.packet.RosterPacket.Item;

public interface RosterStorage {
    void addEntry(Item item, String str);

    List<Item> getEntries();

    Item getEntry(String str);

    int getEntryCount();

    String getRosterVersion();

    void removeEntry(String str);

    void updateLocalEntry(Item item);
}
