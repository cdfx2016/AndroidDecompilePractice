package com.easemob.chat;

import com.easemob.util.EMLog;
import java.util.ArrayList;
import java.util.List;

public class EMCmdManager {
    private static final String TAG = "cmdmgr";
    private static EMCmdManager instance = null;
    List<CmdMsgProcessor> cmdProcessors;

    private EMCmdManager() {
        this.cmdProcessors = null;
        this.cmdProcessors = new ArrayList();
        addCmdProcessor(new SwipeCmdProcessor());
    }

    public static EMCmdManager getInstance() {
        if (instance == null) {
            instance = new EMCmdManager();
        }
        return instance;
    }

    public void addCmdProcessor(CmdMsgProcessor cmdMsgProcessor) {
        EMLog.d(TAG, "add cmd processor for action:" + cmdMsgProcessor.getAction() + " cls:" + cmdMsgProcessor.getClass().getName());
        this.cmdProcessors.add(cmdMsgProcessor);
    }

    public void processCmd(EMMessage eMMessage) {
        CmdMessageBody cmdMessageBody = (CmdMessageBody) eMMessage.body;
        EMLog.d(TAG, "process cmd msg. action:" + cmdMessageBody.action + " params:" + cmdMessageBody.params);
        for (int i = 0; i < this.cmdProcessors.size(); i++) {
            CmdMsgProcessor cmdMsgProcessor = (CmdMsgProcessor) this.cmdProcessors.get(i);
            if (cmdMsgProcessor.getAction().equals(cmdMessageBody.action)) {
                EMLog.d(TAG, "process cmd action:" + cmdMessageBody.action + " with processor:" + cmdMsgProcessor.getClass().getName());
                try {
                    if (cmdMsgProcessor.processCmd(eMMessage)) {
                        EMLog.d(TAG, "process cmd return true. stop");
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }
}
