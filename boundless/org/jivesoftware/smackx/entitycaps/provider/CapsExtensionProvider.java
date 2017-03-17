package org.jivesoftware.smackx.entitycaps.provider;

import java.io.IOException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.jivesoftware.smackx.entitycaps.packet.CapsExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CapsExtensionProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, XMPPException {
        if (xmlPullParser.getEventType() == 2 && xmlPullParser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT)) {
            String attributeValue = xmlPullParser.getAttributeValue(null, "hash");
            String attributeValue2 = xmlPullParser.getAttributeValue(null, "ver");
            String attributeValue3 = xmlPullParser.getAttributeValue(null, "node");
            xmlPullParser.next();
            if (xmlPullParser.getEventType() != 3 || !xmlPullParser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT)) {
                throw new XMPPException("Malformed nested Caps element");
            } else if (attributeValue != null && attributeValue2 != null && attributeValue3 != null) {
                return new CapsExtension(attributeValue3, attributeValue2, attributeValue);
            } else {
                throw new XMPPException("Caps elment with missing attributes");
            }
        }
        throw new XMPPException("Malformed Caps element");
    }
}
