package com.xiaomi.channel.commonutils.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import com.xiaomi.channel.commonutils.logger.b;

public class f {
    public static Account a(Context context) {
        try {
            Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
            return (accountsByType == null || accountsByType.length <= 0) ? null : accountsByType[0];
        } catch (Exception e) {
            b.d(e.toString());
            return null;
        }
    }
}
