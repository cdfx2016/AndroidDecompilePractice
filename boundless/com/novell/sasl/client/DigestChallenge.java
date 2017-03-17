package com.novell.sasl.client;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.harmony.javax.security.sasl.SaslException;

class DigestChallenge {
    private static final int CIPHER_3DES = 1;
    private static final int CIPHER_DES = 2;
    private static final int CIPHER_RC4 = 8;
    private static final int CIPHER_RC4_40 = 4;
    private static final int CIPHER_RC4_56 = 16;
    private static final int CIPHER_RECOGNIZED_MASK = 31;
    private static final int CIPHER_UNRECOGNIZED = 32;
    public static final int QOP_AUTH = 1;
    public static final int QOP_AUTH_CONF = 4;
    public static final int QOP_AUTH_INT = 2;
    public static final int QOP_UNRECOGNIZED = 8;
    private String m_algorithm = null;
    private String m_characterSet = null;
    private int m_cipherOptions = 0;
    private int m_maxBuf = -1;
    private String m_nonce = null;
    private int m_qop = 0;
    private ArrayList m_realms = new ArrayList(5);
    private boolean m_staleFlag = false;

    DigestChallenge(byte[] bArr) throws SaslException {
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
            String name = parsedDirective.getName();
            if (name.equals("realm")) {
                handleRealm(parsedDirective);
            } else if (name.equals("nonce")) {
                handleNonce(parsedDirective);
            } else if (name.equals("qop")) {
                handleQop(parsedDirective);
            } else if (name.equals("maxbuf")) {
                handleMaxbuf(parsedDirective);
            } else if (name.equals("charset")) {
                handleCharset(parsedDirective);
            } else if (name.equals("algorithm")) {
                handleAlgorithm(parsedDirective);
            } else if (name.equals("cipher")) {
                handleCipher(parsedDirective);
            } else if (name.equals("stale")) {
                handleStale(parsedDirective);
            }
        }
        if (-1 == this.m_maxBuf) {
            this.m_maxBuf = 65536;
        }
        if (this.m_qop == 0) {
            this.m_qop = 1;
        } else if ((this.m_qop & 1) != 1) {
            throw new SaslException("Only qop-auth is supported by client");
        } else if ((this.m_qop & 4) == 4 && (this.m_cipherOptions & 31) == 0) {
            throw new SaslException("Invalid cipher options");
        } else if (this.m_nonce == null) {
            throw new SaslException("Missing nonce directive");
        } else if (this.m_staleFlag) {
            throw new SaslException("Unexpected stale flag");
        } else if (this.m_algorithm == null) {
            throw new SaslException("Missing algorithm directive");
        }
    }

    public String getAlgorithm() {
        return this.m_algorithm;
    }

    public String getCharacterSet() {
        return this.m_characterSet;
    }

    public int getCipherOptions() {
        return this.m_cipherOptions;
    }

    public int getMaxBuf() {
        return this.m_maxBuf;
    }

    public String getNonce() {
        return this.m_nonce;
    }

    public int getQop() {
        return this.m_qop;
    }

    public ArrayList getRealms() {
        return this.m_realms;
    }

    public boolean getStaleFlag() {
        return this.m_staleFlag;
    }

    void handleAlgorithm(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_algorithm != null) {
            throw new SaslException("Too many algorithm directives.");
        }
        this.m_algorithm = parsedDirective.getValue();
        if (!"md5-sess".equals(this.m_algorithm)) {
            throw new SaslException("Invalid algorithm directive value: " + this.m_algorithm);
        }
    }

    void handleCharset(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_characterSet != null) {
            throw new SaslException("Too many charset directives.");
        }
        this.m_characterSet = parsedDirective.getValue();
        if (!this.m_characterSet.equals("utf-8")) {
            throw new SaslException("Invalid character encoding directive");
        }
    }

    void handleCipher(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_cipherOptions != 0) {
            throw new SaslException("Too many cipher directives.");
        }
        TokenParser tokenParser = new TokenParser(parsedDirective.getValue());
        tokenParser.parseToken();
        for (Object parseToken = tokenParser.parseToken(); parseToken != null; parseToken = tokenParser.parseToken()) {
            if ("3des".equals(parseToken)) {
                this.m_cipherOptions |= 1;
            } else if ("des".equals(parseToken)) {
                this.m_cipherOptions |= 2;
            } else if ("rc4-40".equals(parseToken)) {
                this.m_cipherOptions |= 4;
            } else if ("rc4".equals(parseToken)) {
                this.m_cipherOptions |= 8;
            } else if ("rc4-56".equals(parseToken)) {
                this.m_cipherOptions |= 16;
            } else {
                this.m_cipherOptions |= 32;
            }
        }
        if (this.m_cipherOptions == 0) {
            this.m_cipherOptions = 32;
        }
    }

    void handleMaxbuf(ParsedDirective parsedDirective) throws SaslException {
        if (-1 != this.m_maxBuf) {
            throw new SaslException("Too many maxBuf directives.");
        }
        this.m_maxBuf = Integer.parseInt(parsedDirective.getValue());
        if (this.m_maxBuf == 0) {
            throw new SaslException("Max buf value must be greater than zero.");
        }
    }

    void handleNonce(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_nonce != null) {
            throw new SaslException("Too many nonce values.");
        }
        this.m_nonce = parsedDirective.getValue();
    }

    void handleQop(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_qop != 0) {
            throw new SaslException("Too many qop directives.");
        }
        TokenParser tokenParser = new TokenParser(parsedDirective.getValue());
        for (String parseToken = tokenParser.parseToken(); parseToken != null; parseToken = tokenParser.parseToken()) {
            if (parseToken.equals("auth")) {
                this.m_qop |= 1;
            } else if (parseToken.equals("auth-int")) {
                this.m_qop |= 2;
            } else if (parseToken.equals("auth-conf")) {
                this.m_qop |= 4;
            } else {
                this.m_qop |= 8;
            }
        }
    }

    void handleRealm(ParsedDirective parsedDirective) {
        this.m_realms.add(parsedDirective.getValue());
    }

    void handleStale(ParsedDirective parsedDirective) throws SaslException {
        if (this.m_staleFlag) {
            throw new SaslException("Too many stale directives.");
        } else if ("true".equals(parsedDirective.getValue())) {
            this.m_staleFlag = true;
        } else {
            throw new SaslException("Invalid stale directive value: " + parsedDirective.getValue());
        }
    }
}
