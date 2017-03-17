package com.easemob.cloud;

import android.content.Context;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.easemob.cloud.CustomMultiPartEntity.ProgressListener;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpFileManager extends CloudFileManager {
    private static String USER_SERVER_URL;
    private Context appContext;
    private long totalSize;

    public HttpFileManager(Context context, String str) {
        this.appContext = context.getApplicationContext();
        if (str.startsWith("http")) {
            USER_SERVER_URL = str;
        } else {
            USER_SERVER_URL = "https://" + str;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean sendFiletoServer(java.lang.String r20, java.lang.String r21, java.lang.String r22, java.lang.String r23, com.easemob.cloud.CloudOperationCallback r24) throws com.easemob.exceptions.EaseMobException {
        /*
        r19 = this;
        r5 = new java.io.File;
        r0 = r20;
        r5.<init>(r0);
        r2 = r5.isFile();
        if (r2 != 0) goto L_0x001d;
    L_0x000d:
        r2 = "CloudFileManager";
        r3 = "Source file doesn't exist";
        com.easemob.util.EMLog.e(r2, r3);
        r2 = "Source file doesn't exist";
        r0 = r24;
        r0.onError(r2);
        r2 = 0;
    L_0x001c:
        return r2;
    L_0x001d:
        r3 = 0;
        r6 = 0;
        r7 = "\r\n";
        r8 = "--";
        r9 = "*****";
        r0 = r19;
        r2 = r0.appContext;
        r10 = com.easemob.util.NetUtils.getUploadBufSize(r2);
        r12 = r5.length();
        r11 = (int) r12;
        r4 = 0;
        r12 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x02e7 }
        r2 = new java.io.File;	 Catch:{ Exception -> 0x02e7 }
        r0 = r20;
        r2.<init>(r0);	 Catch:{ Exception -> 0x02e7 }
        r12.<init>(r2);	 Catch:{ Exception -> 0x02e7 }
        if (r23 == 0) goto L_0x02cb;
    L_0x0041:
        r2 = new java.net.URL;	 Catch:{ Exception -> 0x02e7 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02e7 }
        r14 = USER_SERVER_URL;	 Catch:{ Exception -> 0x02e7 }
        r14 = java.lang.String.valueOf(r14);	 Catch:{ Exception -> 0x02e7 }
        r13.<init>(r14);	 Catch:{ Exception -> 0x02e7 }
        r14 = "/";
        r13 = r13.append(r14);	 Catch:{ Exception -> 0x02e7 }
        r14 = "#";
        r15 = "/";
        r0 = r23;
        r14 = r0.replaceFirst(r14, r15);	 Catch:{ Exception -> 0x02e7 }
        r13 = r13.append(r14);	 Catch:{ Exception -> 0x02e7 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x02e7 }
        r2.<init>(r13);	 Catch:{ Exception -> 0x02e7 }
    L_0x0069:
        r2 = r2.openConnection();	 Catch:{ Exception -> 0x02e7 }
        r2 = (java.net.HttpURLConnection) r2;	 Catch:{ Exception -> 0x02e7 }
        r3 = 1;
        r2.setDoInput(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = 1;
        r2.setDoOutput(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = 0;
        r2.setUseCaches(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r0 = r19;
        r3 = r0.appContext;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = com.easemob.util.NetUtils.getUploadBufSize(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r2.setChunkedStreamingMode(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "POST";
        r2.setRequestMethod(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "Connection";
        r13 = "Keep-Alive";
        r2.setRequestProperty(r3, r13);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "Content-Type";
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = "multipart/form-data; charset=utf-8; boundary=";
        r13.<init>(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13 = r13.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13 = r13.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13 = r13.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r2.setRequestProperty(r3, r13);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r2.connect();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r2.getOutputStream();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3.<init>(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "";
        if (r22 == 0) goto L_0x0114;
    L_0x00d2:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = "Content-Disposition: form-data; name=\"app\"";
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = java.lang.String.valueOf(r22);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x0114:
        if (r23 == 0) goto L_0x0158;
    L_0x0116:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = "Content-Disposition: form-data; name=\"id\"";
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = java.lang.String.valueOf(r23);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x0158:
        r14 = "/";
        r0 = r21;
        r14 = r0.indexOf(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r14 <= 0) goto L_0x01c1;
    L_0x0162:
        r14 = 0;
        r15 = "/";
        r0 = r21;
        r15 = r0.lastIndexOf(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r0 = r21;
        r14 = r0.substring(r14, r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = "/";
        r0 = r21;
        r15 = r0.lastIndexOf(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r0 = r21;
        r21 = r0.substring(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r16 = "Content-Disposition: form-data; name=\"path\"";
        r15.<init>(r16);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = r15.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = r15.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = r15.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = java.lang.String.valueOf(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15.<init>(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r15.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x01c1:
        r14 = "Content-Disposition: form-data; name=\"file\"; filename=\"";
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = "UTF-8";
        r0 = r21;
        r14 = r0.getBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.write(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = "\"";
        r14.<init>(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r14.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r5.getName();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = ".3gp";
        r14 = r14.endsWith(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r14 != 0) goto L_0x01fb;
    L_0x01ef:
        r14 = r5.getName();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = ".amr";
        r14 = r14.endsWith(r15);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r14 == 0) goto L_0x02fa;
    L_0x01fb:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "Content-Type: audio/3gp";
        r3 = r5.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x0212:
        r5 = "connstr";
        com.easemob.util.EMLog.d(r5, r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r12.available();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = java.lang.Math.min(r14, r10);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r15 = new byte[r3];	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5 = 0;
        r5 = r12.read(r15, r5, r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r16 = "Image length";
        r17 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = java.lang.String.valueOf(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r0 = r17;
        r0.<init>(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = r17.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r0 = r16;
        com.easemob.util.EMLog.d(r0, r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r18 = r4;
        r4 = r6;
        r6 = r3;
        r3 = r18;
    L_0x0248:
        if (r5 > 0) goto L_0x0338;
    L_0x024a:
        r13.writeBytes(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3.<init>(r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r9);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r8);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.writeBytes(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r2.getResponseCode();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4 = r2.getResponseMessage();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5 = "Server Response Code ";
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6.<init>();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = r6.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = r6.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        com.easemob.util.EMLog.d(r5, r6);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5 = "Server Response Message";
        com.easemob.util.EMLog.d(r5, r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 != r4) goto L_0x03a6;
    L_0x028c:
        r3 = "http";
        r4 = "file server resp 200 ok";
        com.easemob.util.EMLog.d(r3, r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x0293:
        r5 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4 = r2.getInputStream();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3.<init>(r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = 0;
    L_0x02a2:
        r4 = r5.readLine();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r4 != 0) goto L_0x03c4;
    L_0x02a8:
        r5.close();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r12.close();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.flush();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r13.close();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r3 == 0) goto L_0x03db;
    L_0x02b6:
        r4 = "Invalid file";
        r3 = r3.contains(r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r3 == 0) goto L_0x03db;
    L_0x02be:
        r3 = "Invalid file";
        r0 = r24;
        r0.onError(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r2.disconnect();
        r2 = 0;
        goto L_0x001c;
    L_0x02cb:
        r2 = new java.net.URL;	 Catch:{ Exception -> 0x02e7 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02e7 }
        r14 = USER_SERVER_URL;	 Catch:{ Exception -> 0x02e7 }
        r14 = java.lang.String.valueOf(r14);	 Catch:{ Exception -> 0x02e7 }
        r13.<init>(r14);	 Catch:{ Exception -> 0x02e7 }
        r0 = r21;
        r13 = r13.append(r0);	 Catch:{ Exception -> 0x02e7 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x02e7 }
        r2.<init>(r13);	 Catch:{ Exception -> 0x02e7 }
        goto L_0x0069;
    L_0x02e7:
        r2 = move-exception;
    L_0x02e8:
        r2.printStackTrace();	 Catch:{ all -> 0x02f5 }
        r4 = new com.easemob.exceptions.EaseMobException;	 Catch:{ all -> 0x02f5 }
        r2 = r2.getMessage();	 Catch:{ all -> 0x02f5 }
        r4.<init>(r2);	 Catch:{ all -> 0x02f5 }
        throw r4;	 Catch:{ all -> 0x02f5 }
    L_0x02f5:
        r2 = move-exception;
    L_0x02f6:
        r3.disconnect();
        throw r2;
    L_0x02fa:
        r5 = r5.getName();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r14 = ".mp4";
        r5 = r5.endsWith(r14);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        if (r5 == 0) goto L_0x031f;
    L_0x0306:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "Content-Type: video/mpeg4";
        r3 = r5.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        goto L_0x0212;
    L_0x031f:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = "Content-Type: image/png";
        r3 = r5.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.append(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        goto L_0x0212;
    L_0x0338:
        r5 = r5 + r3;
        r3 = 0;
        r13.write(r15, r3, r6);	 Catch:{ OutOfMemoryError -> 0x0375 }
        r3 = (float) r5;
        r6 = (float) r11;
        r3 = r3 / r6;
        r6 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r3 = r3 * r6;
        r3 = (int) r3;
        r6 = r4 + 5;
        if (r3 <= r6) goto L_0x03e8;
    L_0x0348:
        r4 = "HttpFileManager";
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r14 = java.lang.String.valueOf(r3);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r6.<init>(r14);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        com.easemob.util.EMLog.d(r4, r6);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r0 = r24;
        r0.onProgress(r3);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
    L_0x035f:
        r4 = r12.available();	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r4 = java.lang.Math.min(r4, r10);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r6 = 0;
        r6 = r12.read(r15, r6, r4);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r18 = r5;
        r5 = r6;
        r6 = r4;
        r4 = r3;
        r3 = r18;
        goto L_0x0248;
    L_0x0375:
        r3 = move-exception;
        r3.printStackTrace();	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r3 = new com.easemob.exceptions.EaseMobException;	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        r4 = "outofmemoryerror";
        r3.<init>(r4);	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
        throw r3;	 Catch:{ Exception -> 0x0381, all -> 0x03bc }
    L_0x0381:
        r3 = move-exception;
        r3.printStackTrace();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4 = new com.easemob.exceptions.EaseMobException;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = "error:";
        r5.<init>(r6);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r5.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r4.<init>(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        throw r4;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
    L_0x039e:
        r3 = move-exception;
        r18 = r3;
        r3 = r2;
        r2 = r18;
        goto L_0x02e8;
    L_0x03a6:
        r4 = "http";
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = "file server resp error, reponse code: ";
        r5.<init>(r6);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r5.append(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r3.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        com.easemob.util.EMLog.e(r4, r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        goto L_0x0293;
    L_0x03bc:
        r3 = move-exception;
        r18 = r3;
        r3 = r2;
        r2 = r18;
        goto L_0x02f6;
    L_0x03c4:
        r3 = "CloudFileManager";
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r7 = "RESULT Message: ";
        r6.<init>(r7);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = r6.append(r4);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r6 = r6.toString();	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        com.easemob.util.EMLog.d(r3, r6);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r3 = r4;
        goto L_0x02a2;
    L_0x03db:
        r3 = 100;
        r0 = r24;
        r0.onProgress(r3);	 Catch:{ Exception -> 0x039e, all -> 0x03bc }
        r2.disconnect();
        r2 = 1;
        goto L_0x001c;
    L_0x03e8:
        r3 = r4;
        goto L_0x035f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.easemob.cloud.HttpFileManager.sendFiletoServer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.easemob.cloud.CloudOperationCallback):boolean");
    }

    private boolean sendFiletoServerHttp(String str, String str2, String str3, String str4, Map<String, String> map, final CloudOperationCallback cloudOperationCallback) throws EaseMobException {
        File file = new File(str);
        if (file.isFile()) {
            String str5 = "";
            Object obj = USER_SERVER_URL + "/";
            if (str3 != null) {
                obj = new StringBuilder(String.valueOf(obj)).append(str3.replaceFirst("#", "/")).append("/").toString();
            }
            try {
                HttpUriRequest httpPost = new HttpPost(str2.startsWith("http") ? str2 : new StringBuilder(String.valueOf(obj)).append(str2).toString());
                CustomMultiPartEntity customMultiPartEntity = new CustomMultiPartEntity(new ProgressListener(this) {
                    final /* synthetic */ HttpFileManager a;

                    public void transferred(long j) {
                        cloudOperationCallback.onProgress((int) ((((float) j) / ((float) this.a.totalSize)) * 100.0f));
                    }
                });
                if (str3 != null) {
                    customMultiPartEntity.addPart("app", new StringBody(str3));
                }
                if (str4 != null) {
                    customMultiPartEntity.addPart("id", new StringBody(str4));
                }
                if (map != null) {
                    for (Entry entry : map.entrySet()) {
                        httpPost.addHeader((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                if (str2.indexOf("/") > 0) {
                    str5 = str2.substring(0, str2.lastIndexOf("/"));
                    str2 = str2.substring(str2.lastIndexOf("/"));
                    customMultiPartEntity.addPart("path", new StringBody(str5));
                }
                str5 = (file.getName().endsWith(".3gp") || file.getName().endsWith(".amr")) ? "audio/3gp" : "image/png";
                customMultiPartEntity.addPart("file", new FileBody(file, str2, str5, "UTF-8"));
                HttpParams basicHttpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
                HttpConnectionParams.setTcpNoDelay(basicHttpParams, true);
                this.totalSize = customMultiPartEntity.getContentLength();
                httpPost.setEntity(customMultiPartEntity);
                HttpResponse execute = new DefaultHttpClient(basicHttpParams).execute(httpPost);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()));
                str5 = null;
                StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    EMLog.d("CloudFileManager", "RESULT Message: " + readLine);
                    stringBuilder.append(readLine);
                    str5 = readLine;
                }
                bufferedReader.close();
                EMLog.d("CloudFileManager", "server resp:" + execute.getStatusLine());
                if (str5 == null || !str5.contains("Invalid file")) {
                    cloudOperationCallback.onProgress(100);
                    cloudOperationCallback.onSuccess(stringBuilder.toString());
                    return true;
                }
                cloudOperationCallback.onError("Invalid file");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                throw new EaseMobException(e.getMessage());
            }
        }
        EMLog.e("CloudFileManager", "Source file doesn't exist");
        cloudOperationCallback.onError("Source file doesn't exist");
        return false;
    }

    public boolean authorization() {
        return true;
    }

    public void deleteFileInBackground(final String str, final String str2, String str3, final CloudOperationCallback cloudOperationCallback) {
        new Thread(this) {
            final /* synthetic */ HttpFileManager a;

            public void a() {
                String str = IOUtils.LINE_SEPARATOR_WINDOWS;
                String str2 = "--";
                String str3 = "*****";
                String str4 = "";
                Object obj = HttpFileManager.USER_SERVER_URL + "/";
                if (str2 != null) {
                    obj = new StringBuilder(String.valueOf(obj)).append(str2.replaceFirst("#", "/")).append("/").toString();
                }
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str.startsWith("http") ? str : new StringBuilder(String.valueOf(obj)).append(str).toString()).openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + str3);
                    httpURLConnection.setRequestProperty("file", str);
                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.writeBytes(new StringBuilder(String.valueOf(str2)).append(str3).append(str).toString());
                    if (str2 != null) {
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"app\"" + str + str);
                        dataOutputStream.writeBytes(str2 + str);
                        dataOutputStream.writeBytes(new StringBuilder(String.valueOf(str2)).append(str3).append(str).toString());
                    }
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + str + "\"" + str);
                    dataOutputStream.writeBytes(str);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    while (true) {
                        str2 = bufferedReader.readLine();
                        if (str2 == null) {
                            break;
                        }
                        EMLog.d("CloudFileManager", "RESULT Message: " + str2);
                    }
                    bufferedReader.close();
                    dataOutputStream.close();
                    httpURLConnection.disconnect();
                    if (cloudOperationCallback != null) {
                        cloudOperationCallback.onSuccess(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cloudOperationCallback != null) {
                        cloudOperationCallback.onError(e.toString());
                    }
                }
            }
        }.start();
    }

    public void downloadFile(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback) {
        Object obj = USER_SERVER_URL + "/";
        if (str3 != null) {
            obj = new StringBuilder(String.valueOf(obj)).append(str3.replaceFirst("#", "/")).append("/").toString();
        }
        if (str4 != null) {
            obj = new StringBuilder(String.valueOf(obj)).append(str4).toString();
        }
        String stringBuilder = new StringBuilder(String.valueOf(obj)).append(str).toString();
        if (!str.startsWith("http")) {
            str = new StringBuilder(String.valueOf(stringBuilder)).append(str).toString();
        }
        downloadFile(str, str2, map, cloudOperationCallback);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void downloadFile(java.lang.String r16, java.lang.String r17, java.util.Map<java.lang.String, java.lang.String> r18, com.easemob.cloud.CloudOperationCallback r19) {
        /*
        r15 = this;
        r2 = java.lang.System.out;	 Catch:{ Exception -> 0x0118 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0118 }
        r4 = "remoteUrl:";
        r3.<init>(r4);	 Catch:{ Exception -> 0x0118 }
        r0 = r16;
        r3 = r3.append(r0);	 Catch:{ Exception -> 0x0118 }
        r4 = ";localFIlePath:";
        r3 = r3.append(r4);	 Catch:{ Exception -> 0x0118 }
        r0 = r17;
        r3 = r3.append(r0);	 Catch:{ Exception -> 0x0118 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0118 }
        r2.println(r3);	 Catch:{ Exception -> 0x0118 }
        r2 = "+";
        r0 = r16;
        r2 = r0.contains(r2);	 Catch:{ Exception -> 0x0118 }
        if (r2 == 0) goto L_0x01ca;
    L_0x002c:
        r2 = "+";
        r3 = "%2B";
        r0 = r16;
        r2 = r0.replaceAll(r2, r3);	 Catch:{ Exception -> 0x0118 }
    L_0x0036:
        r3 = "#";
        r3 = r2.contains(r3);	 Catch:{ Exception -> 0x01c4 }
        if (r3 == 0) goto L_0x0046;
    L_0x003e:
        r3 = "#";
        r4 = "%23";
        r2 = r2.replaceAll(r3, r4);	 Catch:{ Exception -> 0x01c4 }
    L_0x0046:
        r3 = "CloudFileManager";
        r4 = new java.lang.StringBuilder;
        r5 = "download file: remote: ";
        r4.<init>(r5);
        r4 = r4.append(r2);
        r5 = " , local: ";
        r4 = r4.append(r5);
        r0 = r17;
        r4 = r4.append(r0);
        r4 = r4.toString();
        com.easemob.util.EMLog.d(r3, r4);
        r3 = "CloudFileManager";
        r4 = new java.lang.StringBuilder;
        r5 = "local exists:";
        r4.<init>(r5);
        r5 = new java.io.File;
        r0 = r17;
        r5.<init>(r0);
        r5 = r5.exists();
        r4 = r4.append(r5);
        r4 = r4.toString();
        com.easemob.util.EMLog.d(r3, r4);
        r3 = new java.io.File;
        r0 = r17;
        r3.<init>(r0);
        r4 = r3.getParentFile();
        r4 = r4.exists();
        if (r4 != 0) goto L_0x0099;
    L_0x0096:
        r3.mkdirs();
    L_0x0099:
        r8 = 0;
        r6 = new org.apache.http.impl.client.DefaultHttpClient;
        r6.<init>();
        r5 = 0;
        r4 = 0;
        r7 = new org.apache.http.client.methods.HttpGet;	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r7.<init>(r2);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        if (r18 == 0) goto L_0x00b6;
    L_0x00a8:
        r2 = r18.entrySet();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r9 = r2.iterator();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
    L_0x00b0:
        r2 = r9.hasNext();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        if (r2 != 0) goto L_0x0122;
    L_0x00b6:
        r2 = new org.apache.http.params.BasicHttpParams;	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2.<init>();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        org.apache.http.params.HttpConnectionParams.setConnectionTimeout(r2, r3);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
        org.apache.http.params.HttpConnectionParams.setSoTimeout(r2, r3);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r7.setParams(r2);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = r6.execute(r7);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = r2.getStatusLine();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = r3.getStatusCode();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 != r6) goto L_0x018c;
    L_0x00d8:
        r2 = r2.getEntity();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        if (r2 == 0) goto L_0x010d;
    L_0x00de:
        r10 = r2.getContentLength();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r5 = r2.getContent();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x01bf, all -> 0x01a1 }
        r2 = new java.io.File;	 Catch:{ Exception -> 0x01bf, all -> 0x01a1 }
        r0 = r17;
        r2.<init>(r0);	 Catch:{ Exception -> 0x01bf, all -> 0x01a1 }
        r3.<init>(r2);	 Catch:{ Exception -> 0x01bf, all -> 0x01a1 }
        r2 = r15.appContext;	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r2 = com.easemob.util.NetUtils.getDownloadBufSize(r2);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r9 = new byte[r2];	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r6 = 0;
        r2 = r8;
    L_0x00fd:
        r8 = r5.read(r9);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r4 = -1;
        if (r8 != r4) goto L_0x015a;
    L_0x0104:
        if (r19 == 0) goto L_0x01c7;
    L_0x0106:
        r2 = 0;
        r0 = r19;
        r0.onSuccess(r2);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r4 = r3;
    L_0x010d:
        if (r4 == 0) goto L_0x0112;
    L_0x010f:
        r4.close();	 Catch:{ Exception -> 0x01b2 }
    L_0x0112:
        if (r5 == 0) goto L_0x0117;
    L_0x0114:
        r5.close();	 Catch:{ Exception -> 0x01b2 }
    L_0x0117:
        return;
    L_0x0118:
        r2 = move-exception;
        r2 = r16;
    L_0x011b:
        r3 = java.lang.System.out;
        r3.println();
        goto L_0x0046;
    L_0x0122:
        r2 = r9.next();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = (java.util.Map.Entry) r2;	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = r2.getKey();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = r2.getValue();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r7.addHeader(r3, r2);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        goto L_0x00b0;
    L_0x0139:
        r2 = move-exception;
        r3 = r4;
        r4 = r5;
    L_0x013c:
        r2.printStackTrace();	 Catch:{ all -> 0x01bb }
        if (r19 == 0) goto L_0x014a;
    L_0x0141:
        r2 = r2.getMessage();	 Catch:{ all -> 0x01bb }
        r0 = r19;
        r0.onError(r2);	 Catch:{ all -> 0x01bb }
    L_0x014a:
        if (r3 == 0) goto L_0x014f;
    L_0x014c:
        r3.close();	 Catch:{ Exception -> 0x0155 }
    L_0x014f:
        if (r4 == 0) goto L_0x0117;
    L_0x0151:
        r4.close();	 Catch:{ Exception -> 0x0155 }
        goto L_0x0117;
    L_0x0155:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0117;
    L_0x015a:
        r12 = (long) r8;
        r6 = r6 + r12;
        r12 = 100;
        r12 = r12 * r6;
        r12 = r12 / r10;
        r4 = (int) r12;	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r12 = "HttpFileManager";
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r14 = java.lang.String.valueOf(r4);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r13.<init>(r14);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        com.easemob.util.EMLog.d(r12, r13);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        r12 = 100;
        if (r4 == r12) goto L_0x017b;
    L_0x0177:
        r12 = r2 + 5;
        if (r4 <= r12) goto L_0x0183;
    L_0x017b:
        if (r19 == 0) goto L_0x0182;
    L_0x017d:
        r0 = r19;
        r0.onProgress(r4);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
    L_0x0182:
        r2 = r4;
    L_0x0183:
        r4 = 0;
        r3.write(r9, r4, r8);	 Catch:{ Exception -> 0x0189, all -> 0x01b8 }
        goto L_0x00fd;
    L_0x0189:
        r2 = move-exception;
        r4 = r5;
        goto L_0x013c;
    L_0x018c:
        if (r19 == 0) goto L_0x010d;
    L_0x018e:
        r2 = r2.getStatusLine();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = r2.getStatusCode();	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        r0 = r19;
        r0.onError(r2);	 Catch:{ Exception -> 0x0139, all -> 0x01a1 }
        goto L_0x010d;
    L_0x01a1:
        r2 = move-exception;
    L_0x01a2:
        if (r4 == 0) goto L_0x01a7;
    L_0x01a4:
        r4.close();	 Catch:{ Exception -> 0x01ad }
    L_0x01a7:
        if (r5 == 0) goto L_0x01ac;
    L_0x01a9:
        r5.close();	 Catch:{ Exception -> 0x01ad }
    L_0x01ac:
        throw r2;
    L_0x01ad:
        r3 = move-exception;
        r3.printStackTrace();
        goto L_0x01ac;
    L_0x01b2:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0117;
    L_0x01b8:
        r2 = move-exception;
        r4 = r3;
        goto L_0x01a2;
    L_0x01bb:
        r2 = move-exception;
        r5 = r4;
        r4 = r3;
        goto L_0x01a2;
    L_0x01bf:
        r2 = move-exception;
        r3 = r4;
        r4 = r5;
        goto L_0x013c;
    L_0x01c4:
        r3 = move-exception;
        goto L_0x011b;
    L_0x01c7:
        r4 = r3;
        goto L_0x010d;
    L_0x01ca:
        r2 = r16;
        goto L_0x0036;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.easemob.cloud.HttpFileManager.downloadFile(java.lang.String, java.lang.String, java.util.Map, com.easemob.cloud.CloudOperationCallback):void");
    }

    public void downloadThumbnailFile(String str, String str2, String str3, String str4, int i, boolean z, Map<String, String> map, CloudOperationCallback cloudOperationCallback) {
        Object obj = USER_SERVER_URL;
        if (str3 != null) {
            obj = new StringBuilder(String.valueOf(obj)).append(str3.replaceFirst("#", "/")).append("/").toString();
        }
        if (str4 != null) {
            obj = new StringBuilder(String.valueOf(obj)).append(str4).toString();
        }
        if (i > 0) {
            obj = new StringBuilder(String.valueOf(obj)).append("&size=").append(i).toString();
        }
        if (z) {
            obj = new StringBuilder(String.valueOf(obj)).append("&save=true").toString();
        }
        if (!str.startsWith("http:")) {
            str = new StringBuilder(String.valueOf(obj)).append(str).toString();
        }
        downloadFile(str, str2, map, cloudOperationCallback);
    }

    public void downloadThumbnailFile(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback) {
        downloadThumbnailFile(str, str2, str3, str4, 0, false, map, cloudOperationCallback);
    }

    public void uploadFile(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback) {
        try {
            sendFiletoServerHttp(str, str2, str3, str4, map, cloudOperationCallback);
        } catch (Exception e) {
            e.printStackTrace();
            cloudOperationCallback.onError(e.toString());
        }
    }

    public void uploadFileInBackground(String str, String str2, String str3, String str4, Map<String, String> map, CloudOperationCallback cloudOperationCallback) {
        final String str5 = str;
        final String str6 = str2;
        final String str7 = str3;
        final String str8 = str4;
        final Map<String, String> map2 = map;
        final CloudOperationCallback cloudOperationCallback2 = cloudOperationCallback;
        new Thread(this) {
            final /* synthetic */ HttpFileManager a;

            public void a() {
                try {
                    this.a.sendFiletoServerHttp(str5, str6, str7, str8, map2, cloudOperationCallback2);
                } catch (Exception e) {
                    e.printStackTrace();
                    cloudOperationCallback2.onError(e.toString());
                }
            }
        }.start();
    }
}
