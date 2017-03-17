package org.jivesoftware.smackx.provider;

import com.fanyu.boundless.config.Preferences;
import java.util.ArrayList;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.RemoteRosterEntry;
import org.jivesoftware.smackx.packet.RosterExchange;
import org.xmlpull.v1.XmlPullParser;

public class RosterExchangeProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        PacketExtension rosterExchange = new RosterExchange();
        Object obj = null;
        String str = "";
        String str2 = "";
        ArrayList arrayList = new ArrayList();
        while (obj == null) {
            ArrayList arrayList2;
            String attributeValue;
            Object obj2;
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    arrayList2 = new ArrayList();
                    str2 = xmlPullParser.getAttributeValue("", "jid");
                    attributeValue = xmlPullParser.getAttributeValue("", Preferences.sbry);
                } else {
                    arrayList2 = arrayList;
                    attributeValue = str2;
                    str2 = str;
                }
                if (xmlPullParser.getName().equals("group")) {
                    arrayList2.add(xmlPullParser.nextText());
                    obj2 = obj;
                } else {
                    obj2 = obj;
                }
            } else {
                if (next == 3) {
                    if (xmlPullParser.getName().equals("item")) {
                        rosterExchange.addRosterEntry(new RemoteRosterEntry(str, str2, (String[]) arrayList.toArray(new String[arrayList.size()])));
                    }
                    if (xmlPullParser.getName().equals("x")) {
                        ArrayList arrayList3 = arrayList;
                        attributeValue = str2;
                        str2 = str;
                        int i = 1;
                        arrayList2 = arrayList3;
                    }
                }
                arrayList2 = arrayList;
                attributeValue = str2;
                str2 = str;
                obj2 = obj;
            }
            obj = obj2;
            str = str2;
            str2 = attributeValue;
            arrayList = arrayList2;
        }
        return rosterExchange;
    }
}
