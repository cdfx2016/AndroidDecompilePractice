package cn.finalteam.galleryfinal.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import com.fanyu.boundless.util.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class Utils {
    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(FileUtil.FILE_EXTENSION_SEPARATOR);
        if (start == -1 || end == -1) {
            return null;
        }
        return pathandname.substring(start + 1, end);
    }

    public static void saveBitmap(Bitmap bitmap, CompressFormat format, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(format, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateBitmap(String path, int orientation, int screenWidth, int screenHeight) {
        Bitmap bitmap = null;
        int maxWidth = screenWidth / 2;
        int maxHeight = screenHeight / 2;
        try {
            int sourceWidth;
            int sourceHeight;
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (orientation == 90 || orientation == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }
            boolean compress = false;
            if (sourceWidth > maxWidth || sourceHeight > maxHeight) {
                float widthRatio = ((float) sourceWidth) / ((float) maxWidth);
                float heightRatio = ((float) sourceHeight) / ((float) maxHeight);
                options.inJustDecodeBounds = false;
                if (new File(path).length() > 512000) {
                    options.inSampleSize = (int) Math.max(widthRatio, heightRatio);
                    compress = true;
                }
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate((float) orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if ((sourceWidth > maxWidth || sourceHeight > maxHeight) && compress) {
                float maxRatio = Math.max(((float) sourceWidth) / ((float) maxWidth), ((float) sourceHeight) / ((float) maxHeight));
                Bitmap bm = Bitmap.createScaledBitmap(bitmap, (int) (((float) sourceWidth) / maxRatio), (int) (((float) sourceHeight) / maxRatio), true);
                bitmap.recycle();
                return bm;
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    public static int getRandom(int min, int max) {
        return (new Random().nextInt(max) % ((max - min) + 1)) + min;
    }
}
