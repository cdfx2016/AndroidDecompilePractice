package cn.finalteam.toolsfinal;

import android.app.Activity;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.Stack;

public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getActivityManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        if (activityStack == null) {
            return null;
        }
        return (Activity) activityStack.lastElement();
    }

    public void finishActivity() {
        if (activityStack != null) {
            finishActivity((Activity) activityStack.lastElement());
        }
    }

    public void finishActivity(Activity activity) {
        if (activityStack != null && activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public void finishActivity(Class<?> cls) {
        if (activityStack != null) {
            Iterator<Activity> iterator = activityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity) iterator.next();
                if (activity != null && activity.getClass().equals(cls)) {
                    activity.finish();
                    iterator.remove();
                }
            }
        }
    }

    public void finishAllActivity() {
        if (activityStack != null) {
            Iterator<Activity> iterator = activityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity) iterator.next();
                if (!(activity == null || activity.isFinishing())) {
                    activity.finish();
                }
            }
            activityStack.clear();
        }
    }

    public Activity getActivity(String activityName) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            if (activity != null && TextUtils.equals(activity.getClass().getName(), activityName)) {
                return activity;
            }
        }
        return null;
    }

    public void appExit(Context context) {
        try {
            finishAllActivity();
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
        }
    }
}
