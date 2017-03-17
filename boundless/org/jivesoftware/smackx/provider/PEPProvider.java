package org.jivesoftware.smackx.provider;

import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class PEPProvider implements PacketExtensionProvider {
    Map<String, PacketExtensionProvider> nodeParsers = new HashMap();
    PacketExtension pepItem;

    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        while (obj == null) {
            Object obj2;
            int next = xmlPullParser.next();
            if (next != 2) {
                if (next == 3 && xmlPullParser.getName().equals("event")) {
                    obj2 = 1;
                }
                obj2 = obj;
            } else if (xmlPullParser.getName().equals("event")) {
                obj2 = obj;
            } else {
                if (xmlPullParser.getName().equals("items")) {
                    PacketExtensionProvider packetExtensionProvider = (PacketExtensionProvider) this.nodeParsers.get(xmlPullParser.getAttributeValue("", "node"));
                    if (packetExtensionProvider != null) {
                        this.pepItem = packetExtensionProvider.parseExtension(xmlPullParser);
                    }
                    obj2 = obj;
                }
                obj2 = obj;
            }
            obj = obj2;
        }
        return this.pepItem;
    }

    public void registerPEPParserExtension(String str, PacketExtensionProvider packetExtensionProvider) {
        this.nodeParsers.put(str, packetExtensionProvider);
    }
}
