package org.jivesoftware.smack.util.dns;

import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;

public class DNSJavaResolver implements DNSResolver {
    private static DNSJavaResolver instance = new DNSJavaResolver();

    private DNSJavaResolver() {
    }

    public static DNSResolver getInstance() {
        return instance;
    }

    public List<SRVRecord> lookupSRVRecords(String str) {
        List<SRVRecord> arrayList = new ArrayList();
        try {
            Record[] run = new Lookup(str, 33).run();
            if (run == null) {
                return arrayList;
            }
            for (Record record : run) {
                SRVRecord sRVRecord = (SRVRecord) record;
                if (!(sRVRecord == null || sRVRecord.getTarget() == null)) {
                    try {
                        arrayList.add(new SRVRecord(sRVRecord.getTarget().toString(), sRVRecord.getPort(), sRVRecord.getPriority(), sRVRecord.getWeight()));
                    } catch (Exception e) {
                    }
                }
            }
            return arrayList;
        } catch (Exception e2) {
        }
    }
}
