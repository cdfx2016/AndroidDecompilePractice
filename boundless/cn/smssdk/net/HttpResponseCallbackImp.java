package cn.smssdk.net;

import com.mob.tools.network.HttpResponseCallback;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class HttpResponseCallbackImp implements HttpResponseCallback {
    private HashMap<String, Object> a;

    public HttpResponseCallbackImp(HashMap<String, Object> hashMap) {
        this.a = hashMap;
    }

    public void handleInput(InputStream inputStream) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[65536];
        int read = inputStream.read(bArr);
        while (read > 0) {
            byteArrayOutputStream.write(bArr, 0, read);
            read = inputStream.read(bArr);
        }
        byteArrayOutputStream.flush();
        this.a.put("bResp", byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.close();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResponse(com.mob.tools.network.HttpConnection r6) throws java.lang.Throwable {
        /*
        r5 = this;
        r4 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        r1 = r6.getResponseCode();
        r0 = r5.a;
        r2 = "httpStatus";
        r3 = java.lang.Integer.valueOf(r1);
        r0.put(r2, r3);
        r0 = r6.getHeaderFields();
        r2 = "hash";
        r0 = r0.get(r2);
        r0 = (java.util.List) r0;
        if (r0 == 0) goto L_0x0033;
    L_0x001f:
        r2 = r0.size();
        if (r2 <= 0) goto L_0x0033;
    L_0x0025:
        r2 = 0;
        r0 = r0.get(r2);
        r0 = (java.lang.String) r0;
        r2 = r5.a;
        r3 = "hash";
        r2.put(r3, r0);
    L_0x0033:
        r0 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r1 == r0) goto L_0x0039;
    L_0x0037:
        if (r1 != r4) goto L_0x0058;
    L_0x0039:
        if (r1 != r4) goto L_0x0049;
    L_0x003b:
        r0 = r6.getErrorStream();
        r1 = r0;
    L_0x0040:
        r5.handleInput(r1);	 Catch:{ Throwable -> 0x004f }
        if (r1 == 0) goto L_0x0048;
    L_0x0045:
        r1.close();	 Catch:{ Throwable -> 0x00b5 }
    L_0x0048:
        return;
    L_0x0049:
        r0 = r6.getInputStream();
        r1 = r0;
        goto L_0x0040;
    L_0x004f:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0051 }
    L_0x0051:
        r0 = move-exception;
        if (r1 == 0) goto L_0x0057;
    L_0x0054:
        r1.close();	 Catch:{ Throwable -> 0x00b7 }
    L_0x0057:
        throw r0;
    L_0x0058:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = new java.io.InputStreamReader;
        r3 = r6.getErrorStream();
        r4 = "utf-8";
        r4 = java.nio.charset.Charset.forName(r4);
        r0.<init>(r3, r4);
        r3 = new java.io.BufferedReader;
        r3.<init>(r0);
        r0 = r3.readLine();
    L_0x0076:
        if (r0 == 0) goto L_0x008b;
    L_0x0078:
        r4 = r2.length();
        if (r4 <= 0) goto L_0x0083;
    L_0x007e:
        r4 = 10;
        r2.append(r4);
    L_0x0083:
        r2.append(r0);
        r0 = r3.readLine();
        goto L_0x0076;
    L_0x008b:
        r3.close();
        r0 = new java.util.HashMap;
        r0.<init>();
        r3 = "error";
        r2 = r2.toString();
        r0.put(r3, r2);
        r2 = "status";
        r1 = java.lang.Integer.valueOf(r1);
        r0.put(r2, r1);
        r1 = new java.lang.Throwable;
        r2 = new com.mob.tools.utils.Hashon;
        r2.<init>();
        r0 = r2.fromHashMap(r0);
        r1.<init>(r0);
        throw r1;
    L_0x00b5:
        r0 = move-exception;
        goto L_0x0048;
    L_0x00b7:
        r1 = move-exception;
        goto L_0x0057;
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.smssdk.net.HttpResponseCallbackImp.onResponse(com.mob.tools.network.HttpConnection):void");
    }
}
