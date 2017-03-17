package cn.finalteam.toolsfinal.coder;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.nio.charset.Charset;

public class HexCoder {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final Charset charset;

    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[(len >> 1)];
        int i = 0;
        int j = 0;
        while (j < len) {
            j++;
            j++;
            out[i] = (byte) (((toDigit(data[j], j) << 4) | toDigit(data[j], j)) & 255);
            i++;
        }
        return out;
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j + 1;
            out[j] = toDigits[(data[i] & PsExtractor.VIDEO_STREAM_MASK) >>> 4];
            j = i2 + 1;
            out[i2] = toDigits[data[i] & 15];
        }
        return out;
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(char ch, int index) throws RuntimeException {
        int digit = Character.digit(ch, 16);
        if (digit != -1) {
            return digit;
        }
        throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
    }

    public HexCoder() {
        this.charset = DEFAULT_CHARSET;
    }

    public HexCoder(Charset charset) {
        this.charset = charset;
    }

    public HexCoder(String charsetName) {
        this(Charset.forName(charsetName));
    }

    public byte[] decode(byte[] array) {
        return decodeHex(new String(array, getCharset()).toCharArray());
    }

    public Object decode(Object object) {
        try {
            return decodeHex(object instanceof String ? ((String) object).toCharArray() : (char[]) object);
        } catch (ClassCastException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] encode(byte[] array) {
        return encodeHexString(array).getBytes(getCharset());
    }

    public Object encode(Object object) {
        try {
            return encodeHex(object instanceof String ? ((String) object).getBytes(getCharset()) : (byte[]) object);
        } catch (ClassCastException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        return this.charset.name();
    }

    public String toString() {
        return super.toString() + "[charsetName=" + this.charset + "]";
    }
}
