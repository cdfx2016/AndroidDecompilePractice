package com.easemob.cloud;

public interface CloudOperationCallback {
    void onError(String str);

    void onProgress(int i);

    void onSuccess(String str);
}
