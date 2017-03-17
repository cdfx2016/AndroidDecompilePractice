package com.novell.sasl.client;

import cn.finalteam.toolsfinal.io.IOUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.harmony.javax.security.sasl.SaslException;

class DirectiveList {
    private static final int STATE_LOOKING_FOR_COMMA = 6;
    private static final int STATE_LOOKING_FOR_DIRECTIVE = 2;
    private static final int STATE_LOOKING_FOR_EQUALS = 4;
    private static final int STATE_LOOKING_FOR_FIRST_DIRECTIVE = 1;
    private static final int STATE_LOOKING_FOR_VALUE = 5;
    private static final int STATE_NO_UTF8_SUPPORT = 9;
    private static final int STATE_SCANNING_NAME = 3;
    private static final int STATE_SCANNING_QUOTED_STRING_VALUE = 7;
    private static final int STATE_SCANNING_TOKEN_VALUE = 8;
    private String m_curName;
    private int m_curPos = 0;
    private ArrayList m_directiveList = new ArrayList(10);
    private String m_directives;
    private int m_errorPos = -1;
    private int m_scanStart = 0;
    private int m_state = 1;

    DirectiveList(byte[] bArr) {
        try {
            this.m_directives = new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.m_state = 9;
        }
    }

    void addDirective(String str, boolean z) {
        String str2;
        if (z) {
            StringBuffer stringBuffer = new StringBuffer(this.m_curPos - this.m_scanStart);
            int i = 0;
            int i2 = this.m_scanStart;
            while (i2 < this.m_curPos) {
                if (IOUtils.DIR_SEPARATOR_WINDOWS == this.m_directives.charAt(i2)) {
                    i2++;
                }
                stringBuffer.setCharAt(i, this.m_directives.charAt(i2));
                i++;
                i2++;
            }
            str2 = new String(stringBuffer);
        } else {
            str2 = this.m_directives.substring(this.m_scanStart, this.m_curPos);
        }
        this.m_directiveList.add(new ParsedDirective(str, str2, this.m_state == 7 ? 1 : 2));
    }

    Iterator getIterator() {
        return this.m_directiveList.iterator();
    }

    boolean isValidTokenChar(char c) {
        return (c < '\u0000' || c > ' ') && ((c < ':' || c > '@') && !((c >= '[' && c <= ']') || ',' == c || '%' == c || '(' == c || ')' == c || '{' == c || '}' == c || '' == c));
    }

    boolean isWhiteSpace(char c) {
        return '\t' == c || '\n' == c || '\r' == c || ' ' == c;
    }

    void parseDirectives() throws SaslException {
        String str = "<no name>";
        if (this.m_state == 9) {
            throw new SaslException("No UTF-8 support on platform");
        }
        boolean z = false;
        boolean z2 = false;
        while (this.m_curPos < this.m_directives.length()) {
            boolean charAt = this.m_directives.charAt(this.m_curPos);
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
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Invalid name character");
                    }
                case 3:
                    if (isValidTokenChar(charAt)) {
                        continue;
                    } else if (isWhiteSpace(charAt)) {
                        str = this.m_directives.substring(this.m_scanStart, this.m_curPos);
                        this.m_state = 4;
                        break;
                    } else if (true == charAt) {
                        str = this.m_directives.substring(this.m_scanStart, this.m_curPos);
                        this.m_state = 5;
                        break;
                    } else {
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Invalid name character");
                    }
                case 4:
                    if (isWhiteSpace(charAt)) {
                        continue;
                    } else if (true == charAt) {
                        this.m_state = 5;
                        break;
                    } else {
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Expected equals sign '='.");
                    }
                case 5:
                    if (isWhiteSpace(charAt)) {
                        continue;
                    } else if (true == charAt) {
                        this.m_scanStart = this.m_curPos + 1;
                        this.m_state = 7;
                        break;
                    } else if (isValidTokenChar(charAt)) {
                        this.m_scanStart = this.m_curPos;
                        this.m_state = 8;
                        break;
                    } else {
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Unexpected character");
                    }
                case 6:
                    if (isWhiteSpace(charAt)) {
                        continue;
                    } else if (charAt) {
                        this.m_state = 2;
                        break;
                    } else {
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Expected a comma.");
                    }
                case 7:
                    if (IOUtils.DIR_SEPARATOR_WINDOWS == charAt) {
                        z = true;
                    }
                    if (true == charAt && true != r3) {
                        addDirective(str, z);
                        this.m_state = 6;
                        z = false;
                        break;
                    }
                case 8:
                    if (isValidTokenChar(charAt)) {
                        continue;
                    } else if (isWhiteSpace(charAt)) {
                        addDirective(str, false);
                        this.m_state = 6;
                        break;
                    } else if (true == charAt) {
                        addDirective(str, false);
                        this.m_state = 2;
                        break;
                    } else {
                        this.m_errorPos = this.m_curPos;
                        throw new SaslException("Parse error: Invalid value character");
                    }
                default:
                    break;
            }
            this.m_curPos++;
            z2 = charAt;
        }
        switch (this.m_state) {
            case 2:
                throw new SaslException("Parse error: Trailing comma.");
            case 3:
            case 4:
            case 5:
                throw new SaslException("Parse error: Missing value.");
            case 7:
                throw new SaslException("Parse error: Missing closing quote.");
            case 8:
                addDirective(str, false);
                return;
            default:
                return;
        }
    }
}
