package org.jivesoftware.smackx.provider;

import com.fanyu.boundless.config.Preferences;
import java.text.ParseException;
import java.util.Date;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.StreamInitiation;
import org.jivesoftware.smackx.packet.StreamInitiation.File;
import org.xmlpull.v1.XmlPullParser;

public class StreamInitiationProvider implements IQProvider {
    public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "mime-type");
        IQ streamInitiation = new StreamInitiation();
        DataFormProvider dataFormProvider = new DataFormProvider();
        String str = null;
        String str2 = null;
        Object obj = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        boolean z = false;
        DataForm dataForm = null;
        while (obj == null) {
            int next = xmlPullParser.next();
            String name = xmlPullParser.getName();
            String namespace = xmlPullParser.getNamespace();
            if (next == 2) {
                if (name.equals("file")) {
                    str3 = xmlPullParser.getAttributeValue("", Preferences.sbry);
                    namespace = xmlPullParser.getAttributeValue("", "size");
                    name = xmlPullParser.getAttributeValue("", "hash");
                    str4 = name;
                    str = namespace;
                    str2 = str3;
                    str3 = xmlPullParser.getAttributeValue("", "date");
                } else if (name.equals("desc")) {
                    str5 = xmlPullParser.nextText();
                } else if (name.equals("range")) {
                    z = true;
                } else if (name.equals("x") && namespace.equals(Form.NAMESPACE)) {
                    dataForm = (DataForm) dataFormProvider.parseExtension(xmlPullParser);
                }
            } else if (next == 3) {
                if (name.equals("si")) {
                    obj = 1;
                } else if (name.equals("file")) {
                    long j = 0;
                    if (!(str == null || str.trim().length() == 0)) {
                        try {
                            j = Long.parseLong(str);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    Date date = new Date();
                    if (str3 != null) {
                        try {
                            date = StringUtils.parseXEP0082Date(str3);
                        } catch (ParseException e2) {
                        }
                    }
                    File file = new File(str2, j);
                    file.setHash(str4);
                    file.setDate(date);
                    file.setDesc(str5);
                    file.setRanged(z);
                    streamInitiation.setFile(file);
                }
            }
        }
        streamInitiation.setSesssionID(attributeValue);
        streamInitiation.setMimeType(attributeValue2);
        streamInitiation.setFeatureNegotiationForm(dataForm);
        return streamInitiation;
    }
}
