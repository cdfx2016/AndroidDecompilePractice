package org.jivesoftware.smackx.packet;

import com.fanyu.boundless.common.camera.Intents.WifiConnect;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.util.StringUtils;

public class VCard extends IQ {
    private String emailHome;
    private String emailWork;
    private String firstName;
    private Map<String, String> homeAddr = new HashMap();
    private Map<String, String> homePhones = new HashMap();
    private String lastName;
    private String middleName;
    private String organization;
    private String organizationUnit;
    private Map<String, String> otherSimpleFields = new HashMap();
    private Map<String, String> otherUnescapableFields = new HashMap();
    private String photoBinval;
    private String photoMimeType;
    private Map<String, String> workAddr = new HashMap();
    private Map<String, String> workPhones = new HashMap();

    private interface ContentBuilder {
        void addTagContent();
    }

    private class VCardWriter {
        private final StringBuilder sb;

        VCardWriter(StringBuilder stringBuilder) {
            this.sb = stringBuilder;
        }

        private void appendAddress(final Map<String, String> map, final String str) {
            if (map.size() > 0) {
                appendTag("ADR", true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.appendEmptyTag(str);
                        for (Entry entry : map.entrySet()) {
                            VCardWriter.this.appendTag((String) entry.getKey(), StringUtils.escapeForXML((String) entry.getValue()));
                        }
                    }
                });
            }
        }

        private void appendEmail(final String str, final String str2) {
            if (str != null) {
                appendTag("EMAIL", true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.appendEmptyTag(str2);
                        VCardWriter.this.appendEmptyTag("INTERNET");
                        VCardWriter.this.appendEmptyTag("PREF");
                        VCardWriter.this.appendTag("USERID", StringUtils.escapeForXML(str));
                    }
                });
            }
        }

        private void appendEmptyTag(Object obj) {
            this.sb.append('<').append(obj).append("/>");
        }

        private void appendGenericFields() {
            for (Entry entry : VCard.this.otherSimpleFields.entrySet()) {
                appendTag(((String) entry.getKey()).toString(), StringUtils.escapeForXML((String) entry.getValue()));
            }
            for (Entry entry2 : VCard.this.otherUnescapableFields.entrySet()) {
                appendTag(((String) entry2.getKey()).toString(), (String) entry2.getValue());
            }
        }

        private void appendN() {
            appendTag("N", true, new ContentBuilder() {
                public void addTagContent() {
                    VCardWriter.this.appendTag("FAMILY", StringUtils.escapeForXML(VCard.this.lastName));
                    VCardWriter.this.appendTag("GIVEN", StringUtils.escapeForXML(VCard.this.firstName));
                    VCardWriter.this.appendTag("MIDDLE", StringUtils.escapeForXML(VCard.this.middleName));
                }
            });
        }

        private void appendOrganization() {
            if (VCard.this.hasOrganizationFields()) {
                appendTag("ORG", true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.appendTag("ORGNAME", StringUtils.escapeForXML(VCard.this.organization));
                        VCardWriter.this.appendTag("ORGUNIT", StringUtils.escapeForXML(VCard.this.organizationUnit));
                    }
                });
            }
        }

        private void appendPhones(Map<String, String> map, final String str) {
            for (final Entry entry : map.entrySet()) {
                appendTag("TEL", true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.appendEmptyTag(entry.getKey());
                        VCardWriter.this.appendEmptyTag(str);
                        VCardWriter.this.appendTag("NUMBER", StringUtils.escapeForXML((String) entry.getValue()));
                    }
                });
            }
        }

        private void appendPhoto() {
            if (VCard.this.photoBinval != null) {
                appendTag("PHOTO", true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.appendTag("BINVAL", VCard.this.photoBinval);
                        VCardWriter.this.appendTag(WifiConnect.TYPE, StringUtils.escapeForXML(VCard.this.photoMimeType));
                    }
                });
            }
        }

        private void appendTag(String str, final String str2) {
            if (str2 != null) {
                appendTag(str, true, new ContentBuilder() {
                    public void addTagContent() {
                        VCardWriter.this.sb.append(str2.trim());
                    }
                });
            }
        }

        private void appendTag(String str, String str2, String str3, boolean z, ContentBuilder contentBuilder) {
            this.sb.append('<').append(str);
            if (str2 != null) {
                this.sb.append(' ').append(str2).append('=').append('\'').append(str3).append('\'');
            }
            if (z) {
                this.sb.append('>');
                contentBuilder.addTagContent();
                this.sb.append("</").append(str).append(">\n");
                return;
            }
            this.sb.append("/>\n");
        }

        private void appendTag(String str, boolean z, ContentBuilder contentBuilder) {
            appendTag(str, null, null, z, contentBuilder);
        }

        private void buildActualContent() {
            if (VCard.this.hasNameField()) {
                appendN();
            }
            appendOrganization();
            appendGenericFields();
            appendPhoto();
            appendEmail(VCard.this.emailWork, "WORK");
            appendEmail(VCard.this.emailHome, "HOME");
            appendPhones(VCard.this.workPhones, "WORK");
            appendPhones(VCard.this.homePhones, "HOME");
            appendAddress(VCard.this.workAddr, "WORK");
            appendAddress(VCard.this.homeAddr, "HOME");
        }

        public void write() {
            appendTag("vCard", "xmlns", "vcard-temp", VCard.this.hasContent(), new ContentBuilder() {
                public void addTagContent() {
                    VCardWriter.this.buildActualContent();
                }
            });
        }
    }

    private void checkAuthenticated(Connection connection, boolean z) {
        if (connection == null) {
            throw new IllegalArgumentException("No connection was provided");
        } else if (!connection.isAuthenticated()) {
            throw new IllegalArgumentException("Connection is not authenticated");
        } else if (z && connection.isAnonymous()) {
            throw new IllegalArgumentException("Connection cannot be anonymous");
        }
    }

    private void copyFieldsFrom(VCard vCard) {
        for (Field field : VCard.class.getDeclaredFields()) {
            if (field.getDeclaringClass() == VCard.class && !Modifier.isFinal(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    field.set(this, field.get(vCard));
                } catch (Throwable e) {
                    throw new RuntimeException("This cannot happen:" + field, e);
                }
            }
        }
    }

    private void doLoad(Connection connection, String str) throws XMPPException {
        setType(Type.GET);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(getPacketID()));
        connection.sendPacket(this);
        Packet nextResult = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        if (nextResult == null) {
            String str2 = "Timeout getting VCard information";
            throw new XMPPException(str2, new XMPPError(Condition.request_timeout, str2));
        } else if (nextResult.getError() != null) {
            throw new XMPPException(nextResult.getError());
        } else {
            try {
                copyFieldsFrom((VCard) nextResult);
            } catch (ClassCastException e) {
                System.out.println("No VCard for " + str);
            }
        }
    }

    public static byte[] getBytes(URL url) throws IOException {
        File file = new File(url.getPath());
        return file.exists() ? getFileBytes(file) : null;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bufferedInputStream;
        Throwable th;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            try {
                byte[] bArr = new byte[((int) file.length())];
                if (bufferedInputStream.read(bArr) != bArr.length) {
                    throw new IOException("Entire file not read");
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                return bArr;
            } catch (Throwable th2) {
                th = th2;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedInputStream = null;
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            throw th;
        }
    }

    private boolean hasContent() {
        return hasNameField() || hasOrganizationFields() || this.emailHome != null || this.emailWork != null || this.otherSimpleFields.size() > 0 || this.otherUnescapableFields.size() > 0 || this.homeAddr.size() > 0 || this.homePhones.size() > 0 || this.workAddr.size() > 0 || this.workPhones.size() > 0 || this.photoBinval != null;
    }

    private boolean hasNameField() {
        return (this.firstName == null && this.lastName == null && this.middleName == null) ? false : true;
    }

    private boolean hasOrganizationFields() {
        return (this.organization == null && this.organizationUnit == null) ? false : true;
    }

    private void updateFN() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.firstName != null) {
            stringBuilder.append(StringUtils.escapeForXML(this.firstName)).append(' ');
        }
        if (this.middleName != null) {
            stringBuilder.append(StringUtils.escapeForXML(this.middleName)).append(' ');
        }
        if (this.lastName != null) {
            stringBuilder.append(StringUtils.escapeForXML(this.lastName));
        }
        setField("FN", stringBuilder.toString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VCard vCard = (VCard) obj;
        if (this.emailHome != null) {
            if (!this.emailHome.equals(vCard.emailHome)) {
                return false;
            }
        } else if (vCard.emailHome != null) {
            return false;
        }
        if (this.emailWork != null) {
            if (!this.emailWork.equals(vCard.emailWork)) {
                return false;
            }
        } else if (vCard.emailWork != null) {
            return false;
        }
        if (this.firstName != null) {
            if (!this.firstName.equals(vCard.firstName)) {
                return false;
            }
        } else if (vCard.firstName != null) {
            return false;
        }
        if (!this.homeAddr.equals(vCard.homeAddr) || !this.homePhones.equals(vCard.homePhones)) {
            return false;
        }
        if (this.lastName != null) {
            if (!this.lastName.equals(vCard.lastName)) {
                return false;
            }
        } else if (vCard.lastName != null) {
            return false;
        }
        if (this.middleName != null) {
            if (!this.middleName.equals(vCard.middleName)) {
                return false;
            }
        } else if (vCard.middleName != null) {
            return false;
        }
        if (this.organization != null) {
            if (!this.organization.equals(vCard.organization)) {
                return false;
            }
        } else if (vCard.organization != null) {
            return false;
        }
        if (this.organizationUnit != null) {
            if (!this.organizationUnit.equals(vCard.organizationUnit)) {
                return false;
            }
        } else if (vCard.organizationUnit != null) {
            return false;
        }
        if (!this.otherSimpleFields.equals(vCard.otherSimpleFields) || !this.workAddr.equals(vCard.workAddr)) {
            return false;
        }
        if (this.photoBinval != null) {
            if (!this.photoBinval.equals(vCard.photoBinval)) {
                return false;
            }
        } else if (vCard.photoBinval != null) {
            return false;
        }
        return this.workPhones.equals(vCard.workPhones);
    }

    public String getAddressFieldHome(String str) {
        return (String) this.homeAddr.get(str);
    }

    public String getAddressFieldWork(String str) {
        return (String) this.workAddr.get(str);
    }

    public byte[] getAvatar() {
        return this.photoBinval == null ? null : StringUtils.decodeBase64(this.photoBinval);
    }

    public String getAvatarHash() {
        String str = null;
        byte[] avatar = getAvatar();
        if (avatar == null) {
            return str;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(avatar);
            return StringUtils.encodeHex(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return str;
        }
    }

    public String getAvatarMimeType() {
        return this.photoMimeType;
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        new VCardWriter(stringBuilder).write();
        return stringBuilder.toString();
    }

    public String getEmailHome() {
        return this.emailHome;
    }

    public String getEmailWork() {
        return this.emailWork;
    }

    public String getField(String str) {
        return (String) this.otherSimpleFields.get(str);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getJabberId() {
        return (String) this.otherSimpleFields.get("JABBERID");
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public String getNickName() {
        return (String) this.otherSimpleFields.get("NICKNAME");
    }

    public String getOrganization() {
        return this.organization;
    }

    public String getOrganizationUnit() {
        return this.organizationUnit;
    }

    public String getPhoneHome(String str) {
        return (String) this.homePhones.get(str);
    }

    public String getPhoneWork(String str) {
        return (String) this.workPhones.get(str);
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((this.organizationUnit != null ? this.organizationUnit.hashCode() : 0) + (((this.organization != null ? this.organization.hashCode() : 0) + (((this.emailWork != null ? this.emailWork.hashCode() : 0) + (((this.emailHome != null ? this.emailHome.hashCode() : 0) + (((this.middleName != null ? this.middleName.hashCode() : 0) + (((this.lastName != null ? this.lastName.hashCode() : 0) + (((this.firstName != null ? this.firstName.hashCode() : 0) + (((((((this.homePhones.hashCode() * 29) + this.workPhones.hashCode()) * 29) + this.homeAddr.hashCode()) * 29) + this.workAddr.hashCode()) * 29)) * 29)) * 29)) * 29)) * 29)) * 29)) * 29)) * 29) + this.otherSimpleFields.hashCode()) * 29;
        if (this.photoBinval != null) {
            i = this.photoBinval.hashCode();
        }
        return hashCode + i;
    }

    public void load(Connection connection) throws XMPPException {
        checkAuthenticated(connection, true);
        setFrom(connection.getUser());
        doLoad(connection, connection.getUser());
    }

    public void load(Connection connection, String str) throws XMPPException {
        checkAuthenticated(connection, false);
        setTo(str);
        doLoad(connection, str);
    }

    public void removeAvatar() {
        this.photoBinval = null;
        this.photoMimeType = null;
    }

    public void save(Connection connection) throws XMPPException {
        checkAuthenticated(connection, true);
        setType(Type.SET);
        setFrom(connection.getUser());
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(getPacketID()));
        connection.sendPacket(this);
        Packet nextResult = createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (nextResult == null) {
            throw new XMPPException("No response from server on status set.");
        } else if (nextResult.getError() != null) {
            throw new XMPPException(nextResult.getError());
        }
    }

    public void setAddressFieldHome(String str, String str2) {
        this.homeAddr.put(str, str2);
    }

    public void setAddressFieldWork(String str, String str2) {
        this.workAddr.put(str, str2);
    }

    public void setAvatar(String str, String str2) {
        this.photoBinval = str;
        this.photoMimeType = str2;
    }

    public void setAvatar(URL url) {
        byte[] bArr = new byte[0];
        try {
            bArr = getBytes(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setAvatar(bArr);
    }

    public void setAvatar(byte[] bArr) {
        setAvatar(bArr, "image/jpeg");
    }

    public void setAvatar(byte[] bArr, String str) {
        if (bArr == null) {
            removeAvatar();
        } else {
            setAvatar(StringUtils.encodeBase64(bArr), str);
        }
    }

    public void setEmailHome(String str) {
        this.emailHome = str;
    }

    public void setEmailWork(String str) {
        this.emailWork = str;
    }

    public void setField(String str, String str2) {
        setField(str, str2, false);
    }

    public void setField(String str, String str2, boolean z) {
        if (z) {
            this.otherUnescapableFields.put(str, str2);
        } else {
            this.otherSimpleFields.put(str, str2);
        }
    }

    public void setFirstName(String str) {
        this.firstName = str;
        updateFN();
    }

    public void setJabberId(String str) {
        this.otherSimpleFields.put("JABBERID", str);
    }

    public void setLastName(String str) {
        this.lastName = str;
        updateFN();
    }

    public void setMiddleName(String str) {
        this.middleName = str;
        updateFN();
    }

    public void setNickName(String str) {
        this.otherSimpleFields.put("NICKNAME", str);
    }

    public void setOrganization(String str) {
        this.organization = str;
    }

    public void setOrganizationUnit(String str) {
        this.organizationUnit = str;
    }

    public void setPhoneHome(String str, String str2) {
        this.homePhones.put(str, str2);
    }

    public void setPhoneWork(String str, String str2) {
        this.workPhones.put(str, str2);
    }

    public String toString() {
        return getChildElementXML();
    }
}
