package org.jivesoftware.smackx.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Field;
import org.jivesoftware.smackx.ReportedData.Row;
import org.xmlpull.v1.XmlPullParser;

class SimpleUserSearch extends IQ {
    private ReportedData data;
    private Form form;

    SimpleUserSearch() {
    }

    private String getItemsToSearch() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.form == null) {
            this.form = Form.getFormFrom(this);
        }
        if (this.form == null) {
            return "";
        }
        Iterator fields = this.form.getFields();
        while (fields.hasNext()) {
            FormField formField = (FormField) fields.next();
            String variable = formField.getVariable();
            String singleValue = getSingleValue(formField);
            if (singleValue.trim().length() > 0) {
                stringBuilder.append("<").append(variable).append(">").append(singleValue).append("</").append(variable).append(">");
            }
        }
        return stringBuilder.toString();
    }

    private static String getSingleValue(FormField formField) {
        Iterator values = formField.getValues();
        return values.hasNext() ? (String) values.next() : "";
    }

    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<query xmlns=\"jabber:iq:search\">");
        stringBuilder.append(getItemsToSearch());
        stringBuilder.append("</query>");
        return stringBuilder.toString();
    }

    public ReportedData getReportedData() {
        return this.data;
    }

    protected void parseItems(XmlPullParser xmlPullParser) throws Exception {
        ReportedData reportedData = new ReportedData();
        reportedData.addColumn(new Column("JID", "jid", FormField.TYPE_TEXT_SINGLE));
        List arrayList = new ArrayList();
        Object obj = null;
        while (obj == null) {
            ArrayList arrayList2;
            Object obj2;
            if (xmlPullParser.getAttributeCount() > 0) {
                String attributeValue = xmlPullParser.getAttributeValue("", "jid");
                List arrayList3 = new ArrayList();
                arrayList3.add(attributeValue);
                arrayList.add(new Field("jid", arrayList3));
            }
            int next = xmlPullParser.next();
            if (next == 2 && xmlPullParser.getName().equals("item")) {
                arrayList2 = new ArrayList();
                obj2 = obj;
            } else if (next == 3 && xmlPullParser.getName().equals("item")) {
                reportedData.addRow(new Row(arrayList));
                r0 = arrayList;
                obj2 = obj;
            } else if (next == 2) {
                String name = xmlPullParser.getName();
                attributeValue = xmlPullParser.nextText();
                arrayList3 = new ArrayList();
                arrayList3.add(attributeValue);
                arrayList.add(new Field(name, arrayList3));
                Iterator columns = reportedData.getColumns();
                obj2 = null;
                while (columns.hasNext()) {
                    obj2 = ((Column) columns.next()).getVariable().equals(name) ? 1 : obj2;
                }
                if (obj2 == null) {
                    reportedData.addColumn(new Column(name, name, FormField.TYPE_TEXT_SINGLE));
                }
                r0 = arrayList;
                obj2 = obj;
            } else if (next == 3 && xmlPullParser.getName().equals("query")) {
                r0 = arrayList;
                int i = 1;
            } else {
                r0 = arrayList;
                obj2 = obj;
            }
            Object obj3 = arrayList2;
            obj = obj2;
        }
        this.data = reportedData;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}
