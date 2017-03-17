package org.jivesoftware.smackx.bytestreams.ibb.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;
import org.xmlpull.v1.XmlPullParser;

public class DataPacketProvider implements PacketExtensionProvider, IQProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        return new DataPacketExtension(xmlPullParser.getAttributeValue("", "sid"), Long.parseLong(xmlPullParser.getAttributeValue("", "seq")), xmlPullParser.nextText());
    }

    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        return new Data((DataPacketExtension) parseExtension(xmlPullParser));
    }
}
