package com.alibaba.fastjson.serializer;

public class MapSerializer extends SerializeFilterable implements ObjectSerializer {
    public static MapSerializer instance = new MapSerializer();

    public void write(com.alibaba.fastjson.serializer.JSONSerializer r38, java.lang.Object r39, java.lang.Object r40, java.lang.reflect.Type r41, int r42) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.OutOfMemoryError: Java heap space
	at java.util.Arrays.copyOf(Arrays.java:3181)
	at java.util.ArrayList.grow(ArrayList.java:261)
	at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:235)
	at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:227)
	at java.util.ArrayList.add(ArrayList.java:458)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:463)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:464)
*/
        /*
        r37 = this;
        r0 = r38;
        r0 = r0.out;
        r26 = r0;
        if (r39 != 0) goto L_0x000c;
    L_0x0008:
        r26.writeNull();
    L_0x000b:
        return;
    L_0x000c:
        r23 = r39;
        r23 = (java.util.Map) r23;
        r4 = r38.containsReference(r39);
        if (r4 == 0) goto L_0x001a;
    L_0x0016:
        r38.writeReference(r39);
        goto L_0x000b;
    L_0x001a:
        r0 = r38;
        r0 = r0.context;
        r27 = r0;
        r4 = 0;
        r0 = r38;
        r1 = r27;
        r2 = r39;
        r3 = r40;
        r0.setContext(r1, r2, r3, r4);
        r4 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r0.write(r4);	 Catch:{ all -> 0x0233 }
        r38.incrementIndent();	 Catch:{ all -> 0x0233 }
        r28 = 0;	 Catch:{ all -> 0x0233 }
        r10 = 0;	 Catch:{ all -> 0x0233 }
        r21 = 1;	 Catch:{ all -> 0x0233 }
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0087;	 Catch:{ all -> 0x0233 }
    L_0x0045:
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r4 = r0.config;	 Catch:{ all -> 0x0233 }
        r0 = r4.typeKey;	 Catch:{ all -> 0x0233 }
        r33 = r0;	 Catch:{ all -> 0x0233 }
        r24 = r23.getClass();	 Catch:{ all -> 0x0233 }
        r4 = com.alibaba.fastjson.JSONObject.class;	 Catch:{ all -> 0x0233 }
        r0 = r24;	 Catch:{ all -> 0x0233 }
        if (r0 == r4) goto L_0x0063;	 Catch:{ all -> 0x0233 }
    L_0x0057:
        r4 = java.util.HashMap.class;	 Catch:{ all -> 0x0233 }
        r0 = r24;	 Catch:{ all -> 0x0233 }
        if (r0 == r4) goto L_0x0063;	 Catch:{ all -> 0x0233 }
    L_0x005d:
        r4 = java.util.LinkedHashMap.class;	 Catch:{ all -> 0x0233 }
        r0 = r24;	 Catch:{ all -> 0x0233 }
        if (r0 != r4) goto L_0x023b;	 Catch:{ all -> 0x0233 }
    L_0x0063:
        r0 = r23;	 Catch:{ all -> 0x0233 }
        r1 = r33;	 Catch:{ all -> 0x0233 }
        r4 = r0.containsKey(r1);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x023b;	 Catch:{ all -> 0x0233 }
    L_0x006d:
        r17 = 1;	 Catch:{ all -> 0x0233 }
    L_0x006f:
        if (r17 != 0) goto L_0x0087;	 Catch:{ all -> 0x0233 }
    L_0x0071:
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r1 = r33;	 Catch:{ all -> 0x0233 }
        r0.writeFieldName(r1);	 Catch:{ all -> 0x0233 }
        r4 = r39.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.getName();	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r0.writeString(r4);	 Catch:{ all -> 0x0233 }
        r21 = 0;	 Catch:{ all -> 0x0233 }
    L_0x0087:
        r4 = r23.entrySet();	 Catch:{ all -> 0x0233 }
        r36 = r4.iterator();	 Catch:{ all -> 0x0233 }
        r30 = r10;	 Catch:{ all -> 0x0233 }
    L_0x0091:
        r4 = r36.hasNext();	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x03bf;	 Catch:{ all -> 0x0233 }
    L_0x0097:
        r19 = r36.next();	 Catch:{ all -> 0x0233 }
        r19 = (java.util.Map.Entry) r19;	 Catch:{ all -> 0x0233 }
        r9 = r19.getValue();	 Catch:{ all -> 0x0233 }
        r20 = r19.getKey();	 Catch:{ all -> 0x0233 }
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r0 = r0.propertyPreFilters;	 Catch:{ all -> 0x0233 }
        r29 = r0;	 Catch:{ all -> 0x0233 }
        if (r29 == 0) goto L_0x00cc;	 Catch:{ all -> 0x0233 }
    L_0x00ad:
        r4 = r29.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x00cc;	 Catch:{ all -> 0x0233 }
    L_0x00b3:
        if (r20 == 0) goto L_0x00bb;	 Catch:{ all -> 0x0233 }
    L_0x00b5:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x023f;	 Catch:{ all -> 0x0233 }
    L_0x00bb:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r4 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.applyName(r1, r2, r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x00cc:
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.propertyPreFilters;	 Catch:{ all -> 0x0233 }
        r29 = r0;	 Catch:{ all -> 0x0233 }
        if (r29 == 0) goto L_0x00f3;	 Catch:{ all -> 0x0233 }
    L_0x00d4:
        r4 = r29.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x00f3;	 Catch:{ all -> 0x0233 }
    L_0x00da:
        if (r20 == 0) goto L_0x00e2;	 Catch:{ all -> 0x0233 }
    L_0x00dc:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0261;	 Catch:{ all -> 0x0233 }
    L_0x00e2:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r4 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.applyName(r1, r2, r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x00f3:
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r0 = r0.propertyFilters;	 Catch:{ all -> 0x0233 }
        r31 = r0;	 Catch:{ all -> 0x0233 }
        if (r31 == 0) goto L_0x011a;	 Catch:{ all -> 0x0233 }
    L_0x00fb:
        r4 = r31.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x011a;	 Catch:{ all -> 0x0233 }
    L_0x0101:
        if (r20 == 0) goto L_0x0109;	 Catch:{ all -> 0x0233 }
    L_0x0103:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0283;	 Catch:{ all -> 0x0233 }
    L_0x0109:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r4 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.apply(r1, r2, r4, r9);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x011a:
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.propertyFilters;	 Catch:{ all -> 0x0233 }
        r31 = r0;	 Catch:{ all -> 0x0233 }
        if (r31 == 0) goto L_0x0141;	 Catch:{ all -> 0x0233 }
    L_0x0122:
        r4 = r31.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x0141;	 Catch:{ all -> 0x0233 }
    L_0x0128:
        if (r20 == 0) goto L_0x0130;	 Catch:{ all -> 0x0233 }
    L_0x012a:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x02a5;	 Catch:{ all -> 0x0233 }
    L_0x0130:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r4 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.apply(r1, r2, r4, r9);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x0141:
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r0 = r0.nameFilters;	 Catch:{ all -> 0x0233 }
        r25 = r0;	 Catch:{ all -> 0x0233 }
        if (r25 == 0) goto L_0x0165;	 Catch:{ all -> 0x0233 }
    L_0x0149:
        r4 = r25.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x0165;	 Catch:{ all -> 0x0233 }
    L_0x014f:
        if (r20 == 0) goto L_0x0157;	 Catch:{ all -> 0x0233 }
    L_0x0151:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x02c7;	 Catch:{ all -> 0x0233 }
    L_0x0157:
        r20 = (java.lang.String) r20;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r3 = r20;	 Catch:{ all -> 0x0233 }
        r20 = r0.processKey(r1, r2, r3, r9);	 Catch:{ all -> 0x0233 }
    L_0x0165:
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.nameFilters;	 Catch:{ all -> 0x0233 }
        r25 = r0;	 Catch:{ all -> 0x0233 }
        if (r25 == 0) goto L_0x03e4;	 Catch:{ all -> 0x0233 }
    L_0x016d:
        r4 = r25.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x03e4;	 Catch:{ all -> 0x0233 }
    L_0x0173:
        if (r20 == 0) goto L_0x017b;	 Catch:{ all -> 0x0233 }
    L_0x0175:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x02e7;	 Catch:{ all -> 0x0233 }
    L_0x017b:
        r20 = (java.lang.String) r20;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r3 = r20;	 Catch:{ all -> 0x0233 }
        r20 = r0.processKey(r1, r2, r3, r9);	 Catch:{ all -> 0x0233 }
        r35 = r20;	 Catch:{ all -> 0x0233 }
    L_0x018b:
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r0 = r0.valueFilters;	 Catch:{ all -> 0x0233 }
        r34 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.contextValueFilters;	 Catch:{ all -> 0x0233 }
        r18 = r0;	 Catch:{ all -> 0x0233 }
        if (r34 == 0) goto L_0x019f;	 Catch:{ all -> 0x0233 }
    L_0x0199:
        r4 = r34.size();	 Catch:{ all -> 0x0233 }
        if (r4 > 0) goto L_0x01a7;	 Catch:{ all -> 0x0233 }
    L_0x019f:
        if (r18 == 0) goto L_0x01bf;	 Catch:{ all -> 0x0233 }
    L_0x01a1:
        r4 = r18.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x01bf;	 Catch:{ all -> 0x0233 }
    L_0x01a7:
        if (r35 == 0) goto L_0x01af;	 Catch:{ all -> 0x0233 }
    L_0x01a9:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0309;	 Catch:{ all -> 0x0233 }
    L_0x01af:
        r6 = 0;	 Catch:{ all -> 0x0233 }
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r8 = r0;	 Catch:{ all -> 0x0233 }
        r4 = r37;	 Catch:{ all -> 0x0233 }
        r5 = r38;	 Catch:{ all -> 0x0233 }
        r7 = r39;	 Catch:{ all -> 0x0233 }
        r9 = r4.processValue(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0233 }
    L_0x01bf:
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.valueFilters;	 Catch:{ all -> 0x0233 }
        r34 = r0;	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r0 = r0.contextValueFilters;	 Catch:{ all -> 0x0233 }
        r18 = r0;	 Catch:{ all -> 0x0233 }
        if (r34 == 0) goto L_0x01d3;	 Catch:{ all -> 0x0233 }
    L_0x01cd:
        r4 = r34.size();	 Catch:{ all -> 0x0233 }
        if (r4 > 0) goto L_0x01db;	 Catch:{ all -> 0x0233 }
    L_0x01d3:
        if (r18 == 0) goto L_0x01f4;	 Catch:{ all -> 0x0233 }
    L_0x01d5:
        r4 = r18.size();	 Catch:{ all -> 0x0233 }
        if (r4 <= 0) goto L_0x01f4;	 Catch:{ all -> 0x0233 }
    L_0x01db:
        if (r35 == 0) goto L_0x01e3;	 Catch:{ all -> 0x0233 }
    L_0x01dd:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x032a;	 Catch:{ all -> 0x0233 }
    L_0x01e3:
        r12 = 0;	 Catch:{ all -> 0x0233 }
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r14 = r0;	 Catch:{ all -> 0x0233 }
        r10 = r37;	 Catch:{ all -> 0x0233 }
        r11 = r38;	 Catch:{ all -> 0x0233 }
        r13 = r39;	 Catch:{ all -> 0x0233 }
        r15 = r9;	 Catch:{ all -> 0x0233 }
        r9 = r10.processValue(r11, r12, r13, r14, r15);	 Catch:{ all -> 0x0233 }
    L_0x01f4:
        if (r9 != 0) goto L_0x0200;	 Catch:{ all -> 0x0233 }
    L_0x01f6:
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.WRITE_MAP_NULL_FEATURES;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x0200:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.String;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x034b;	 Catch:{ all -> 0x0233 }
    L_0x0206:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x0233 }
        r22 = r0;	 Catch:{ all -> 0x0233 }
        if (r21 != 0) goto L_0x0215;	 Catch:{ all -> 0x0233 }
    L_0x020e:
        r4 = 44;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r0.write(r4);	 Catch:{ all -> 0x0233 }
    L_0x0215:
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0222;	 Catch:{ all -> 0x0233 }
    L_0x021f:
        r38.println();	 Catch:{ all -> 0x0233 }
    L_0x0222:
        r4 = 1;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r1 = r22;	 Catch:{ all -> 0x0233 }
        r0.writeFieldName(r1, r4);	 Catch:{ all -> 0x0233 }
    L_0x022a:
        r21 = 0;	 Catch:{ all -> 0x0233 }
        if (r9 != 0) goto L_0x038e;	 Catch:{ all -> 0x0233 }
    L_0x022e:
        r26.writeNull();	 Catch:{ all -> 0x0233 }
        goto L_0x0091;
    L_0x0233:
        r4 = move-exception;
        r0 = r27;
        r1 = r38;
        r1.context = r0;
        throw r4;
    L_0x023b:
        r17 = 0;
        goto L_0x006f;
    L_0x023f:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x024f;	 Catch:{ all -> 0x0233 }
    L_0x0249:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x00cc;	 Catch:{ all -> 0x0233 }
    L_0x024f:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.applyName(r1, r2, r8);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x00cc;	 Catch:{ all -> 0x0233 }
    L_0x025f:
        goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x0261:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0271;	 Catch:{ all -> 0x0233 }
    L_0x026b:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x00f3;	 Catch:{ all -> 0x0233 }
    L_0x0271:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.applyName(r1, r2, r8);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x00f3;	 Catch:{ all -> 0x0233 }
    L_0x0281:
        goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x0283:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0293;	 Catch:{ all -> 0x0233 }
    L_0x028d:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x011a;	 Catch:{ all -> 0x0233 }
    L_0x0293:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.apply(r1, r2, r8, r9);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x011a;	 Catch:{ all -> 0x0233 }
    L_0x02a3:
        goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x02a5:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x02b5;	 Catch:{ all -> 0x0233 }
    L_0x02af:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0141;	 Catch:{ all -> 0x0233 }
    L_0x02b5:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r4 = r0.apply(r1, r2, r8, r9);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0141;	 Catch:{ all -> 0x0233 }
    L_0x02c5:
        goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x02c7:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x02d7;	 Catch:{ all -> 0x0233 }
    L_0x02d1:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0165;	 Catch:{ all -> 0x0233 }
    L_0x02d7:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r20 = r0.processKey(r1, r2, r8, r9);	 Catch:{ all -> 0x0233 }
        goto L_0x0165;	 Catch:{ all -> 0x0233 }
    L_0x02e7:
        r4 = r20.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x02f7;	 Catch:{ all -> 0x0233 }
    L_0x02f1:
        r0 = r20;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x03e4;	 Catch:{ all -> 0x0233 }
    L_0x02f7:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r20);	 Catch:{ all -> 0x0233 }
        r0 = r37;	 Catch:{ all -> 0x0233 }
        r1 = r38;	 Catch:{ all -> 0x0233 }
        r2 = r39;	 Catch:{ all -> 0x0233 }
        r20 = r0.processKey(r1, r2, r8, r9);	 Catch:{ all -> 0x0233 }
        r35 = r20;	 Catch:{ all -> 0x0233 }
        goto L_0x018b;	 Catch:{ all -> 0x0233 }
    L_0x0309:
        r4 = r35.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0319;	 Catch:{ all -> 0x0233 }
    L_0x0313:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x01bf;	 Catch:{ all -> 0x0233 }
    L_0x0319:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r35);	 Catch:{ all -> 0x0233 }
        r6 = 0;	 Catch:{ all -> 0x0233 }
        r4 = r37;	 Catch:{ all -> 0x0233 }
        r5 = r38;	 Catch:{ all -> 0x0233 }
        r7 = r39;	 Catch:{ all -> 0x0233 }
        r9 = r4.processValue(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0233 }
        goto L_0x01bf;	 Catch:{ all -> 0x0233 }
    L_0x032a:
        r4 = r35.getClass();	 Catch:{ all -> 0x0233 }
        r4 = r4.isPrimitive();	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x033a;	 Catch:{ all -> 0x0233 }
    L_0x0334:
        r0 = r35;	 Catch:{ all -> 0x0233 }
        r4 = r0 instanceof java.lang.Number;	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x01f4;	 Catch:{ all -> 0x0233 }
    L_0x033a:
        r8 = com.alibaba.fastjson.JSON.toJSONString(r35);	 Catch:{ all -> 0x0233 }
        r6 = 0;	 Catch:{ all -> 0x0233 }
        r4 = r37;	 Catch:{ all -> 0x0233 }
        r5 = r38;	 Catch:{ all -> 0x0233 }
        r7 = r39;	 Catch:{ all -> 0x0233 }
        r9 = r4.processValue(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0233 }
        goto L_0x01f4;	 Catch:{ all -> 0x0233 }
    L_0x034b:
        if (r21 != 0) goto L_0x0354;	 Catch:{ all -> 0x0233 }
    L_0x034d:
        r4 = 44;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r0.write(r4);	 Catch:{ all -> 0x0233 }
    L_0x0354:
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.BrowserCompatible;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0372;	 Catch:{ all -> 0x0233 }
    L_0x035e:
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.WriteNonStringKeyAsString;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 != 0) goto L_0x0372;	 Catch:{ all -> 0x0233 }
    L_0x0368:
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.BrowserSecure;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r4 = r0.isEnabled(r4);	 Catch:{ all -> 0x0233 }
        if (r4 == 0) goto L_0x0386;	 Catch:{ all -> 0x0233 }
    L_0x0372:
        r32 = com.alibaba.fastjson.JSON.toJSONString(r35);	 Catch:{ all -> 0x0233 }
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r1 = r32;	 Catch:{ all -> 0x0233 }
        r0.write(r1);	 Catch:{ all -> 0x0233 }
    L_0x037d:
        r4 = 58;	 Catch:{ all -> 0x0233 }
        r0 = r26;	 Catch:{ all -> 0x0233 }
        r0.write(r4);	 Catch:{ all -> 0x0233 }
        goto L_0x022a;	 Catch:{ all -> 0x0233 }
    L_0x0386:
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r1 = r35;	 Catch:{ all -> 0x0233 }
        r0.write(r1);	 Catch:{ all -> 0x0233 }
        goto L_0x037d;	 Catch:{ all -> 0x0233 }
    L_0x038e:
        r16 = r9.getClass();	 Catch:{ all -> 0x0233 }
        r0 = r16;	 Catch:{ all -> 0x0233 }
        r1 = r28;	 Catch:{ all -> 0x0233 }
        if (r0 != r1) goto L_0x03aa;	 Catch:{ all -> 0x0233 }
    L_0x0398:
        r14 = 0;	 Catch:{ all -> 0x0233 }
        r15 = 0;	 Catch:{ all -> 0x0233 }
        r10 = r30;	 Catch:{ all -> 0x0233 }
        r11 = r38;	 Catch:{ all -> 0x0233 }
        r12 = r9;	 Catch:{ all -> 0x0233 }
        r13 = r35;	 Catch:{ all -> 0x0233 }
        r10.write(r11, r12, r13, r14, r15);	 Catch:{ all -> 0x0233 }
        r10 = r30;	 Catch:{ all -> 0x0233 }
    L_0x03a6:
        r30 = r10;	 Catch:{ all -> 0x0233 }
        goto L_0x0091;	 Catch:{ all -> 0x0233 }
    L_0x03aa:
        r28 = r16;	 Catch:{ all -> 0x0233 }
        r0 = r38;	 Catch:{ all -> 0x0233 }
        r1 = r16;	 Catch:{ all -> 0x0233 }
        r10 = r0.getObjectWriter(r1);	 Catch:{ all -> 0x0233 }
        r14 = 0;	 Catch:{ all -> 0x0233 }
        r15 = 0;	 Catch:{ all -> 0x0233 }
        r11 = r38;	 Catch:{ all -> 0x0233 }
        r12 = r9;	 Catch:{ all -> 0x0233 }
        r13 = r35;	 Catch:{ all -> 0x0233 }
        r10.write(r11, r12, r13, r14, r15);	 Catch:{ all -> 0x0233 }
        goto L_0x03a6;
    L_0x03bf:
        r0 = r27;
        r1 = r38;
        r1.context = r0;
        r38.decrementIdent();
        r4 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
        r0 = r26;
        r4 = r0.isEnabled(r4);
        if (r4 == 0) goto L_0x03db;
    L_0x03d2:
        r4 = r23.size();
        if (r4 <= 0) goto L_0x03db;
    L_0x03d8:
        r38.println();
    L_0x03db:
        r4 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r0 = r26;
        r0.write(r4);
        goto L_0x000b;
    L_0x03e4:
        r35 = r20;
        goto L_0x018b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.MapSerializer.write(com.alibaba.fastjson.serializer.JSONSerializer, java.lang.Object, java.lang.Object, java.lang.reflect.Type, int):void");
    }
}
