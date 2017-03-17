package com.easemob.chat;

import com.easemob.EMCallBack;

public abstract class FileMessageBody extends MessageBody {
    public transient EMCallBack downloadCallback = null;
    public transient boolean downloaded = false;
    String fileName = null;
    String localUrl = null;
    String remoteUrl = null;
    String secret = null;

    public String getFileName() {
        return this.fileName;
    }

    public String getLocalUrl() {
        return this.localUrl;
    }

    public String getRemoteUrl() {
        return this.remoteUrl;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setDownloadCallback(EMCallBack eMCallBack) {
        if (this.downloaded) {
            eMCallBack.onProgress(100, null);
            eMCallBack.onSuccess();
            return;
        }
        this.downloadCallback = eMCallBack;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setLocalUrl(String str) {
        this.localUrl = str;
    }

    public void setRemoteUrl(String str) {
        this.remoteUrl = str;
    }

    public void setSecret(String str) {
        this.secret = str;
    }
}
