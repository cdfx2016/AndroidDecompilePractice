package cn.smssdk.contact.a;

/* compiled from: Email */
public class c extends b {
    public String b() {
        return b("data1");
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
                return 5;
            case 4:
                return 4;
            default:
                return -1;
        }
    }
}
