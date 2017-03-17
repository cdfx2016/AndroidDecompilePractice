package org.jivesoftware.smackx.receipts;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class DeliveryReceiptRequest implements PacketExtension {
    public static final String ELEMENT = "request";

    public static class Provider implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) {
            return new DeliveryReceiptRequest();
        }
    }

    public String getElementName() {
        return "request";
    }

    public String getNamespace() {
        return "urn:xmpp:receipts";
    }

    public String toXML() {
        return "<request xmlns='urn:xmpp:receipts'/>";
    }
}
