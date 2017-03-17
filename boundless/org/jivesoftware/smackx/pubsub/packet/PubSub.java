package org.jivesoftware.smackx.pubsub.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;

public class PubSub extends IQ {
    private PubSubNamespace ns = PubSubNamespace.BASIC;

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }

    public String getElementName() {
        return "pubsub";
    }

    public PacketExtension getExtension(PubSubElementType pubSubElementType) {
        return getExtension(pubSubElementType.getElementName(), pubSubElementType.getNamespace().getXmlns());
    }

    public String getNamespace() {
        return this.ns.getXmlns();
    }

    public PubSubNamespace getPubSubNamespace() {
        return this.ns;
    }

    public void setPubSubNamespace(PubSubNamespace pubSubNamespace) {
        this.ns = pubSubNamespace;
    }
}
