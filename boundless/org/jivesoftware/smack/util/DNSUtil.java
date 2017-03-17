package org.jivesoftware.smack.util;

import com.fanyu.boundless.util.FileUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.jivesoftware.smack.util.dns.DNSResolver;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smack.util.dns.SRVRecord;

public class DNSUtil {
    private static Map<String, List<HostAddress>> cache = new Cache(100, 600000);
    private static DNSResolver dnsResolver = null;

    private static int bisect(int[] iArr, double d) {
        int i = 0;
        int length = iArr.length;
        int i2 = 0;
        while (i < length && d >= ((double) iArr[i])) {
            i2++;
            i++;
        }
        return i2;
    }

    public static DNSResolver getDNSResolver() {
        return dnsResolver;
    }

    private static List<HostAddress> resolveDomain(String str, char c) {
        String str2 = c + str;
        if (cache.containsKey(str2)) {
            List<HostAddress> list = (List) cache.get(str2);
            if (list != null) {
                return list;
            }
        }
        if (dnsResolver == null) {
            throw new IllegalStateException("No DNS resolver active.");
        }
        List<HostAddress> arrayList = new ArrayList();
        r0 = c == 's' ? "_xmpp-server._tcp." + str : c == 'c' ? "_xmpp-client._tcp." + str : str;
        Collection sortSRVRecords = sortSRVRecords(dnsResolver.lookupSRVRecords(r0));
        if (sortSRVRecords != null) {
            arrayList.addAll(sortSRVRecords);
        }
        arrayList.add(new HostAddress(str));
        cache.put(str2, arrayList);
        return arrayList;
    }

    public static List<HostAddress> resolveXMPPDomain(String str) {
        return resolveDomain(str, 'c');
    }

    public static List<HostAddress> resolveXMPPServerDomain(String str) {
        return resolveDomain(str, 's');
    }

    public static void setDNSResolver(DNSResolver dNSResolver) {
        dnsResolver = dNSResolver;
    }

    protected static List<HostAddress> sortSRVRecords(List<SRVRecord> list) {
        if (list.size() == 1 && ((SRVRecord) list.get(0)).getFQDN().equals(FileUtil.FILE_EXTENSION_SEPARATOR)) {
            return null;
        }
        Collections.sort(list);
        SortedMap treeMap = new TreeMap();
        for (SRVRecord sRVRecord : list) {
            Integer valueOf = Integer.valueOf(sRVRecord.getPriority());
            List list2 = (List) treeMap.get(valueOf);
            if (list2 == null) {
                list2 = new LinkedList();
                treeMap.put(valueOf, list2);
            }
            list2.add(sRVRecord);
        }
        List<HostAddress> arrayList = new ArrayList(list.size());
        for (Integer num : treeMap.keySet()) {
            List<SRVRecord> list3 = (List) treeMap.get(num);
            while (true) {
                int size = list3.size();
                if (size > 0) {
                    int[] iArr = new int[list3.size()];
                    int i = 0;
                    int i2 = 0;
                    int i3 = 1;
                    for (SRVRecord weight : list3) {
                        i3 = weight.getWeight() > 0 ? 0 : i3;
                    }
                    for (SRVRecord weight2 : list3) {
                        i += weight2.getWeight() + i3;
                        iArr[i2] = i;
                        i2++;
                    }
                    arrayList.add((SRVRecord) list3.remove(i == 0 ? (int) (Math.random() * ((double) size)) : bisect(iArr, Math.random() * ((double) i))));
                }
            }
        }
        return arrayList;
    }
}
