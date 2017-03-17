package com.easemob.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ApacheHttpClient {
    public static String httpGet(String str) {
        return httpGet(str, null);
    }

    public static String httpGet(String str, Map<String, String> map) {
        HttpClient defaultHttpClient = new DefaultHttpClient();
        if (map != null) {
            String stringBuilder = new StringBuilder(String.valueOf(str)).append("?").toString();
            String str2 = stringBuilder;
            for (Entry entry : map.entrySet()) {
                str2 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str2)).append((String) entry.getKey()).append("=").append((String) entry.getValue()).toString())).append("&").toString();
            }
            str = str2.substring(0, str2.length() - 1);
        }
        try {
            HttpResponse execute = defaultHttpClient.execute(new HttpGet(str));
            int statusCode = execute.getStatusLine().getStatusCode();
            return statusCode == 200 ? EntityUtils.toString(execute.getEntity()) : "返回码：" + statusCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String httpPost(String str, String str2, Map<String, String> map) throws Exception {
        HttpClient defaultHttpClient = new DefaultHttpClient();
        HttpUriRequest httpPost = new HttpPost(str);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                httpPost.addHeader((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try {
            httpPost.setEntity(new StringEntity(str2));
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            int statusCode = execute.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(execute.getEntity());
            }
            System.out.println("返回码：" + statusCode);
            return "返回码：" + statusCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String httpPost(String str, List<NameValuePair> list, Map<String, String> map) throws Exception {
        HttpClient defaultHttpClient = new DefaultHttpClient();
        HttpUriRequest httpPost = new HttpPost(str);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                httpPost.addHeader((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            int statusCode = execute.getStatusLine().getStatusCode();
            return statusCode == 200 ? EntityUtils.toString(execute.getEntity()) : "返回码：" + statusCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
