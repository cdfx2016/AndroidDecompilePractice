package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;

public abstract class PEPItem implements PacketExtension {
    String id;

    public PEPItem(String str) {
        this.id = str;
    }

    public String getElementName() {
        return "item";
    }

    abstract String getItemDetailsXML();

    public String getNamespace() {
        return "http://jabber.org/protocol/pubsub";
    }

    abstract String getNode();

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" id=\"").append(this.id).append("\">");
        stringBuilder.append(getItemDetailsXML());
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
