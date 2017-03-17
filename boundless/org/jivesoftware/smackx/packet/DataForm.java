package org.jivesoftware.smackx.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;

public class DataForm implements PacketExtension {
    private final List<FormField> fields = new ArrayList();
    private List<String> instructions = new ArrayList();
    private final List<Item> items = new ArrayList();
    private ReportedData reportedData;
    private String title;
    private String type;

    public static class Item {
        private List<FormField> fields = new ArrayList();

        public Item(List<FormField> list) {
            this.fields = list;
        }

        public Iterator<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields)).iterator();
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<item>");
            Iterator fields = getFields();
            while (fields.hasNext()) {
                stringBuilder.append(((FormField) fields.next()).toXML());
            }
            stringBuilder.append("</item>");
            return stringBuilder.toString();
        }
    }

    public static class ReportedData {
        private List<FormField> fields = new ArrayList();

        public ReportedData(List<FormField> list) {
            this.fields = list;
        }

        public Iterator<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields)).iterator();
        }

        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<reported>");
            Iterator fields = getFields();
            while (fields.hasNext()) {
                stringBuilder.append(((FormField) fields.next()).toXML());
            }
            stringBuilder.append("</reported>");
            return stringBuilder.toString();
        }
    }

    public DataForm(String str) {
        this.type = str;
    }

    public void addField(FormField formField) {
        synchronized (this.fields) {
            this.fields.add(formField);
        }
    }

    public void addInstruction(String str) {
        synchronized (this.instructions) {
            this.instructions.add(str);
        }
    }

    public void addItem(Item item) {
        synchronized (this.items) {
            this.items.add(item);
        }
    }

    public String getElementName() {
        return "x";
    }

    public Iterator<FormField> getFields() {
        Iterator<FormField> it;
        synchronized (this.fields) {
            it = Collections.unmodifiableList(new ArrayList(this.fields)).iterator();
        }
        return it;
    }

    public Iterator<String> getInstructions() {
        Iterator<String> it;
        synchronized (this.instructions) {
            it = Collections.unmodifiableList(new ArrayList(this.instructions)).iterator();
        }
        return it;
    }

    public Iterator<Item> getItems() {
        Iterator<Item> it;
        synchronized (this.items) {
            it = Collections.unmodifiableList(new ArrayList(this.items)).iterator();
        }
        return it;
    }

    public String getNamespace() {
        return Form.NAMESPACE;
    }

    public ReportedData getReportedData() {
        return this.reportedData;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return this.type;
    }

    public boolean hasHiddenFormTypeField() {
        boolean z = false;
        for (FormField formField : this.fields) {
            boolean z2 = (formField.getVariable().equals("FORM_TYPE") && formField.getType() != null && formField.getType().equals(FormField.TYPE_HIDDEN)) ? true : z;
            z = z2;
        }
        return z;
    }

    public void setInstructions(List<String> list) {
        this.instructions = list;
    }

    public void setReportedData(ReportedData reportedData) {
        this.reportedData = reportedData;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\" type=\"" + getType() + "\">");
        if (getTitle() != null) {
            stringBuilder.append("<title>").append(getTitle()).append("</title>");
        }
        Iterator instructions = getInstructions();
        while (instructions.hasNext()) {
            stringBuilder.append("<instructions>").append((String) instructions.next()).append("</instructions>");
        }
        if (getReportedData() != null) {
            stringBuilder.append(getReportedData().toXML());
        }
        instructions = getItems();
        while (instructions.hasNext()) {
            stringBuilder.append(((Item) instructions.next()).toXML());
        }
        instructions = getFields();
        while (instructions.hasNext()) {
            stringBuilder.append(((FormField) instructions.next()).toXML());
        }
        stringBuilder.append("</").append(getElementName()).append(">");
        return stringBuilder.toString();
    }
}
