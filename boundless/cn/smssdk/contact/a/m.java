package cn.smssdk.contact.a;

import android.util.Base64;

/* compiled from: Photo */
public class m extends b {
    public String b() {
        byte[] a = a("data15");
        if (a != null) {
            return Base64.encodeToString(a, 2);
        }
        return null;
    }
}
