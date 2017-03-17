package cn.finalteam.toolsfinal;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import cn.finalteam.toolsfinal.io.FileUtils;
import com.alibaba.fastjson.asm.Opcodes;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    public static byte[] bitmapToByte(Bitmap b) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static String bitmapToString(Bitmap bitmap) {
        return Base64.encodeToString(bitmapToByte(bitmap), 0);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, ((float) newWidth) / ((float) org.getWidth()), ((float) newHeight) / ((float) org.getHeight()));
    }

    public static Bitmap scaleImage(Bitmap src, float scaleWidth, float scaleHeight) {
        if (src == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0);
        canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitmap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        return newBitMap;
    }

    public static void saveBitmap(Bitmap bitmap, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap, int quality, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (((long) baos.toByteArray().length) > maxSize) {
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
        if (needRecycle) {
            bitmap.recycle();
        }
        return bm;
    }

    public static Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, int targetWidth, int targeHeight) {
        float scaleWidth = ((float) targetWidth) / ((float) bitmap.getWidth());
        float scaleHeight = ((float) targeHeight) / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        return bm;
    }

    public static Bitmap compressBitmap(String imageFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        return compress(imageFile, null, false, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    private static Bitmap compress(String imageFile, String targetFile, boolean isSave, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        int inSampleSize = 1;
        while (sourceWidth / inSampleSize > targetWidth) {
            inSampleSize++;
        }
        while (sourceHeight / inSampleSize > targeHeight) {
            inSampleSize++;
        }
        if (inSampleSize <= 0) {
            inSampleSize = 1;
        }
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = compressBitmap(BitmapFactory.decodeFile(imageFile, options), false, targetWidth, targeHeight);
        if (qualityCompress) {
            bitmap = compressBitmap(bitmap, true, maxSize);
        }
        if (isSave) {
            String savePath = imageFile;
            if (!StringUtils.isEmpty(targetFile)) {
                savePath = targetFile;
            }
            saveBitmap(bitmap, new File(savePath));
        }
        return bitmap;
    }

    public static void compressImage(String imageFile, String targetFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        compress(imageFile, targetFile, true, qualityCompress, maxSize, targetWidth, targeHeight).recycle();
    }

    public static void compressImage(String imageFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        compressImage(imageFile, null, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    public static void compressImage(String imageFile, int targetWidth, int targeHeight) {
        compressImage(imageFile, null, false, 0, targetWidth, targeHeight);
    }

    public static Bitmap compressBitmap(String imageFile, int targetWidth, int targeHeight) {
        return compressBitmap(imageFile, false, 0, targetWidth, targeHeight);
    }

    public static void compressImageSmall(String imageFile, int scale) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        compressImage(imageFile, options.outWidth / scale, options.outHeight / scale);
    }

    public static Bitmap compressBitmapSmall(String imageFile, int scale) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        return compressBitmap(imageFile, options.outWidth / scale, options.outHeight / scale);
    }

    public static void compressImageBig(String imageFile, int scale) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        compressImage(imageFile, options.outWidth * scale, options.outHeight * scale);
    }

    public static Bitmap compressBitmapBig(String imageFile, int scale) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        return compressBitmap(imageFile, options.outWidth * scale, options.outHeight * scale);
    }

    public static void compressImage(String imageFile, String targetFile, boolean qualityCompress, long maxSize) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        compressImage(imageFile, targetFile, qualityCompress, maxSize, options.outWidth / 2, options.outHeight / 2);
    }

    public static void compressImage(String imageFile, boolean qualityCompress, long maxSize) {
        compressImage(imageFile, null, qualityCompress, maxSize);
    }

    public static Bitmap compressBitmap(String imageFile, boolean qualityCompress, long maxSize) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        return compressBitmap(imageFile, qualityCompress, maxSize, options.outWidth / 2, options.outHeight / 2);
    }

    public static void compressImage(String imageFile, long maxSize) {
        compressImage(imageFile, true, maxSize);
    }

    public static Bitmap compressBimap(String imageFile, long maxSize) {
        return compressBitmap(imageFile, true, maxSize);
    }

    public static void compressImage(String imageFile) {
        compressImage(imageFile, true, (long) FileUtils.ONE_MB);
    }

    public static Bitmap compressBitmap(String imageFile) {
        return compressBitmap(imageFile, true, (long) FileUtils.ONE_MB);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress, boolean needRecycle) {
        Matrix m = new Matrix();
        m.postRotate((float) degress);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        return bm;
    }

    public static final int getDegress(String path) {
        try {
            switch (new ExifInterface(path).getAttributeInt("Orientation", 1)) {
                case 3:
                    return Opcodes.GETFIELD;
                case 6:
                    return 90;
                case 8:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
