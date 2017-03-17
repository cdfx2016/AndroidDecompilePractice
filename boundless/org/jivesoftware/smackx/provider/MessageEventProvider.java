package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.packet.MessageEvent;
import org.xmlpull.v1.XmlPullParser;

public class MessageEventProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        PacketExtension messageEvent = new MessageEvent();
        boolean z = false;
        while (!z) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("id")) {
                    messageEvent.setPacketID(xmlPullParser.nextText());
                }
                if (xmlPullParser.getName().equals(MessageEvent.COMPOSING)) {
                    messageEvent.setComposing(true);
                }
                if (xmlPullParser.getName().equals(MessageEvent.DELIVERED)) {
                    messageEvent.setDelivered(true);
                }
                if (xmlPullParser.getName().equals(MessageEvent.DISPLAYED)) {
                    messageEvent.setDisplayed(true);
                }
                if (xmlPullParser.getName().equals(MessageEvent.OFFLINE)) {
                    messageEvent.setOffline(true);
                }
            } else if (next == 3 && xmlPullParser.getName().equals("x")) {
                z = true;
            }
        }
        return messageEvent;
    }
}
