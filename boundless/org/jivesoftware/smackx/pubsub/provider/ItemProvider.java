package org.jivesoftware.smackx.pubsub.provider;

import com.easemob.util.HanziToPinyin.Token;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.xmlpull.v1.XmlPullParser;

public class ItemProvider implements PacketExtensionProvider {
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
        int i = next;
        Object obj2 = null;
        while (obj2 == null) {
            if (i == 3 && xmlPullParser.getName().equals(name)) {
                obj2 = 1;
            } else {
                if (xmlPullParser.getEventType() == 2) {
                    stringBuilder.append("<").append(xmlPullParser.getName());
                    if (xmlPullParser.getName().equals(name2) && namespace.length() > 0) {
                        stringBuilder.append(" xmlns=\"").append(namespace).append("\"");
                    }
                    i = xmlPullParser.getAttributeCount();
                    for (int i2 = 0; i2 < i; i2++) {
                        stringBuilder.append(Token.SEPARATOR).append(xmlPullParser.getAttributeName(i2)).append("=\"").append(xmlPullParser.getAttributeValue(i2)).append("\"");
                    }
                    if (xmlPullParser.isEmptyElementTag()) {
                        stringBuilder.append("/>");
                        obj = 1;
                    } else {
                        stringBuilder.append(">");
                    }
                } else if (xmlPullParser.getEventType() == 3) {
                    if (obj != null) {
                        obj = null;
                    } else {
                        stringBuilder.append("</").append(xmlPullParser.getName()).append(">");
                    }
                } else if (xmlPullParser.getEventType() == 4) {
                    stringBuilder.append(xmlPullParser.getText());
                }
                i = xmlPullParser.next();
            }
        }
        return new PayloadItem(attributeValue, attributeValue2, new SimplePayload(name2, namespace, stringBuilder.toString()));
    }
}
