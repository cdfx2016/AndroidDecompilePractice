package com.xiaomi.network;

import android.text.TextUtils;
import com.fanyu.boundless.config.Preferences;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public class Fallback {
    public String a;
    public String b;
    public String c;
    public String d;
    public String e;
    public String f;
    public String g;
    protected String h;
    private long i;
    private ArrayList<c> j = new ArrayList();
    private String k;
    private double l = 0.1d;
    private String m = "s.mi1.cc";
    private long n = 86400000;

    public Fallback(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the host is empty");
        }
        this.i = System.currentTimeMillis();
        this.j.add(new c(str, -1));
        this.a = HostManager.getInstance().getActiveNetworkLabel();
        this.b = str;
    }

    private synchronized void c(String str) {
        Iterator it = this.j.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals(((c) it.next()).a, str)) {
                it.remove();
            }
        }
    }

    public synchronized Fallback a(JSONObject jSONObject) {
        this.a = jSONObject.optString("net");
        this.n = jSONObject.getLong("ttl");
        this.l = jSONObject.getDouble("pct");
        this.i = jSONObject.getLong("ts");
        this.d = jSONObject.optString("city");
        this.c = jSONObject.optString("prv");
        this.g = jSONObject.optString("cty");
        this.e = jSONObject.optString("isp");
        this.f = jSONObject.optString(Preferences.IP_KEY);
        this.b = jSONObject.optString("host");
        this.h = jSONObject.optString("xf");
        JSONArray jSONArray = jSONObject.getJSONArray("fbs");
        for (int i = 0; i < jSONArray.length(); i++) {
            a(new c().a(jSONArray.getJSONObject(i)));
        }
        return this;
    }

    public ArrayList<String> a(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the url is empty.");
        }
        URL url = new URL(str);
        if (TextUtils.equals(url.getHost(), this.b)) {
            ArrayList<String> arrayList = new ArrayList();
            Iterator it = a(true).iterator();
            while (it.hasNext()) {
                Host a = Host.a((String) it.next(), url.getPort());
                arrayList.add(new URL(url.getProtocol(), a.b(), a.a(), url.getFile()).toString());
            }
            return arrayList;
        }
        throw new IllegalArgumentException("the url is not supported by the fallback");
    }

    public synchronized ArrayList<String> a(boolean z) {
        ArrayList<String> arrayList;
        synchronized (this) {
            c[] cVarArr = new c[this.j.size()];
            this.j.toArray(cVarArr);
            Arrays.sort(cVarArr);
            arrayList = new ArrayList();
            for (c cVar : cVarArr) {
                if (z) {
                    arrayList.add(cVar.a);
                } else {
                    int indexOf = cVar.a.indexOf(":");
                    if (indexOf != -1) {
                        arrayList.add(cVar.a.substring(0, indexOf));
                    } else {
                        arrayList.add(cVar.a);
                    }
                }
            }
        }
        return arrayList;
    }

    public void a(double d) {
        this.l = d;
    }

    public void a(long j) {
        if (j <= 0) {
            throw new IllegalArgumentException("the duration is invalid " + j);
        }
        this.n = j;
    }

    synchronized void a(c cVar) {
        c(cVar.a);
        this.j.add(cVar);
    }

    public void a(String str, int i, long j, long j2, Exception exception) {
        a(str, new AccessHistory(i, j, j2, exception));
    }

    public void a(String str, long j, long j2) {
        try {
            b(new URL(str).getHost(), j, j2);
        } catch (MalformedURLException e) {
        }
    }

    public void a(String str, long j, long j2, Exception exception) {
        try {
            b(new URL(str).getHost(), j, j2, exception);
        } catch (MalformedURLException e) {
        }
    }

    public synchronized void a(String str, AccessHistory accessHistory) {
        Iterator it = this.j.iterator();
        while (it.hasNext()) {
            c cVar = (c) it.next();
            if (TextUtils.equals(str, cVar.a)) {
                cVar.a(accessHistory);
                break;
            }
        }
    }

    public synchronized void a(String[] strArr) {
        for (int size = this.j.size() - 1; size >= 0; size--) {
            for (CharSequence equals : strArr) {
                if (TextUtils.equals(((c) this.j.get(size)).a, equals)) {
                    this.j.remove(size);
                    break;
                }
            }
        }
        Iterator it = this.j.iterator();
        int i = 0;
        while (it.hasNext()) {
            c cVar = (c) it.next();
            i = cVar.b > i ? cVar.b : i;
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            a(new c(strArr[i2], (strArr.length + i) - i2));
        }
    }

    public boolean a() {
        return TextUtils.equals(this.a, HostManager.getInstance().getActiveNetworkLabel());
    }

    public boolean a(Fallback fallback) {
        return TextUtils.equals(this.a, fallback.a);
    }

    public void b(String str) {
        this.m = str;
    }

    public void b(String str, long j, long j2) {
        a(str, 0, j, j2, null);
    }

    public void b(String str, long j, long j2, Exception exception) {
        a(str, -1, j, j2, exception);
    }

    public boolean b() {
        return System.currentTimeMillis() - this.i < this.n;
    }

    boolean c() {
        long j = 864000000;
        if (864000000 < this.n) {
            j = this.n;
        }
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis - this.i > j || (currentTimeMillis - this.i > this.n && this.a.startsWith("WIFI-"));
    }

    public synchronized ArrayList<String> d() {
        return a(false);
    }

    public synchronized String e() {
        String str;
        if (!TextUtils.isEmpty(this.k)) {
            str = this.k;
        } else if (TextUtils.isEmpty(this.e)) {
            str = "hardcode_isp";
        } else {
            this.k = HostManager.join(new String[]{this.e, this.c, this.d, this.g, this.f}, "_");
            str = this.k;
        }
        return str;
    }

    public synchronized JSONObject f() {
        JSONObject jSONObject;
        jSONObject = new JSONObject();
        jSONObject.put("net", this.a);
        jSONObject.put("ttl", this.n);
        jSONObject.put("pct", this.l);
        jSONObject.put("ts", this.i);
        jSONObject.put("city", this.d);
        jSONObject.put("prv", this.c);
        jSONObject.put("cty", this.g);
        jSONObject.put("isp", this.e);
        jSONObject.put(Preferences.IP_KEY, this.f);
        jSONObject.put("host", this.b);
        jSONObject.put("xf", this.h);
        JSONArray jSONArray = new JSONArray();
        Iterator it = this.j.iterator();
        while (it.hasNext()) {
            jSONArray.put(((c) it.next()).a());
        }
        jSONObject.put("fbs", jSONArray);
        return jSONObject;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.a);
        stringBuilder.append("\n");
        stringBuilder.append(e());
        Iterator it = this.j.iterator();
        while (it.hasNext()) {
            c cVar = (c) it.next();
            stringBuilder.append("\n");
            stringBuilder.append(cVar.toString());
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
