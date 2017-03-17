package com.mob.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.ResHelper;
import com.mob.tools.utils.UIHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FakeActivity {
    private static Class<? extends Activity> shellClass;
    protected Activity activity;
    private View contentView;
    private HashMap<String, Object> result;
    private FakeActivity resultReceiver;

    public static void setShell(Class<? extends Activity> shellClass) {
        shellClass = shellClass;
    }

    public static void registerExecutor(String scheme, Object executor) {
        if (shellClass != null) {
            try {
                Method registerExecutor = shellClass.getMethod("registerExecutor", new Class[]{String.class, Object.class});
                registerExecutor.setAccessible(true);
                registerExecutor.invoke(null, new Object[]{scheme, executor});
                return;
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
                return;
            }
        }
        MobUIShell.registerExecutor(scheme, executor);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setContentViewLayoutResName(String name) {
        if (this.activity != null) {
            int resId = ResHelper.getLayoutRes(this.activity, name);
            if (resId > 0) {
                this.activity.setContentView(resId);
            }
        }
    }

    public void setContentView(View view) {
        this.contentView = view;
    }

    public View getContentView() {
        return this.contentView;
    }

    public <T extends View> T findViewById(int id) {
        if (this.activity == null) {
            return null;
        }
        return this.activity.findViewById(id);
    }

    public <T extends View> T findViewByResName(View view, String name) {
        if (this.activity == null) {
            return null;
        }
        int resId = ResHelper.getIdRes(this.activity, name);
        if (resId > 0) {
            return view.findViewById(resId);
        }
        return null;
    }

    public <T extends View> T findViewByResName(String name) {
        if (this.activity == null) {
            return null;
        }
        int resId = ResHelper.getIdRes(this.activity, name);
        if (resId > 0) {
            return findViewById(resId);
        }
        return null;
    }

    public void onCreate() {
    }

    public void onNewIntent(Intent intent) {
    }

    public void onStart() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public void onRestart() {
    }

    public boolean onFinish() {
        return false;
    }

    public void onDestroy() {
    }

    public final void finish() {
        if (this.activity != null) {
            this.activity.finish();
        }
    }

    public boolean onKeyEvent(int keyCode, KeyEvent event) {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void startActivity(Intent intent) {
        startActivityForResult(intent, -1);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (this.activity != null) {
            this.activity.startActivityForResult(intent, requestCode);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    public Context getContext() {
        return this.activity;
    }

    public void show(Context context, Intent i) {
        showForResult(context, i, null);
    }

    public void show(Context context, Intent i, boolean forceNewTask) {
        showForResult(context, i, null, forceNewTask);
    }

    public void showForResult(Context context, Intent i, FakeActivity resultReceiver) {
        showForResult(context, i, resultReceiver, false);
    }

    public void showForResult(Context context, Intent i, FakeActivity resultReceiver, final boolean forceNewTask) {
        Intent iExec;
        this.resultReceiver = resultReceiver;
        Message msg = new Message();
        String launchTime = null;
        if (shellClass != null) {
            iExec = new Intent(context, shellClass);
            try {
                Method registerExecutor = shellClass.getMethod("registerExecutor", new Class[]{Object.class});
                registerExecutor.setAccessible(true);
                launchTime = (String) registerExecutor.invoke(null, new Object[]{this});
            } catch (Throwable t) {
                MobLog.getInstance().w(t);
            }
        } else {
            iExec = new Intent(context, MobUIShell.class);
            launchTime = MobUIShell.registerExecutor(this);
        }
        iExec.putExtra("launch_time", launchTime);
        iExec.putExtra("executor_name", getClass().getName());
        if (i != null) {
            iExec.putExtras(i);
        }
        msg.obj = new Object[]{context, iExec};
        UIHandler.sendMessage(msg, new Callback() {
            public boolean handleMessage(Message msg) {
                Object[] objs = (Object[]) msg.obj;
                Context cxt = objs[0];
                Intent i = objs[1];
                if (forceNewTask) {
                    i.addFlags(268435456);
                    i.addFlags(134217728);
                } else if (!(cxt instanceof Activity)) {
                    i.addFlags(268435456);
                }
                cxt.startActivity(i);
                return false;
            }
        });
    }

    public FakeActivity getParent() {
        return this.resultReceiver;
    }

    public final void setResult(HashMap<String, Object> result) {
        this.result = result;
    }

    public void sendResult() {
        if (this.resultReceiver != null) {
            this.resultReceiver.onResult(this.result);
        }
    }

    public void onResult(HashMap<String, Object> hashMap) {
    }

    public void runOnUIThread(final Runnable r) {
        UIHandler.sendEmptyMessage(0, new Callback() {
            public boolean handleMessage(Message msg) {
                r.run();
                return false;
            }
        });
    }

    public void runOnUIThread(final Runnable r, long delayMillis) {
        UIHandler.sendEmptyMessageDelayed(0, delayMillis, new Callback() {
            public boolean handleMessage(Message msg) {
                r.run();
                return false;
            }
        });
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (this.activity != null) {
            this.activity.setRequestedOrientation(requestedOrientation);
        }
    }

    public void requestPortraitOrientation() {
        setRequestedOrientation(1);
    }

    public void requestLandscapeOrientation() {
        setRequestedOrientation(0);
    }

    public int getOrientation() {
        return this.activity.getResources().getConfiguration().orientation;
    }

    public void requestFullScreen(boolean fullScreen) {
        if (this.activity != null) {
            if (fullScreen) {
                this.activity.getWindow().addFlags(1024);
                this.activity.getWindow().clearFlags(2048);
            } else {
                this.activity.getWindow().addFlags(2048);
                this.activity.getWindow().clearFlags(1024);
            }
            this.activity.getWindow().getDecorView().requestLayout();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void requestPermissions(String[] permissions, int requestCode) {
        if (this.activity != null && VERSION.SDK_INT >= 23) {
            try {
                ReflectHelper.invokeInstanceMethod(this.activity, "requestPermissions", permissions, Integer.valueOf(requestCode));
            } catch (Throwable t) {
                MobLog.getInstance().d(t);
            }
        }
    }
}
