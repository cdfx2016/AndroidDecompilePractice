package org.jivesoftware.smack.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends IQ {
    private Map<String, String> attributes = new HashMap();
    private String instructions = null;
    private boolean registered = false;
    private boolean remove = false;
    private List<String> requiredFields = new ArrayList();

    public void addAttribute(String str, String str2) {
        this.attributes.put(str, str2);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:register\">");
        if (!(this.instructions == null || this.remove)) {
            stringBuilder.append("<instructions>").append(this.instructions).append("</instructions>");
        }
        if (this.attributes != null && this.attributes.size() > 0 && !this.remove) {
            for (String str : this.attributes.keySet()) {
                String str2 = (String) this.attributes.get(str);
                stringBuilder.append("<").append(str).append(">");
                stringBuilder.append(str2);
                stringBuilder.append("</").append(str).append(">");
            }
        } else if (this.remove) {
            stringBuilder.append("</remove>");
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public String getField(String str) {
        return (String) this.attributes.get(str);
    }

    public List<String> getFieldNames() {
        return new ArrayList(this.attributes.keySet());
    }

    public String getInstructions() {
        return this.instructions;
    }

    public List<String> getRequiredFields() {
        return this.requiredFields;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void setAttributes(Map<String, String> map) {
        this.attributes = map;
    }

    public void setInstructions(String str) {
        this.instructions = str;
    }

    public void setPassword(String str) {
        this.attributes.put("password", str);
    }

    public void setRegistered(boolean z) {
        this.registered = z;
    }

    public void setRemove(boolean z) {
        this.remove = z;
    }

    public void setUsername(String str) {
        this.attributes.put("username", str);
    }
}
