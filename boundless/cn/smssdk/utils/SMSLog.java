package cn.smssdk.utils;

import android.content.Context;
import com.mob.commons.logcollector.LogsCollector;
import com.mob.tools.log.NLog;

public class SMSLog extends NLog {
    private SMSLog(Context context, final int i, final String str) {
        NLog.setCollector("SMSSDK", new LogsCollector(this, context) {
            final /* synthetic */ SMSLog c;

            protected int getSDKVersion() {
                return i;
            }

            protected String getSDKTag() {
                return "SMSSDK";
            }

            protected String getAppkey() {
                return str;
            }
        });
    }

    protected String getSDKTag() {
        return "SMSSDK";
    }

    public static NLog prepare(Context context, int i, String str) {
        return new SMSLog(context, i, str);
    }

    public static NLog getInstance() {
        return NLog.getInstanceForSDK("SMSSDK", true);
    }
}
