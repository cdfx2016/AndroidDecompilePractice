package com.mob.tools;

import com.mob.tools.log.NLog;

public class MobLog extends NLog {
    private MobLog() {
    }

    protected String getSDKTag() {
        return "MOBTOOLS";
    }

    public static NLog getInstance() {
        return NLog.getInstanceForSDK("MOBTOOLS", true);
    }
}
