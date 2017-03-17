package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.IQ;

public class PEPPubSub extends IQ {
    PEPItem item;

    public PEPPubSub(PEPItem pEPItem) {
        this.item = pEPItem;
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        stringBuilder.append("<publish node=\"").append(this.item.getNode()).append("\">");
        stringBuilder.append(this.item.toXML());
        stringBuilder.append("</publish>");
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }

    public String getElementName() {
        return "pubsub";
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/pubsub";
    }
}
