package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;

public class PEPEvent implements PacketExtension {
    PEPItem item;

    public PEPEvent(PEPItem pEPItem) {
        this.item = pEPItem;
    }

    public void addPEPItem(PEPItem pEPItem) {
        this.item = pEPItem;
    }

    public String getElementName() {
        return "event";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/pubsub";
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        stringBuilder.append(this.item.toXML());
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
