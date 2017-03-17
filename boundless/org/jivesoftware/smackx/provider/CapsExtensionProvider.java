package org.jivesoftware.smackx.provider;

import java.io.IOException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.entitycaps.EntityCapsManager;
import org.jivesoftware.smackx.entitycaps.packet.CapsExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CapsExtensionProvider implements PacketExtensionProvider {
    private static final int MAX_DEPTH = 10;

    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, XMPPException {
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        while (true) {
            if (xmlPullParser.getEventType() == 2 && xmlPullParser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT)) {
                str2 = xmlPullParser.getAttributeValue(null, "hash");
                str = xmlPullParser.getAttributeValue(null, "ver");
                str3 = xmlPullParser.getAttributeValue(null, "node");
            }
            if (xmlPullParser.getEventType() == 3 && xmlPullParser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT)) {
                break;
            }
            xmlPullParser.next();
            if (i < 10) {
                i++;
            } else {
                throw new XMPPException("Malformed caps element");
            }
        }
        if (str2 != null && str != null && str3 != null) {
            return new CapsExtension(str3, str, str2);
        }
        throw new XMPPException("Caps elment with missing attributes");
    }
}
