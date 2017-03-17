package cn.smssdk.contact;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.mob.tools.FakeActivity;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.ResHelper;
import java.util.ArrayList;
import java.util.HashMap;

/* compiled from: AlertPage */
public class a extends FakeActivity implements OnClickListener {
    private static a a;
    private ArrayList<Runnable> b = new ArrayList();
    private ArrayList<Runnable> c = new ArrayList();
    private TextView d;
    private TextView e;
    private HashMap<String, Object> f = new HashMap();

    public a() {
        a = this;
        this.f.put("okActions", this.b);
        this.f.put("cancelActions", this.c);
        setResult(this.f);
    }

    public static boolean a() {
        return a != null;
    }

    public static void a(Runnable runnable, Runnable runnable2) {
        a.b.add(runnable);
        a.c.add(runnable2);
    }

    public void onCreate() {
        this.activity.setContentView(b());
    }

    private LinearLayout b() {
        LinearLayout linearLayout = new LinearLayout(this.activity);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(-1);
        View textView = new TextView(this.activity);
        textView.setBackgroundColor(-13617865);
        int dipToPx = ResHelper.dipToPx(this.activity, 26);
        textView.setPadding(dipToPx, 0, dipToPx, 0);
        textView.setTextColor(-3158065);
        textView.setTextSize(1, 20.0f);
        textView.setText(c());
        textView.setGravity(16);
        linearLayout.addView(textView, new LayoutParams(-1, ResHelper.dipToPx(this.activity, 52)));
        textView = new View(this.activity);
        textView.setBackgroundColor(-15066083);
        linearLayout.addView(textView, new LayoutParams(-1, ResHelper.dipToPx(this.activity, 2)));
        textView = new TextView(this.activity);
        dipToPx = ResHelper.dipToPx(this.activity, 15);
        textView.setPadding(dipToPx, dipToPx, dipToPx, dipToPx);
        textView.setTextColor(-6710887);
        textView.setTextSize(1, 18.0f);
        textView.setText(d());
        ViewGroup.LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.weight = 1.0f;
        linearLayout.addView(textView, layoutParams);
        textView = new LinearLayout(this.activity);
        dipToPx = ResHelper.dipToPx(this.activity, 5);
        textView.setPadding(dipToPx, dipToPx, dipToPx, dipToPx);
        linearLayout.addView(textView, new LayoutParams(-1, -2));
        this.d = new TextView(this.activity);
        this.d.setTextColor(-6102899);
        this.d.setTextSize(1, 20.0f);
        this.d.setText(e());
        this.d.setBackgroundDrawable(f());
        this.d.setGravity(17);
        int dipToPx2 = ResHelper.dipToPx(this.activity, 48);
        ViewGroup.LayoutParams layoutParams2 = new LayoutParams(-1, dipToPx2);
        layoutParams2.weight = 1.0f;
        textView.addView(this.d, layoutParams2);
        this.d.setOnClickListener(this);
        textView.addView(new View(this.activity), new LayoutParams(dipToPx, -1));
        this.e = new TextView(this.activity);
        this.e.setTextColor(-1);
        this.e.setTextSize(1, 20.0f);
        this.e.setText(g());
        this.e.setBackgroundDrawable(h());
        this.e.setGravity(17);
        layoutParams = new LayoutParams(-1, dipToPx2);
        layoutParams.weight = 1.0f;
        textView.addView(this.e, layoutParams);
        this.e.setOnClickListener(this);
        return linearLayout;
    }

    private String c() {
        if ("zh".equals(DeviceHelper.getInstance(this.activity).getOSLanguage())) {
            return String.valueOf(new char[]{'警', '告'});
        }
        return "Warning";
    }

    private String d() {
        String str;
        String appName = DeviceHelper.getInstance(this.activity).getAppName();
        if ("zh".equals(DeviceHelper.getInstance(this.activity).getOSLanguage())) {
            str = "\"%s\"" + String.valueOf(new char[]{'想', '访', '问', '您', '的', '通', '信', '录'});
        } else {
            str = "\"%s\" would like to access your contacts.";
        }
        return String.format(str, new Object[]{appName});
    }

    private String e() {
        if ("zh".equals(DeviceHelper.getInstance(this.activity).getOSLanguage())) {
            return String.valueOf(new char[]{'取', '消'});
        }
        return "Cancel";
    }

    private Drawable f() {
        return new ShapeDrawable(new Shape(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(-6102899);
                RectF rectF = new RectF(0.0f, 0.0f, getWidth(), getHeight());
                int dipToPx = ResHelper.dipToPx(this.a.activity, 4);
                canvas.drawRoundRect(rectF, (float) dipToPx, (float) dipToPx, paint);
                paint.setColor(-1);
                int dipToPx2 = ResHelper.dipToPx(this.a.activity, 2);
                canvas.drawRoundRect(new RectF((float) dipToPx2, (float) dipToPx2, getWidth() - ((float) dipToPx2), getHeight() - ((float) dipToPx2)), (float) dipToPx2, (float) dipToPx2, paint);
            }
        });
    }

    private String g() {
        if ("zh".equals(DeviceHelper.getInstance(this.activity).getOSLanguage())) {
            return String.valueOf(new char[]{'继', '续'});
        }
        return "OK";
    }

    private Drawable h() {
        return new ShapeDrawable(new Shape(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(-6102899);
                RectF rectF = new RectF(0.0f, 0.0f, getWidth(), getHeight());
                int dipToPx = ResHelper.dipToPx(this.a.activity, 4);
                canvas.drawRoundRect(rectF, (float) dipToPx, (float) dipToPx, paint);
            }
        });
    }

    public void onClick(View view) {
        if (view.equals(this.e)) {
            this.f.put("res", Boolean.valueOf(true));
        }
        finish();
    }

    public void onDestroy() {
        a = null;
        super.onDestroy();
    }
}
