package com.easemob.chat;

public interface CmdMsgProcessor {
    String getAction();

    boolean processCmd(EMMessage eMMessage);
}
