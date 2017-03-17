package com.easemob.chat.a.b;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.xmlpull.v1.XmlPullParser;

public class b implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        String attributeValue = xmlPullParser.getAttributeValue(null, "id");
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "node");
        String name = xmlPullParser.getName();
        int next = xmlPullParser.next();
        if (next == 3) {
            return new Item(attributeValue, attributeValue2);
        }
        String name2 = xmlPullParser.getName();
        String namespace = xmlPullParser.getNamespace();
        if (ProviderManager.getInstance().getExtensionProvider(name2, namespace) != null) {
            return new PayloadItem(attributeValue, attributeValue2, PacketParserUtils.parsePacketExtension(name2, namespace, xmlPullParser));
        }
        Object obj = null;
        StringBuilder stringBuilder = new StringBuilder();
        while (obj == null) {
            if (next == 3 && xmlPullParser.getName().equals(name)) {
                obj = 1;
            } else if (!(next == 2 && xmlPullParser.isEmptyElementTag())) {
                stringBuilder.append(xmlPullParser.getText());
            }
            if (obj == null) {
                next = xmlPullParser.next();
            }
        }
        return new a(stringBuilder.toString());
    }
}
