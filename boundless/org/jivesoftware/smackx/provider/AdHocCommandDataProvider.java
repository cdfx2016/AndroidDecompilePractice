package org.jivesoftware.smackx.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.commands.AdHocCommand.Action;
import org.jivesoftware.smackx.commands.AdHocCommand.SpecificErrorCondition;
import org.jivesoftware.smackx.commands.AdHocCommand.Status;
import org.jivesoftware.smackx.commands.AdHocCommandNote;
import org.jivesoftware.smackx.commands.AdHocCommandNote.Type;
import org.jivesoftware.smackx.packet.AdHocCommandData;
import org.jivesoftware.smackx.packet.AdHocCommandData.SpecificError;
import org.jivesoftware.smackx.packet.DataForm;
import org.xmlpull.v1.XmlPullParser;

public class AdHocCommandDataProvider implements IQProvider {

    public static class BadActionError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.badAction);
        }
    }

    public static class BadLocaleError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.badLocale);
        }
    }

    public static class BadPayloadError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.badPayload);
        }
    }

    public static class BadSessionIDError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.badSessionid);
        }
    }

    public static class MalformedActionError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.malformedAction);
        }
    }

    public static class SessionExpiredError implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
            return new SpecificError(SpecificErrorCondition.sessionExpired);
        }
    }

    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        Object obj;
        int next;
        String name;
        String attributeValue;
        IQ adHocCommandData = new AdHocCommandData();
        DataFormProvider dataFormProvider = new DataFormProvider();
        adHocCommandData.setSessionID(xmlPullParser.getAttributeValue("", "sessionid"));
        adHocCommandData.setNode(xmlPullParser.getAttributeValue("", "node"));
        String attributeValue2 = xmlPullParser.getAttributeValue("", "status");
        if (Status.executing.toString().equalsIgnoreCase(attributeValue2)) {
            adHocCommandData.setStatus(Status.executing);
        } else if (Status.completed.toString().equalsIgnoreCase(attributeValue2)) {
            adHocCommandData.setStatus(Status.completed);
        } else if (Status.canceled.toString().equalsIgnoreCase(attributeValue2)) {
            adHocCommandData.setStatus(Status.canceled);
        }
        attributeValue2 = xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_ACTION);
        if (attributeValue2 != null) {
            Action valueOf = Action.valueOf(attributeValue2);
            if (valueOf == null || valueOf.equals(Action.unknown)) {
                adHocCommandData.setAction(Action.unknown);
                obj = null;
                while (obj == null) {
                    next = xmlPullParser.next();
                    name = xmlPullParser.getName();
                    String namespace = xmlPullParser.getNamespace();
                    if (next != 2) {
                        if (xmlPullParser.getName().equals("actions")) {
                            attributeValue = xmlPullParser.getAttributeValue("", "execute");
                            if (attributeValue != null) {
                                adHocCommandData.setExecuteAction(Action.valueOf(attributeValue));
                            }
                        } else if (xmlPullParser.getName().equals("next")) {
                            adHocCommandData.addAction(Action.next);
                        } else if (xmlPullParser.getName().equals("complete")) {
                            adHocCommandData.addAction(Action.complete);
                        } else if (xmlPullParser.getName().equals("prev")) {
                            adHocCommandData.addAction(Action.prev);
                        } else if (!name.equals("x") && namespace.equals(Form.NAMESPACE)) {
                            adHocCommandData.setForm((DataForm) dataFormProvider.parseExtension(xmlPullParser));
                        } else if (xmlPullParser.getName().equals("note")) {
                            adHocCommandData.addNote(new AdHocCommandNote(Type.valueOf(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE)), xmlPullParser.nextText()));
                        } else if (xmlPullParser.getName().equals("error")) {
                            adHocCommandData.setError(PacketParserUtils.parseError(xmlPullParser));
                        }
                    } else if (next == 3 && xmlPullParser.getName().equals("command")) {
                        obj = 1;
                    }
                }
                return adHocCommandData;
            }
            adHocCommandData.setAction(valueOf);
        }
        obj = null;
        while (obj == null) {
            next = xmlPullParser.next();
            name = xmlPullParser.getName();
            String namespace2 = xmlPullParser.getNamespace();
            if (next != 2) {
                obj = 1;
            } else if (xmlPullParser.getName().equals("actions")) {
                attributeValue = xmlPullParser.getAttributeValue("", "execute");
                if (attributeValue != null) {
                    adHocCommandData.setExecuteAction(Action.valueOf(attributeValue));
                }
            } else if (xmlPullParser.getName().equals("next")) {
                adHocCommandData.addAction(Action.next);
            } else if (xmlPullParser.getName().equals("complete")) {
                adHocCommandData.addAction(Action.complete);
            } else if (xmlPullParser.getName().equals("prev")) {
                adHocCommandData.addAction(Action.prev);
            } else {
                if (!name.equals("x")) {
                }
                if (xmlPullParser.getName().equals("note")) {
                    adHocCommandData.addNote(new AdHocCommandNote(Type.valueOf(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE)), xmlPullParser.nextText()));
                } else if (xmlPullParser.getName().equals("error")) {
                    adHocCommandData.setError(PacketParserUtils.parseError(xmlPullParser));
                }
            }
        }
        return adHocCommandData;
    }
}
