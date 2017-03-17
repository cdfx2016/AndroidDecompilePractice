package cn.smssdk.a;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.smssdk.net.f;
import cn.smssdk.utils.SMSLog;
import cn.smssdk.utils.SPHelper;
import com.google.android.exoplayer2.ExoPlayerFactory;
import java.util.ArrayList;

/* compiled from: Synchronizer */
public class b implements Callback {
    private Handler a = new Handler(this);
    private cn.smssdk.contact.b b;
    private SPHelper c;
    private f d;
    private a e;
    private int f;
    private Callback g;

    public b(Context context, a aVar) {
        this.b = cn.smssdk.contact.b.a(context);
        this.c = SPHelper.getInstance(context);
        this.d = f.a(context);
        this.e = aVar;
    }

    public void a(int i, Callback callback) {
        this.a.removeMessages(1);
        this.f = i;
        this.g = callback;
        this.a.sendEmptyMessageDelayed(1, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public boolean handleMessage(Message message) {
        new Thread(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public void run() {
                try {
                    int a = this.a.a();
                    if (this.a.g != null) {
                        Message message = new Message();
                        message.what = this.a.f;
                        message.arg1 = a;
                        this.a.g.handleMessage(message);
                    }
                } catch (Throwable th) {
                    SMSLog.getInstance().w(th);
                }
            }
        }.start();
        return false;
    }

    public int a() throws Throwable {
        int i = 0;
        String[] b = this.b.b();
        String[] bufferedContactPhones;
        try {
            bufferedContactPhones = this.c.getBufferedContactPhones();
            this.c.setBufferedContactPhones(b);
        } catch (Throwable th) {
            SMSLog.getInstance().w(th);
            bufferedContactPhones = new String[0];
        }
        ArrayList arrayList = new ArrayList();
        for (String str : b) {
            if (str != null) {
                int i2 = 1;
                for (Object equals : r0) {
                    if (str.equals(equals)) {
                        i2 = 0;
                        break;
                    }
                }
                if (i2 != 0) {
                    arrayList.add(str);
                }
            }
        }
        if (arrayList.size() <= 0) {
            return 0;
        }
        String[] strArr = new String[arrayList.size()];
        while (i < strArr.length) {
            strArr[i] = (String) arrayList.get(i);
            i++;
        }
        ArrayList a = this.e.a(this.d.a(strArr));
        try {
            this.c.setBufferedNewFriends(a);
            this.c.setRequestNewFriendsTime();
        } catch (Throwable th2) {
            SMSLog.getInstance().w(th2);
        }
        return a.size();
    }
}
