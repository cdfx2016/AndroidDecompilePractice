package com.alibaba.fastjson.parser.deserializer;

public class StackTraceElementDeserializer implements ObjectDeserializer {
    public static final StackTraceElementDeserializer instance = new StackTraceElementDeserializer();

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r13, java.lang.reflect.Type r14, java.lang.Object r15) {
        /*
        r12 = this;
        r4 = r13.lexer;
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x000f;
    L_0x000a:
        r4.nextToken();
        r9 = 0;
    L_0x000e:
        return r9;
    L_0x000f:
        r9 = r4.token();
        r10 = 12;
        if (r9 == r10) goto L_0x0041;
    L_0x0017:
        r9 = r4.token();
        r10 = 16;
        if (r9 == r10) goto L_0x0041;
    L_0x001f:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "syntax error: ";
        r10 = r10.append(r11);
        r11 = r4.token();
        r11 = com.alibaba.fastjson.parser.JSONToken.name(r11);
        r10 = r10.append(r11);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
    L_0x0041:
        r0 = 0;
        r6 = 0;
        r2 = 0;
        r5 = 0;
        r7 = 0;
        r8 = 0;
    L_0x0047:
        r9 = r13.getSymbolTable();
        r3 = r4.scanSymbol(r9);
        if (r3 != 0) goto L_0x0074;
    L_0x0051:
        r9 = r4.token();
        r10 = 13;
        if (r9 != r10) goto L_0x0064;
    L_0x0059:
        r9 = 16;
        r4.nextToken(r9);
    L_0x005e:
        r9 = new java.lang.StackTraceElement;
        r9.<init>(r0, r6, r2, r5);
        goto L_0x000e;
    L_0x0064:
        r9 = r4.token();
        r10 = 16;
        if (r9 != r10) goto L_0x0074;
    L_0x006c:
        r9 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas;
        r9 = r4.isEnabled(r9);
        if (r9 != 0) goto L_0x0047;
    L_0x0074:
        r9 = 4;
        r4.nextTokenWithColon(r9);
        r9 = "className";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x00ac;
    L_0x0080:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x0097;
    L_0x0088:
        r0 = 0;
    L_0x0089:
        r9 = r4.token();
        r10 = 13;
        if (r9 != r10) goto L_0x0047;
    L_0x0091:
        r9 = 16;
        r4.nextToken(r9);
        goto L_0x005e;
    L_0x0097:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x00a3;
    L_0x009e:
        r0 = r4.stringVal();
        goto L_0x0089;
    L_0x00a3:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x00ac:
        r9 = "methodName";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x00d3;
    L_0x00b4:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x00be;
    L_0x00bc:
        r6 = 0;
        goto L_0x0089;
    L_0x00be:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x00ca;
    L_0x00c5:
        r6 = r4.stringVal();
        goto L_0x0089;
    L_0x00ca:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x00d3:
        r9 = "fileName";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x00fa;
    L_0x00db:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x00e5;
    L_0x00e3:
        r2 = 0;
        goto L_0x0089;
    L_0x00e5:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x00f1;
    L_0x00ec:
        r2 = r4.stringVal();
        goto L_0x0089;
    L_0x00f1:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x00fa:
        r9 = "lineNumber";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x0123;
    L_0x0102:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x010d;
    L_0x010a:
        r5 = 0;
        goto L_0x0089;
    L_0x010d:
        r9 = r4.token();
        r10 = 2;
        if (r9 != r10) goto L_0x011a;
    L_0x0114:
        r5 = r4.intValue();
        goto L_0x0089;
    L_0x011a:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x0123:
        r9 = "nativeMethod";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x015f;
    L_0x012b:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x013a;
    L_0x0133:
        r9 = 16;
        r4.nextToken(r9);
        goto L_0x0089;
    L_0x013a:
        r9 = r4.token();
        r10 = 6;
        if (r9 != r10) goto L_0x0148;
    L_0x0141:
        r9 = 16;
        r4.nextToken(r9);
        goto L_0x0089;
    L_0x0148:
        r9 = r4.token();
        r10 = 7;
        if (r9 != r10) goto L_0x0156;
    L_0x014f:
        r9 = 16;
        r4.nextToken(r9);
        goto L_0x0089;
    L_0x0156:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x015f:
        r9 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY;
        if (r3 != r9) goto L_0x01a1;
    L_0x0163:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x0190;
    L_0x016a:
        r1 = r4.stringVal();
        r9 = "java.lang.StackTraceElement";
        r9 = r1.equals(r9);
        if (r9 != 0) goto L_0x0089;
    L_0x0176:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "syntax error : ";
        r10 = r10.append(r11);
        r10 = r10.append(r1);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
    L_0x0190:
        r9 = r4.token();
        r10 = 8;
        if (r9 == r10) goto L_0x0089;
    L_0x0198:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x01a1:
        r9 = "moduleName";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x01ca;
    L_0x01a9:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x01b4;
    L_0x01b1:
        r7 = 0;
        goto L_0x0089;
    L_0x01b4:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x01c1;
    L_0x01bb:
        r7 = r4.stringVal();
        goto L_0x0089;
    L_0x01c1:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x01ca:
        r9 = "moduleVersion";
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x01f3;
    L_0x01d2:
        r9 = r4.token();
        r10 = 8;
        if (r9 != r10) goto L_0x01dd;
    L_0x01da:
        r8 = 0;
        goto L_0x0089;
    L_0x01dd:
        r9 = r4.token();
        r10 = 4;
        if (r9 != r10) goto L_0x01ea;
    L_0x01e4:
        r8 = r4.stringVal();
        goto L_0x0089;
    L_0x01ea:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = "syntax error";
        r9.<init>(r10);
        throw r9;
    L_0x01f3:
        r9 = new com.alibaba.fastjson.JSONException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "syntax error : ";
        r10 = r10.append(r11);
        r10 = r10.append(r3);
        r10 = r10.toString();
        r9.<init>(r10);
        throw r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.StackTraceElementDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):T");
    }

    public int getFastMatchToken() {
        return 12;
    }
}
