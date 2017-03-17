package org.jivesoftware.smackx.provider;

import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.FormField.Option;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.DataForm.Item;
import org.jivesoftware.smackx.packet.DataForm.ReportedData;
import org.xmlpull.v1.XmlPullParser;

public class DataFormProvider implements PacketExtensionProvider {
    private FormField parseField(XmlPullParser xmlPullParser) throws Exception {
        boolean z = false;
        FormField formField = new FormField(xmlPullParser.getAttributeValue("", "var"));
        formField.setLabel(xmlPullParser.getAttributeValue("", "label"));
        formField.setType(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE));
        while (!z) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("desc")) {
                    formField.setDescription(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("value")) {
                    formField.addValue(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("required")) {
                    formField.setRequired(true);
                } else if (xmlPullParser.getName().equals("option")) {
                    formField.addOption(parseOption(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("field")) {
                z = true;
            }
        }
        return formField;
    }

    private Item parseItem(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        List arrayList = new ArrayList();
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("field")) {
                    arrayList.add(parseField(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("item")) {
                obj = 1;
            }
        }
        return new Item(arrayList);
    }

    private Option parseOption(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        Option option = null;
        String attributeValue = xmlPullParser.getAttributeValue("", "label");
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("value")) {
                    option = new Option(attributeValue, xmlPullParser.nextText());
                }
            } else if (next == 3 && xmlPullParser.getName().equals("option")) {
                obj = 1;
            }
        }
        return option;
    }

    private ReportedData parseReported(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        List arrayList = new ArrayList();
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("field")) {
                    arrayList.add(parseField(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("reported")) {
                obj = 1;
            }
        }
        return new ReportedData(arrayList);
    }

    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        Object obj = null;
        PacketExtension dataForm = new DataForm(xmlPullParser.getAttributeValue("", MessageEncoder.ATTR_TYPE));
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("instructions")) {
                    dataForm.addInstruction(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("title")) {
                    dataForm.setTitle(xmlPullParser.nextText());
                } else if (xmlPullParser.getName().equals("field")) {
                    dataForm.addField(parseField(xmlPullParser));
                } else if (xmlPullParser.getName().equals("item")) {
                    dataForm.addItem(parseItem(xmlPullParser));
                } else if (xmlPullParser.getName().equals("reported")) {
                    dataForm.setReportedData(parseReported(xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals(dataForm.getElementName())) {
                obj = 1;
            }
        }
        return dataForm;
    }
}
