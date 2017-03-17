package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.StringUtils;

public class Authentication extends IQ {
    private String digest = null;
    private String password = null;
    private String resource = null;
    private String username = null;

    public Authentication() {
        setType(Type.SET);
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:auth\">");
        if (this.username != null) {
            if (this.username.equals("")) {
                stringBuilder.append("<username/>");
            } else {
                stringBuilder.append("<username>").append(this.username).append("</username>");
            }
        }
        if (this.digest != null) {
            if (this.digest.equals("")) {
                stringBuilder.append("<digest/>");
            } else {
                stringBuilder.append("<digest>").append(this.digest).append("</digest>");
            }
        }
        if (this.password != null && this.digest == null) {
            if (this.password.equals("")) {
                stringBuilder.append("<password/>");
            } else {
                stringBuilder.append("<password>").append(StringUtils.escapeForXML(this.password)).append("</password>");
            }
        }
        if (this.resource != null) {
            if (this.resource.equals("")) {
                stringBuilder.append("<resource/>");
            } else {
                stringBuilder.append("<resource>").append(this.resource).append("</resource>");
            }
        }
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public String getDigest() {
        return this.digest;
    }

    public String getPassword() {
        return this.password;
    }

    public String getResource() {
        return this.resource;
    }

    public String getUsername() {
        return this.username;
    }

    public void setDigest(String str) {
        this.digest = str;
    }

    public void setDigest(String str, String str2) {
        this.digest = StringUtils.hash(str + str2);
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setResource(String str) {
        this.resource = str;
    }

    public void setUsername(String str) {
        this.username = str;
    }
}
