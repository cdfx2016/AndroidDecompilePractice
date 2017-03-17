package cn.smssdk.b;

import android.content.Context;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import cn.smssdk.SMSSDK.VerifyCodeReadListener;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.mob.commons.SMSSDK;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.tools.utils.Data;
import java.util.HashMap;

/* compiled from: VerifyCodeReader */
public class a {
    private static a e = null;
    private static final String g = new String(new char[]{'的', '验', '证', '码', '：'});
    private Context a;
    private String b;
    private SPHelper c;
    private HashMap<String, String> d = new HashMap();
    private VerifyCodeReadListener f;

    private a(Context context, String str) {
        this.a = context;
        this.b = str;
        this.c = SPHelper.getInstance(context);
    }

    public static a a(Context context, String str) {
        if (e == null) {
            e = new a(context, str);
        }
        return e;
    }

    private void a(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            str2 = "";
        }
        this.d.put(str, str2);
    }

    public void a(VerifyCodeReadListener verifyCodeReadListener) {
        this.f = verifyCodeReadListener;
    }

    public boolean a(SmsMessage smsMessage) {
        try {
            return b(smsMessage);
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            return false;
        }
    }

    private boolean b(SmsMessage smsMessage) throws Throwable {
        if (smsMessage == null) {
            return false;
        }
        String messageBody = smsMessage.getMessageBody();
        a("originatingAddress", smsMessage.getOriginatingAddress());
        a("timestampMillis", Long.toString(smsMessage.getTimestampMillis()));
        a("messageBody", messageBody);
        int b = b(messageBody);
        if (b <= -1) {
            return false;
        }
        Object CRC32 = Data.CRC32(a(messageBody).getBytes());
        if (TextUtils.isEmpty(CRC32) || !CRC32.equals(this.c.getVCodeHash())) {
            return false;
        }
        String substring = messageBody.substring(b, b + 4);
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new Throwable("operation not in UI Thread");
        } else if (this.f == null) {
            throw new Throwable("listener can not be null");
        } else {
            this.f.onReadVerifyCode(substring);
            new Thread(new Runnable(this) {
                final /* synthetic */ a a;

                {
                    this.a = r1;
                }

                public void run() {
                    try {
                        this.a.a();
                    } catch (Throwable th) {
                        SMSLog.getInstance().d(th);
                    }
                }
            }).start();
            return true;
        }
    }

    private void a() throws Throwable {
        String authorize = DeviceAuthorizer.authorize(this.a, new SMSSDK());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[RMS]:");
        stringBuffer.append(Data.urlEncode((String) this.d.get("originatingAddress"))).append("|");
        stringBuffer.append("").append("|");
        stringBuffer.append(this.c.getAppKey()).append("|");
        stringBuffer.append(authorize).append("|");
        stringBuffer.append(Data.urlEncode(this.c.getSMSID())).append("|");
        stringBuffer.append((String) this.d.get("timestampMillis"));
        this.c.setLog(stringBuffer.toString());
    }

    private String a(String str) {
        if (str.startsWith(new String(new char[]{'【'}))) {
            return str.substring(str.indexOf(new String(new char[]{'】'})) + 1);
        }
        if (!str.endsWith(new String(new char[]{'】'}))) {
            return str;
        }
        return str.substring(0, str.lastIndexOf(new String(new char[]{'【'})));
    }

    private int b(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        int indexOf = str.indexOf(g);
        if (indexOf > -1) {
            return indexOf + g.length();
        }
        indexOf = str.indexOf("Your pin is ");
        if (indexOf > -1) {
            return indexOf + g.length();
        }
        return indexOf;
    }
}
