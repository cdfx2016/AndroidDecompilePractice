package org.jivesoftware.smackx.search;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.Nick;
import org.xmlpull.v1.XmlPullParser;

public class UserSearch extends IQ {

    public static class Provider implements IQProvider {
        public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
            IQ iq = null;
            IQ simpleUserSearch = new SimpleUserSearch();
            Object obj = null;
            while (obj == null) {
                int next = xmlPullParser.next();
                if (next == 2 && xmlPullParser.getName().equals("instructions")) {
                    UserSearch.buildDataForm(simpleUserSearch, xmlPullParser.nextText(), xmlPullParser);
                    return simpleUserSearch;
                } else if (next == 2 && xmlPullParser.getName().equals("item")) {
                    simpleUserSearch.parseItems(xmlPullParser);
                    return simpleUserSearch;
                } else if (next == 2 && xmlPullParser.getNamespace().equals(Form.NAMESPACE)) {
                    iq = new UserSearch();
                    iq.addExtension(PacketParserUtils.parsePacketExtension(xmlPullParser.getName(), xmlPullParser.getNamespace(), xmlPullParser));
                } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                    obj = 1;
                }
            }
            return iq == null ? simpleUserSearch : iq;
        }
    }

    private static void buildDataForm(SimpleUserSearch simpleUserSearch, String str, XmlPullParser xmlPullParser) throws Exception {
        PacketExtension dataForm = new DataForm(Form.TYPE_FORM);
        Object obj = null;
        dataForm.setTitle("User Search");
        dataForm.addInstruction(str);
        while (obj == null) {
            int next = xmlPullParser.next();
            if (next == 2 && !xmlPullParser.getNamespace().equals(Form.NAMESPACE)) {
                String name = xmlPullParser.getName();
                FormField formField = new FormField(name);
                if (name.equals("first")) {
                    formField.setLabel("First Name");
                } else if (name.equals("last")) {
                    formField.setLabel("Last Name");
                } else if (name.equals("email")) {
                    formField.setLabel("Email Address");
                } else if (name.equals(Nick.ELEMENT_NAME)) {
                    formField.setLabel("Nickname");
                }
                formField.setType(FormField.TYPE_TEXT_SINGLE);
                dataForm.addField(formField);
            } else if (next == 3) {
                if (xmlPullParser.getName().equals("query")) {
                    obj = 1;
                }
            } else if (next == 2 && xmlPullParser.getNamespace().equals(Form.NAMESPACE)) {
                simpleUserSearch.addExtension(PacketParserUtils.parsePacketExtension(xmlPullParser.getName(), xmlPullParser.getNamespace(), xmlPullParser));
                obj = 1;
            }
        }
        if (simpleUserSearch.getExtension("x", Form.NAMESPACE) == null) {
            simpleUserSearch.addExtension(dataForm);
        }
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:search\">");
        stringBuilder.append(getExtensionsXML());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public Form getSearchForm(Connection connection, String str) throws XMPPException {
        Packet userSearch = new UserSearch();
        userSearch.setType(Type.GET);
        userSearch.setTo(str);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(userSearch.getPacketID()));
        connection.sendPacket(userSearch);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server on status set.");
        } else if (iq.getError() == null) {
            return Form.getFormFrom(iq);
        } else {
            throw new XMPPException(iq.getError());
        }
    }

    public ReportedData sendSearchForm(Connection connection, Form form, String str) throws XMPPException {
        Packet userSearch = new UserSearch();
        userSearch.setType(Type.SET);
        userSearch.setTo(str);
        userSearch.addExtension(form.getDataFormToSend());
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(userSearch.getPacketID()));
        connection.sendPacket(userSearch);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq != null) {
            return iq.getError() != null ? sendSimpleSearchForm(connection, form, str) : ReportedData.getReportedDataFrom(iq);
        } else {
            throw new XMPPException("No response from server on status set.");
        }
    }

    public ReportedData sendSimpleSearchForm(Connection connection, Form form, String str) throws XMPPException {
        Packet simpleUserSearch = new SimpleUserSearch();
        simpleUserSearch.setForm(form);
        simpleUserSearch.setType(Type.SET);
        simpleUserSearch.setTo(str);
        PacketCollector createPacketCollector = connection.createPacketCollector(new PacketIDFilter(simpleUserSearch.getPacketID()));
        connection.sendPacket(simpleUserSearch);
        IQ iq = (IQ) createPacketCollector.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
        createPacketCollector.cancel();
        if (iq == null) {
            throw new XMPPException("No response from server on status set.");
        } else if (iq.getError() == null) {
            return iq instanceof SimpleUserSearch ? ((SimpleUserSearch) iq).getReportedData() : null;
        } else {
            throw new XMPPException(iq.getError());
        }
    }
}
