package com.easemob.chat;

import com.easemob.util.EMLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

class EMRosterListener implements RosterListener {
    private static final String TAG = "contact";
    private EMContactManager contactManager;
    private Roster roster;

    public EMRosterListener(EMContactManager eMContactManager, Roster roster) {
        this.contactManager = eMContactManager;
        this.roster = roster;
    }

    public void entriesAdded(Collection<String> collection) {
    }

    public void entriesDeleted(Collection<String> collection) {
        EMLog.d(TAG, "on contact entries deleted:" + collection);
    }

    public void entriesUpdated(Collection<String> collection) {
        EMLog.d(TAG, "on contact entries updated:" + collection);
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        for (String str : collection) {
            RosterEntry entry = this.roster.getEntry(str);
            if (entry.getType() == ItemType.both || entry.getType() == ItemType.from) {
                arrayList.add(EMContactManager.getUserNameFromEid(str));
            }
            if (entry.getType() == ItemType.none) {
                if (EMContactManager.getInstance().deleteContactsSet.contains(str)) {
                    arrayList2.add(EMContactManager.getUserNameFromEid(str));
                } else {
                    this.contactManager.contactListener.onContactRefused(EMContactManager.getUserNameFromEid(str));
                }
                try {
                    this.roster.removeEntry(entry);
                } catch (Exception e) {
                }
            }
            if (!(this.contactManager.contactListener == null || arrayList.size() == 0)) {
                this.contactManager.contactListener.onContactAdded(arrayList);
            }
            if (!(this.contactManager.contactListener == null || arrayList2.size() == 0)) {
                this.contactManager.contactListener.onContactDeleted(arrayList2);
            }
        }
    }

    public void presenceChanged(Presence presence) {
    }
}
