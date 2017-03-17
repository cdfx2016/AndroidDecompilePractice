package org.jivesoftware.smack.util.collections;

import java.util.Map.Entry;

public abstract class AbstractMapEntry<K, V> extends AbstractKeyValue<K, V> implements Entry<K, V> {
    protected AbstractMapEntry(K k, V v) {
        super(k, v);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r5) {
        /*
        r4 = this;
        r0 = 1;
        r1 = 0;
        if (r5 != r4) goto L_0x0005;
    L_0x0004:
        return r0;
    L_0x0005:
        r2 = r5 instanceof java.util.Map.Entry;
        if (r2 != 0) goto L_0x000b;
    L_0x0009:
        r0 = r1;
        goto L_0x0004;
    L_0x000b:
        r5 = (java.util.Map.Entry) r5;
        r2 = r4.getKey();
        if (r2 != 0) goto L_0x0027;
    L_0x0013:
        r2 = r5.getKey();
        if (r2 != 0) goto L_0x0025;
    L_0x0019:
        r2 = r4.getValue();
        if (r2 != 0) goto L_0x0036;
    L_0x001f:
        r2 = r5.getValue();
        if (r2 == 0) goto L_0x0004;
    L_0x0025:
        r0 = r1;
        goto L_0x0004;
    L_0x0027:
        r2 = r4.getKey();
        r3 = r5.getKey();
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0025;
    L_0x0035:
        goto L_0x0019;
    L_0x0036:
        r2 = r4.getValue();
        r3 = r5.getValue();
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0025;
    L_0x0044:
        goto L_0x0004;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.util.collections.AbstractMapEntry.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int i = 0;
        int hashCode = getKey() == null ? 0 : getKey().hashCode();
        if (getValue() != null) {
            i = getValue().hashCode();
        }
        return hashCode ^ i;
    }

    public V setValue(V v) {
        V v2 = this.value;
        this.value = v;
        return v2;
    }
}
