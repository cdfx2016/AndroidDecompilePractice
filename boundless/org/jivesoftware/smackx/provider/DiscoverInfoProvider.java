package org.jivesoftware.smackx.provider;

import com.fanyu.boundless.config.Preferences;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.xmlpull.v1.XmlPullParser;

public class DiscoverInfoProvider implements IQProvider {
    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ discoverInfo = new DiscoverInfo();
        Object obj = null;
        String str = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        String str5 = "";
        discoverInfo.setNode(xmlPullParser.getAttributeValue("", "node"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("identity")) {
                    str = xmlPullParser.getAttributeValue("", "category");
                    str2 = xmlPullParser.getAttributeValue("", Preferences.sbry);
                    str3 = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE);
                    str5 = xmlPullParser.getAttributeValue(xmlPullParser.getNamespace("xml"), "lang");
                } else if (xmlPullParser.getName().equals("feature")) {
                    str4 = xmlPullParser.getAttributeValue("", "var");
                } else {
                    discoverInfo.addExtension(PacketParserUtils.parsePacketExtension(xmlPullParser.getName(), xmlPullParser.getNamespace(), xmlPullParser));
                }
            } else if (next == 3) {
                if (xmlPullParser.getName().equals("identity")) {
                    Identity identity = new Identity(str, str2, str3);
                    if (str5 != null) {
                        identity.setLanguage(str5);
                    }
                    discoverInfo.addIdentity(identity);
                }
                if (xmlPullParser.getName().equals("feature")) {
                    discoverInfo.addFeature(str4);
                }
                if (xmlPullParser.getName().equals("query")) {
                    obj = 1;
                }
            }
        }
        return discoverInfo;
    }
}
