package org.jivesoftware.smack.packet;

public class PrivacyItem {
    private boolean allow;
    private boolean filterIQ = false;
    private boolean filterMessage = false;
    private boolean filterPresence_in = false;
    private boolean filterPresence_out = false;
    private int order;
    private PrivacyRule rule;

    public static class PrivacyRule {
        public static final String SUBSCRIPTION_BOTH = "both";
        public static final String SUBSCRIPTION_FROM = "from";
        public static final String SUBSCRIPTION_NONE = "none";
        public static final String SUBSCRIPTION_TO = "to";
        private Type type;
        private String value;

        protected static PrivacyRule fromString(String str) {
            if (str == null) {
                return null;
            }
            PrivacyRule privacyRule = new PrivacyRule();
            privacyRule.setType(Type.valueOf(str.toLowerCase()));
            return privacyRule;
        }

        private void setSuscriptionValue(String str) {
            if (str == null) {
            }
            String str2 = SUBSCRIPTION_BOTH.equalsIgnoreCase(str) ? SUBSCRIPTION_BOTH : "to".equalsIgnoreCase(str) ? "to" : "from".equalsIgnoreCase(str) ? "from" : SUBSCRIPTION_NONE.equalsIgnoreCase(str) ? SUBSCRIPTION_NONE : null;
            this.value = str2;
        }

        private void setType(Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isSuscription() {
            return getType() == Type.subscription;
        }

        protected void setValue(String str) {
            if (isSuscription()) {
                setSuscriptionValue(str);
            } else {
                this.value = str;
            }
        }
    }

    public enum Type {
        group,
        jid,
        subscription
    }

    public PrivacyItem(String str, boolean z, int i) {
        setRule(PrivacyRule.fromString(str));
        setAllow(z);
        setOrder(i);
    }

    private PrivacyRule getRule() {
        return this.rule;
    }

    private void setAllow(boolean z) {
        this.allow = z;
    }

    private void setRule(PrivacyRule privacyRule) {
        this.rule = privacyRule;
    }

    public int getOrder() {
        return this.order;
    }

    public Type getType() {
        return getRule() == null ? null : getRule().getType();
    }

    public String getValue() {
        return getRule() == null ? null : getRule().getValue();
    }

    public boolean isAllow() {
        return this.allow;
    }

    public boolean isFilterEverything() {
        return (isFilterIQ() || isFilterMessage() || isFilterPresence_in() || isFilterPresence_out()) ? false : true;
    }

    public boolean isFilterIQ() {
        return this.filterIQ;
    }

    public boolean isFilterMessage() {
        return this.filterMessage;
    }

    public boolean isFilterPresence_in() {
        return this.filterPresence_in;
    }

    public boolean isFilterPresence_out() {
        return this.filterPresence_out;
    }

    public void setFilterIQ(boolean z) {
        this.filterIQ = z;
    }

    public void setFilterMessage(boolean z) {
        this.filterMessage = z;
    }

    public void setFilterPresence_in(boolean z) {
        this.filterPresence_in = z;
    }

    public void setFilterPresence_out(boolean z) {
        this.filterPresence_out = z;
    }

    public void setOrder(int i) {
        this.order = i;
    }

    public void setValue(String str) {
        if (getRule() != null || str != null) {
            getRule().setValue(str);
        }
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<item");
        if (isAllow()) {
            stringBuilder.append(" action=\"allow\"");
        } else {
            stringBuilder.append(" action=\"deny\"");
        }
        stringBuilder.append(" order=\"").append(getOrder()).append("\"");
        if (getType() != null) {
            stringBuilder.append(" type=\"").append(getType()).append("\"");
        }
        if (getValue() != null) {
            stringBuilder.append(" value=\"").append(getValue()).append("\"");
        }
        if (isFilterEverything()) {
            stringBuilder.append("/>");
        } else {
            stringBuilder.append(">");
            if (isFilterIQ()) {
                stringBuilder.append("<iq/>");
            }
            if (isFilterMessage()) {
                stringBuilder.append("<message/>");
            }
            if (isFilterPresence_in()) {
                stringBuilder.append("<presence-in/>");
            }
            if (isFilterPresence_out()) {
                stringBuilder.append("<presence-out/>");
            }
            stringBuilder.append("</item>");
        }
        return stringBuilder.toString();
    }
}
