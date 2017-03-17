package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;

public abstract class IQ extends Packet {
    private Type type = Type.GET;

    public static class Type {
        public static final Type ERROR = new Type("error");
        public static final Type GET = new Type("get");
        public static final Type RESULT = new Type(Form.TYPE_RESULT);
        public static final Type SET = new Type("set");
        private String value;

        private Type(String str) {
            this.value = str;
        }

        public static Type fromString(String str) {
            if (str == null) {
                return null;
            }
            String toLowerCase = str.toLowerCase();
            return GET.toString().equals(toLowerCase) ? GET : SET.toString().equals(toLowerCase) ? SET : ERROR.toString().equals(toLowerCase) ? ERROR : RESULT.toString().equals(toLowerCase) ? RESULT : null;
        }

        public String toString() {
            return this.value;
        }
    }

    public IQ(IQ iq) {
        super(iq);
        this.type = iq.getType();
    }

    public static IQ createErrorResponse(final IQ iq, XMPPError xMPPError) {
        if (iq.getType() == Type.GET || iq.getType() == Type.SET) {
            IQ anonymousClass2 = new IQ() {
                public String getChildElementXML() {
                    return iq.getChildElementXML();
                }
            };
            anonymousClass2.setType(Type.ERROR);
            anonymousClass2.setPacketID(iq.getPacketID());
            anonymousClass2.setFrom(iq.getTo());
            anonymousClass2.setTo(iq.getFrom());
            anonymousClass2.setError(xMPPError);
            return anonymousClass2;
        }
        throw new IllegalArgumentException("IQ must be of type 'set' or 'get'. Original IQ: " + iq.toXML());
    }

    public static IQ createResultIQ(IQ iq) {
        if (iq.getType() == Type.GET || iq.getType() == Type.SET) {
            IQ anonymousClass1 = new IQ() {
                public String getChildElementXML() {
                    return null;
                }
            };
            anonymousClass1.setType(Type.RESULT);
            anonymousClass1.setPacketID(iq.getPacketID());
            anonymousClass1.setFrom(iq.getTo());
            anonymousClass1.setTo(iq.getFrom());
            return anonymousClass1;
        }
        throw new IllegalArgumentException("IQ must be of type 'set' or 'get'. Original IQ: " + iq.toXML());
    }

    public abstract String getChildElementXML();

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        if (type == null) {
            this.type = Type.GET;
        } else {
            this.type = type;
        }
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<iq ");
        if (getPacketID() != null) {
            stringBuilder.append("id=\"" + getPacketID() + "\" ");
        }
        if (getTo() != null) {
            stringBuilder.append("to=\"").append(StringUtils.escapeForXML(getTo())).append("\" ");
        }
        if (getFrom() != null) {
            stringBuilder.append("from=\"").append(StringUtils.escapeForXML(getFrom())).append("\" ");
        }
        if (this.type == null) {
            stringBuilder.append("type=\"get\">");
        } else {
            stringBuilder.append("type=\"").append(getType()).append("\">");
        }
        String childElementXML = getChildElementXML();
        if (childElementXML != null) {
            stringBuilder.append(childElementXML);
        }
        XMPPError error = getError();
        if (error != null) {
            stringBuilder.append(error.toXML());
        }
        stringBuilder.append("</iq>");
        return stringBuilder.toString();
    }
}
