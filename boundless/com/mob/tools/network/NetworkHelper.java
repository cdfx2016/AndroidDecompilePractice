package com.mob.tools.network;

import android.content.Context;
import android.os.Build.VERSION;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.fanyu.boundless.util.FileUtil;
import com.mob.tools.MobLog;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class NetworkHelper {
    public static int connectionTimeout;
    public static int readTimout;

    public static class NetworkTimeOut {
        public int connectionTimeout;
        public int readTimout;
    }

    public static final class SimpleX509TrustManager implements X509TrustManager {
        private X509TrustManager standardTrustManager;

        public SimpleX509TrustManager(KeyStore keystore) {
            try {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                tmf.init(keystore);
                TrustManager[] trustManagers = tmf.getTrustManagers();
                if (trustManagers == null || trustManagers.length == 0) {
                    throw new NoSuchAlgorithmException("no trust manager found.");
                }
                this.standardTrustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                MobLog.getInstance().d("failed to initialize the standard trust manager: " + e.getMessage(), new Object[0]);
                this.standardTrustManager = null;
            }
        }

        public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
            if (certificates == null) {
                throw new IllegalArgumentException("there were no certificates.");
            } else if (certificates.length == 1) {
                certificates[0].checkValidity();
            } else if (this.standardTrustManager != null) {
                this.standardTrustManager.checkServerTrusted(certificates, authType);
            } else {
                throw new CertificateException("there were one more certificates but no trust manager found.");
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public String httpGet(String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("httpGet: " + url, new Object[0]);
        if (values != null) {
            String param = kvPairsToUrl(values);
            if (param.length() > 0) {
                url = url + "?" + param;
            }
        }
        HttpURLConnection conn = getConnection(url, timeout);
        if (headers != null) {
            Iterator i$ = headers.iterator();
            while (i$.hasNext()) {
                KVPair<String> header = (KVPair) i$.next();
                conn.setRequestProperty(header.name, (String) header.value);
            }
        }
        conn.connect();
        int status = conn.getResponseCode();
        StringBuilder sb;
        BufferedReader br;
        String txt;
        if (status == 200) {
            sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8")));
            for (txt = br.readLine(); txt != null; txt = br.readLine()) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(txt);
            }
            br.close();
            conn.disconnect();
            String resp = sb.toString();
            MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
            return resp;
        }
        sb = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
        for (txt = br.readLine(); txt != null; txt = br.readLine()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(txt);
        }
        br.close();
        conn.disconnect();
        HashMap<String, Object> errMap = new HashMap();
        errMap.put("error", sb.toString());
        errMap.put("status", Integer.valueOf(status));
        throw new Throwable(new Hashon().fromHashMap(errMap));
    }

    public String downloadCache(Context context, String url, String cacheFolder, boolean skipIfCached, NetworkTimeOut timeout) throws Throwable {
        File cache;
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("downloading: " + url, new Object[0]);
        if (skipIfCached) {
            cache = new File(ResHelper.getCachePath(context, cacheFolder), Data.MD5(url));
            if (skipIfCached && cache.exists()) {
                MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
                return cache.getAbsolutePath();
            }
        }
        HttpURLConnection conn = getConnection(url, timeout);
        conn.connect();
        int status = conn.getResponseCode();
        if (status == 200) {
            List<String> headers;
            String name = null;
            Map<String, List<String>> map = conn.getHeaderFields();
            if (map != null) {
                headers = (List) map.get("Content-Disposition");
                if (headers != null && headers.size() > 0) {
                    for (String part : ((String) headers.get(0)).split(";")) {
                        if (part.trim().startsWith(MessageEncoder.ATTR_FILENAME)) {
                            name = part.split("=")[1];
                            if (name.startsWith("\"") && name.endsWith("\"")) {
                                name = name.substring(1, name.length() - 1);
                            }
                        }
                    }
                }
            }
            if (name == null) {
                name = Data.MD5(url);
                if (map != null) {
                    headers = (List) map.get("Content-Type");
                    if (headers != null && headers.size() > 0) {
                        String value = (String) headers.get(0);
                        value = value == null ? "" : value.trim();
                        if (value.startsWith("image/")) {
                            String type = value.substring("image/".length());
                            StringBuilder append = new StringBuilder().append(name).append(FileUtil.FILE_EXTENSION_SEPARATOR);
                            if ("jpeg".equals(type)) {
                                type = "jpg";
                            }
                            name = append.append(type).toString();
                        } else {
                            int index = url.lastIndexOf(47);
                            String lastPart = null;
                            if (index > 0) {
                                lastPart = url.substring(index + 1);
                            }
                            if (lastPart != null && lastPart.length() > 0) {
                                int dot = lastPart.lastIndexOf(46);
                                if (dot > 0 && lastPart.length() - dot < 10) {
                                    name = name + lastPart.substring(dot);
                                }
                            }
                        }
                    }
                }
            }
            cache = new File(ResHelper.getCachePath(context, cacheFolder), name);
            if (skipIfCached && cache.exists()) {
                conn.disconnect();
                MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
                return cache.getAbsolutePath();
            }
            if (!cache.getParentFile().exists()) {
                cache.getParentFile().mkdirs();
            }
            if (cache.exists()) {
                cache.delete();
            }
            try {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(cache);
                byte[] buf = new byte[1024];
                for (int len = is.read(buf); len > 0; len = is.read(buf)) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                is.close();
                fos.close();
                conn.disconnect();
                MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
                return cache.getAbsolutePath();
            } catch (Throwable th) {
                if (cache.exists()) {
                    cache.delete();
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
            for (String txt = br.readLine(); txt != null; txt = br.readLine()) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(txt);
            }
            br.close();
            conn.disconnect();
            HashMap<String, Object> errMap = new HashMap();
            errMap.put("error", sb.toString());
            errMap.put("status", Integer.valueOf(status));
            throw new Throwable(new Hashon().fromHashMap(errMap));
        }
    }

    public void rawGet(String url, RawNetworkCallback callback, NetworkTimeOut timeout) throws Throwable {
        rawGet(url, null, callback, timeout);
    }

    public void rawGet(String url, ArrayList<KVPair<String>> headers, RawNetworkCallback callback, NetworkTimeOut timeout) throws Throwable {
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("rawGet: " + url, new Object[0]);
        HttpURLConnection conn = getConnection(url, timeout);
        if (headers != null) {
            Iterator i$ = headers.iterator();
            while (i$.hasNext()) {
                KVPair<String> header = (KVPair) i$.next();
                conn.setRequestProperty(header.name, (String) header.value);
            }
        }
        conn.connect();
        int status = conn.getResponseCode();
        if (status == 200) {
            if (callback != null) {
                callback.onResponse(conn.getInputStream());
            }
            conn.disconnect();
            MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
            return;
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
        for (String txt = br.readLine(); txt != null; txt = br.readLine()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(txt);
        }
        br.close();
        conn.disconnect();
        HashMap<String, Object> errMap = new HashMap();
        errMap.put("error", sb.toString());
        errMap.put("status", Integer.valueOf(status));
        throw new Throwable(new Hashon().fromHashMap(errMap));
    }

    public void rawGet(String url, HttpResponseCallback callback, NetworkTimeOut timeout) throws Throwable {
        rawGet(url, null, callback, timeout);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void rawGet(java.lang.String r12, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r13, com.mob.tools.network.HttpResponseCallback r14, com.mob.tools.network.NetworkHelper.NetworkTimeOut r15) throws java.lang.Throwable {
        /*
        r11 = this;
        r10 = 0;
        r4 = java.lang.System.currentTimeMillis();
        r6 = com.mob.tools.MobLog.getInstance();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "rawGet: ";
        r7 = r7.append(r8);
        r7 = r7.append(r12);
        r7 = r7.toString();
        r8 = new java.lang.Object[r10];
        r6.i(r7, r8);
        r0 = r11.getConnection(r12, r15);
        if (r13 == 0) goto L_0x0041;
    L_0x0027:
        r2 = r13.iterator();
    L_0x002b:
        r6 = r2.hasNext();
        if (r6 == 0) goto L_0x0041;
    L_0x0031:
        r1 = r2.next();
        r1 = (com.mob.tools.network.KVPair) r1;
        r7 = r1.name;
        r6 = r1.value;
        r6 = (java.lang.String) r6;
        r0.setRequestProperty(r7, r6);
        goto L_0x002b;
    L_0x0041:
        r0.connect();
        if (r14 == 0) goto L_0x007b;
    L_0x0046:
        r6 = new com.mob.tools.network.HttpConnectionImpl23;	 Catch:{ Throwable -> 0x0074 }
        r6.<init>(r0);	 Catch:{ Throwable -> 0x0074 }
        r14.onResponse(r6);	 Catch:{ Throwable -> 0x0074 }
        r0.disconnect();
    L_0x0051:
        r6 = com.mob.tools.MobLog.getInstance();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "use time: ";
        r7 = r7.append(r8);
        r8 = java.lang.System.currentTimeMillis();
        r8 = r8 - r4;
        r7 = r7.append(r8);
        r7 = r7.toString();
        r8 = new java.lang.Object[r10];
        r6.i(r7, r8);
        return;
    L_0x0074:
        r3 = move-exception;
        throw r3;	 Catch:{ all -> 0x0076 }
    L_0x0076:
        r6 = move-exception;
        r0.disconnect();
        throw r6;
    L_0x007b:
        r0.disconnect();
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.rawGet(java.lang.String, java.util.ArrayList, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    public String jsonPost(String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        Iterator i$;
        HashMap<String, Object> errMap;
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("jsonPost: " + url, new Object[0]);
        HttpURLConnection conn = getConnection(url, timeout);
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        conn.setRequestProperty("content-type", "application/json");
        if (headers != null) {
            i$ = headers.iterator();
            while (i$.hasNext()) {
                KVPair<String> header = (KVPair) i$.next();
                conn.setRequestProperty(header.name, (String) header.value);
            }
        }
        StringPart sp = new StringPart();
        if (values != null) {
            errMap = new HashMap();
            i$ = values.iterator();
            while (i$.hasNext()) {
                KVPair<String> p = (KVPair) i$.next();
                errMap.put(p.name, p.value);
            }
            sp.append(new Hashon().fromHashMap(errMap));
        }
        conn.connect();
        OutputStream os = conn.getOutputStream();
        InputStream is = sp.toInputStream();
        byte[] buf = new byte[65536];
        for (int len = is.read(buf); len > 0; len = is.read(buf)) {
            os.write(buf, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        int status = conn.getResponseCode();
        StringBuilder sb;
        BufferedReader br;
        String txt;
        if (status == 200 || status == 201) {
            sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8")));
            for (txt = br.readLine(); txt != null; txt = br.readLine()) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(txt);
            }
            br.close();
            conn.disconnect();
            String resp = sb.toString();
            MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
            return resp;
        }
        sb = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
        for (txt = br.readLine(); txt != null; txt = br.readLine()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(txt);
        }
        br.close();
        conn.disconnect();
        errMap = new HashMap();
        errMap.put("error", sb.toString());
        errMap.put("status", Integer.valueOf(status));
        throw new Throwable(new Hashon().fromHashMap(errMap));
    }

    public String httpPost(String url, ArrayList<KVPair<String>> values, KVPair<String> file, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        return httpPost(url, (ArrayList) values, (KVPair) file, (ArrayList) headers, 0, timeout);
    }

    public String httpPost(String url, ArrayList<KVPair<String>> values, KVPair<String> file, ArrayList<KVPair<String>> headers, int chunkLength, NetworkTimeOut timeout) throws Throwable {
        ArrayList<KVPair<String>> files = new ArrayList();
        if (!(file == null || file.value == null || !new File((String) file.value).exists())) {
            files.add(file);
        }
        return httpPostFiles(url, values, files, headers, chunkLength, timeout);
    }

    public String httpPostFiles(String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> files, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        return httpPostFiles(url, values, files, headers, 0, timeout);
    }

    public String httpPostFiles(String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> files, ArrayList<KVPair<String>> headers, int chunkLength, NetworkTimeOut timeout) throws Throwable {
        final HashMap<String, String> tmpMap = new HashMap();
        httpPost(url, values, files, headers, chunkLength, new HttpResponseCallback() {
            public void onResponse(HttpConnection conn) throws Throwable {
                int status = conn.getResponseCode();
                StringBuilder sb;
                BufferedReader br;
                String txt;
                if (status == 200 || status == 201) {
                    sb = new StringBuilder();
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8")));
                    for (txt = br.readLine(); txt != null; txt = br.readLine()) {
                        if (sb.length() > 0) {
                            sb.append('\n');
                        }
                        sb.append(txt);
                    }
                    br.close();
                    tmpMap.put("resp", sb.toString());
                    return;
                }
                sb = new StringBuilder();
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
                for (txt = br.readLine(); txt != null; txt = br.readLine()) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(txt);
                }
                br.close();
                HashMap<String, Object> errMap = new HashMap();
                errMap.put("error", sb.toString());
                errMap.put("status", Integer.valueOf(status));
                throw new Throwable(new Hashon().fromHashMap(errMap));
            }
        }, timeout);
        return (String) tmpMap.get("resp");
    }

    public void httpPost(String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> files, ArrayList<KVPair<String>> headers, HttpResponseCallback callback, NetworkTimeOut timeout) throws Throwable {
        httpPost(url, values, files, headers, 0, callback, timeout);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void httpPost(java.lang.String r21, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r22, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r23, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r24, int r25, com.mob.tools.network.HttpResponseCallback r26, com.mob.tools.network.NetworkHelper.NetworkTimeOut r27) throws java.lang.Throwable {
        /*
        r20 = this;
        r14 = java.lang.System.currentTimeMillis();
        r13 = com.mob.tools.MobLog.getInstance();
        r16 = new java.lang.StringBuilder;
        r16.<init>();
        r17 = "httpPost: ";
        r16 = r16.append(r17);
        r0 = r16;
        r1 = r21;
        r16 = r0.append(r1);
        r16 = r16.toString();
        r17 = 0;
        r0 = r17;
        r0 = new java.lang.Object[r0];
        r17 = r0;
        r0 = r16;
        r1 = r17;
        r13.i(r0, r1);
        r0 = r20;
        r1 = r21;
        r2 = r27;
        r5 = r0.getConnection(r1, r2);
        r13 = 1;
        r5.setDoOutput(r13);
        if (r23 == 0) goto L_0x0077;
    L_0x003e:
        r13 = r23.size();
        if (r13 <= 0) goto L_0x0077;
    L_0x0044:
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r23;
        r11 = r0.getFilePostHTTPPart(r5, r1, r2, r3);
        if (r25 < 0) goto L_0x0057;
    L_0x0052:
        r0 = r25;
        r5.setChunkedStreamingMode(r0);
    L_0x0057:
        if (r24 == 0) goto L_0x008c;
    L_0x0059:
        r7 = r24.iterator();
    L_0x005d:
        r13 = r7.hasNext();
        if (r13 == 0) goto L_0x008c;
    L_0x0063:
        r6 = r7.next();
        r6 = (com.mob.tools.network.KVPair) r6;
        r0 = r6.name;
        r16 = r0;
        r13 = r6.value;
        r13 = (java.lang.String) r13;
        r0 = r16;
        r5.setRequestProperty(r0, r13);
        goto L_0x005d;
    L_0x0077:
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r11 = r0.getTextPostHTTPPart(r5, r1, r2);
        r16 = r11.length();
        r0 = r16;
        r13 = (int) r0;
        r5.setFixedLengthStreamingMode(r13);
        goto L_0x0057;
    L_0x008c:
        r5.connect();
        r10 = r5.getOutputStream();
        r8 = r11.toInputStream();
        r13 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r4 = new byte[r13];
        r9 = r8.read(r4);
    L_0x009f:
        if (r9 <= 0) goto L_0x00aa;
    L_0x00a1:
        r13 = 0;
        r10.write(r4, r13, r9);
        r9 = r8.read(r4);
        goto L_0x009f;
    L_0x00aa:
        r10.flush();
        r8.close();
        r10.close();
        if (r26 == 0) goto L_0x00fb;
    L_0x00b5:
        r13 = new com.mob.tools.network.HttpConnectionImpl23;	 Catch:{ Throwable -> 0x00f4 }
        r13.<init>(r5);	 Catch:{ Throwable -> 0x00f4 }
        r0 = r26;
        r0.onResponse(r13);	 Catch:{ Throwable -> 0x00f4 }
        r5.disconnect();
    L_0x00c2:
        r13 = com.mob.tools.MobLog.getInstance();
        r16 = new java.lang.StringBuilder;
        r16.<init>();
        r17 = "use time: ";
        r16 = r16.append(r17);
        r18 = java.lang.System.currentTimeMillis();
        r18 = r18 - r14;
        r0 = r16;
        r1 = r18;
        r16 = r0.append(r1);
        r16 = r16.toString();
        r17 = 0;
        r0 = r17;
        r0 = new java.lang.Object[r0];
        r17 = r0;
        r0 = r16;
        r1 = r17;
        r13.i(r0, r1);
        return;
    L_0x00f4:
        r12 = move-exception;
        throw r12;	 Catch:{ all -> 0x00f6 }
    L_0x00f6:
        r13 = move-exception;
        r5.disconnect();
        throw r13;
    L_0x00fb:
        r5.disconnect();
        goto L_0x00c2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.httpPost(java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    private HTTPPart getFilePostHTTPPart(HttpURLConnection conn, String url, ArrayList<KVPair<String>> values, ArrayList<KVPair<String>> files) throws Throwable {
        Iterator i$;
        String boundary = UUID.randomUUID().toString();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        MultiPart mp = new MultiPart();
        StringPart sp = new StringPart();
        if (values != null) {
            i$ = values.iterator();
            while (i$.hasNext()) {
                KVPair<String> value = (KVPair) i$.next();
                sp.append("--").append(boundary).append(IOUtils.LINE_SEPARATOR_WINDOWS);
                sp.append("Content-Disposition: form-data; name=\"").append(value.name).append("\"\r\n\r\n");
                sp.append((String) value.value).append(IOUtils.LINE_SEPARATOR_WINDOWS);
            }
        }
        mp.append(sp);
        i$ = files.iterator();
        while (i$.hasNext()) {
            KVPair<String> file = (KVPair) i$.next();
            sp = new StringPart();
            File imageFile = new File((String) file.value);
            sp.append("--").append(boundary).append(IOUtils.LINE_SEPARATOR_WINDOWS);
            sp.append("Content-Disposition: form-data; name=\"").append(file.name).append("\"; filename=\"").append(imageFile.getName()).append("\"\r\n");
            String mime = URLConnection.getFileNameMap().getContentTypeFor((String) file.value);
            if (mime == null || mime.length() <= 0) {
                if (((String) file.value).toLowerCase().endsWith("jpg") || ((String) file.value).toLowerCase().endsWith("jpeg")) {
                    mime = "image/jpeg";
                } else if (((String) file.value).toLowerCase().endsWith("png")) {
                    mime = "image/png";
                } else if (((String) file.value).toLowerCase().endsWith("gif")) {
                    mime = "image/gif";
                } else {
                    FileInputStream fis = new FileInputStream((String) file.value);
                    mime = URLConnection.guessContentTypeFromStream(fis);
                    fis.close();
                    if (mime == null || mime.length() <= 0) {
                        mime = "application/octet-stream";
                    }
                }
            }
            sp.append("Content-Type: ").append(mime).append("\r\n\r\n");
            mp.append(sp);
            FilePart fp = new FilePart();
            fp.setFile((String) file.value);
            mp.append(fp);
            sp = new StringPart();
            sp.append(IOUtils.LINE_SEPARATOR_WINDOWS);
            mp.append(sp);
        }
        sp = new StringPart();
        sp.append("--").append(boundary).append("--\r\n");
        mp.append(sp);
        return mp;
    }

    private HTTPPart getTextPostHTTPPart(HttpURLConnection conn, String url, ArrayList<KVPair<String>> values) throws Throwable {
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        StringPart sp = new StringPart();
        if (values != null) {
            sp.append(kvPairsToUrl(values));
        }
        return sp;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void rawPost(java.lang.String r27, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r28, com.mob.tools.network.HTTPPart r29, com.mob.tools.network.RawNetworkCallback r30, com.mob.tools.network.NetworkHelper.NetworkTimeOut r31) throws java.lang.Throwable {
        /*
        r26 = this;
        r18 = java.lang.System.currentTimeMillis();
        r21 = com.mob.tools.MobLog.getInstance();
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "rawpost: ";
        r22 = r22.append(r23);
        r0 = r22;
        r1 = r27;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r23 = 0;
        r0 = r23;
        r0 = new java.lang.Object[r0];
        r23 = r0;
        r21.i(r22, r23);
        r0 = r26;
        r1 = r27;
        r2 = r31;
        r6 = r0.getConnection(r1, r2);
        r21 = 1;
        r0 = r21;
        r6.setDoOutput(r0);
        r21 = 0;
        r0 = r21;
        r6.setChunkedStreamingMode(r0);
        if (r28 == 0) goto L_0x0066;
    L_0x0044:
        r9 = r28.iterator();
    L_0x0048:
        r21 = r9.hasNext();
        if (r21 == 0) goto L_0x0066;
    L_0x004e:
        r8 = r9.next();
        r8 = (com.mob.tools.network.KVPair) r8;
        r0 = r8.name;
        r22 = r0;
        r0 = r8.value;
        r21 = r0;
        r21 = (java.lang.String) r21;
        r0 = r22;
        r1 = r21;
        r6.setRequestProperty(r0, r1);
        goto L_0x0048;
    L_0x0066:
        r6.connect();
        r14 = r6.getOutputStream();
        r11 = r29.toInputStream();
        r21 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r21;
        r5 = new byte[r0];
        r13 = r11.read(r5);
    L_0x007b:
        if (r13 <= 0) goto L_0x0089;
    L_0x007d:
        r21 = 0;
        r0 = r21;
        r14.write(r5, r0, r13);
        r13 = r11.read(r5);
        goto L_0x007b;
    L_0x0089:
        r14.flush();
        r11.close();
        r14.close();
        r16 = r6.getResponseCode();
        r21 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r16;
        r1 = r21;
        if (r0 != r1) goto L_0x00ef;
    L_0x009e:
        if (r30 == 0) goto L_0x00eb;
    L_0x00a0:
        r10 = r6.getInputStream();
        r0 = r30;
        r0.onResponse(r10);	 Catch:{ Throwable -> 0x00df }
        if (r10 == 0) goto L_0x00ae;
    L_0x00ab:
        r10.close();	 Catch:{ Throwable -> 0x0161 }
    L_0x00ae:
        r6.disconnect();
    L_0x00b1:
        r21 = com.mob.tools.MobLog.getInstance();
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = "use time: ";
        r22 = r22.append(r23);
        r24 = java.lang.System.currentTimeMillis();
        r24 = r24 - r18;
        r0 = r22;
        r1 = r24;
        r22 = r0.append(r1);
        r22 = r22.toString();
        r23 = 0;
        r0 = r23;
        r0 = new java.lang.Object[r0];
        r23 = r0;
        r21.i(r22, r23);
        return;
    L_0x00df:
        r17 = move-exception;
        throw r17;	 Catch:{ all -> 0x00e1 }
    L_0x00e1:
        r21 = move-exception;
        if (r10 == 0) goto L_0x00e7;
    L_0x00e4:
        r10.close();	 Catch:{ Throwable -> 0x0164 }
    L_0x00e7:
        r6.disconnect();
        throw r21;
    L_0x00eb:
        r6.disconnect();
        goto L_0x00b1;
    L_0x00ef:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r12 = new java.io.InputStreamReader;
        r21 = r6.getErrorStream();
        r22 = "utf-8";
        r22 = java.nio.charset.Charset.forName(r22);
        r0 = r21;
        r1 = r22;
        r12.<init>(r0, r1);
        r4 = new java.io.BufferedReader;
        r4.<init>(r12);
        r20 = r4.readLine();
    L_0x0111:
        if (r20 == 0) goto L_0x012a;
    L_0x0113:
        r21 = r15.length();
        if (r21 <= 0) goto L_0x0120;
    L_0x0119:
        r21 = 10;
        r0 = r21;
        r15.append(r0);
    L_0x0120:
        r0 = r20;
        r15.append(r0);
        r20 = r4.readLine();
        goto L_0x0111;
    L_0x012a:
        r4.close();
        r6.disconnect();
        r7 = new java.util.HashMap;
        r7.<init>();
        r21 = "error";
        r22 = r15.toString();
        r0 = r21;
        r1 = r22;
        r7.put(r0, r1);
        r21 = "status";
        r22 = java.lang.Integer.valueOf(r16);
        r0 = r21;
        r1 = r22;
        r7.put(r0, r1);
        r21 = new java.lang.Throwable;
        r22 = new com.mob.tools.utils.Hashon;
        r22.<init>();
        r0 = r22;
        r22 = r0.fromHashMap(r7);
        r21.<init>(r22);
        throw r21;
    L_0x0161:
        r21 = move-exception;
        goto L_0x00ae;
    L_0x0164:
        r22 = move-exception;
        goto L_0x00e7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.rawPost(java.lang.String, java.util.ArrayList, com.mob.tools.network.HTTPPart, com.mob.tools.network.RawNetworkCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void rawPost(java.lang.String r19, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r20, com.mob.tools.network.HTTPPart r21, com.mob.tools.network.HttpResponseCallback r22, com.mob.tools.network.NetworkHelper.NetworkTimeOut r23) throws java.lang.Throwable {
        /*
        r18 = this;
        r12 = java.lang.System.currentTimeMillis();
        r14 = com.mob.tools.MobLog.getInstance();
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "rawpost: ";
        r15 = r15.append(r16);
        r0 = r19;
        r15 = r15.append(r0);
        r15 = r15.toString();
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r14.i(r15, r16);
        r0 = r18;
        r1 = r19;
        r2 = r23;
        r5 = r0.getConnection(r1, r2);
        r14 = 1;
        r5.setDoOutput(r14);
        r14 = 0;
        r5.setChunkedStreamingMode(r14);
        if (r20 == 0) goto L_0x0056;
    L_0x003c:
        r7 = r20.iterator();
    L_0x0040:
        r14 = r7.hasNext();
        if (r14 == 0) goto L_0x0056;
    L_0x0046:
        r6 = r7.next();
        r6 = (com.mob.tools.network.KVPair) r6;
        r15 = r6.name;
        r14 = r6.value;
        r14 = (java.lang.String) r14;
        r5.setRequestProperty(r15, r14);
        goto L_0x0040;
    L_0x0056:
        r5.connect();
        r10 = r5.getOutputStream();
        r8 = r21.toInputStream();
        r14 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r4 = new byte[r14];
        r9 = r8.read(r4);
    L_0x0069:
        if (r9 <= 0) goto L_0x0074;
    L_0x006b:
        r14 = 0;
        r10.write(r4, r14, r9);
        r9 = r8.read(r4);
        goto L_0x0069;
    L_0x0074:
        r10.flush();
        r8.close();
        r10.close();
        if (r22 == 0) goto L_0x00bd;
    L_0x007f:
        r14 = new com.mob.tools.network.HttpConnectionImpl23;	 Catch:{ Throwable -> 0x00b6 }
        r14.<init>(r5);	 Catch:{ Throwable -> 0x00b6 }
        r0 = r22;
        r0.onResponse(r14);	 Catch:{ Throwable -> 0x00b6 }
        r5.disconnect();
    L_0x008c:
        r14 = com.mob.tools.MobLog.getInstance();
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "use time: ";
        r15 = r15.append(r16);
        r16 = java.lang.System.currentTimeMillis();
        r16 = r16 - r12;
        r15 = r15.append(r16);
        r15 = r15.toString();
        r16 = 0;
        r0 = r16;
        r0 = new java.lang.Object[r0];
        r16 = r0;
        r14.i(r15, r16);
        return;
    L_0x00b6:
        r11 = move-exception;
        throw r11;	 Catch:{ all -> 0x00b8 }
    L_0x00b8:
        r14 = move-exception;
        r5.disconnect();
        throw r14;
    L_0x00bd:
        r5.disconnect();
        goto L_0x008c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.rawPost(java.lang.String, java.util.ArrayList, com.mob.tools.network.HTTPPart, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getHttpPostResponse(java.lang.String r21, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r22, com.mob.tools.network.KVPair<java.lang.String> r23, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r24, com.mob.tools.network.HttpResponseCallback r25, com.mob.tools.network.NetworkHelper.NetworkTimeOut r26) throws java.lang.Throwable {
        /*
        r20 = this;
        r14 = java.lang.System.currentTimeMillis();
        r16 = com.mob.tools.MobLog.getInstance();
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r18 = "httpPost: ";
        r17 = r17.append(r18);
        r0 = r17;
        r1 = r21;
        r17 = r0.append(r1);
        r17 = r17.toString();
        r18 = 0;
        r0 = r18;
        r0 = new java.lang.Object[r0];
        r18 = r0;
        r16.i(r17, r18);
        r0 = r20;
        r1 = r21;
        r2 = r26;
        r5 = r0.getConnection(r1, r2);
        r16 = 1;
        r0 = r16;
        r5.setDoOutput(r0);
        r16 = 0;
        r0 = r16;
        r5.setChunkedStreamingMode(r0);
        if (r23 == 0) goto L_0x009b;
    L_0x0044:
        r0 = r23;
        r0 = r0.value;
        r16 = r0;
        if (r16 == 0) goto L_0x009b;
    L_0x004c:
        r17 = new java.io.File;
        r0 = r23;
        r0 = r0.value;
        r16 = r0;
        r16 = (java.lang.String) r16;
        r0 = r17;
        r1 = r16;
        r0.<init>(r1);
        r16 = r17.exists();
        if (r16 == 0) goto L_0x009b;
    L_0x0063:
        r6 = new java.util.ArrayList;
        r6.<init>();
        r0 = r23;
        r6.add(r0);
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r12 = r0.getFilePostHTTPPart(r5, r1, r2, r6);
    L_0x0077:
        if (r24 == 0) goto L_0x00a6;
    L_0x0079:
        r8 = r24.iterator();
    L_0x007d:
        r16 = r8.hasNext();
        if (r16 == 0) goto L_0x00a6;
    L_0x0083:
        r7 = r8.next();
        r7 = (com.mob.tools.network.KVPair) r7;
        r0 = r7.name;
        r17 = r0;
        r0 = r7.value;
        r16 = r0;
        r16 = (java.lang.String) r16;
        r0 = r17;
        r1 = r16;
        r5.setRequestProperty(r0, r1);
        goto L_0x007d;
    L_0x009b:
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r12 = r0.getTextPostHTTPPart(r5, r1, r2);
        goto L_0x0077;
    L_0x00a6:
        r5.connect();
        r11 = r5.getOutputStream();
        r9 = r12.toInputStream();
        r16 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r16;
        r4 = new byte[r0];
        r10 = r9.read(r4);
    L_0x00bb:
        if (r10 <= 0) goto L_0x00c9;
    L_0x00bd:
        r16 = 0;
        r0 = r16;
        r11.write(r4, r0, r10);
        r10 = r9.read(r4);
        goto L_0x00bb;
    L_0x00c9:
        r11.flush();
        r9.close();
        r11.close();
        if (r25 == 0) goto L_0x0116;
    L_0x00d4:
        r16 = new com.mob.tools.network.HttpConnectionImpl23;	 Catch:{ Throwable -> 0x010f }
        r0 = r16;
        r0.<init>(r5);	 Catch:{ Throwable -> 0x010f }
        r0 = r25;
        r1 = r16;
        r0.onResponse(r1);	 Catch:{ Throwable -> 0x010f }
        r5.disconnect();
    L_0x00e5:
        r16 = com.mob.tools.MobLog.getInstance();
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r18 = "use time: ";
        r17 = r17.append(r18);
        r18 = java.lang.System.currentTimeMillis();
        r18 = r18 - r14;
        r17 = r17.append(r18);
        r17 = r17.toString();
        r18 = 0;
        r0 = r18;
        r0 = new java.lang.Object[r0];
        r18 = r0;
        r16.i(r17, r18);
        return;
    L_0x010f:
        r13 = move-exception;
        throw r13;	 Catch:{ all -> 0x0111 }
    L_0x0111:
        r16 = move-exception;
        r5.disconnect();
        throw r16;
    L_0x0116:
        r5.disconnect();
        goto L_0x00e5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.getHttpPostResponse(java.lang.String, java.util.ArrayList, com.mob.tools.network.KVPair, java.util.ArrayList, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    public String httpPut(String url, ArrayList<KVPair<String>> values, KVPair<String> file, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("httpPut: " + url, new Object[0]);
        if (values != null) {
            String param = kvPairsToUrl(values);
            if (param.length() > 0) {
                url = url + "?" + param;
            }
        }
        HttpURLConnection conn = getConnection(url, timeout);
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        if (headers != null) {
            Iterator i$ = headers.iterator();
            while (i$.hasNext()) {
                KVPair<String> header = (KVPair) i$.next();
                conn.setRequestProperty(header.name, (String) header.value);
            }
        }
        conn.connect();
        OutputStream os = conn.getOutputStream();
        FilePart fp = new FilePart();
        fp.setFile((String) file.value);
        InputStream is = fp.toInputStream();
        byte[] buf = new byte[65536];
        for (int len = is.read(buf); len > 0; len = is.read(buf)) {
            os.write(buf, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        int status = conn.getResponseCode();
        StringBuilder sb;
        String txt;
        if (status == 200 || status == 201) {
            BufferedReader br;
            sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8")));
            for (txt = br.readLine(); txt != null; txt = br.readLine()) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(txt);
            }
            br.close();
            conn.disconnect();
            String resp = sb.toString();
            MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
            return resp;
        }
        sb = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
        for (txt = br.readLine(); txt != null; txt = br.readLine()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(txt);
        }
        br.close();
        HashMap<String, Object> errMap = new HashMap();
        errMap.put("error", sb.toString());
        errMap.put("status", Integer.valueOf(status));
        throw new Throwable(new Hashon().fromHashMap(errMap));
    }

    public ArrayList<KVPair<String[]>> httpHead(String url, ArrayList<KVPair<String>> values, KVPair<String> kVPair, ArrayList<KVPair<String>> headers, NetworkTimeOut timeout) throws Throwable {
        Iterator i$;
        long time = System.currentTimeMillis();
        MobLog.getInstance().i("httpHead: " + url, new Object[0]);
        if (values != null) {
            String param = kvPairsToUrl(values);
            if (param.length() > 0) {
                url = url + "?" + param;
            }
        }
        HttpURLConnection conn = getConnection(url, timeout);
        conn.setRequestMethod("HEAD");
        if (headers != null) {
            i$ = headers.iterator();
            while (i$.hasNext()) {
                KVPair<String> header = (KVPair) i$.next();
                conn.setRequestProperty(header.name, (String) header.value);
            }
        }
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        ArrayList<KVPair<String[]>> list = new ArrayList();
        if (map != null) {
            for (Entry<String, List<String>> ent : map.entrySet()) {
                List<String> value = (List) ent.getValue();
                if (value == null) {
                    list.add(new KVPair((String) ent.getKey(), new String[0]));
                } else {
                    String[] hds = new String[value.size()];
                    for (int i = 0; i < hds.length; i++) {
                        hds[i] = (String) value.get(i);
                    }
                    list.add(new KVPair((String) ent.getKey(), hds));
                }
            }
        }
        conn.disconnect();
        MobLog.getInstance().i("use time: " + (System.currentTimeMillis() - time), new Object[0]);
        return list;
    }

    public void httpPatch(String url, ArrayList<KVPair<String>> values, KVPair<String> file, long offset, ArrayList<KVPair<String>> headers, OnReadListener listener, HttpResponseCallback callback, NetworkTimeOut timeout) throws Throwable {
        if (VERSION.SDK_INT >= 23) {
            httpPatchImpl23(url, values, file, offset, headers, listener, callback, timeout);
        } else {
            httpPatchImpl(url, values, file, offset, headers, listener, callback, timeout);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void httpPatchImpl(java.lang.String r33, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r34, com.mob.tools.network.KVPair<java.lang.String> r35, long r36, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r38, com.mob.tools.network.OnReadListener r39, com.mob.tools.network.HttpResponseCallback r40, com.mob.tools.network.NetworkHelper.NetworkTimeOut r41) throws java.lang.Throwable {
        /*
        r32 = this;
        r26 = java.lang.System.currentTimeMillis();
        r28 = com.mob.tools.MobLog.getInstance();
        r29 = new java.lang.StringBuilder;
        r29.<init>();
        r30 = "httpPatch: ";
        r29 = r29.append(r30);
        r0 = r29;
        r1 = r33;
        r29 = r0.append(r1);
        r29 = r29.toString();
        r30 = 0;
        r0 = r30;
        r0 = new java.lang.Object[r0];
        r30 = r0;
        r28.i(r29, r30);
        if (r34 == 0) goto L_0x0059;
    L_0x002c:
        r0 = r32;
        r1 = r34;
        r18 = r0.kvPairsToUrl(r1);
        r28 = r18.length();
        if (r28 <= 0) goto L_0x0059;
    L_0x003a:
        r28 = new java.lang.StringBuilder;
        r28.<init>();
        r0 = r28;
        r1 = r33;
        r28 = r0.append(r1);
        r29 = "?";
        r28 = r28.append(r29);
        r0 = r28;
        r1 = r18;
        r28 = r0.append(r1);
        r33 = r28.toString();
    L_0x0059:
        r20 = new com.mob.tools.network.HttpPatch;
        r0 = r20;
        r1 = r33;
        r0.<init>(r1);
        if (r38 == 0) goto L_0x0088;
    L_0x0064:
        r14 = r38.iterator();
    L_0x0068:
        r28 = r14.hasNext();
        if (r28 == 0) goto L_0x0088;
    L_0x006e:
        r10 = r14.next();
        r10 = (com.mob.tools.network.KVPair) r10;
        r0 = r10.name;
        r29 = r0;
        r0 = r10.value;
        r28 = r0;
        r28 = (java.lang.String) r28;
        r0 = r20;
        r1 = r29;
        r2 = r28;
        r0.setHeader(r1, r2);
        goto L_0x0068;
    L_0x0088:
        r9 = new com.mob.tools.network.FilePart;
        r9.<init>();
        r0 = r39;
        r9.setOnReadListener(r0);
        r0 = r35;
        r0 = r0.value;
        r28 = r0;
        r28 = (java.lang.String) r28;
        r0 = r28;
        r9.setFile(r0);
        r0 = r36;
        r9.setOffset(r0);
        r15 = r9.toInputStream();
        r28 = r9.length();
        r16 = r28 - r36;
        r8 = new org.apache.http.entity.InputStreamEntity;
        r0 = r16;
        r8.<init>(r15, r0);
        r28 = "application/offset+octet-stream";
        r0 = r28;
        r8.setContentEncoding(r0);
        r0 = r20;
        r0.setEntity(r8);
        r11 = new org.apache.http.params.BasicHttpParams;
        r11.<init>();
        if (r41 != 0) goto L_0x01b6;
    L_0x00c8:
        r7 = connectionTimeout;
    L_0x00ca:
        if (r7 <= 0) goto L_0x00cf;
    L_0x00cc:
        org.apache.http.params.HttpConnectionParams.setConnectionTimeout(r11, r7);
    L_0x00cf:
        if (r41 != 0) goto L_0x01bc;
    L_0x00d1:
        r21 = readTimout;
    L_0x00d3:
        if (r21 <= 0) goto L_0x00da;
    L_0x00d5:
        r0 = r21;
        org.apache.http.params.HttpConnectionParams.setSoTimeout(r11, r0);
    L_0x00da:
        r0 = r20;
        r0.setParams(r11);
        r6 = 0;
        r28 = "https://";
        r0 = r33;
        r1 = r28;
        r28 = r0.startsWith(r1);
        if (r28 == 0) goto L_0x01c4;
    L_0x00ec:
        r28 = java.security.KeyStore.getDefaultType();
        r25 = java.security.KeyStore.getInstance(r28);
        r28 = 0;
        r29 = 0;
        r0 = r25;
        r1 = r28;
        r2 = r29;
        r0.load(r1, r2);
        r23 = new com.mob.tools.network.SSLSocketFactoryEx;
        r0 = r23;
        r1 = r25;
        r0.<init>(r1);
        r28 = org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
        r0 = r23;
        r1 = r28;
        r0.setHostnameVerifier(r1);
        r19 = new org.apache.http.params.BasicHttpParams;
        r19.<init>();
        r4 = org.apache.http.HttpVersion.HTTP_1_1;
        r0 = r19;
        org.apache.http.params.HttpProtocolParams.setVersion(r0, r4);
        r28 = "UTF-8";
        r0 = r19;
        r1 = r28;
        org.apache.http.params.HttpProtocolParams.setContentCharset(r0, r1);
        r22 = new org.apache.http.conn.scheme.SchemeRegistry;
        r22.<init>();
        r13 = org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory();
        r28 = new org.apache.http.conn.scheme.Scheme;
        r29 = "http";
        r30 = 80;
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r0.<init>(r1, r13, r2);
        r0 = r22;
        r1 = r28;
        r0.register(r1);
        r28 = new org.apache.http.conn.scheme.Scheme;
        r29 = "https";
        r30 = 443; // 0x1bb float:6.21E-43 double:2.19E-321;
        r0 = r28;
        r1 = r29;
        r2 = r23;
        r3 = r30;
        r0.<init>(r1, r2, r3);
        r0 = r22;
        r1 = r28;
        r0.register(r1);
        r5 = new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
        r0 = r19;
        r1 = r22;
        r5.<init>(r0, r1);
        r6 = new org.apache.http.impl.client.DefaultHttpClient;
        r0 = r19;
        r6.<init>(r5, r0);
    L_0x016f:
        r0 = r20;
        r12 = r6.execute(r0);
        if (r40 == 0) goto L_0x01d5;
    L_0x0177:
        r28 = new com.mob.tools.network.HttpConnectionImpl;	 Catch:{ Throwable -> 0x01ca }
        r0 = r28;
        r0.<init>(r12);	 Catch:{ Throwable -> 0x01ca }
        r0 = r40;
        r1 = r28;
        r0.onResponse(r1);	 Catch:{ Throwable -> 0x01ca }
        r28 = r6.getConnectionManager();
        r28.shutdown();
    L_0x018c:
        r28 = com.mob.tools.MobLog.getInstance();
        r29 = new java.lang.StringBuilder;
        r29.<init>();
        r30 = "use time: ";
        r29 = r29.append(r30);
        r30 = java.lang.System.currentTimeMillis();
        r30 = r30 - r26;
        r29 = r29.append(r30);
        r29 = r29.toString();
        r30 = 0;
        r0 = r30;
        r0 = new java.lang.Object[r0];
        r30 = r0;
        r28.i(r29, r30);
        return;
    L_0x01b6:
        r0 = r41;
        r7 = r0.connectionTimeout;
        goto L_0x00ca;
    L_0x01bc:
        r0 = r41;
        r0 = r0.readTimout;
        r21 = r0;
        goto L_0x00d3;
    L_0x01c4:
        r6 = new org.apache.http.impl.client.DefaultHttpClient;
        r6.<init>();
        goto L_0x016f;
    L_0x01ca:
        r24 = move-exception;
        throw r24;	 Catch:{ all -> 0x01cc }
    L_0x01cc:
        r28 = move-exception;
        r29 = r6.getConnectionManager();
        r29.shutdown();
        throw r28;
    L_0x01d5:
        r28 = r6.getConnectionManager();
        r28.shutdown();
        goto L_0x018c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.httpPatchImpl(java.lang.String, java.util.ArrayList, com.mob.tools.network.KVPair, long, java.util.ArrayList, com.mob.tools.network.OnReadListener, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void httpPatchImpl23(java.lang.String r21, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r22, com.mob.tools.network.KVPair<java.lang.String> r23, long r24, java.util.ArrayList<com.mob.tools.network.KVPair<java.lang.String>> r26, com.mob.tools.network.OnReadListener r27, com.mob.tools.network.HttpResponseCallback r28, com.mob.tools.network.NetworkHelper.NetworkTimeOut r29) throws java.lang.Throwable {
        /*
        r20 = this;
        r14 = java.lang.System.currentTimeMillis();
        r16 = com.mob.tools.MobLog.getInstance();
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r18 = "httpPatch: ";
        r17 = r17.append(r18);
        r0 = r17;
        r1 = r21;
        r17 = r0.append(r1);
        r17 = r17.toString();
        r18 = 0;
        r0 = r18;
        r0 = new java.lang.Object[r0];
        r18 = r0;
        r16.i(r17, r18);
        if (r22 == 0) goto L_0x0057;
    L_0x002c:
        r0 = r20;
        r1 = r22;
        r12 = r0.kvPairsToUrl(r1);
        r16 = r12.length();
        if (r16 <= 0) goto L_0x0057;
    L_0x003a:
        r16 = new java.lang.StringBuilder;
        r16.<init>();
        r0 = r16;
        r1 = r21;
        r16 = r0.append(r1);
        r17 = "?";
        r16 = r16.append(r17);
        r0 = r16;
        r16 = r0.append(r12);
        r21 = r16.toString();
    L_0x0057:
        r0 = r20;
        r1 = r21;
        r2 = r29;
        r5 = r0.getConnection(r1, r2);
        r16 = 1;
        r0 = r16;
        r5.setDoOutput(r0);
        r16 = 0;
        r0 = r16;
        r5.setChunkedStreamingMode(r0);
        r16 = "PATCH";
        r0 = r16;
        r5.setRequestMethod(r0);
        r16 = "Content-Type";
        r17 = "application/offset+octet-stream";
        r0 = r16;
        r1 = r17;
        r5.setRequestProperty(r0, r1);
        if (r26 == 0) goto L_0x00a5;
    L_0x0083:
        r8 = r26.iterator();
    L_0x0087:
        r16 = r8.hasNext();
        if (r16 == 0) goto L_0x00a5;
    L_0x008d:
        r7 = r8.next();
        r7 = (com.mob.tools.network.KVPair) r7;
        r0 = r7.name;
        r17 = r0;
        r0 = r7.value;
        r16 = r0;
        r16 = (java.lang.String) r16;
        r0 = r17;
        r1 = r16;
        r5.setRequestProperty(r0, r1);
        goto L_0x0087;
    L_0x00a5:
        r5.connect();
        r11 = r5.getOutputStream();
        r6 = new com.mob.tools.network.FilePart;
        r6.<init>();
        r0 = r27;
        r6.setOnReadListener(r0);
        r0 = r23;
        r0 = r0.value;
        r16 = r0;
        r16 = (java.lang.String) r16;
        r0 = r16;
        r6.setFile(r0);
        r0 = r24;
        r6.setOffset(r0);
        r9 = r6.toInputStream();
        r16 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r16;
        r4 = new byte[r0];
        r10 = r9.read(r4);
    L_0x00d6:
        if (r10 <= 0) goto L_0x00e4;
    L_0x00d8:
        r16 = 0;
        r0 = r16;
        r11.write(r4, r0, r10);
        r10 = r9.read(r4);
        goto L_0x00d6;
    L_0x00e4:
        r11.flush();
        r9.close();
        r11.close();
        if (r28 == 0) goto L_0x0131;
    L_0x00ef:
        r16 = new com.mob.tools.network.HttpConnectionImpl23;	 Catch:{ Throwable -> 0x012a }
        r0 = r16;
        r0.<init>(r5);	 Catch:{ Throwable -> 0x012a }
        r0 = r28;
        r1 = r16;
        r0.onResponse(r1);	 Catch:{ Throwable -> 0x012a }
        r5.disconnect();
    L_0x0100:
        r16 = com.mob.tools.MobLog.getInstance();
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r18 = "use time: ";
        r17 = r17.append(r18);
        r18 = java.lang.System.currentTimeMillis();
        r18 = r18 - r14;
        r17 = r17.append(r18);
        r17 = r17.toString();
        r18 = 0;
        r0 = r18;
        r0 = new java.lang.Object[r0];
        r18 = r0;
        r16.i(r17, r18);
        return;
    L_0x012a:
        r13 = move-exception;
        throw r13;	 Catch:{ all -> 0x012c }
    L_0x012c:
        r16 = move-exception;
        r5.disconnect();
        throw r16;
    L_0x0131:
        r5.disconnect();
        goto L_0x0100;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mob.tools.network.NetworkHelper.httpPatchImpl23(java.lang.String, java.util.ArrayList, com.mob.tools.network.KVPair, long, java.util.ArrayList, com.mob.tools.network.OnReadListener, com.mob.tools.network.HttpResponseCallback, com.mob.tools.network.NetworkHelper$NetworkTimeOut):void");
    }

    private String kvPairsToUrl(ArrayList<KVPair<String>> values) throws Throwable {
        StringBuilder sb = new StringBuilder();
        Iterator i$ = values.iterator();
        while (i$.hasNext()) {
            KVPair<String> value = (KVPair) i$.next();
            String encodedName = Data.urlEncode(value.name, "utf-8");
            String encodedValue = value.value != null ? Data.urlEncode((String) value.value, "utf-8") : "";
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(encodedName).append('=').append(encodedValue);
        }
        return sb.toString();
    }

    private HttpURLConnection getConnection(String urlStr, NetworkTimeOut timeout) throws Throwable {
        Object obj;
        int readTimout;
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        String filedName = "methodTokens";
        boolean staticType = false;
        Object methods = null;
        if (null != null) {
            try {
                methods = ReflectHelper.getStaticField("HttpURLConnection", filedName);
            } catch (Throwable th) {
            }
        } else {
            methods = ReflectHelper.getInstanceField(conn, filedName);
        }
        if (methods == null) {
            filedName = "PERMITTED_USER_METHODS";
            staticType = true;
            if (1 != null) {
                try {
                    methods = ReflectHelper.getStaticField("HttpURLConnection", filedName);
                } catch (Throwable th2) {
                    obj = methods;
                }
            } else {
                methods = ReflectHelper.getInstanceField(conn, filedName);
            }
            obj = methods;
        } else {
            obj = methods;
        }
        if (obj != null) {
            String[] methodTokens = (String[]) obj;
            String[] myMethodTokens = new String[(methodTokens.length + 1)];
            System.arraycopy(methodTokens, 0, myMethodTokens, 0, methodTokens.length);
            myMethodTokens[methodTokens.length] = HttpPatch.METHOD_NAME;
            if (staticType) {
                ReflectHelper.setStaticField("HttpURLConnection", filedName, myMethodTokens);
            } else {
                ReflectHelper.setInstanceField(conn, filedName, myMethodTokens);
            }
        }
        if (VERSION.SDK_INT < 8) {
            System.setProperty("http.keepAlive", "false");
        }
        if (conn instanceof HttpsURLConnection) {
            HostnameVerifier hostnameVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new SimpleX509TrustManager(null)}, new SecureRandom());
            httpsConn.setSSLSocketFactory(sc.getSocketFactory());
            httpsConn.setHostnameVerifier(hostnameVerifier);
        }
        int connectionTimeout = timeout == null ? connectionTimeout : timeout.connectionTimeout;
        if (connectionTimeout > 0) {
            conn.setConnectTimeout(connectionTimeout);
        }
        if (timeout == null) {
            readTimout = readTimout;
        } else {
            readTimout = timeout.readTimout;
        }
        if (readTimout > 0) {
            conn.setReadTimeout(readTimout);
        }
        return conn;
    }
}
