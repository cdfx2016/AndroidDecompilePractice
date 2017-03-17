package com.novell.sasl.client;

import org.apache.harmony.javax.security.sasl.SaslException;

class TokenParser {
    private static final int STATE_DONE = 6;
    private static final int STATE_LOOKING_FOR_COMMA = 4;
    private static final int STATE_LOOKING_FOR_FIRST_TOKEN = 1;
    private static final int STATE_LOOKING_FOR_TOKEN = 2;
    private static final int STATE_PARSING_ERROR = 5;
    private static final int STATE_SCANNING_TOKEN = 3;
    private int m_curPos = 0;
    private int m_scanStart = 0;
    private int m_state = 1;
    private String m_tokens;

    TokenParser(String str) {
        this.m_tokens = str;
    }

    boolean isValidTokenChar(char c) {
        return (c < '\u0000' || c > ' ') && ((c < ':' || c > '@') && !((c >= '[' && c <= ']') || ',' == c || '%' == c || '(' == c || ')' == c || '{' == c || '}' == c || '' == c));
    }

    boolean isWhiteSpace(char c) {
        return '\t' == c || '\n' == c || '\r' == c || ' ' == c;
    }

    String parseToken() throws SaslException {
        String str = null;
        if (this.m_state == 6) {
            return null;
        }
        while (this.m_curPos < this.m_tokens.length() && str == null) {
            char charAt = this.m_tokens.charAt(this.m_curPos);
            switch (this.m_state) {
                case 1:
                case 2:
                    if (isWhiteSpace(charAt)) {
                        continue;
                    } else if (isValidTokenChar(charAt)) {
                        this.m_scanStart = this.m_curPos;
                        this.m_state = 3;
                        break;
                    } else {
                        this.m_state = 5;
                        throw new SaslException("Invalid token character at position " + this.m_curPos);
                    }
                case 3:
                    if (isValidTokenChar(charAt)) {
                        continue;
                    } else if (isWhiteSpace(charAt)) {
                        str = this.m_tokens.substring(this.m_scanStart, this.m_curPos);
                        this.m_state = 4;
                        break;
                    } else if (',' == charAt) {
                        str = this.m_tokens.substring(this.m_scanStart, this.m_curPos);
                        this.m_state = 2;
                        break;
                    } else {
                        this.m_state = 5;
                        throw new SaslException("Invalid token character at position " + this.m_curPos);
                    }
                case 4:
                    if (isWhiteSpace(charAt)) {
                        continue;
                    } else if (charAt == ',') {
                        this.m_state = 2;
                        break;
                    } else {
                        this.m_state = 5;
                        throw new SaslException("Expected a comma, found '" + charAt + "' at postion " + this.m_curPos);
                    }
                default:
                    break;
            }
            this.m_curPos++;
        }
        if (str != null) {
            return str;
        }
        switch (this.m_state) {
            case 1:
            case 4:
                return str;
            case 2:
                throw new SaslException("Trialing comma");
            case 3:
                str = this.m_tokens.substring(this.m_scanStart);
                this.m_state = 6;
                return str;
            default:
                return str;
        }
    }
}
