package cn.smssdk.net;

import android.content.Context;
import android.text.TextUtils;
import cn.finalteam.toolsfinal.coder.RSACoder;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.fanyu.boundless.config.Preferences;
import com.mob.tools.utils.ResHelper;
import java.util.ArrayList;
import java.util.HashMap;

/* compiled from: ServiceApi */
public class g extends a {
    private long i;
    private int j;
    private int k;
    private long l;
    private ArrayList<String> m;
    private String n;
    private SPHelper o;

    public g(Context context) {
        this.o = SPHelper.getInstance(context);
    }

    public void a(HashMap<String, Object> hashMap) throws Throwable {
        this.b = (String) hashMap.get(Preferences.sbry);
        if (TextUtils.isEmpty(this.b)) {
            throw new Throwable("GET API NAME ERROR");
        }
        if (this.b.equals("getZoneList")) {
            this.a = 2;
        } else if (this.b.equals("getToken")) {
            this.a = 3;
        } else if (this.b.equals("submitUser")) {
            this.a = 4;
        } else if (this.b.equals("uploadContacts")) {
            this.a = 5;
        } else if (this.b.equals("getFriend")) {
            this.a = 6;
        } else if (this.b.equals("logCollect")) {
            this.a = 7;
        } else if (this.b.equals("logInstall")) {
            this.a = 8;
        } else if (this.b.equals("sendTextSMS")) {
            this.a = 9;
        } else if (this.b.equals("sendVoiceSMS")) {
            this.a = 10;
        } else if (this.b.equals("verifyCode")) {
            this.a = 11;
        } else if (this.b.equals("uploadCollectData")) {
            this.a = 12;
        } else {
            this.a = 0;
        }
        this.c = (String) hashMap.get(MessageEncoder.ATTR_URL);
        this.m = (ArrayList) hashMap.get("params");
        this.n = (String) hashMap.get("params_chunk");
        if (this.m == null && TextUtils.isEmpty(this.n)) {
            throw new Throwable("GET API PARAMS ERROR");
        }
        String str = (String) hashMap.get("encode");
        if (!TextUtils.isEmpty(str)) {
            if (str.equals(RSACoder.KEY_ALGORITHM)) {
                this.d = 2;
            } else if (str.equals("AES")) {
                this.d = 1;
            }
        }
        Integer num = (Integer) hashMap.get("zip");
        if (num == null || num.intValue() != 1) {
            this.e = false;
        } else {
            this.e = true;
        }
        num = (Integer) hashMap.get("request");
        if (num == null || num.intValue() != 1) {
            this.f = false;
        } else {
            this.f = true;
        }
        try {
            a((String) hashMap.get("frequency"));
        } catch (Throwable th) {
            this.g = false;
        }
        e();
    }

    private void a(String str) throws Throwable {
        int i = 0;
        if (str == null || str.length() < 5) {
            this.g = false;
            return;
        }
        int length = str.length();
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            if (str.charAt(i3) == ':') {
                Object substring;
                if (i2 == 0) {
                    substring = str.substring(i, i3);
                    if (TextUtils.isDigitsOnly(substring)) {
                        this.j = ResHelper.parseInt(substring);
                        i = i3 + 1;
                        i2++;
                    } else {
                        throw new Throwable("parse frequency string ERROR");
                    }
                } else if (i2 == 1) {
                    substring = str.substring(i, i3);
                    if (TextUtils.isDigitsOnly(substring)) {
                        this.i = (long) ResHelper.parseInt(substring);
                        i = i3 + 1;
                        i2++;
                        char charAt = str.charAt(i3 + 1);
                        if (charAt == 's') {
                            this.i *= 1000;
                        } else if (charAt == 'm') {
                            this.i *= 60000;
                        } else if (charAt == 'h') {
                            this.i *= 3600000;
                        } else if (charAt == 'd') {
                            this.i *= 86400000;
                        } else if (charAt == 'w') {
                            this.i *= 604800000;
                        } else if (charAt == 'M') {
                            this.i *= 2592000000L;
                        } else if (charAt == 'y') {
                            this.i = (long) (((double) this.i) * 3.1536E10d);
                        } else {
                            throw new Throwable("parse frequency string ERROR");
                        }
                    }
                    throw new Throwable("parse frequency string ERROR");
                }
            }
            if (i3 == length - 1 && r1 < 2) {
                throw new Throwable("parse frequency string ERROR");
            }
        }
        this.g = true;
    }

    public void c() {
        if (this.g) {
            if (this.g) {
                this.k++;
            }
            d();
        }
    }

    public boolean b() {
        if (!this.f) {
            return true;
        }
        if (this.g) {
            long currentTimeMillis = System.currentTimeMillis();
            if (this.l <= currentTimeMillis) {
                this.k = 0;
                this.l = currentTimeMillis + this.i;
                if (this.a == 9 || this.a == 10) {
                    currentTimeMillis = this.l / this.i;
                    if (this.l % this.i > 0) {
                        this.l = currentTimeMillis * this.i;
                    }
                }
            } else if (this.k >= this.j) {
                return true;
            }
        }
        return false;
    }

    private void d() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.k);
        stringBuilder.append("|");
        stringBuilder.append(this.l);
        this.o.setLimit(this.b, stringBuilder.toString());
    }

    private void e() {
        if (this.g) {
            Object limit = this.o.getLimit(this.b);
            if (!TextUtils.isEmpty(limit)) {
                String[] split = limit.split("\\|");
                try {
                    this.k = ResHelper.parseInt(split[0]);
                } catch (Throwable th) {
                    SMSLog.getInstance().d(th);
                }
                this.l = Long.parseLong(split[1]);
            }
        }
    }

    protected HashMap<String, Object> a(String str, String str2, HashMap<String, Object> hashMap) throws Throwable {
        if (this.m == null || this.m.size() <= 0) {
            return e.a().a(this.n, str, str2, (HashMap) hashMap);
        }
        return e.a().a(this.m, str, str2, (HashMap) hashMap);
    }
}
