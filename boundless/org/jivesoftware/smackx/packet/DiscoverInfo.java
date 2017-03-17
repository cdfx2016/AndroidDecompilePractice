package org.jivesoftware.smackx.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;

public class DiscoverInfo extends IQ {
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";
    private final List<Feature> features = new CopyOnWriteArrayList();
    private final List<Identity> identities = new CopyOnWriteArrayList();
    private String node;

    public static class Feature {
        private String variable;

        public Feature(String str) {
            if (str == null) {
                throw new IllegalArgumentException("variable cannot be null");
            }
            this.variable = str;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            return this.variable.equals(((Feature) obj).variable);
        }

        public String getVar() {
            return this.variable;
        }

        public int hashCode() {
            return this.variable.hashCode() * 37;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<feature var=\"").append(StringUtils.escapeForXML(this.variable)).append("\"/>");
            return stringBuilder.toString();
        }
    }

    public static class Identity implements Comparable<Identity> {
        private String category;
        private String lang;
        private String name;
        private String type;

        public Identity(String str, String str2) {
            this.category = str;
            this.name = str2;
        }

        public Identity(String str, String str2, String str3) {
            if (str == null || str3 == null) {
                throw new IllegalArgumentException("category and type cannot be null");
            }
            this.category = str;
            this.name = str2;
            this.type = str3;
        }

        public int compareTo(Identity identity) {
            String str = identity.lang == null ? "" : identity.lang;
            String str2 = this.lang == null ? "" : this.lang;
            String str3 = identity.type == null ? "" : identity.type;
            String str4 = this.type == null ? "" : this.type;
            return this.category.equals(identity.category) ? str4.equals(str3) ? str2.equals(str) ? 0 : str2.compareTo(str) : str4.compareTo(str3) : this.category.compareTo(identity.category);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Identity identity = (Identity) obj;
            if (!this.category.equals(identity.category)) {
                return false;
            }
            if (!(identity.lang == null ? "" : identity.lang).equals(this.lang == null ? "" : this.lang)) {
                return false;
            }
            if (!(identity.type == null ? "" : identity.type).equals(this.type == null ? "" : this.type)) {
                return false;
            }
            return (this.name == null ? "" : identity.name).equals(identity.name == null ? "" : identity.name);
        }

        public String getCategory() {
            return this.category;
        }

        public String getLanguage() {
            return this.lang;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((this.type == null ? 0 : this.type.hashCode()) + (((this.lang == null ? 0 : this.lang.hashCode()) + ((this.category.hashCode() + 37) * 37)) * 37)) * 37;
            if (this.name != null) {
                i = this.name.hashCode();
            }
            return hashCode + i;
        }

        public void setLanguage(String str) {
            this.lang = str;
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setType(String str) {
            this.type = str;
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<identity");
            if (this.lang != null) {
                stringBuilder.append(" xml:lang=\"").append(StringUtils.escapeForXML(this.lang)).append("\"");
            }
            stringBuilder.append(" category=\"").append(StringUtils.escapeForXML(this.category)).append("\"");
            stringBuilder.append(" name=\"").append(StringUtils.escapeForXML(this.name)).append("\"");
            if (this.type != null) {
                stringBuilder.append(" type=\"").append(StringUtils.escapeForXML(this.type)).append("\"");
            }
            stringBuilder.append("/>");
            return stringBuilder.toString();
        }
    }

    public DiscoverInfo(DiscoverInfo discoverInfo) {
        super(discoverInfo);
        setNode(discoverInfo.getNode());
        synchronized (discoverInfo.features) {
            for (Feature addFeature : discoverInfo.features) {
                addFeature(addFeature);
            }
        }
        synchronized (discoverInfo.identities) {
            for (Identity addIdentity : discoverInfo.identities) {
                addIdentity(addIdentity);
            }
        }
    }

    private void addFeature(Feature feature) {
        synchronized (this.features) {
            this.features.add(feature);
        }
    }

    public void addFeature(String str) {
        addFeature(new Feature(str));
    }

    public void addFeatures(Collection<String> collection) {
        if (collection != null) {
            for (String addFeature : collection) {
                addFeature(addFeature);
            }
        }
    }

    public void addIdentities(Collection<Identity> collection) {
        if (collection != null) {
            synchronized (this.identities) {
                this.identities.addAll(collection);
            }
        }
    }

    public void addIdentity(Identity identity) {
        synchronized (this.identities) {
            this.identities.add(identity);
        }
    }

    public boolean containsDuplicateFeatures() {
        List<Feature> linkedList = new LinkedList();
        for (Feature feature : this.features) {
            for (Feature equals : linkedList) {
                if (feature.equals(equals)) {
                    return true;
                }
            }
            linkedList.add(feature);
        }
        return false;
    }

    public boolean containsDuplicateIdentities() {
        List<Identity> linkedList = new LinkedList();
        for (Identity identity : this.identities) {
            for (Identity equals : linkedList) {
                if (identity.equals(equals)) {
                    return true;
                }
            }
            linkedList.add(identity);
        }
        return false;
    }

    public boolean containsFeature(String str) {
        Iterator features = getFeatures();
        while (features.hasNext()) {
            if (str.equals(((Feature) features.next()).getVar())) {
                return true;
            }
        }
        return false;
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"http://jabber.org/protocol/disco#info\"");
        if (getNode() != null) {
            stringBuilder.append(" node=\"");
            stringBuilder.append(StringUtils.escapeForXML(getNode()));
            stringBuilder.append("\"");
        }
        stringBuilder.append(">");
        synchronized (this.identities) {
            for (Identity toXML : this.identities) {
                stringBuilder.append(toXML.toXML());
            }
        }
        synchronized (this.features) {
            for (Feature toXML2 : this.features) {
                stringBuilder.append(toXML2.toXML());
            }
        }
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public Iterator<Feature> getFeatures() {
        Iterator<Feature> it;
        synchronized (this.features) {
            it = Collections.unmodifiableList(this.features).iterator();
        }
        return it;
    }

    public Iterator<Identity> getIdentities() {
        Iterator<Identity> it;
        synchronized (this.identities) {
            it = Collections.unmodifiableList(this.identities).iterator();
        }
        return it;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String str) {
        this.node = str;
    }
}
