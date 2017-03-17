package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class OfflineMessageInfo implements PacketExtension {
    private String node = null;

    public static class Provider implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            PacketExtension offlineMessageInfo = new OfflineMessageInfo();
            Object obj = null;
            while (obj == null) {
                int next = xmlPullParser.next();
                if (next == 2) {
                    if (xmlPullParser.getName().equals("item")) {
                        offlineMessageInfo.setNode(xmlPullParser.getAttributeValue("", "node"));
                    }
                } else if (next == 3 && xmlPullParser.getName().equals(MessageEvent.OFFLINE)) {
                    obj = 1;
                }
            }
            return offlineMessageInfo;
        }
    }

    public String getElementName() {
        return MessageEvent.OFFLINE;
    }

    public String getNamespace() {
        return "http://jabber.org/protocol/offline";
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String str) {
        this.node = str;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        if (getNode() != null) {
            stringBuilder.append("<item node=\"").append(getNode()).append("\"/>");
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
