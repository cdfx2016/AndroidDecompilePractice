package com.easemob.chat;

import android.content.Context;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import java.io.File;

class SwipeCmdProcessor implements CmdMsgProcessor {
    private static final String TAG = "SWIPE";
    private static final String action = "swipe";

    SwipeCmdProcessor() {
    }

    private void deleteDir(File file) {
        try {
            if (file.exists()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
                String[] list = file.list();
                for (String file2 : list) {
                    new File(file, file2).delete();
                }
                file.delete();
                EMLog.d(TAG, "deleted " + list.length + " files under:" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAction() {
        return action;
    }

    public boolean processCmd(EMMessage eMMessage) {
        try {
            EMLog.d(TAG, "SWIPE data on the phone...");
            if (eMMessage.getFrom().contains("admin") || eMMessage.getFrom().contains("cloudcode")) {
                Context applicationContext = EMChatConfig.getInstance().getApplicationContext();
                String str = EMSessionManager.getInstance(applicationContext).currentUser.username;
                EMChatManager.getInstance().logout();
                EMLog.d(TAG, "delete msg db return:" + applicationContext.deleteDatabase(new StringBuilder(String.valueOf(str)).append("_emmsg.db").toString()));
                EMLog.d(TAG, "delete user db return:" + applicationContext.deleteDatabase(new StringBuilder(String.valueOf(str)).append("_emuser.db").toString()));
                deleteDir(PathUtil.getInstance().getVoicePath());
                deleteDir(PathUtil.getInstance().getImagePath());
                deleteDir(PathUtil.getInstance().getVideoPath());
                EMLog.d(TAG, "SWIPE data finished");
                return true;
            }
            EMLog.d(TAG, "skip. only admin can requst swipe operation");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
