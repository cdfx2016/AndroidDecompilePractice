package org.jivesoftware.smackx.pubsub.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.Subscription.State;
import org.xmlpull.v1.XmlPullParser;

public class SubscriptionProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        State state = null;
        String attributeValue = xmlPullParser.getAttributeValue(null, "jid");
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "node");
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "subid");
        String attributeValue4 = xmlPullParser.getAttributeValue(null, "subscription");
        boolean z = false;
        if (xmlPullParser.next() == 2 && xmlPullParser.getName().equals("subscribe-options")) {
            if (xmlPullParser.next() == 2 && xmlPullParser.getName().equals("required")) {
                z = true;
            }
            while (xmlPullParser.next() != 3) {
                if (xmlPullParser.getName() == "subscribe-options") {
                    break;
                }
            }
        }
        boolean z2 = z;
        while (xmlPullParser.getEventType() != 3) {
            xmlPullParser.next();
        }
        if (attributeValue4 != null) {
            state = State.valueOf(attributeValue4);
        }
        return new Subscription(attributeValue, attributeValue2, attributeValue3, state, z2);
    }
}
