package org.jivesoftware.smack.parsing;

public class ExceptionLoggingCallback extends ParsingExceptionCallback {
    public void handleUnparsablePacket(UnparsablePacket unparsablePacket) throws Exception {
        System.err.print("Smack message parsing exception: " + unparsablePacket.getParsingException().getMessage());
        unparsablePacket.getParsingException().printStackTrace();
        System.err.println("Unparsed content: " + unparsablePacket.getContent());
    }
}
