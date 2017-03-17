package org.jivesoftware.smack.parsing;

public class ExceptionThrowingCallback extends ParsingExceptionCallback {
    public void handleUnparsablePacket(UnparsablePacket unparsablePacket) throws Exception {
        throw unparsablePacket.getParsingException();
    }
}
