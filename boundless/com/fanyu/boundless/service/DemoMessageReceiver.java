package com.fanyu.boundless.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.fanyu.boundless.R;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.util.SystemUtils;
import com.fanyu.boundless.view.home.ArriveOrLeaveSchoolActivity;
import com.fanyu.boundless.view.home.HuodongZuoyeActivity;
import com.fanyu.boundless.view.home.NoticeMessageActivity;
import com.fanyu.boundless.view.home.SubmitHomeWorkActivity;
import com.fanyu.boundless.view.home.ZuoYeListActivity;
import com.fanyu.boundless.view.home.ZuoyeBobaoActivity;
import com.fanyu.boundless.view.main.MainAcitivity;
import com.fanyu.boundless.view.myself.event.RefreshApplyEvent;
import com.fanyu.boundless.view.myself.event.UpdateClassEvent;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.view.theclass.ClassXiaoXiListActivity;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import de.greenrobot.event.EventBus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DemoMessageReceiver extends PushMessageReceiver {
    private String mAccount;
    private String mAlias;
    private String mEndTime;
    private String mRegId;
    private String mStartTime;
    private String mTopic;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v("com.fanyu.boundless", "onReceivePassThroughMessage is called. " + message.toString());
        String log = context.getString(R.string.recv_passthrough_message, new Object[]{message.getContent()});
        if (!TextUtils.isEmpty(message.getTopic())) {
            this.mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
        }
        Message.obtain().obj = log;
    }

    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v("com.fanyu.boundless", "onNotificationMessageClicked is called. " + message.toString());
        String log = context.getString(R.string.click_notification_message, new Object[]{message.getContent()});
        if (!TextUtils.isEmpty(message.getTopic())) {
            this.mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            this.mAlias = message.getAlias();
        }
        Message msg = Message.obtain();
        if (message.isNotified()) {
            msg.obj = log;
        }
    }

    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v("com.fanyu.boundless", "onNotificationMessageArrived is called. " + message.toString());
        String log = context.getString(R.string.arrive_notification_message, new Object[]{message.getContent()});
        if (!TextUtils.isEmpty(message.getTopic())) {
            this.mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            this.mAlias = message.getAlias();
            Map<String, String> classidString = message.getExtra();
            String classidString2 = (String) classidString.get("classid");
            String classnameString = (String) classidString.get("classname");
            if (StringUtils.isEmpty(classidString2)) {
                if (!classidString2.equals("1")) {
                    this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(context);
                    this.sharedPreferencesUtil.putString(Preferences.CLASS_NAME, classnameString);
                    this.sharedPreferencesUtil.putString(Preferences.CLASS_ID, classidString2);
                    this.sharedPreferencesUtil.putString(Preferences.USER_TYPE, "3");
                }
                EventBus.getDefault().post(new UpdateClassEvent());
                EventBus.getDefault().post(new UpdateMainMessageEvent());
                EventBus.getDefault().post(new RefreshApplyEvent());
            } else {
                EventBus.getDefault().post(new UpdateClassEvent());
                EventBus.getDefault().post(new UpdateMainMessageEvent());
                EventBus.getDefault().post(new RefreshApplyEvent());
            }
        }
        if (message.getTitle().equals("成员管理")) {
            EventBus.getDefault().post(new UpdateClassEvent());
        }
        Message.obtain().obj = log;
    }

    public void onReceiveMessage(Context context, MiPushMessage message) {
        Log.v("com.fanyu.boundless", "onReceiveMessage is called. " + message.toString());
        String log = context.getString(R.string.click_notification_message, new Object[]{message.getContent()});
        if (!TextUtils.isEmpty(message.getTopic())) {
            this.mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            this.mAlias = message.getAlias();
        }
        Message msg = Message.obtain();
        if (message.isNotified()) {
            msg.obj = log;
        }
        if (SystemUtils.isAppAlive(context, "com.fanyu.boundless")) {
            new Intent(context, MainAcitivity.class).setFlags(268435456);
            Intent intent;
            if (message.getTitle().equals("作业播报")) {
                new Intent(context, ZuoyeBobaoActivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("活动作业")) {
                new Intent(context, HuodongZuoyeActivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("班级通知")) {
                new Intent(context, NoticeMessageActivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("到校离校")) {
                new Intent(context, ArriveOrLeaveSchoolActivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("班级申请")) {
                new Intent(context, ClassXiaoXiListActivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("申请通过")) {
                new Intent(context, MainAcitivity.class).setFlags(268435456);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("作业通知") && message.getContent().equals("作业已批阅，请及时查看！")) {
                classidString = message.getExtra();
                itemid = (String) classidString.get("itemid");
                xuyaoid = (String) classidString.get("xuyaoid");
                zhurenid = (String) classidString.get("zhurenid");
                intent = new Intent(context, SubmitHomeWorkActivity.class);
                intent.setFlags(268435456);
                intent.putExtra("classid", itemid);
                intent.putExtra("mytype", "student");
                intent.putExtra("content", "有新作业提交，请注意查收！");
                intent.putExtra("senduserid", xuyaoid);
                intent.putExtra("receiveid", zhurenid);
                intent.putExtra("zhurenid", zhurenid);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else if (message.getTitle().equals("作业通知") && message.getContent().equals("有新作业提交，请注意查收！")) {
                classidString = message.getExtra();
                itemid = (String) classidString.get("itemid");
                xuyaoid = (String) classidString.get("xuyaoid");
                zhurenid = (String) classidString.get("zhurenid");
                intent = new Intent(context, ZuoYeListActivity.class);
                intent.setFlags(268435456);
                intent.putExtra("itemid", itemid);
                intent.putExtra("mytype", "teacher");
                intent.putExtra("content", "作业已批阅，请及时查看！");
                intent.putExtra("senduserid", zhurenid);
                intent.putExtra("zhurenid", zhurenid);
                intent.putExtra("receiveid", xuyaoid);
                context.startActivities(new Intent[]{mainIntent, intent});
                return;
            } else {
                return;
            }
        }
        Log.i("NotificationReceiver", "the app process is dead");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.fanyu.boundless");
        launchIntent.setFlags(270532608);
        context.startActivity(launchIntent);
    }

    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String log;
        Log.v("com.fanyu.boundless", "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = (arguments == null || arguments.size() <= 0) ? null : (String) arguments.get(0);
        String cmdArg2 = (arguments == null || arguments.size() <= 1) ? null : (String) arguments.get(1);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, new Object[]{this.mAlias});
            } else {
                log = context.getString(R.string.set_alias_fail, new Object[]{message.getReason()});
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, new Object[]{this.mAlias});
            } else {
                log = context.getString(R.string.unset_alias_fail, new Object[]{message.getReason()});
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, new Object[]{this.mAccount});
            } else {
                log = context.getString(R.string.set_account_fail, new Object[]{message.getReason()});
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, new Object[]{this.mAccount});
            } else {
                log = context.getString(R.string.unset_account_fail, new Object[]{message.getReason()});
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, new Object[]{this.mTopic});
            } else {
                log = context.getString(R.string.subscribe_topic_fail, new Object[]{message.getReason()});
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == 0) {
                this.mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, new Object[]{this.mTopic});
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, new Object[]{message.getReason()});
            }
        } else if (!MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            log = message.getReason();
        } else if (message.getResultCode() == 0) {
            this.mStartTime = cmdArg1;
            this.mEndTime = cmdArg2;
            log = context.getString(R.string.set_accept_time_success, new Object[]{this.mStartTime, this.mEndTime});
        } else {
            log = context.getString(R.string.set_accept_time_fail, new Object[]{message.getReason()});
        }
        Message.obtain().obj = log;
    }

    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String log;
        Log.v("com.fanyu.boundless", "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = (arguments == null || arguments.size() <= 0) ? null : (String) arguments.get(0);
        if (!MiPushClient.COMMAND_REGISTER.equals(command)) {
            log = message.getReason();
        } else if (message.getResultCode() == 0) {
            this.mRegId = cmdArg1;
            log = context.getString(R.string.register_success);
        } else {
            log = context.getString(R.string.register_fail);
        }
        Message.obtain().obj = log;
    }

    @SuppressLint({"SimpleDateFormat"})
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }
}
