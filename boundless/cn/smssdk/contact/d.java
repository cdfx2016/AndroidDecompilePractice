package cn.smssdk.contact;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.smssdk.net.f;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.ResHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/* compiled from: Synchronizer */
public class d implements Callback {
    private Handler a = new Handler(this);
    private b b;
    private SPHelper c;
    private f d;
    private String e;

    public d(Context context, b bVar) {
        this.b = bVar;
        this.c = SPHelper.getInstance(context);
        this.d = f.a(context);
        this.e = ResHelper.getCacheRoot(context) + ".slock";
    }

    public void a() {
        if (!b()) {
            this.a.removeMessages(1);
            this.a.sendEmptyMessageDelayed(1, 180000);
        }
    }

    public boolean handleMessage(Message message) {
        new Thread(this) {
            final /* synthetic */ d a;

            {
                this.a = r1;
            }

            public void run() {
                String str = null;
                try {
                    String verifyCountry = this.a.c.getVerifyCountry();
                    String verifyPhone = this.a.c.getVerifyPhone();
                    str = verifyCountry;
                } catch (Throwable th) {
                    SMSLog.getInstance().w(th);
                }
                ArrayList a = this.a.b.a(false);
                String a2 = this.a.a((Object) a);
                String bufferedContactsSignature = this.a.c.getBufferedContactsSignature();
                if (!(a == null || a.isEmpty() || a2 == null || a2.equals(bufferedContactsSignature))) {
                    this.a.d.a(str, verifyPhone, a);
                }
                this.a.c.setBufferedContactsSignature(a2);
                this.a.c();
            }
        }.start();
        return false;
    }

    private String a(Object obj) throws Throwable {
        if (obj == null) {
            return null;
        }
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        return Data.CRC32(byteArrayOutputStream.toByteArray());
    }

    private boolean b() {
        try {
            File file = new File(this.e);
            if (file.exists()) {
                if (System.currentTimeMillis() - file.lastModified() < 86400000) {
                    return true;
                }
                file.delete();
                file.createNewFile();
                return false;
            }
            file.createNewFile();
            return false;
        } catch (Throwable e) {
            SMSLog.getInstance().w(e);
            return false;
        }
    }

    private void c() {
        try {
            File file = new File(this.e);
            if (file.exists()) {
                Thread.sleep(ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                file.delete();
            }
        } catch (Throwable e) {
            SMSLog.getInstance().w(e);
        }
    }
}
