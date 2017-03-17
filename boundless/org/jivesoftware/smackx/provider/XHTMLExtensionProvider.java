package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.XHTMLExtension;
import org.xmlpull.v1.XmlPullParser;

public class XHTMLExtensionProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        PacketExtension xHTMLExtension = new XHTMLExtension();
        Object obj = null;
        StringBuilder stringBuilder = new StringBuilder();
        int depth = xmlPullParser.getDepth();
        int depth2 = xmlPullParser.getDepth();
        String str = "";
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals(TtmlNode.TAG_BODY)) {
                    stringBuilder = new StringBuilder();
                    depth2 = xmlPullParser.getDepth();
                }
                str = xmlPullParser.getText();
                stringBuilder.append(xmlPullParser.getText());
            } else if (next == 4) {
                if (stringBuilder != null) {
                    stringBuilder.append(StringUtils.escapeForXML(xmlPullParser.getText()));
                }
            } else if (next == 3) {
                if (xmlPullParser.getName().equals(TtmlNode.TAG_BODY) && xmlPullParser.getDepth() <= r1) {
                    stringBuilder.append(xmlPullParser.getText());
                    xHTMLExtension.addBody(stringBuilder.toString());
                } else if (xmlPullParser.getName().equals(xHTMLExtension.getElementName()) && xmlPullParser.getDepth() <= depth) {
                    obj = 1;
                } else if (str == null || !str.equals(xmlPullParser.getText())) {
                    stringBuilder.append(xmlPullParser.getText());
                }
            }
        }
        return xHTMLExtension;
    }
}
