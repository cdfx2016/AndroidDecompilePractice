package org.jivesoftware.smackx.provider;

import com.fanyu.boundless.common.camera.Intents.WifiConnect;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VCardProvider implements IQProvider {
    private static final String PREFERRED_ENCODING = "UTF-8";

    private static class VCardReader {
        private final Document document;
        private final VCard vCard;

        VCardReader(VCard vCard, Document document) {
            this.vCard = vCard;
            this.document = document;
        }

        private void appendText(StringBuilder stringBuilder, Node node) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                String nodeValue = item.getNodeValue();
                if (nodeValue != null) {
                    stringBuilder.append(nodeValue);
                }
                appendText(stringBuilder, item);
            }
        }

        private String getTagContents(String str) {
            NodeList elementsByTagName = this.document.getElementsByTagName(str);
            return (elementsByTagName == null || elementsByTagName.getLength() != 1) ? null : getTextContent(elementsByTagName.item(0));
        }

        private String getTextContent(Node node) {
            StringBuilder stringBuilder = new StringBuilder();
            appendText(stringBuilder, node);
            return stringBuilder.toString();
        }

        private boolean isWorkHome(String str) {
            return "HOME".equals(str) || "WORK".equals(str);
        }

        private void setupAddresses() {
            NodeList elementsByTagName = this.document.getElementsByTagName("ADR");
            if (elementsByTagName != null) {
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    Element element = (Element) elementsByTagName.item(i);
                    String str = null;
                    List arrayList = new ArrayList();
                    List arrayList2 = new ArrayList();
                    NodeList childNodes = element.getChildNodes();
                    int i2 = 0;
                    while (i2 < childNodes.getLength()) {
                        String str2;
                        Node item = childNodes.item(i2);
                        if (item.getNodeType() != (short) 1) {
                            str2 = str;
                        } else {
                            str2 = item.getNodeName();
                            if (!isWorkHome(str2)) {
                                arrayList.add(str2);
                                arrayList2.add(getTextContent(item));
                                str2 = str;
                            }
                        }
                        i2++;
                        str = str2;
                    }
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        if ("HOME".equals(str)) {
                            this.vCard.setAddressFieldHome((String) arrayList.get(i3), (String) arrayList2.get(i3));
                        } else {
                            this.vCard.setAddressFieldWork((String) arrayList.get(i3), (String) arrayList2.get(i3));
                        }
                    }
                }
            }
        }

        private void setupEmails() {
            NodeList elementsByTagName = this.document.getElementsByTagName("USERID");
            if (elementsByTagName != null) {
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    Element element = (Element) elementsByTagName.item(i);
                    if ("WORK".equals(element.getParentNode().getFirstChild().getNodeName())) {
                        this.vCard.setEmailWork(getTextContent(element));
                    } else {
                        this.vCard.setEmailHome(getTextContent(element));
                    }
                }
            }
        }

        private void setupPhones() {
            NodeList elementsByTagName = this.document.getElementsByTagName("TEL");
            if (elementsByTagName != null) {
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    NodeList childNodes = elementsByTagName.item(i).getChildNodes();
                    int i2 = 0;
                    String str = null;
                    String str2 = null;
                    String str3 = null;
                    while (i2 < childNodes.getLength()) {
                        String str4;
                        Node item = childNodes.item(i2);
                        if (item.getNodeType() != (short) 1) {
                            str4 = str3;
                        } else {
                            str4 = item.getNodeName();
                            if ("NUMBER".equals(str4)) {
                                str = getTextContent(item);
                                str4 = str3;
                            } else if (!isWorkHome(str4)) {
                                str2 = str4;
                                str4 = str3;
                            }
                        }
                        i2++;
                        str3 = str4;
                    }
                    if (!(str2 == null || str == null)) {
                        if ("HOME".equals(str3)) {
                            this.vCard.setPhoneHome(str2, str);
                        } else {
                            this.vCard.setPhoneWork(str2, str);
                        }
                    }
                }
            }
        }

        private void setupPhoto() {
            String str = null;
            int i = 0;
            NodeList elementsByTagName = this.document.getElementsByTagName("PHOTO");
            if (elementsByTagName.getLength() == 1) {
                elementsByTagName = elementsByTagName.item(0).getChildNodes();
                int length = elementsByTagName.getLength();
                List<Node> arrayList = new ArrayList(length);
                while (i < length) {
                    arrayList.add(elementsByTagName.item(i));
                    i++;
                }
                String str2 = null;
                for (Node node : arrayList) {
                    String nodeName = node.getNodeName();
                    String textContent = node.getTextContent();
                    if (nodeName.equals("BINVAL")) {
                        String str3 = str;
                        str = textContent;
                        textContent = str3;
                    } else if (nodeName.equals(WifiConnect.TYPE)) {
                        str = str2;
                    } else {
                        textContent = str;
                        str = str2;
                    }
                    str2 = str;
                    str = textContent;
                }
                if (str2 != null && str != null) {
                    this.vCard.setAvatar(str2, str);
                }
            }
        }

        private void setupSimpleFields() {
            NodeList childNodes = this.document.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item instanceof Element) {
                    Element element = (Element) item;
                    String nodeName = element.getNodeName();
                    if (element.getChildNodes().getLength() == 0) {
                        this.vCard.setField(nodeName, "");
                    } else if (element.getChildNodes().getLength() == 1 && (element.getChildNodes().item(0) instanceof Text)) {
                        this.vCard.setField(nodeName, getTextContent(element));
                    }
                }
            }
        }

        public void initializeFields() {
            this.vCard.setFirstName(getTagContents("GIVEN"));
            this.vCard.setLastName(getTagContents("FAMILY"));
            this.vCard.setMiddleName(getTagContents("MIDDLE"));
            setupPhoto();
            setupEmails();
            this.vCard.setOrganization(getTagContents("ORGNAME"));
            this.vCard.setOrganizationUnit(getTagContents("ORGUNIT"));
            setupSimpleFields();
            setupPhones();
            setupAddresses();
        }
    }

    public static VCard createVCardFromXML(String str) throws Exception {
        VCard vCard = new VCard();
        new VCardReader(vCard, DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes("UTF-8")))).initializeFields();
        return vCard;
    }

    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            int eventType = xmlPullParser.getEventType();
            while (true) {
                switch (eventType) {
                    case 2:
                        stringBuilder.append('<').append(xmlPullParser.getName()).append('>');
                        break;
                    case 3:
                        stringBuilder.append("</").append(xmlPullParser.getName()).append('>');
                        break;
                    case 4:
                        stringBuilder.append(StringUtils.escapeForXML(xmlPullParser.getText()));
                        break;
                }
                if (eventType == 3 && "vCard".equals(xmlPullParser.getName())) {
                    return createVCardFromXML(stringBuilder.toString());
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
