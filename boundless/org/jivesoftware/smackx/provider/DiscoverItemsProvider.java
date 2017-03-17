package org.jivesoftware.smackx.provider;

import com.fanyu.boundless.config.Preferences;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.xmlpull.v1.XmlPullParser;

public class DiscoverItemsProvider implements IQProvider {
    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        IQ discoverItems = new DiscoverItems();
        Object obj = null;
        String str = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        discoverItems.setNode(xmlPullParser.getAttributeValue("", "node"));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2 && "item".equals(xmlPullParser.getName())) {
                str = xmlPullParser.getAttributeValue("", "jid");
                str2 = xmlPullParser.getAttributeValue("", Preferences.sbry);
                str4 = xmlPullParser.getAttributeValue("", "node");
                str3 = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_ACTION);
            } else if (next == 3 && "item".equals(xmlPullParser.getName())) {
                Item item = new Item(str);
                item.setName(str2);
                item.setNode(str4);
                item.setAction(str3);
                discoverItems.addItem(item);
            } else if (next == 3 && "query".equals(xmlPullParser.getName())) {
                obj = 1;
            }
        }
        return discoverItems;
    }
}
