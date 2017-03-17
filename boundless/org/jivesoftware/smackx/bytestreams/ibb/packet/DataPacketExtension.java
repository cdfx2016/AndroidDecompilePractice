package org.jivesoftware.smackx.bytestreams.ibb.packet;

import com.easemob.util.HanziToPinyin.Token;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;

public class DataPacketExtension implements PacketExtension {
    public static final String ELEMENT_NAME = "data";
    private final String data;
    private byte[] decodedData;
    private final long seq;
    private final String sessionID;

    public DataPacketExtension(String str, long j, String str2) {
        if (str == null || "".equals(str)) {
            throw new IllegalArgumentException("Session ID must not be null or empty");
        } else if (j < 0 || j > 65535) {
            throw new IllegalArgumentException("Sequence must not be between 0 and 65535");
        } else if (str2 == null) {
            throw new IllegalArgumentException("Data must not be null");
        } else {
            this.sessionID = str;
            this.seq = j;
            this.data = str2;
        }
    }

    public String getData() {
        return this.data;
    }

    public byte[] getDecodedData() {
        if (this.decodedData != null) {
            return this.decodedData;
        }
        if (this.data.matches(".*={1,2}+.+")) {
            return null;
        }
        this.decodedData = StringUtils.decodeBase64(this.data);
        return this.decodedData;
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return InBandBytestreamManager.NAMESPACE;
    }

    public long getSeq() {
        return this.seq;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<");
        stringBuilder.append(getElementName());
        stringBuilder.append(Token.SEPARATOR);
        stringBuilder.append("xmlns=\"");
        stringBuilder.append(InBandBytestreamManager.NAMESPACE);
        stringBuilder.append("\" ");
        stringBuilder.append("seq=\"");
        stringBuilder.append(this.seq);
        stringBuilder.append("\" ");
        stringBuilder.append("sid=\"");
        stringBuilder.append(this.sessionID);
        stringBuilder.append("\">");
        stringBuilder.append(this.data);
        stringBuilder.append("</");
        stringBuilder.append(getElementName());
        stringBuilder.append(">");
        return stringBuilder.toString();
    }
}
