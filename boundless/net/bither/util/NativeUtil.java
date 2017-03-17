package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.easemob.util.ImageUtils;
import java.io.ByteArrayOutputStream;

public class NativeUtil {
    private static int DEFAULT_QUALITY = 95;

    private static native String compressBitmap(Bitmap bitmap, int i, int i2, int i3, byte[] bArr, boolean z);

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
    }

    public static void compressBitmap(Bitmap bit, String fileName, boolean optimize) {
        saveBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
    }

    public static void compressBitmap(Bitmap image, String filePath) {
        int ratio = getRatioSize(image.getWidth(), image.getHeight());
        Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Config.ARGB_8888);
        new Canvas(result).drawBitmap(image, null, new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        result.compress(CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            result.compress(CompressFormat.JPEG, options, baos);
        }
        saveBitmap(result, options, filePath, true);
        if (result != null && !result.isRecycled()) {
            result.recycle();
        }
    }

    public static int getRatioSize(int bitWidth, int bitHeight) {
        int ratio = 1;
        if (bitWidth > bitHeight && bitWidth > ImageUtils.SCALE_IMAGE_HEIGHT) {
            ratio = bitWidth / ImageUtils.SCALE_IMAGE_HEIGHT;
        } else if (bitWidth < bitHeight && bitHeight > 1280) {
            ratio = bitHeight / 1280;
        }
        if (ratio <= 0) {
            return 1;
        }
        return ratio;
    }

    public static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
        compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
    }
}
