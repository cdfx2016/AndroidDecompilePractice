package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public class FromMatchesFilter implements PacketFilter {
    private String address;
    private boolean matchBareJID = false;

    public FromMatchesFilter(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.address = str.toLowerCase();
        this.matchBareJID = "".equals(StringUtils.parseResource(str));
    }

    public boolean accept(Packet packet) {
        return packet.getFrom() == null ? false : this.matchBareJID ? packet.getFrom().toLowerCase().startsWith(this.address) : this.address.equals(packet.getFrom().toLowerCase());
    }

    public String toString() {
        return "FromMatchesFilter: " + this.address;
    }
}
