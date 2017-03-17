package com.mob.commons;

public class SMSSDK implements MobProduct {
    private static String appKey;

    public static void setAppKey(String str) {
        appKey = str;
    }

    public String getProductTag() {
        return "SMSSDK";
    }

    public String getProductAppkey() {
        return appKey;
    }

    public int getSdkver() {
        return 26;
    }
}
