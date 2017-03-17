package org.jivesoftware.smackx.provider;

import java.text.ParseException;
import java.util.Date;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.xmlpull.v1.XmlPullParser;

public class DelayInformationProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        Date parseDate;
        String str = null;
        try {
            parseDate = StringUtils.parseDate(xmlPullParser.getAttributeValue("", "stamp"));
        } catch (ParseException e) {
            parseDate = null == null ? new Date(0) : null;
        }
        PacketExtension delayInformation = new DelayInformation(parseDate);
        delayInformation.setFrom(xmlPullParser.getAttributeValue("", "from"));
        String nextText = xmlPullParser.nextText();
        if (!"".equals(nextText)) {
            str = nextText;
        }
        delayInformation.setReason(str);
        return delayInformation;
    }
}
