package cn.smssdk.contact.a;

import android.text.TextUtils;
import com.easemob.util.HanziToPinyin.Token;
import com.xiaomi.mipush.sdk.Constants;

/* compiled from: Phone */
public class l extends b {
    public String b() {
        Object b = b("data1");
        if (TextUtils.isEmpty(b)) {
            return null;
        }
        return b.replace(Token.SEPARATOR, "").replace(Constants.ACCEPT_TIME_SEPARATOR_SERVER, "");
    }

    public int c() {
        return a(a("data2", -1));
    }

    public String d() {
        if (a("data2", -1) == 0) {
            return b("data3");
        }
        return null;
    }

    protected int a(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
            default:
                return -1;
        }
    }
}
