package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.DataForm.Item;

public class ReportedData {
    private List<Column> columns = new ArrayList();
    private List<Row> rows = new ArrayList();
    private String title = "";

    public static class Column {
        private String label;
        private String type;
        private String variable;

        public Column(String str, String str2, String str3) {
            this.label = str;
            this.variable = str2;
            this.type = str3;
        }

        public String getLabel() {
            return this.label;
        }

        public String getType() {
            return this.type;
        }

        public String getVariable() {
            return this.variable;
        }
    }

    public static class Field {
        private List<String> values;
        private String variable;

        public Field(String str, List<String> list) {
            this.variable = str;
            this.values = list;
        }

        public Iterator<String> getValues() {
            return Collections.unmodifiableList(this.values).iterator();
        }

        public String getVariable() {
            return this.variable;
        }
    }

    public static class Row {
        private List<Field> fields = new ArrayList();

        public Row(List<Field> list) {
            this.fields = list;
        }

        private Iterator<Field> getFields() {
            return Collections.unmodifiableList(new ArrayList(this.fields)).iterator();
        }

        public Iterator getValues(String str) {
            Iterator fields = getFields();
            while (fields.hasNext()) {
                Field field = (Field) fields.next();
                if (str.equalsIgnoreCase(field.getVariable())) {
                    return field.getValues();
                }
            }
            return null;
        }
    }

    private ReportedData(DataForm dataForm) {
        Iterator fields = dataForm.getReportedData().getFields();
        while (fields.hasNext()) {
            FormField formField = (FormField) fields.next();
            this.columns.add(new Column(formField.getLabel(), formField.getVariable(), formField.getType()));
        }
        fields = dataForm.getItems();
        while (fields.hasNext()) {
            Item item = (Item) fields.next();
            List arrayList = new ArrayList(this.columns.size());
            Iterator fields2 = item.getFields();
            while (fields2.hasNext()) {
                formField = (FormField) fields2.next();
                List arrayList2 = new ArrayList();
                Iterator values = formField.getValues();
                while (values.hasNext()) {
                    arrayList2.add(values.next());
                }
                arrayList.add(new Field(formField.getVariable(), arrayList2));
            }
            this.rows.add(new Row(arrayList));
        }
        this.title = dataForm.getTitle();
    }

    public static ReportedData getReportedDataFrom(Packet packet) {
        PacketExtension extension = packet.getExtension("x", Form.NAMESPACE);
        if (extension != null) {
            DataForm dataForm = (DataForm) extension;
            if (dataForm.getReportedData() != null) {
                return new ReportedData(dataForm);
            }
        }
        return null;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public Iterator<Column> getColumns() {
        return Collections.unmodifiableList(new ArrayList(this.columns)).iterator();
    }

    public Iterator<Row> getRows() {
        return Collections.unmodifiableList(new ArrayList(this.rows)).iterator();
    }

    public String getTitle() {
        return this.title;
    }
}
