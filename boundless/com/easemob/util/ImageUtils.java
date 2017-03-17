package com.easemob.util;

import android.content.Context;
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
import android.graphics.RectF;
import android.media.ExifInterface;
import com.alibaba.fastjson.asm.Opcodes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ImageUtils {
    public static final int SCALE_IMAGE_HEIGHT = 960;
    public static final int SCALE_IMAGE_WIDTH = 640;

    public static int calculateInSampleSize(Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        if (i3 <= i2 && i4 <= i) {
            return 1;
        }
        int round = Math.round(((float) i3) / ((float) i2));
        i3 = Math.round(((float) i4) / ((float) i));
        return round > i3 ? round : i3;
    }

    public static Bitmap decodeScaleImage(Context context, int i, int i2, int i3) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), i, options);
        options.inSampleSize = calculateInSampleSize(options, i2, i3);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), i, options);
    }

    public static Bitmap decodeScaleImage(String str, int i, int i2) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        int calculateInSampleSize = calculateInSampleSize(options, i, i2);
        EMLog.d("img", "original wid" + options.outWidth + " original height:" + options.outHeight + " sample:" + calculateInSampleSize);
        options.inSampleSize = calculateInSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap decodeFile = BitmapFactory.decodeFile(str, options);
        int readPictureDegree = readPictureDegree(str);
        if (decodeFile == null || readPictureDegree == 0) {
            return decodeFile;
        }
        Bitmap rotaingImageView = rotaingImageView(readPictureDegree, decodeFile);
        decodeFile.recycle();
        return rotaingImageView;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        return getRoundedCornerBitmap(bitmap, 6.0f);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float f) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static String getScaledImage(Context context, String str) {
        File file = new File(str);
        if (file.exists()) {
            long length = file.length();
            EMLog.d("img", "original img size:" + length);
            if (length <= 102400) {
                EMLog.d("img", "use original small image");
            } else {
                Bitmap decodeScaleImage = decodeScaleImage(str, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT);
                try {
                    File createTempFile = File.createTempFile("image", ".jpg", context.getFilesDir());
                    OutputStream fileOutputStream = new FileOutputStream(createTempFile);
                    decodeScaleImage.compress(CompressFormat.JPEG, 60, fileOutputStream);
                    fileOutputStream.close();
                    EMLog.d("img", "compared to small fle" + createTempFile.getAbsolutePath() + " size:" + createTempFile.length());
                    str = createTempFile.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static String getScaledImage(Context context, String str, int i) {
        File file = new File(str);
        if (file.exists()) {
            long length = file.length();
            EMLog.d("img", "original img size:" + length);
            if (length > 102400) {
                Bitmap decodeScaleImage = decodeScaleImage(str, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT);
                try {
                    File file2 = new File(context.getExternalCacheDir(), "eaemobTemp" + i + ".jpg");
                    OutputStream fileOutputStream = new FileOutputStream(file2);
                    decodeScaleImage.compress(CompressFormat.JPEG, 60, fileOutputStream);
                    fileOutputStream.close();
                    EMLog.d("img", "compared to small fle" + file2.getAbsolutePath() + " size:" + file2.length());
                    str = file2.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static String getThumbnailImage(String str, int i) {
        Bitmap decodeScaleImage = decodeScaleImage(str, i, i);
        try {
            File createTempFile = File.createTempFile("image", ".jpg");
            OutputStream fileOutputStream = new FileOutputStream(createTempFile);
            decodeScaleImage.compress(CompressFormat.JPEG, 60, fileOutputStream);
            fileOutputStream.close();
            EMLog.d("img", "generate thumbnail image at:" + createTempFile.getAbsolutePath() + " size:" + createTempFile.length());
            str = createTempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Bitmap mergeImages(int i, int i2, List<Bitmap> list) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-3355444);
        EMLog.d("img", "merge images to size:" + i + "*" + i2 + " with images:" + list.size());
        int i3 = list.size() <= 4 ? 2 : 3;
        int i4 = (i - 4) / i3;
        int i5 = 0;
        int i6 = 0;
        while (i5 < i3) {
            int i7 = i6;
            for (int i8 = 0; i8 < i3; i8++) {
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap((Bitmap) list.get(i7), i4, i4, true);
                Bitmap roundedCornerBitmap = getRoundedCornerBitmap(createScaledBitmap, 2.0f);
                createScaledBitmap.recycle();
                canvas.drawBitmap(roundedCornerBitmap, (float) ((i8 * i4) + (i8 + 2)), (float) ((i5 * i4) + (i5 + 2)), null);
                roundedCornerBitmap.recycle();
                i7++;
                if (i7 == list.size()) {
                    return createBitmap;
                }
            }
            i5++;
            i6 = i7;
        }
        return createBitmap;
    }

    public static int readPictureDegree(String str) {
        try {
            switch (new ExifInterface(str).getAttributeInt("Orientation", 1)) {
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

    public static Bitmap rotaingImageView(int i, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) i);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
