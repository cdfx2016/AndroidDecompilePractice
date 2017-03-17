package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.RemoteRosterEntry;

public class RosterExchange implements PacketExtension {
    private List<RemoteRosterEntry> remoteRosterEntries = new ArrayList();

    public RosterExchange(Roster roster) {
        for (RosterEntry addRosterEntry : roster.getEntries()) {
            addRosterEntry(addRosterEntry);
        }
    }

    public void addRosterEntry(RosterEntry rosterEntry) {
        List arrayList = new ArrayList();
        for (RosterGroup name : rosterEntry.getGroups()) {
            arrayList.add(name.getName());
        }
        addRosterEntry(new RemoteRosterEntry(rosterEntry.getUser(), rosterEntry.getName(), (String[]) arrayList.toArray(new String[arrayList.size()])));
    }

    public void addRosterEntry(RemoteRosterEntry remoteRosterEntry) {
        synchronized (this.remoteRosterEntries) {
            this.remoteRosterEntries.add(remoteRosterEntry);
        }
    }

    public String getElementName() {
        return "x";
    }

    public int getEntryCount() {
        return this.remoteRosterEntries.size();
    }

    public String getNamespace() {
        return "jabber:x:roster";
    }

    public Iterator<RemoteRosterEntry> getRosterEntries() {
        Iterator<RemoteRosterEntry> it;
        synchronized (this.remoteRosterEntries) {
            it = Collections.unmodifiableList(new ArrayList(this.remoteRosterEntries)).iterator();
        }
        return it;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        Iterator rosterEntries = getRosterEntries();
        while (rosterEntries.hasNext()) {
            stringBuilder.append(((RemoteRosterEntry) rosterEntries.next()).toXML());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
