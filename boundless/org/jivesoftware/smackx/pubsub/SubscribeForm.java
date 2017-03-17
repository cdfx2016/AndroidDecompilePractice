package org.jivesoftware.smackx.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UnknownFormatConversionException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.packet.DataForm;

public class SubscribeForm extends Form {
    public SubscribeForm(Form form) {
        super(form.getDataFormToSend());
    }

    public SubscribeForm(DataForm dataForm) {
        super(dataForm);
    }

    public SubscribeForm(FormType formType) {
        super(formType.toString());
    }

    private void addField(SubscribeOptionFields subscribeOptionFields, String str) {
        String fieldName = subscribeOptionFields.getFieldName();
        if (getField(fieldName) == null) {
            FormField formField = new FormField(fieldName);
            formField.setType(str);
            addField(formField);
        }
    }

    private String getFieldValue(SubscribeOptionFields subscribeOptionFields) {
        return (String) getField(subscribeOptionFields.getFieldName()).getValues().next();
    }

    private Iterator<String> getFieldValues(SubscribeOptionFields subscribeOptionFields) {
        return getField(subscribeOptionFields.getFieldName()).getValues();
    }

    private static boolean parseBoolean(String str) {
        return "1".equals(str) || "true".equals(str);
    }

    public int getDigestFrequency() {
        return Integer.parseInt(getFieldValue(SubscribeOptionFields.digest_frequency));
    }

    public Date getExpiry() {
        String fieldValue = getFieldValue(SubscribeOptionFields.expire);
        try {
            return StringUtils.parseDate(fieldValue);
        } catch (Throwable e) {
            UnknownFormatConversionException unknownFormatConversionException = new UnknownFormatConversionException(fieldValue);
            unknownFormatConversionException.initCause(e);
            throw unknownFormatConversionException;
        }
    }

    public Iterator<PresenceState> getShowValues() {
        ArrayList arrayList = new ArrayList(5);
        Iterator fieldValues = getFieldValues(SubscribeOptionFields.show_values);
        while (fieldValues.hasNext()) {
            arrayList.add(PresenceState.valueOf((String) fieldValues.next()));
        }
        return arrayList.iterator();
    }

    public boolean isDeliverOn() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.deliver));
    }

    public boolean isDigestOn() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.digest));
    }

    public boolean isIncludeBody() {
        return parseBoolean(getFieldValue(SubscribeOptionFields.include_body));
    }

    public void setDeliverOn(boolean z) {
        addField(SubscribeOptionFields.deliver, FormField.TYPE_BOOLEAN);
        setAnswer(SubscribeOptionFields.deliver.getFieldName(), z);
    }

    public void setDigestFrequency(int i) {
        addField(SubscribeOptionFields.digest_frequency, FormField.TYPE_TEXT_SINGLE);
        setAnswer(SubscribeOptionFields.digest_frequency.getFieldName(), i);
    }

    public void setDigestOn(boolean z) {
        addField(SubscribeOptionFields.deliver, FormField.TYPE_BOOLEAN);
        setAnswer(SubscribeOptionFields.deliver.getFieldName(), z);
    }

    public void setExpiry(Date date) {
        addField(SubscribeOptionFields.expire, FormField.TYPE_TEXT_SINGLE);
        setAnswer(SubscribeOptionFields.expire.getFieldName(), StringUtils.formatXEP0082Date(date));
    }

    public void setIncludeBody(boolean z) {
        addField(SubscribeOptionFields.include_body, FormField.TYPE_BOOLEAN);
        setAnswer(SubscribeOptionFields.include_body.getFieldName(), z);
    }

    public void setShowValues(Collection<PresenceState> collection) {
        List arrayList = new ArrayList(collection.size());
        for (PresenceState presenceState : collection) {
            arrayList.add(presenceState.toString());
        }
        addField(SubscribeOptionFields.show_values, FormField.TYPE_LIST_MULTI);
        setAnswer(SubscribeOptionFields.show_values.getFieldName(), arrayList);
    }
}
