package com.easemob.chat;

import android.content.Intent;

public interface OnNotificationClickListener {
    Intent onNotificationClick(EMMessage eMMessage);
}
