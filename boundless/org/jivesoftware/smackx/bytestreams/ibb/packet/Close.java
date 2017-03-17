package org.jivesoftware.smackx.bytestreams.ibb.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;

public class Close extends IQ {
    private final String sessionID;

    public Close(String str) {
        if (str == null || "".equals(str)) {
            throw new IllegalArgumentException("Session ID must not be null or empty");
        }
        this.sessionID = str;
        setType(Type.SET);
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<close ");
        stringBuilder.append("xmlns=\"");
        stringBuilder.append(InBandBytestreamManager.NAMESPACE);
        stringBuilder.append("\" ");
        stringBuilder.append("sid=\"");
        stringBuilder.append(this.sessionID);
        stringBuilder.append("\"");
        stringBuilder.append("/>");
        return stringBuilder.toString();
    }

    public String getSessionID() {
        return this.sessionID;
    }
}
