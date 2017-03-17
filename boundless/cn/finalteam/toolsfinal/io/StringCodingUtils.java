package cn.finalteam.toolsfinal.io;

import android.os.Build.VERSION;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringCodingUtils {
    public static byte[] getBytes(String src, Charset charSet) {
        if (VERSION.SDK_INT >= 9) {
            return src.getBytes(charSet);
        }
        try {
            return src.getBytes(charSet.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
