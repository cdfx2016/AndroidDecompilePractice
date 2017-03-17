package com.fanyu.boundless.config;

import android.app.Activity;
import java.util.Stack;

public class MyActivityManager {
    public static MyActivityManager instance;
    private Stack<Activity> activityStack;

    public static MyActivityManager getsInstances() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    public MyActivityManager getScreenManager() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            this.activityStack.remove(activity);
        }
    }

    public Activity currentActivity() {
        if (this.activityStack.empty()) {
            return null;
        }
        return (Activity) this.activityStack.lastElement();
    }

    public void pushActivity(Activity activity) {
        if (this.activityStack == null) {
            this.activityStack = new Stack();
        }
        this.activityStack.add(activity);
    }

    public void popAllActivityExceptOne(String cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity != null && !activity.getClass().equals(cls)) {
                popActivity(activity);
            } else {
                return;
            }
        }
    }
}
