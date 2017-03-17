package cn.smssdk;

import android.os.Handler.Callback;
import android.os.Message;

public class ReflectableEnventHandler extends EventHandler {
    private int a;
    private Callback b;
    private int c;
    private Callback d;
    private int e;
    private Callback f;
    private int g;
    private Callback h;

    public void setOnRegisterCallback(int i, Callback callback) {
        this.a = i;
        this.b = callback;
    }

    public void onRegister() {
        if (this.b != null) {
            Message message = new Message();
            message.what = this.a;
            this.b.handleMessage(message);
        }
    }

    public void setBeforeEventCallback(int i, Callback callback) {
        this.c = i;
        this.d = callback;
    }

    public void beforeEvent(int i, Object obj) {
        if (this.d != null) {
            Message message = new Message();
            message.what = this.c;
            message.obj = new Object[]{Integer.valueOf(i), obj};
            this.d.handleMessage(message);
        }
    }

    public void setAfterEventCallback(int i, Callback callback) {
        this.e = i;
        this.f = callback;
    }

    public void afterEvent(int i, int i2, Object obj) {
        if (this.f != null) {
            Message message = new Message();
            message.what = this.e;
            message.obj = new Object[]{Integer.valueOf(i), Integer.valueOf(i2), obj};
            this.f.handleMessage(message);
        }
    }

    public void setOnUnregisterCallback(int i, Callback callback) {
        this.g = i;
        this.h = callback;
    }

    public void onUnregister() {
        if (this.h != null) {
            Message message = new Message();
            message.what = this.g;
            this.h.handleMessage(message);
        }
    }
}
