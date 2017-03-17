package com.easemob.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.easemob.util.HanziToPinyin.Token;
import java.util.HashSet;
import java.util.Locale;

public class EMNotifier {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$easemob$chat$EMMessage$Type = null;
    private static final String TAG = "notify";
    private static EMNotifier instance;
    private static final String[] msg_ch = new String[]{"发来一条消息", "发来一张图片", "发来一段语音", "发来位置信息", "发来一个视频", "发来一个文件", "%1个联系人发来%2条消息"};
    private static final String[] msg_eng = new String[]{"sent a message", "sent a picture", "sent a voice", "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"};
    private static int notifyID = 341;
    static Ringtone ringtone = null;
    private Context appContext;
    private String appName;
    private HashSet<String> fromUsers = new HashSet();
    private long lastNotifiyTime;
    private String[] msgs;
    private NotificationManager notificationManager = null;
    private int notificationNum = 0;
    private OnMessageNotifyListener onMessageNotifyListener;
    private String packageName;

    static /* synthetic */ int[] $SWITCH_TABLE$com$easemob$chat$EMMessage$Type() {
        int[] iArr = $SWITCH_TABLE$com$easemob$chat$EMMessage$Type;
        if (iArr == null) {
            iArr = new int[Type.values().length];
            try {
                iArr[Type.CMD.ordinal()] = 7;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Type.FILE.ordinal()] = 6;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Type.IMAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Type.LOCATION.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Type.TXT.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Type.VIDEO.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Type.VOICE.ordinal()] = 5;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$com$easemob$chat$EMMessage$Type = iArr;
        }
        return iArr;
    }

    private EMNotifier(Context context) {
        this.appContext = context;
        if (this.notificationManager == null) {
            this.notificationManager = (NotificationManager) context.getSystemService("notification");
        }
        this.onMessageNotifyListener = EMChatManager.getInstance().getChatOptions().onMessageNotifyListener;
        if (this.appContext.getApplicationInfo().labelRes != 0) {
            this.appName = this.appContext.getString(this.appContext.getApplicationInfo().labelRes);
        } else {
            this.appName = "";
        }
        this.packageName = this.appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            this.msgs = msg_ch;
        } else {
            this.msgs = msg_eng;
        }
    }

    public static synchronized EMNotifier getInstance(Context context) {
        EMNotifier eMNotifier;
        synchronized (EMNotifier.class) {
            eMNotifier = instance == null ? new EMNotifier(context) : instance;
        }
        return eMNotifier;
    }

    private void sendBroadcast(EMMessage eMMessage) {
        Intent intent = new Intent(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intent.putExtra("msgid", eMMessage.msgId);
        intent.putExtra("from", eMMessage.from.username);
        intent.putExtra(MessageEncoder.ATTR_TYPE, eMMessage.type.ordinal());
        intent.putExtra(TtmlNode.TAG_BODY, MessageEncoder.getJSONMsg(eMMessage, false));
        EMLog.d(TAG, "send new message broadcast for msg:" + eMMessage.msgId);
        this.appContext.sendOrderedBroadcast(intent, null);
    }

    private void sendNotification(EMMessage eMMessage) {
        try {
            String str = "";
            CharSequence stringBuilder = new StringBuilder(String.valueOf(eMMessage.from.getNick())).append(Token.SEPARATOR).toString();
            String str2;
            Object obj;
            switch ($SWITCH_TABLE$com$easemob$chat$EMMessage$Type()[eMMessage.type.ordinal()]) {
                case 1:
                    str = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[0]).toString();
                    str2 = ((TextMessageBody) eMMessage.body).message;
                    obj = str;
                    break;
                case 2:
                    stringBuilder = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[1]).toString();
                    break;
                case 3:
                    stringBuilder = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[4]).toString();
                    break;
                case 4:
                    stringBuilder = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[3]).toString();
                    break;
                case 5:
                    stringBuilder = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[2]).toString();
                    break;
                case 6:
                    str = new StringBuilder(String.valueOf(stringBuilder)).append(this.msgs[5]).toString();
                    str2 = ((FileMessageBody) eMMessage.body).fileName;
                    obj = str;
                    break;
            }
            if (this.onMessageNotifyListener != null) {
                stringBuilder = this.onMessageNotifyListener.onNewMessageNotify(eMMessage);
            }
            Notification notification = new Notification(this.appContext.getApplicationInfo().icon, stringBuilder, System.currentTimeMillis());
            Intent launchIntentForPackage = this.appContext.getPackageManager().getLaunchIntentForPackage(this.packageName);
            if (EMChatManager.getInstance().getChatOptions().onNotificationClickListener != null) {
                launchIntentForPackage = EMChatManager.getInstance().getChatOptions().onNotificationClickListener.onNotificationClick(eMMessage);
            }
            PendingIntent activity = PendingIntent.getActivity(this.appContext, notifyID, launchIntentForPackage, 268435456);
            this.notificationNum++;
            this.fromUsers.add(eMMessage.getFrom());
            int size = this.fromUsers.size();
            stringBuilder = this.msgs[6].replaceFirst("%1", Integer.toString(size)).replaceFirst("%2", Integer.toString(this.notificationNum));
            if (this.onMessageNotifyListener != null) {
                stringBuilder = this.onMessageNotifyListener.onLatestMessageNotify(eMMessage, size, this.notificationNum);
            }
            notification.setLatestEventInfo(this.appContext, this.appName, stringBuilder, activity);
            notification.flags = 16;
            notification.number = this.notificationNum;
            this.notificationManager.notify(notifyID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyChatMsg(EMMessage eMMessage) {
        notifyOnNewMsg();
        if (EasyUtils.isAppRunningForeground(this.appContext)) {
            sendBroadcast(eMMessage);
        } else {
            EMLog.d(TAG, "easemob chat app is not running, sending notification");
            sendNotification(eMMessage);
        }
    }

    public void notifyOnNewMsg() {
        if (EMChatManager.getInstance().getChatOptions().getNotificationEnable()) {
            try {
                if (System.currentTimeMillis() - this.lastNotifiyTime >= 1000) {
                    this.lastNotifiyTime = System.currentTimeMillis();
                    if (EMChatManager.getInstance().getChatOptions().getNoticedByVibrate()) {
                        ((Vibrator) this.appContext.getSystemService("vibrator")).vibrate(500);
                    }
                    if (EMChatManager.getInstance().getChatOptions().getNoticedBySound()) {
                        if (ringtone == null) {
                            Uri defaultUri = RingtoneManager.getDefaultUri(2);
                            ringtone = RingtoneManager.getRingtone(this.appContext, defaultUri);
                            if (ringtone == null) {
                                EMLog.d(TAG, "cant find defaut ringtone at:" + defaultUri.getPath());
                                return;
                            }
                        }
                        if (!ringtone.isPlaying()) {
                            ringtone.play();
                            String str = Build.MANUFACTURER;
                            if (str != null && str.toLowerCase().contains("samsung")) {
                                new Thread() {
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                            if (EMNotifier.ringtone.isPlaying()) {
                                                EMNotifier.ringtone.stop();
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }.run();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void resetNotificationCount() {
        this.notificationNum = 0;
        this.fromUsers.clear();
    }

    void sendAckMsgBroadcast(String str, String str2) {
        Intent intent = new Intent(EMChatManager.getInstance().getAckMessageBroadcastAction());
        intent.putExtra("msgid", str2);
        intent.putExtra("from", str);
        EMLog.d(TAG, "send ack message broadcast for msg:" + str2);
        this.appContext.sendOrderedBroadcast(intent, null);
    }

    public void stop() {
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
        }
    }
}
