package cn.smssdk.net;

import android.text.TextUtils;
import android.util.Base64;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.a;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.MobRSA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

/* compiled from: Crypto */
public class c {
    private static Hashon a = new Hashon();
    private static String b = null;
    private static String c = "c0639567f182bd26b1ef4bc13bba7a4d12cbb891302e2bf5da59da50e9b418621f45c6f528972f6b7410ea38f8eb3369f39c7fc35246b8dddd595b5698155b53";
    private static String d = "35b2181b4f1eca4e19542e86e2439f5cdd1c9253fc4b760c372ba4fabdf750c3a04ec9dfada98428d75a9ed9e3078652e5d07b10467bd9328f3a66be21064621";
    private static int e = 128;

    public static void a(String str, String str2, int i) {
        c = str;
        d = str2;
        e = i;
    }

    public static void a(String str) {
        b = str;
    }

    public static boolean a() {
        return !TextUtils.isEmpty(b);
    }

    public static byte[] b(String str) {
        try {
            return Data.AES128Encode(Data.rawMD5(b.getBytes()), str);
        } catch (Throwable th) {
            SMSLog.getInstance().d(th);
            return null;
        }
    }

    public static byte[] a(byte[] bArr) {
        try {
            return Data.AES128Decode(Data.rawMD5(b.getBytes()), bArr);
        } catch (Throwable th) {
            SMSLog.getInstance().d(th);
            return null;
        }
    }

    public static byte[] a(HashMap<String, Object> hashMap, boolean z, int i) throws Throwable {
        String fromHashMap = a.fromHashMap(hashMap);
        SMSLog.getInstance().d("data before encode: " + fromHashMap, new Object[0]);
        byte[] bytes = fromHashMap.getBytes();
        if (z) {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bytes);
            gZIPOutputStream.close();
            bytes = byteArrayOutputStream.toByteArray();
        }
        if (i != 1) {
            bytes = b(bytes);
        } else if (TextUtils.isEmpty(b)) {
            return null;
        } else {
            bytes = a.a(bytes, Data.rawMD5(b.getBytes()));
        }
        SMSLog.getInstance().i("after encode data size = " + bytes.length, new Object[0]);
        return bytes;
    }

    public static String a(byte[] bArr, int i) throws Throwable {
        byte[] c;
        if (i != 1) {
            c = c(bArr);
        } else if (TextUtils.isEmpty(b)) {
            return null;
        } else {
            c = a.b(bArr, Data.rawMD5(b.getBytes()));
        }
        Object str = new String(c, "utf-8");
        if (!TextUtils.isEmpty(str)) {
            return str.trim();
        }
        throw new Throwable("[decode]Response is empty");
    }

    public static String a(String str, Object obj) throws Throwable {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return Base64.encodeToString(Data.AES128Encode(Data.rawMD5(str.getBytes()), Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2).getBytes()), 2);
    }

    public static Object a(String str, String str2) throws Throwable {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(new String(Data.AES128Decode(Data.rawMD5(str.getBytes()), Base64.decode(str2, 2)), "UTF-8").trim(), 2)));
        String str3 = (String) objectInputStream.readObject();
        objectInputStream.close();
        return str3;
    }

    public static String c(String str) throws Throwable {
        return Data.byteToHex(Data.AES128Encode(Data.rawMD5("sms.mob.com.sdk.2.0.0".getBytes()), str.getBytes()));
    }

    public static String d(String str) throws Throwable {
        byte[] AES128Decode = Data.AES128Decode(Data.rawMD5("sms.mob.com.sdk.2.0.0".getBytes()), e(str));
        if (AES128Decode == null) {
            return null;
        }
        return new String(AES128Decode, "utf-8").trim();
    }

    public static boolean b() {
        c = "c0639567f182bd26b1ef4bc13bba7a4d12cbb891302e2bf5da59da50e9b418621f45c6f528972f6b7410ea38f8eb3369f39c7fc35246b8dddd595b5698155b53";
        d = "35b2181b4f1eca4e19542e86e2439f5cdd1c9253fc4b760c372ba4fabdf750c3a04ec9dfada98428d75a9ed9e3078652e5d07b10467bd9328f3a66be21064621";
        e = 128;
        return true;
    }

    public static byte[] e(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length % 2 == 1) {
            return null;
        }
        int i = length / 2;
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            try {
                bArr[i2] = (byte) Integer.parseInt(str.substring(i2 * 2, (i2 * 2) + 2), 16);
                i2++;
            } catch (Throwable th) {
                return null;
            }
        }
        return bArr;
    }

    private static byte[] b(byte[] bArr) throws Throwable {
        return new MobRSA(e).encode(bArr, new BigInteger(c, 16), new BigInteger(d, 16));
    }

    private static byte[] c(byte[] bArr) throws Throwable {
        return new MobRSA(e).decode(bArr, new BigInteger(c, 16), new BigInteger(d, 16));
    }
}
