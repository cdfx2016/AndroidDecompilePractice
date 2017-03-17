package com.easemob.chat;

import android.content.ContentValues;
import android.text.TextUtils;
import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Status;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.core.b;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin.Token;
import com.easemob.util.ImageUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

class SendMessageThread implements Runnable {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$easemob$chat$EMMessage$Type = null;
    private static final String TAG = "sender";
    private static final int WAIT_TIME_OUT = 60;
    static Hashtable<String, Object> sendLocks;
    private EMCallBack callback;
    private Chat chat;
    private EMMessage msg;
    private MultiUserChat muc;
    private Object mutex = new Object();

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

    public SendMessageThread(Chat chat, EMMessage eMMessage, EMCallBack eMCallBack) {
        this.chat = chat;
        this.msg = eMMessage;
        this.callback = eMCallBack;
    }

    public SendMessageThread(MultiUserChat multiUserChat, EMMessage eMMessage, EMCallBack eMCallBack) {
        this.muc = multiUserChat;
        this.msg = eMMessage;
        this.callback = eMCallBack;
    }

    static synchronized void addSendLock(String str, Object obj) {
        synchronized (SendMessageThread.class) {
            if (sendLocks == null) {
                sendLocks = new Hashtable();
            }
            sendLocks.put(str, obj);
        }
    }

    private String getBaseUrlByAppKey() {
        String str = "";
        return EMChatConfig.USER_SERVER.contains("http") ? EMChatConfig.USER_SERVER + "/" + EMChatConfig.getInstance().APPKEY.replaceFirst("#", "/") + "/chatfiles/" : "https://" + EMChatConfig.USER_SERVER + "/" + EMChatConfig.getInstance().APPKEY.replaceFirst("#", "/") + "/chatfiles/";
    }

    private String getThumbnailImagePath(String str) {
        String str2 = str.substring(0, str.lastIndexOf("/") + 1) + "th" + str.substring(str.lastIndexOf("/") + 1, str.length());
        EMLog.d("msg", "original image path:" + str);
        EMLog.d("msg", "thum image path:" + str2);
        return str2;
    }

    static synchronized void notifySendLock(String str) {
        synchronized (SendMessageThread.class) {
            if (sendLocks != null) {
                Object remove = sendLocks.remove(str);
                if (remove != null) {
                    synchronized (remove) {
                        remove.notify();
                    }
                }
            }
        }
    }

    private void sendFileMessage(EMMessage eMMessage, EMCallBack eMCallBack) {
        final FileMessageBody fileMessageBody = (FileMessageBody) eMMessage.body;
        String str = fileMessageBody.localUrl;
        File file = str != null ? new File(str) : null;
        if (file != null && file.exists()) {
            String encryptFile = EMChatManager.getInstance().getChatOptions().getUseEncryption() ? EMEncryptUtils.encryptFile(str, eMMessage.getTo()) : str;
            EMLog.d(TAG, "start to send file:" + encryptFile + " size:" + file.length());
            HttpFileManager httpFileManager = new HttpFileManager(EMChatConfig.getInstance().applicationContext, EMChatConfig.USER_SERVER);
            final String baseUrlByAppKey = getBaseUrlByAppKey();
            Map hashMap = new HashMap();
            hashMap.put("restrict-access", "true");
            hashMap.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
            final EMMessage eMMessage2 = eMMessage;
            final EMCallBack eMCallBack2 = eMCallBack;
            httpFileManager.uploadFile(encryptFile, baseUrlByAppKey, EMChatConfig.getInstance().APPKEY, null, hashMap, new CloudOperationCallback() {
                public void onError(String str) {
                    EMLog.d(SendMessageThread.TAG, "upload error:" + str);
                    eMMessage2.status = Status.FAIL;
                    SendMessageThread.this.updateMsgState(eMMessage2);
                    if (eMCallBack2 != null) {
                        eMCallBack2.onProgress(100, null);
                        eMCallBack2.onError(-2, str);
                    }
                }

                public void onProgress(int i) {
                    eMMessage2.progress = i;
                    if (eMCallBack2 != null) {
                        eMCallBack2.onProgress(i, null);
                    }
                }

                public void onSuccess(String str) {
                    try {
                        String string;
                        String str2 = "";
                        String str3 = "";
                        try {
                            JSONObject jSONObject = new JSONObject(str).getJSONArray("entities").getJSONObject(0);
                            str2 = jSONObject.getString("uuid");
                            if (jSONObject.has("share-secret")) {
                                string = jSONObject.getString("share-secret");
                                fileMessageBody.remoteUrl = (baseUrlByAppKey + str2).replaceAll("#", "%23").replaceAll(Token.SEPARATOR, "%20");
                                fileMessageBody.secret = string;
                                SendMessageThread.this.sendMessageXmpp(eMMessage2);
                                EMLog.d(SendMessageThread.TAG, "sent msg successfully:" + eMMessage2.toString());
                            }
                        } catch (Exception e) {
                            if (!(e == null || e.getMessage() == null)) {
                                EMLog.d("sendFileMessage", e.getMessage());
                            }
                        }
                        string = str3;
                        fileMessageBody.remoteUrl = (baseUrlByAppKey + str2).replaceAll("#", "%23").replaceAll(Token.SEPARATOR, "%20");
                        fileMessageBody.secret = string;
                        SendMessageThread.this.sendMessageXmpp(eMMessage2);
                        EMLog.d(SendMessageThread.TAG, "sent msg successfully:" + eMMessage2.toString());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (eMCallBack2 != null) {
                            eMCallBack2.onProgress(100, null);
                            eMCallBack2.onError(-2, e2.toString());
                        }
                    }
                }
            });
        } else if (eMCallBack != null) {
            eMCallBack.onError(-3, "file doesn't exist");
        }
    }

    private void sendImageMessage(EMMessage eMMessage, EMCallBack eMCallBack) {
        File file;
        final ImageMessageBody imageMessageBody = (ImageMessageBody) eMMessage.body;
        String str = imageMessageBody.localUrl;
        if (str != null) {
            file = new File(str);
            if (!file.exists()) {
                str = getThumbnailImagePath(str);
                file = new File(str);
            }
        } else {
            file = null;
        }
        if (file != null && file.exists()) {
            boolean z;
            File file2;
            String str2;
            boolean z2 = false;
            if (imageMessageBody.isSendOriginalImage()) {
                z = false;
                file2 = file;
                str2 = str;
            } else {
                String scaledImage = ImageUtils.getScaledImage(EMChatConfig.getInstance().applicationContext, str);
                if (!scaledImage.equals(str)) {
                    EMLog.d(TAG, "send scaled image:" + scaledImage);
                    z2 = true;
                    file = new File(scaledImage);
                }
                z = z2;
                file2 = file;
                str2 = scaledImage;
            }
            EMLog.d(TAG, "start to send file:" + str2 + " size:" + file2.length());
            HttpFileManager httpFileManager = new HttpFileManager(EMChatConfig.getInstance().applicationContext, EMChatConfig.USER_SERVER);
            final String baseUrlByAppKey = getBaseUrlByAppKey();
            EMLog.d(TAG, "remote file path:" + baseUrlByAppKey);
            String encryptFile = EMChatManager.getInstance().getChatOptions().getUseEncryption() ? EMEncryptUtils.encryptFile(str2, eMMessage.getTo()) : str2;
            Map hashMap = new HashMap();
            hashMap.put("restrict-access", "true");
            hashMap.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
            final EMMessage eMMessage2 = eMMessage;
            final EMCallBack eMCallBack2 = eMCallBack;
            httpFileManager.uploadFile(encryptFile, baseUrlByAppKey, EMChatConfig.getInstance().APPKEY, null, hashMap, new CloudOperationCallback() {
                public void onError(String str) {
                    EMLog.d(SendMessageThread.TAG, "upload error:" + str);
                    if (z) {
                        file2.delete();
                    }
                    eMMessage2.status = Status.FAIL;
                    SendMessageThread.this.updateMsgState(eMMessage2);
                    if (eMCallBack2 != null) {
                        eMCallBack2.onProgress(100, null);
                        eMCallBack2.onError(-2, str);
                    }
                }

                public void onProgress(int i) {
                    eMMessage2.progress = i;
                    if (eMCallBack2 != null) {
                        eMCallBack2.onProgress(i, null);
                    }
                }

                public void onSuccess(String str) {
                    String string;
                    String str2;
                    CharSequence replaceAll;
                    String str3 = "";
                    String str4 = "";
                    try {
                        JSONObject jSONObject = new JSONObject(str).getJSONArray("entities").getJSONObject(0);
                        str3 = jSONObject.getString("uuid");
                        if (jSONObject.has("share-secret")) {
                            string = jSONObject.getString("share-secret");
                            str2 = str3;
                            str3 = "";
                            str4 = "";
                            str3 = "";
                            if (EMChatManager.getInstance().getChatOptions().getUseEncryption()) {
                                EMLog.d(SendMessageThread.TAG, "start to upload encrypted thumbnail");
                                Map access$2 = SendMessageThread.this.uploadEncryptedThumbnailImage(file2, eMMessage2);
                                str3 = (String) access$2.get("uuid");
                                str4 = (String) access$2.get(MessageEncoder.ATTR_SECRET);
                                str3 = new StringBuilder(String.valueOf(SendMessageThread.this.getBaseUrlByAppKey())).append(str3).toString();
                                EMLog.d(SendMessageThread.TAG, "encryptd thumbnail uploaded to:" + str3);
                            }
                            String str5 = str4;
                            str4 = str3;
                            str3 = new StringBuilder(String.valueOf(SendMessageThread.this.getBaseUrlByAppKey())).append(str2).toString().replaceAll("#", "%23");
                            replaceAll = str4.replaceAll("#", "%23");
                            imageMessageBody.remoteUrl = str3;
                            ImageMessageBody imageMessageBody = imageMessageBody;
                            if (!TextUtils.isEmpty(replaceAll)) {
                                CharSequence charSequence = replaceAll;
                            }
                            imageMessageBody.thumbnailUrl = str3;
                            imageMessageBody.secret = string;
                            imageMessageBody.thumbnailSecret = str5;
                            SendMessageThread.this.sendMessageXmpp(eMMessage2);
                            EMLog.d(SendMessageThread.TAG, "sent msg successfully:" + eMMessage2.toString());
                            if (z) {
                                file2.delete();
                            }
                        }
                    } catch (Exception e) {
                        EMLog.e("sendImageMessage", "json parse exception remotefilepath:" + baseUrlByAppKey);
                    }
                    string = str4;
                    str2 = str3;
                    try {
                        str3 = "";
                        str4 = "";
                        str3 = "";
                        if (EMChatManager.getInstance().getChatOptions().getUseEncryption()) {
                            EMLog.d(SendMessageThread.TAG, "start to upload encrypted thumbnail");
                            Map access$22 = SendMessageThread.this.uploadEncryptedThumbnailImage(file2, eMMessage2);
                            str3 = (String) access$22.get("uuid");
                            str4 = (String) access$22.get(MessageEncoder.ATTR_SECRET);
                            str3 = new StringBuilder(String.valueOf(SendMessageThread.this.getBaseUrlByAppKey())).append(str3).toString();
                            EMLog.d(SendMessageThread.TAG, "encryptd thumbnail uploaded to:" + str3);
                        }
                        String str52 = str4;
                        str4 = str3;
                        str3 = new StringBuilder(String.valueOf(SendMessageThread.this.getBaseUrlByAppKey())).append(str2).toString().replaceAll("#", "%23");
                        replaceAll = str4.replaceAll("#", "%23");
                        imageMessageBody.remoteUrl = str3;
                        ImageMessageBody imageMessageBody2 = imageMessageBody;
                        if (TextUtils.isEmpty(replaceAll)) {
                            CharSequence charSequence2 = replaceAll;
                        }
                        imageMessageBody2.thumbnailUrl = str3;
                        imageMessageBody.secret = string;
                        imageMessageBody.thumbnailSecret = str52;
                        SendMessageThread.this.sendMessageXmpp(eMMessage2);
                        EMLog.d(SendMessageThread.TAG, "sent msg successfully:" + eMMessage2.toString());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (eMCallBack2 != null) {
                            eMCallBack2.onProgress(100, null);
                            eMCallBack2.onError(-2, e2.toString());
                        }
                    }
                    if (z) {
                        file2.delete();
                    }
                }
            });
        } else if (eMCallBack != null) {
            eMCallBack.onError(-3, "file doesn't exist");
        }
    }

    private void sendMessageXmpp(EMMessage eMMessage) {
        try {
            String jSONMsg = MessageEncoder.getJSONMsg(eMMessage, false);
            EMLog.d(TAG, "try to send msg to:" + eMMessage.to + " msg:" + jSONMsg);
            Message message = new Message();
            message.setPacketID(eMMessage.getMsgId());
            EMChatOptions chatOptions = EMChatManager.getInstance().getChatOptions();
            if (chatOptions.getUseEncryption()) {
                jSONMsg = EMEncryptUtils.encryptMessage(jSONMsg, eMMessage.getTo());
                message.addExtension(new b());
            }
            message.setBody(jSONMsg);
            if (chatOptions.getRequireServerAck()) {
                addSendLock(message.getPacketID(), this.mutex);
            }
            if (eMMessage.getChatType() == ChatType.GroupChat) {
                message.setType(Message.Type.groupchat);
                message.setTo(this.muc.getRoom());
                EMLog.d(TAG, "send message to muc:" + this.muc.getRoom());
                this.muc.sendMessage(message);
            } else {
                this.chat.sendMessage(message);
            }
            if (chatOptions.getRequireServerAck()) {
                EMLog.d(TAG, "wait for server ack...");
                synchronized (this.mutex) {
                    this.mutex.wait(60000);
                }
                EMLog.d(TAG, "exit from wait");
                if (sendLocks.remove(message.getPacketID()) != null) {
                    EMLog.e(TAG, "did not receive ack from server for msg:" + message.getPacketID());
                    eMMessage.status = Status.FAIL;
                    updateMsgState(eMMessage);
                    if (this.callback != null) {
                        this.callback.onError(-2, "no response from server");
                        return;
                    }
                    return;
                }
            }
            eMMessage.msgId = message.getPacketID();
            eMMessage.status = Status.SUCCESS;
            updateMsgState(eMMessage);
            if (this.callback != null) {
                this.callback.onSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            eMMessage.status = Status.FAIL;
            updateMsgState(eMMessage);
            if (this.callback != null) {
                this.callback.onError(-2, e.toString());
            }
        }
    }

    private void updateMsgState(EMMessage eMMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", new StringBuilder(String.valueOf(eMMessage.status.ordinal())).toString());
        EMChatDB.getInstance().updateMessage(eMMessage.msgId, contentValues);
    }

    private Map<String, String> uploadEncryptedThumbnailImage(File file, EMMessage eMMessage) {
        final Map<String, String> hashMap = new HashMap();
        String thumbnailImage = ImageUtils.getThumbnailImage(file.getAbsolutePath(), 100);
        if (EMChatManager.getInstance().getChatOptions().getUseEncryption()) {
            thumbnailImage = EMEncryptUtils.encryptFile(thumbnailImage, eMMessage.getTo());
        }
        HttpFileManager httpFileManager = new HttpFileManager(EMChatConfig.getInstance().applicationContext, EMChatConfig.USER_SERVER);
        String baseUrlByAppKey = getBaseUrlByAppKey();
        Map hashMap2 = new HashMap();
        hashMap2.put("restrict-access", "true");
        hashMap2.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
        httpFileManager.uploadFile(thumbnailImage, baseUrlByAppKey, EMChatConfig.getInstance().APPKEY, null, hashMap2, new CloudOperationCallback() {
            public void onError(String str) {
                EMLog.e(SendMessageThread.TAG, "encrypted thumbnail upload error:" + str);
            }

            public void onProgress(int i) {
            }

            public void onSuccess(String str) {
                EMLog.d(SendMessageThread.TAG, "encrypted thumbnail uploaded");
                Object obj = "";
                Object obj2 = "";
                try {
                    JSONObject jSONObject = new JSONObject(str).getJSONArray("entities").getJSONObject(0);
                    obj = jSONObject.getString("uuid");
                    if (jSONObject.has("share-secret")) {
                        obj2 = jSONObject.getString("share-secret");
                    }
                } catch (Exception e) {
                }
                hashMap.put("uuid", obj);
                hashMap.put(MessageEncoder.ATTR_SECRET, obj2);
            }
        });
        return hashMap;
    }

    public void run() {
        this.msg.status = Status.INPROGRESS;
        switch ($SWITCH_TABLE$com$easemob$chat$EMMessage$Type()[this.msg.type.ordinal()]) {
            case 1:
            case 4:
                sendMessageXmpp(this.msg);
                return;
            case 2:
                sendImageMessage(this.msg, this.callback);
                return;
            case 5:
                sendFileMessage(this.msg, this.callback);
                return;
            default:
                EMLog.e(TAG, "unsupport msg type, need to check:" + this.msg.type);
                return;
        }
    }
}
