package org.jivesoftware.smackx.bytestreams.socks5.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Activate;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.Mode;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHostUsed;
import org.xmlpull.v1.XmlPullParser;

public class BytestreamsProvider implements IQProvider {
    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ bytestream = new Bytestream();
        String attributeValue = xmlPullParser.getAttributeValue("", "sid");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "mode");
        String str = null;
        String str2 = null;
        Object obj = null;
        String str3 = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            String name = xmlPullParser.getName();
            if (next == 2) {
                if (name.equals(StreamHost.ELEMENTNAME)) {
                    str2 = xmlPullParser.getAttributeValue("", "jid");
                    str = xmlPullParser.getAttributeValue("", "host");
                    str3 = xmlPullParser.getAttributeValue("", "port");
                } else if (name.equals(StreamHostUsed.ELEMENTNAME)) {
                    bytestream.setUsedHost(xmlPullParser.getAttributeValue("", "jid"));
                } else if (name.equals(Activate.ELEMENTNAME)) {
                    bytestream.setToActivate(xmlPullParser.getAttributeValue("", "jid"));
                }
            } else if (next == 3) {
                if (name.equals("streamhost")) {
                    if (str3 == null) {
                        bytestream.addStreamHost(str2, str);
                    } else {
                        bytestream.addStreamHost(str2, str, Integer.parseInt(str3));
                    }
                    str3 = null;
                    str = null;
                    str2 = null;
                } else if (name.equals("query")) {
                    obj = 1;
                }
            }
        }
        bytestream.setMode(Mode.fromName(attributeValue2));
        bytestream.setSessionID(attributeValue);
        return bytestream;
    }
}
