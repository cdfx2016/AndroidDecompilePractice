package com.easemob;

public interface EMCallBack {
    public static final int ERROR_EXCEPTION = -1;
    public static final int ERROR_FILE_NOT_FOUND = -3;
    public static final int ERROR_SEND = -2;
    public static final Object data = null;

    void onError(int i, String str);

    void onProgress(int i, String str);

    void onSuccess();
}
