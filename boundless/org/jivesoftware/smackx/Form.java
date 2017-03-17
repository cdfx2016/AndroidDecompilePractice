package org.jivesoftware.smackx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.packet.DataForm;

public class Form {
    public static final String ELEMENT = "x";
    public static final String NAMESPACE = "jabber:x:data";
    public static final String TYPE_CANCEL = "cancel";
    public static final String TYPE_FORM = "form";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_SUBMIT = "submit";
    private DataForm dataForm;

    public Form(String str) {
        this.dataForm = new DataForm(str);
    }

    public Form(DataForm dataForm) {
        this.dataForm = dataForm;
    }

    public static Form getFormFrom(Packet packet) {
        PacketExtension extension = packet.getExtension("x", NAMESPACE);
        if (extension != null) {
            DataForm dataForm = (DataForm) extension;
            if (dataForm.getReportedData() == null) {
                return new Form(dataForm);
            }
        }
        return null;
    }

    private boolean isFormType() {
        return TYPE_FORM.equals(this.dataForm.getType());
    }

    private boolean isSubmitType() {
        return TYPE_SUBMIT.equals(this.dataForm.getType());
    }

    private void setAnswer(FormField formField, Object obj) {
        if (isSubmitType()) {
            formField.resetValues();
            formField.addValue(obj.toString());
            return;
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public void addField(FormField formField) {
        this.dataForm.addField(formField);
    }

    public Form createAnswerForm() {
        if (isFormType()) {
            Form form = new Form(TYPE_SUBMIT);
            Iterator fields = getFields();
            while (fields.hasNext()) {
                FormField formField = (FormField) fields.next();
                if (formField.getVariable() != null) {
                    FormField formField2 = new FormField(formField.getVariable());
                    formField2.setType(formField.getType());
                    form.addField(formField2);
                    if (FormField.TYPE_HIDDEN.equals(formField.getType())) {
                        List arrayList = new ArrayList();
                        Iterator values = formField.getValues();
                        while (values.hasNext()) {
                            arrayList.add(values.next());
                        }
                        form.setAnswer(formField.getVariable(), arrayList);
                    }
                }
            }
            return form;
        }
        throw new IllegalStateException("Only forms of type \"form\" could be answered");
    }

    public DataForm getDataFormToSend() {
        if (!isSubmitType()) {
            return this.dataForm;
        }
        DataForm dataForm = new DataForm(getType());
        Iterator fields = getFields();
        while (fields.hasNext()) {
            FormField formField = (FormField) fields.next();
            if (formField.getValues().hasNext()) {
                dataForm.addField(formField);
            }
        }
        return dataForm;
    }

    public FormField getField(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("Variable must not be null or blank.");
        }
        Iterator fields = getFields();
        while (fields.hasNext()) {
            FormField formField = (FormField) fields.next();
            if (str.equals(formField.getVariable())) {
                return formField;
            }
        }
        return null;
    }

    public Iterator<FormField> getFields() {
        return this.dataForm.getFields();
    }

    public String getInstructions() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator instructions = this.dataForm.getInstructions();
        while (instructions.hasNext()) {
            stringBuilder.append((String) instructions.next());
            if (instructions.hasNext()) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public String getTitle() {
        return this.dataForm.getTitle();
    }

    public String getType() {
        return this.dataForm.getType();
    }

    public void setAnswer(String str, double d) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_TEXT_PRIVATE.equals(field.getType()) || FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            setAnswer(field, Double.valueOf(d));
        } else {
            throw new IllegalArgumentException("This field is not of type double.");
        }
    }

    public void setAnswer(String str, float f) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_TEXT_PRIVATE.equals(field.getType()) || FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            setAnswer(field, Float.valueOf(f));
        } else {
            throw new IllegalArgumentException("This field is not of type float.");
        }
    }

    public void setAnswer(String str, int i) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_TEXT_PRIVATE.equals(field.getType()) || FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            setAnswer(field, Integer.valueOf(i));
        } else {
            throw new IllegalArgumentException("This field is not of type int.");
        }
    }

    public void setAnswer(String str, long j) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_TEXT_PRIVATE.equals(field.getType()) || FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            setAnswer(field, Long.valueOf(j));
        } else {
            throw new IllegalArgumentException("This field is not of type long.");
        }
    }

    public void setAnswer(String str, String str2) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_TEXT_PRIVATE.equals(field.getType()) || FormField.TYPE_TEXT_SINGLE.equals(field.getType()) || FormField.TYPE_JID_SINGLE.equals(field.getType()) || FormField.TYPE_HIDDEN.equals(field.getType())) {
            setAnswer(field, (Object) str2);
        } else {
            throw new IllegalArgumentException("This field is not of type String.");
        }
    }

    public void setAnswer(String str, List<String> list) {
        if (isSubmitType()) {
            FormField field = getField(str);
            if (field == null) {
                throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
            } else if (FormField.TYPE_JID_MULTI.equals(field.getType()) || FormField.TYPE_LIST_MULTI.equals(field.getType()) || FormField.TYPE_LIST_SINGLE.equals(field.getType()) || FormField.TYPE_TEXT_MULTI.equals(field.getType()) || FormField.TYPE_HIDDEN.equals(field.getType())) {
                field.resetValues();
                field.addValues(list);
                return;
            } else {
                throw new IllegalArgumentException("This field only accept list of values.");
            }
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public void setAnswer(String str, boolean z) {
        FormField field = getField(str);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        } else if (FormField.TYPE_BOOLEAN.equals(field.getType())) {
            setAnswer(field, z ? "1" : "0");
        } else {
            throw new IllegalArgumentException("This field is not of type boolean.");
        }
    }

    public void setDefaultAnswer(String str) {
        if (isSubmitType()) {
            FormField field = getField(str);
            if (field != null) {
                field.resetValues();
                Iterator values = field.getValues();
                while (values.hasNext()) {
                    field.addValue((String) values.next());
                }
                return;
            }
            throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
        }
        throw new IllegalStateException("Cannot set an answer if the form is not of type \"submit\"");
    }

    public void setInstructions(String str) {
        List arrayList = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken());
        }
        this.dataForm.setInstructions(arrayList);
    }

    public void setTitle(String str) {
        this.dataForm.setTitle(str);
    }
}
