package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.packet.MessageEvent;
import org.jivesoftware.smackx.packet.MultipleAddresses;
import org.xmlpull.v1.XmlPullParser;

public class MultipleAddressesProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        PacketExtension multipleAddresses = new MultipleAddresses();
        while (obj == null) {
            Object obj2;
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("address")) {
                    multipleAddresses.addAddress(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE), xmlPullParser.getAttributeValue("", "jid"), xmlPullParser.getAttributeValue("", "node"), xmlPullParser.getAttributeValue("", "desc"), "true".equals(xmlPullParser.getAttributeValue("", MessageEvent.DELIVERED)), xmlPullParser.getAttributeValue("", "uri"));
                    obj2 = obj;
                }
                obj2 = obj;
            } else {
                if (next == 3 && xmlPullParser.getName().equals(multipleAddresses.getElementName())) {
                    obj2 = 1;
                }
                obj2 = obj;
            }
            obj = obj2;
        }
        return multipleAddresses;
    }
}
