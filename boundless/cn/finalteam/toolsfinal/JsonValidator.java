package cn.finalteam.toolsfinal;

import cn.finalteam.toolsfinal.io.FilenameUtils;
import cn.finalteam.toolsfinal.io.IOUtils;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class JsonValidator {
    private char c;
    private int col;
    private CharacterIterator it;

    public boolean validate(String input) {
        return valid(input.trim());
    }

    private boolean valid(String input) {
        if ("".equals(input)) {
            return true;
        }
        this.it = new StringCharacterIterator(input);
        this.c = this.it.first();
        this.col = 1;
        if (!value()) {
            return false;
        }
        skipWhiteSpace();
        if (this.c != '￿') {
            return false;
        }
        return true;
    }

    private boolean value() {
        return literal("true") || literal("false") || literal("null") || string() || number() || object() || array();
    }

    private boolean literal(String text) {
        CharacterIterator ci = new StringCharacterIterator(text);
        if (this.c != ci.first()) {
            return false;
        }
        int start = this.col;
        boolean ret = true;
        for (char t = ci.next(); t != '￿'; t = ci.next()) {
            if (t != nextCharacter()) {
                ret = false;
                break;
            }
        }
        nextCharacter();
        return !ret ? ret : ret;
    }

    private boolean array() {
        return aggregate('[', ']', false);
    }

    private boolean object() {
        return aggregate('{', '}', true);
    }

    private boolean aggregate(char entryCharacter, char exitCharacter, boolean prefix) {
        if (this.c != entryCharacter) {
            return false;
        }
        nextCharacter();
        skipWhiteSpace();
        if (this.c == exitCharacter) {
            nextCharacter();
            return true;
        }
        while (true) {
            if (prefix) {
                int i = this.col;
                if (!string()) {
                    return false;
                }
                skipWhiteSpace();
                if (this.c != ':') {
                    return false;
                }
                nextCharacter();
                skipWhiteSpace();
            }
            if (value()) {
                skipWhiteSpace();
                if (this.c != ',') {
                    break;
                }
                nextCharacter();
                skipWhiteSpace();
            } else {
                return false;
            }
        }
        if (this.c != exitCharacter) {
            return false;
        }
        nextCharacter();
        return true;
    }

    private boolean number() {
        if (!Character.isDigit(this.c) && this.c != '-') {
            return false;
        }
        int start = this.col;
        if (this.c == '-') {
            nextCharacter();
        }
        if (this.c == '0') {
            nextCharacter();
        } else if (!Character.isDigit(this.c)) {
            return false;
        } else {
            while (Character.isDigit(this.c)) {
                nextCharacter();
            }
        }
        if (this.c == FilenameUtils.EXTENSION_SEPARATOR) {
            nextCharacter();
            if (!Character.isDigit(this.c)) {
                return false;
            }
            while (Character.isDigit(this.c)) {
                nextCharacter();
            }
        }
        if (this.c == 'e' || this.c == 'E') {
            nextCharacter();
            if (this.c == '+' || this.c == '-') {
                nextCharacter();
            }
            if (!Character.isDigit(this.c)) {
                return false;
            }
            while (Character.isDigit(this.c)) {
                nextCharacter();
            }
        }
        return true;
    }

    private boolean string() {
        if (this.c != '\"') {
            return false;
        }
        boolean escaped = false;
        nextCharacter();
        while (this.c != '￿') {
            if (!escaped && this.c == IOUtils.DIR_SEPARATOR_WINDOWS) {
                escaped = true;
            } else if (escaped) {
                if (!escape()) {
                    return false;
                }
                escaped = false;
            } else if (this.c == '\"') {
                nextCharacter();
                return true;
            }
            nextCharacter();
        }
        return false;
    }

    private boolean escape() {
        if (" \\\"/bfnrtu".indexOf(this.c) < 0) {
            return false;
        }
        if (this.c != 'u' || (ishex(nextCharacter()) && ishex(nextCharacter()) && ishex(nextCharacter()) && ishex(nextCharacter()))) {
            return true;
        }
        return false;
    }

    private boolean ishex(char d) {
        return "0123456789abcdefABCDEF".indexOf(this.c) >= 0;
    }

    private char nextCharacter() {
        this.c = this.it.next();
        this.col++;
        return this.c;
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(this.c)) {
            nextCharacter();
        }
    }
}
