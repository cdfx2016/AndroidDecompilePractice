package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.packet.DelayInfo;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.xmlpull.v1.XmlPullParser;

public class DelayInfoProvider extends DelayInformationProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        return new DelayInfo((DelayInformation) super.parseExtension(xmlPullParser));
    }
}
