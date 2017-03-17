package org.jivesoftware.smackx.pubsub.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.xmlpull.v1.XmlPullParser;

public class PubSubProvider implements IQProvider {
    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ pubSub = new PubSub();
        String namespace = xmlPullParser.getNamespace();
        pubSub.setPubSubNamespace(PubSubNamespace.valueOfFromXmlns(namespace));
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                PacketExtension parsePacketExtension = PacketParserUtils.parsePacketExtension(xmlPullParser.getName(), namespace, xmlPullParser);
                if (parsePacketExtension != null) {
                    pubSub.addExtension(parsePacketExtension);
                }
            } else if (next == 3 && xmlPullParser.getName().equals("pubsub")) {
                obj = 1;
            }
        }
        return pubSub;
    }
}
