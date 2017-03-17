package org.jivesoftware.smack.util;

import com.fanyu.boundless.config.Preferences;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Authentication;
import org.jivesoftware.smack.packet.Bind;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.packet.XMPPError.Condition;
import org.jivesoftware.smack.packet.XMPPError.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sasl.SASLMechanism.Failure;
import org.jivesoftware.smackx.FormField;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PacketParserUtils {
    private static final String PROPERTIES_NAMESPACE = "http://www.jivesoftware.com/xmlns/xmpp/properties";

    public static class UnparsedResultIQ extends IQ {
        private final String str;

        public UnparsedResultIQ(String str) {
            this.str = str;
        }

        public String getChildElementXML() {
            return this.str;
        }
    }

    private static Object decode(Class<?> cls, String str) throws Exception {
        return cls.getName().equals("java.lang.String") ? str : cls.getName().equals(FormField.TYPE_BOOLEAN) ? Boolean.valueOf(str) : cls.getName().equals("int") ? Integer.valueOf(str) : cls.getName().equals("long") ? Long.valueOf(str) : cls.getName().equals("float") ? Float.valueOf(str) : cls.getName().equals("double") ? Double.valueOf(str) : cls.getName().equals("java.lang.Class") ? Class.forName(str) : null;
    }

    private static String getLanguageAttribute(XmlPullParser xmlPullParser) {
        int i = 0;
        while (i < xmlPullParser.getAttributeCount()) {
            String attributeName = xmlPullParser.getAttributeName(i);
            if ("xml:lang".equals(attributeName) || ("lang".equals(attributeName) && "xml".equals(xmlPullParser.getAttributePrefix(i)))) {
                return xmlPullParser.getAttributeValue(i);
            }
            i++;
        }
        return null;
    }

    private static Authentication parseAuthentication(XmlPullParser xmlPullParser) throws Exception {
        Authentication authentication = new Authentication();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("username")) {
                    authentication.setUsername(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("password")) {
                    authentication.setPassword(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("digest")) {
                    authentication.setDigest(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("resource")) {
                    authentication.setResource(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                obj = 1;
            }
        }
        return authentication;
    }

    public static Collection<String> parseCompressionMethods(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        Collection arrayList = new ArrayList();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("method")) {
                    arrayList.add(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("compression")) {
                obj = 1;
            }
        }
        return arrayList;
    }

    private static String parseContent(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return parseContentDepth(xmlPullParser, xmlPullParser.getDepth());
    }

    public static String parseContentDepth(XmlPullParser xmlPullParser, int i) throws XmlPullParserException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            if (xmlPullParser.next() == 3 && xmlPullParser.getDepth() == i) {
                return stringBuffer.toString();
            }
            stringBuffer.append(xmlPullParser.getText());
        }
    }

    public static XMPPError parseError(XmlPullParser xmlPullParser) throws Exception {
        String attributeValue;
        Type valueOf;
        Type type;
        String str = null;
        List arrayList = new ArrayList();
        int i = 0;
        String str2 = "-1";
        String str3 = null;
        while (i < xmlPullParser.getAttributeCount()) {
            attributeValue = xmlPullParser.getAttributeName(i).equals("code") ? xmlPullParser.getAttributeValue("", "code") : str2;
            if (xmlPullParser.getAttributeName(i).equals(MessageEncoder.ATTR_TYPE)) {
                str3 = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE);
            }
            i++;
            str2 = attributeValue;
        }
        Object obj = null;
        attributeValue = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals(MimeTypes.BASE_TYPE_TEXT)) {
                    attributeValue = xmlPullParser.nextText();
                } else {
                    String name = xmlPullParser.getName();
                    String namespace = xmlPullParser.getNamespace();
                    if ("urn:ietf:params:xml:ns:xmpp-stanzas".equals(namespace)) {
                        str = name;
                    } else {
                        arrayList.add(parsePacketExtension(name, namespace, xmlPullParser));
                    }
                }
            } else if (next == 3 && xmlPullParser.getName().equals("error")) {
                obj = 1;
            }
        }
        Type type2 = Type.CANCEL;
        if (str3 != null) {
            try {
                valueOf = Type.valueOf(str3.toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                type = type2;
            }
        } else {
            valueOf = type2;
        }
        type = valueOf;
        return new XMPPError(Integer.parseInt(str2), type, str, attributeValue, arrayList);
    }

    public static IQ parseIQ(XmlPullParser xmlPullParser, Connection connection) throws Exception {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "to");
        String attributeValue3 = xmlPullParser.getAttributeValue("", "from");
        IQ.Type fromString = IQ.Type.fromString(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE));
        XMPPError xMPPError = null;
        IQ iq = null;
        Object iQProvider;
        for (Object obj = null; obj == null; obj = iQProvider) {
            int next = xmlPullParser.next();
            if (next == 2) {
                XMPPError parseError;
                IQ iq2;
                String name = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (name.equals("error")) {
                    parseError = parseError(xmlPullParser);
                    iq2 = iq;
                } else if (name.equals("query") && namespace.equals("jabber:iq:auth")) {
                    r11 = xMPPError;
                    iq2 = parseAuthentication(xmlPullParser);
                    parseError = r11;
                } else if (name.equals("query") && namespace.equals("jabber:iq:roster")) {
                    r11 = xMPPError;
                    iq2 = parseRoster(xmlPullParser);
                    parseError = r11;
                } else if (name.equals("query") && namespace.equals("jabber:iq:register")) {
                    r11 = xMPPError;
                    iq2 = parseRegistration(xmlPullParser);
                    parseError = r11;
                } else if (name.equals("bind") && namespace.equals("urn:ietf:params:xml:ns:xmpp-bind")) {
                    r11 = xMPPError;
                    iq2 = parseResourceBinding(xmlPullParser);
                    parseError = r11;
                } else {
                    iQProvider = ProviderManager.getInstance().getIQProvider(name, namespace);
                    if (iQProvider != null) {
                        if (iQProvider instanceof IQProvider) {
                            r11 = xMPPError;
                            iq2 = ((IQProvider) iQProvider).parseIQ(xmlPullParser);
                            parseError = r11;
                        } else if (iQProvider instanceof Class) {
                            r11 = xMPPError;
                            iq2 = (IQ) parseWithIntrospection(name, (Class) iQProvider, xmlPullParser);
                            parseError = r11;
                        }
                    } else if (IQ.Type.RESULT == fromString) {
                        r11 = xMPPError;
                        iq2 = new UnparsedResultIQ(parseContent(xmlPullParser));
                        parseError = r11;
                    }
                    parseError = xMPPError;
                    iq2 = iq;
                }
                iq = iq2;
                xMPPError = parseError;
                iQProvider = obj;
            } else {
                iQProvider = (next == 3 && xmlPullParser.getName().equals("iq")) ? 1 : obj;
            }
        }
        if (iq == null) {
            if (IQ.Type.GET == fromString || IQ.Type.SET == fromString) {
                Packet anonymousClass1 = new IQ() {
                    public String getChildElementXML() {
                        return null;
                    }
                };
                anonymousClass1.setPacketID(attributeValue);
                anonymousClass1.setTo(attributeValue3);
                anonymousClass1.setFrom(attributeValue2);
                anonymousClass1.setType(IQ.Type.ERROR);
                anonymousClass1.setError(new XMPPError(Condition.feature_not_implemented));
                connection.sendPacket(anonymousClass1);
                return null;
            }
            iq = new IQ() {
                public String getChildElementXML() {
                    return null;
                }
            };
        }
        iq.setPacketID(attributeValue);
        iq.setTo(attributeValue2);
        iq.setFrom(attributeValue3);
        iq.setType(fromString);
        iq.setError(xMPPError);
        return iq;
    }

    public static Collection<String> parseMechanisms(XmlPullParser xmlPullParser) throws Exception {
        Collection arrayList = new ArrayList();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("mechanism")) {
                    arrayList.add(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("mechanisms")) {
                obj = 1;
            }
        }
        return arrayList;
    }

    public static Packet parseMessage(XmlPullParser xmlPullParser) throws Exception {
        Map map = null;
        Packet message = new Message();
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        if (attributeValue == null) {
            attributeValue = Packet.ID_NOT_AVAILABLE;
        }
        message.setPacketID(attributeValue);
        message.setTo(xmlPullParser.getAttributeValue("", "to"));
        message.setFrom(xmlPullParser.getAttributeValue("", "from"));
        message.setType(Message.Type.fromString(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE)));
        attributeValue = getLanguageAttribute(xmlPullParser);
        if (attributeValue == null || "".equals(attributeValue.trim())) {
            attributeValue = Packet.getDefaultLanguage();
        } else {
            message.setLanguage(attributeValue);
        }
        Object obj = null;
        String str = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (name.equals("subject")) {
                    name = getLanguageAttribute(xmlPullParser);
                    if (name == null) {
                        name = attributeValue;
                    }
                    namespace = parseContent(xmlPullParser);
                    if (message.getSubject(name) == null) {
                        message.addSubject(name, namespace);
                    }
                } else if (name.equals(TtmlNode.TAG_BODY)) {
                    name = getLanguageAttribute(xmlPullParser);
                    if (name == null) {
                        name = attributeValue;
                    }
                    namespace = parseContent(xmlPullParser);
                    if (message.getBody(name) == null) {
                        message.addBody(name, namespace);
                    }
                } else if (name.equals("thread")) {
                    if (str == null) {
                        str = xmlPullParser.nextText();
                    }
                } else if (name.equals("error")) {
                    message.setError(parseError(xmlPullParser));
                } else if (name.equals("properties") && namespace.equals(PROPERTIES_NAMESPACE)) {
                    map = parseProperties(xmlPullParser);
                } else {
                    message.addExtension(parsePacketExtension(name, namespace, xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("message")) {
                obj = 1;
            }
        }
        message.setThread(str);
        if (map != null) {
            for (String attributeValue2 : map.keySet()) {
                message.setProperty(attributeValue2, map.get(attributeValue2));
            }
        }
        return message;
    }

    public static PacketExtension parsePacketExtension(String str, String str2, XmlPullParser xmlPullParser) throws Exception {
        Object extensionProvider = ProviderManager.getInstance().getExtensionProvider(str, str2);
        if (extensionProvider != null) {
            if (extensionProvider instanceof PacketExtensionProvider) {
                return ((PacketExtensionProvider) extensionProvider).parseExtension(xmlPullParser);
            }
            if (extensionProvider instanceof Class) {
                return (PacketExtension) parseWithIntrospection(str, (Class) extensionProvider, xmlPullParser);
            }
        }
        DefaultPacketExtension defaultPacketExtension = new DefaultPacketExtension(str, str2);
        extensionProvider = null;
        while (extensionProvider == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (xmlPullParser.isEmptyElementTag()) {
                    defaultPacketExtension.setValue(name, "");
                } else if (xmlPullParser.next() == 4) {
                    defaultPacketExtension.setValue(name, xmlPullParser.getText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals(str)) {
                extensionProvider = 1;
            }
        }
        return defaultPacketExtension;
    }

    public static Presence parsePresence(XmlPullParser xmlPullParser) throws Exception {
        Presence.Type type = Presence.Type.available;
        String attributeValue = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE);
        if (!(attributeValue == null || attributeValue.equals(""))) {
            try {
                type = Presence.Type.valueOf(attributeValue);
            } catch (IllegalArgumentException e) {
                System.err.println("Found invalid presence type " + attributeValue);
            }
        }
        Presence presence = new Presence(type);
        presence.setTo(xmlPullParser.getAttributeValue("", "to"));
        presence.setFrom(xmlPullParser.getAttributeValue("", "from"));
        attributeValue = xmlPullParser.getAttributeValue("", "id");
        presence.setPacketID(attributeValue == null ? Packet.ID_NOT_AVAILABLE : attributeValue);
        String languageAttribute = getLanguageAttribute(xmlPullParser);
        if (!(languageAttribute == null || "".equals(languageAttribute.trim()))) {
            presence.setLanguage(languageAttribute);
        }
        if (attributeValue == null) {
            attributeValue = Packet.ID_NOT_AVAILABLE;
        }
        presence.setPacketID(attributeValue);
        int next;
        for (int i = 0; i == 0; i = next) {
            next = xmlPullParser.next();
            if (next == 2) {
                languageAttribute = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (languageAttribute.equals("status")) {
                    presence.setStatus(xmlPullParser.nextText());
                } else if (languageAttribute.equals("priority")) {
                    try {
                        presence.setPriority(Integer.parseInt(xmlPullParser.nextText()));
                    } catch (NumberFormatException e2) {
                    } catch (IllegalArgumentException e3) {
                        presence.setPriority(0);
                    }
                } else if (languageAttribute.equals("show")) {
                    languageAttribute = xmlPullParser.nextText();
                    try {
                        presence.setMode(Mode.valueOf(languageAttribute));
                    } catch (IllegalArgumentException e4) {
                        System.err.println("Found invalid presence mode " + languageAttribute);
                    }
                } else if (languageAttribute.equals("error")) {
                    presence.setError(parseError(xmlPullParser));
                } else if (languageAttribute.equals("properties") && namespace.equals(PROPERTIES_NAMESPACE)) {
                    Map parseProperties = parseProperties(xmlPullParser);
                    for (String languageAttribute2 : parseProperties.keySet()) {
                        presence.setProperty(languageAttribute2, parseProperties.get(languageAttribute2));
                    }
                } else {
                    try {
                        presence.addExtension(parsePacketExtension(languageAttribute2, namespace, xmlPullParser));
                    } catch (Exception e5) {
                        System.err.println("Failed to parse extension packet in Presence packet.");
                    }
                }
                next = i;
            } else {
                next = (next == 3 && xmlPullParser.getName().equals("presence")) ? 1 : i;
            }
        }
        return presence;
    }

    public static Map<String, Object> parseProperties(XmlPullParser xmlPullParser) throws Exception {
        Map<String, Object> hashMap = new HashMap();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 2 && xmlPullParser.getName().equals("property")) {
                Object obj = null;
                Object obj2 = null;
                Object obj3 = null;
                Object obj4 = null;
                String str = null;
                while (obj4 == null) {
                    int next2 = xmlPullParser.next();
                    if (next2 == 2) {
                        String name = xmlPullParser.getName();
                        if (name.equals(Preferences.sbry)) {
                            obj3 = xmlPullParser.nextText();
                        } else if (name.equals("value")) {
                            obj2 = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE);
                            str = xmlPullParser.nextText();
                        }
                    } else if (next2 == 3 && xmlPullParser.getName().equals("property")) {
                        if ("integer".equals(obj2)) {
                            obj4 = Integer.valueOf(str);
                        } else if ("long".equals(obj2)) {
                            Long valueOf = Long.valueOf(str);
                        } else if ("float".equals(obj2)) {
                            Float valueOf2 = Float.valueOf(str);
                        } else if ("double".equals(obj2)) {
                            Double valueOf3 = Double.valueOf(str);
                        } else if (FormField.TYPE_BOOLEAN.equals(obj2)) {
                            Boolean valueOf4 = Boolean.valueOf(str);
                        } else if ("string".equals(obj2)) {
                            String str2 = str;
                        } else {
                            if ("java-object".equals(obj2)) {
                                try {
                                    obj4 = new ObjectInputStream(new ByteArrayInputStream(StringUtils.decodeBase64(str))).readObject();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            obj4 = obj;
                        }
                        if (!(obj3 == null || obj4 == null)) {
                            hashMap.put(obj3, obj4);
                        }
                        Object obj5 = obj4;
                        obj4 = 1;
                        obj = obj5;
                    }
                }
            } else if (next == 3 && xmlPullParser.getName().equals("properties")) {
                return hashMap;
            }
        }
    }

    private static Registration parseRegistration(XmlPullParser xmlPullParser) throws Exception {
        Registration registration = new Registration();
        Map map = null;
        Object obj = null;
        while (obj == null) {
            Map map2;
            Object obj2;
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getNamespace().equals("jabber:iq:register")) {
                    String name = xmlPullParser.getName();
                    String str = "";
                    if (map == null) {
                        map = new HashMap();
                    }
                    if (xmlPullParser.next() == 4) {
                        str = xmlPullParser.getText();
                    }
                    if (name.equals("instructions")) {
                        registration.setInstructions(str);
                    } else {
                        map.put(name, str);
                    }
                    map2 = map;
                    obj2 = obj;
                } else {
                    registration.addExtension(parsePacketExtension(xmlPullParser.getName(), xmlPullParser.getNamespace(), xmlPullParser));
                    map2 = map;
                    obj2 = obj;
                }
            } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                map2 = map;
                int i = 1;
            } else {
                map2 = map;
                obj2 = obj;
            }
            obj = obj2;
            map = map2;
        }
        registration.setAttributes(map);
        return registration;
    }

    private static Bind parseResourceBinding(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        Bind bind = new Bind();
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("resource")) {
                    bind.setResource(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("jid")) {
                    bind.setJid(xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("bind")) {
                obj = 1;
            }
        }
        return bind;
    }

    private static RosterPacket parseRoster(XmlPullParser xmlPullParser) throws Exception {
        RosterPacket rosterPacket = new RosterPacket();
        Object obj = null;
        Item item = null;
        while (obj == null) {
            Object obj2;
            if (xmlPullParser.getEventType() == 2 && xmlPullParser.getName().equals("query")) {
                rosterPacket.setVersion(xmlPullParser.getAttributeValue(null, "ver"));
            }
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    Item item2 = new Item(xmlPullParser.getAttributeValue("", "jid"), xmlPullParser.getAttributeValue("", Preferences.sbry));
                    item2.setItemStatus(ItemStatus.fromString(xmlPullParser.getAttributeValue("", "ask")));
                    String attributeValue = xmlPullParser.getAttributeValue("", "subscription");
                    if (attributeValue == null) {
                        attributeValue = PrivacyRule.SUBSCRIPTION_NONE;
                    }
                    item2.setItemType(ItemType.valueOf(attributeValue));
                    item = item2;
                }
                if (xmlPullParser.getName().equals("group") && item != null) {
                    String nextText = xmlPullParser.nextText();
                    if (nextText != null && nextText.trim().length() > 0) {
                        item.addGroupName(nextText);
                    }
                    obj2 = obj;
                }
                obj2 = obj;
            } else {
                if (next == 3) {
                    if (xmlPullParser.getName().equals("item")) {
                        rosterPacket.addRosterItem(item);
                    }
                    if (xmlPullParser.getName().equals("query")) {
                        obj2 = 1;
                    }
                }
                obj2 = obj;
            }
            obj = obj2;
        }
        return rosterPacket;
    }

    public static Failure parseSASLFailure(XmlPullParser xmlPullParser) throws Exception {
        String str = null;
        Object obj = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (!xmlPullParser.getName().equals("failure")) {
                    str = xmlPullParser.getName();
                }
            } else if (next == 3 && xmlPullParser.getName().equals("failure")) {
                obj = 1;
            }
        }
        return new Failure(str);
    }

    public static StreamError parseStreamError(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        String str = null;
        int depth = xmlPullParser.getDepth();
        String str2 = null;
        Object obj;
        for (Object obj2 = null; obj2 == null; obj2 = obj) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (StreamError.NAMESPACE.equals(xmlPullParser.getNamespace())) {
                    String name = xmlPullParser.getName();
                    if (!name.equals(MimeTypes.BASE_TYPE_TEXT) || xmlPullParser.isEmptyElementTag()) {
                        str2 = name;
                    } else {
                        xmlPullParser.next();
                        str = xmlPullParser.getText();
                    }
                }
                obj = obj2;
            } else {
                obj = (next == 3 && depth == xmlPullParser.getDepth()) ? 1 : obj2;
            }
        }
        return new StreamError(str2, str);
    }

    public static Object parseWithIntrospection(String str, Class<?> cls, XmlPullParser xmlPullParser) throws Exception {
        Object newInstance = cls.newInstance();
        int i = 0;
        while (i == 0) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                String nextText = xmlPullParser.nextText();
                Object decode = decode(newInstance.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1), new Class[0]).getReturnType(), nextText);
                newInstance.getClass().getMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), new Class[]{r6}).invoke(newInstance, new Object[]{decode});
            } else if (next == 3 && xmlPullParser.getName().equals(str)) {
                i = 1;
            }
        }
        return newInstance;
    }
}
