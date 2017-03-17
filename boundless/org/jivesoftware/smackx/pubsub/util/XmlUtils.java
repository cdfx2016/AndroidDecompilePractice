package org.jivesoftware.smackx.pubsub.util;

import com.easemob.util.HanziToPinyin.Token;

public class XmlUtils {
    public static void appendAttribute(StringBuilder stringBuilder, String str, String str2) {
        stringBuilder.append(Token.SEPARATOR);
        stringBuilder.append(str);
        stringBuilder.append("='");
        stringBuilder.append(str2);
        stringBuilder.append("'");
    }
}
