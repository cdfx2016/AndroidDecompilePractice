package com.easemob.chat;

import android.content.ContentValues;
import com.easemob.chat.EMMessage.Status;
import com.easemob.chat.EMMessage.Type;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.EMLog;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

class ReceiveMessageThread implements Runnable {
    private static final String TAG = "receiver";
    private boolean encrypted = false;
    private EMMessage msg;
    private FileMessageBody msgbody;

    public ReceiveMessageThread(EMMessage eMMessage) {
        this.msg = eMMessage;
        this.msgbody = (FileMessageBody) eMMessage.body;
    }

    public ReceiveMessageThread(EMMessage eMMessage, boolean z) {
        this.msg = eMMessage;
        this.msgbody = (FileMessageBody) eMMessage.body;
        this.encrypted = z;
    }

    public void run() {
        this.msg.status = Status.INPROGRESS;
        try {
            String str;
            String str2 = this.msgbody.localUrl;
            String str3 = this.msgbody.remoteUrl;
            if (this.msg.type == Type.IMAGE) {
                str = ((ImageMessageBody) this.msgbody).thumbnailUrl;
            } else {
                if (this.msg.type != Type.VOICE) {
                    Type type = this.msg.type;
                    type = Type.VIDEO;
                }
                str = str3;
            }
            str3 = this.msg.type == Type.IMAGE ? str2.substring(0, str2.lastIndexOf("/") + 1) + "th" + str2.substring(str2.lastIndexOf("/") + 1, str2.length()) : str2;
            HttpFileManager httpFileManager = new HttpFileManager(EMChatConfig.getInstance().applicationContext, EMChatConfig.USER_SERVER);
            EMLog.d(TAG, "localUrl:" + this.msgbody.localUrl + " remoteurl:" + str + " localThumb:" + str3);
            Map hashMap = new HashMap();
            hashMap.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
            hashMap.put("Accept", "application/octet-stream");
            if (this.msgbody.secret != null) {
                hashMap.put("share-secret", this.msgbody.secret);
            }
            if (this.msg.type == Type.IMAGE) {
                hashMap.put("thumbnail", "true");
            }
            httpFileManager.downloadFile(str, str3, hashMap, new CloudOperationCallback() {
                public void onError(String str) {
                    ReceiveMessageThread.this.msg.status = Status.FAIL;
                    ReceiveMessageThread.this.updateMsgState();
                    if (ReceiveMessageThread.this.msgbody.downloadCallback != null) {
                        ReceiveMessageThread.this.msgbody.downloadCallback.onError(-1, str);
                    }
                }

                public void onProgress(int i) {
                    ReceiveMessageThread.this.msg.progress = i;
                    if (ReceiveMessageThread.this.msgbody.downloadCallback != null) {
                        ReceiveMessageThread.this.msgbody.downloadCallback.onProgress(i, null);
                    }
                }

                public void onSuccess(String str) {
                    File file = new File(str3);
                    EMLog.d(ReceiveMessageThread.TAG, "file downloaded:" + str3 + " size:" + file.length());
                    if (ReceiveMessageThread.this.encrypted) {
                        EMEncryptUtils.decryptFile(file.getAbsolutePath(), ReceiveMessageThread.this.msg.getFrom());
                    }
                    ReceiveMessageThread.this.msgbody.downloaded = true;
                    ReceiveMessageThread.this.msg.status = Status.SUCCESS;
                    ReceiveMessageThread.this.updateMsgState();
                    ReceiveMessageThread.this.msg.progress = 100;
                    if (ReceiveMessageThread.this.msgbody.downloadCallback != null) {
                        ReceiveMessageThread.this.msgbody.downloadCallback.onProgress(100, null);
                        ReceiveMessageThread.this.msgbody.downloadCallback.onSuccess();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            this.msg.status = Status.FAIL;
            updateMsgState();
            this.msgbody.downloaded = false;
            if (this.msgbody.downloadCallback != null) {
                this.msgbody.downloadCallback.onError(-1, e.toString());
            }
        }
    }

    protected void updateMsgState() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", Integer.valueOf(this.msg.status.ordinal()));
        EMChatDB.getInstance().updateMessage(this.msg.getMsgId(), contentValues);
    }
}
