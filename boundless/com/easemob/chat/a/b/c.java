package com.easemob.chat.a.b;

import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class c {
    public static String a(String str) {
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            XmlPullParser newPullParser = newInstance.newPullParser();
            newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            newPullParser.setInput(new StringReader(str));
            Object obj = null;
            while (obj == null) {
                int next = newPullParser.next();
                if (next == 2) {
                    if ("entry".equals(newPullParser.getName())) {
                        return newPullParser.nextText();
                    }
                } else if (next == 3 && "item".equals(newPullParser.getName())) {
                    obj = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
