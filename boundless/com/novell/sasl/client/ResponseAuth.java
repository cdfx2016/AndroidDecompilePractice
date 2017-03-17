package com.novell.sasl.client;

import java.util.Iterator;
import org.apache.harmony.javax.security.sasl.SaslException;

class ResponseAuth {
    private String m_responseValue = null;

    ResponseAuth(byte[] bArr) throws SaslException {
        DirectiveList directiveList = new DirectiveList(bArr);
        try {
            directiveList.parseDirectives();
            checkSemantics(directiveList);
        } catch (SaslException e) {
        }
    }

    void checkSemantics(DirectiveList directiveList) throws SaslException {
        Iterator iterator = directiveList.getIterator();
        while (iterator.hasNext()) {
            ParsedDirective parsedDirective = (ParsedDirective) iterator.next();
            if (parsedDirective.getName().equals("rspauth")) {
                this.m_responseValue = parsedDirective.getValue();
            }
        }
        if (this.m_responseValue == null) {
            throw new SaslException("Missing response-auth directive.");
        }
    }

    public String getResponseValue() {
        return this.m_responseValue;
    }
}
