package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;

public class PacketExtensionFilter implements PacketFilter {
    private String elementName;
    private String namespace;

    public PacketExtensionFilter(String str) {
        this(null, str);
    }

    public PacketExtensionFilter(String str, String str2) {
        this.elementName = str;
        this.namespace = str2;
    }

    public boolean accept(Packet packet) {
        return packet.getExtension(this.elementName, this.namespace) != null;
    }
}
