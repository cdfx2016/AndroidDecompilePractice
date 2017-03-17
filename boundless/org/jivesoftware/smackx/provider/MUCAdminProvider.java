package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.packet.MUCAdmin;
import org.jivesoftware.smackx.packet.MUCAdmin.Item;
import org.jivesoftware.smackx.packet.Nick;
import org.xmlpull.v1.XmlPullParser;

public class MUCAdminProvider implements IQProvider {
    private Item parseItem(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Item item = new Item(xmlPullParser.getAttributeValue("", "affiliation"), xmlPullParser.getAttributeValue("", "role"));
        item.setNick(xmlPullParser.getAttributeValue("", Nick.ELEMENT_NAME));
        item.setJid(xmlPullParser.getAttributeValue("", "jid"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("actor")) {
                    item.setActor(xmlPullParser.getAttributeValue("", "jid"));
                }
                if (xmlPullParser.getName().equals("reason")) {
                    item.setReason(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("item")) {
                obj = 1;
            }
        }
        return item;
    }

    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ mUCAdmin = new MUCAdmin();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    mUCAdmin.addItem(parseItem(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                obj = 1;
            }
        }
        return mUCAdmin;
    }
}
