package org.jivesoftware.smackx.packet;

import java.util.Date;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;

public class StreamInitiation extends IQ {
    private Feature featureNegotiation;
    private File file;
    private String id;
    private String mimeType;

    public class Feature implements PacketExtension {
        private final DataForm data;

        public Feature(DataForm dataForm) {
            this.data = dataForm;
        }

        public DataForm getData() {
            return this.data;
        }

        public String getElementName() {
            return "feature";
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/feature-neg";
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<feature xmlns=\"http://jabber.org/protocol/feature-neg\">");
            stringBuilder.append(this.data.toXML());
            stringBuilder.append("</feature>");
            return stringBuilder.toString();
        }
    }

    public static class File implements PacketExtension {
        private Date date;
        private String desc;
        private String hash;
        private boolean isRanged;
        private final String name;
        private final long size;

        public File(String str, long j) {
            if (str == null) {
                throw new NullPointerException("name cannot be null");
            }
            this.name = str;
            this.size = j;
        }

        public Date getDate() {
            return this.date;
        }

        public String getDesc() {
            return this.desc;
        }

        public String getElementName() {
            return "file";
        }

        public String getHash() {
            return this.hash;
        }

        public String getName() {
            return this.name;
        }

        public String getNamespace() {
            return "http://jabber.org/protocol/si/profile/file-transfer";
        }

        public long getSize() {
            return this.size;
        }

        public boolean isRanged() {
            return this.isRanged;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setDesc(String str) {
            this.desc = str;
        }

        public void setHash(String str) {
            this.hash = str;
        }

        public void setRanged(boolean z) {
            this.isRanged = z;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\" ");
            if (getName() != null) {
                stringBuilder.append("name=\"").append(StringUtils.escapeForXML(getName())).append("\" ");
            }
            if (getSize() > 0) {
                stringBuilder.append("size=\"").append(getSize()).append("\" ");
            }
            if (getDate() != null) {
                stringBuilder.append("date=\"").append(StringUtils.formatXEP0082Date(this.date)).append("\" ");
            }
            if (getHash() != null) {
                stringBuilder.append("hash=\"").append(getHash()).append("\" ");
            }
            if ((this.desc == null || this.desc.length() <= 0) && !this.isRanged) {
                stringBuilder.append("/>");
            } else {
                stringBuilder.append(">");
                if (getDesc() != null && this.desc.length() > 0) {
                    stringBuilder.append("<desc>").append(StringUtils.escapeForXML(getDesc())).append("</desc>");
                }
                if (isRanged()) {
                    stringBuilder.append("<range/>");
                }
                stringBuilder.append("</").append(getElementName()).append(">");
            }
            return stringBuilder.toString();
        }
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        if (getType().equals(Type.SET)) {
            stringBuilder.append("<si xmlns=\"http://jabber.org/protocol/si\" ");
            if (getSessionID() != null) {
                stringBuilder.append("id=\"").append(getSessionID()).append("\" ");
            }
            if (getMimeType() != null) {
                stringBuilder.append("mime-type=\"").append(getMimeType()).append("\" ");
            }
            stringBuilder.append("profile=\"http://jabber.org/protocol/si/profile/file-transfer\">");
            String toXML = this.file.toXML();
            if (toXML != null) {
                stringBuilder.append(toXML);
            }
        } else if (getType().equals(Type.RESULT)) {
            stringBuilder.append("<si xmlns=\"http://jabber.org/protocol/si\">");
        } else {
            throw new IllegalArgumentException("IQ Type not understood");
        }
        if (this.featureNegotiation != null) {
            stringBuilder.append(this.featureNegotiation.toXML());
        }
        stringBuilder.append("</si>");
        return stringBuilder.toString();
    }

    public DataForm getFeatureNegotiationForm() {
        return this.featureNegotiation.getData();
    }

    public File getFile() {
        return this.file;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getSessionID() {
        return this.id;
    }

    public void setFeatureNegotiationForm(DataForm dataForm) {
        this.featureNegotiation = new Feature(dataForm);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setMimeType(String str) {
        this.mimeType = str;
    }

    public void setSesssionID(String str) {
        this.id = str;
    }
}
