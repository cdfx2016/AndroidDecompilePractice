package com.mob.tools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import com.fanyu.boundless.util.FileUtil;
import com.mob.tools.MobLog;
import com.mob.tools.network.HttpConnection;
import com.mob.tools.network.HttpResponseCallback;
import com.mob.tools.network.NetworkHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitmapHelper {
    public static Bitmap getBitmap(String path, int inSampleSize) throws Throwable {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getBitmap(new File(path), inSampleSize);
    }

    public static Bitmap getBitmap(File file, int inSampleSize) throws Throwable {
        if (file == null || !file.exists()) {
            return null;
        }
        InputStream fis = new FileInputStream(file);
        Bitmap bm = getBitmap(fis, inSampleSize);
        fis.close();
        return bm;
    }

    public static Bitmap getBitmap(InputStream is, int inSampleSize) {
        if (is == null) {
            return null;
        }
        Options opt = new Options();
        opt.inPreferredConfig = Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = inSampleSize;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap getBitmap(String path) throws Throwable {
        return getBitmap(path, 1);
    }

    public static Bitmap getBitmap(Context context, String url) throws Throwable {
        return getBitmap(downloadBitmap(context, url));
    }

    public static String downloadBitmap(Context context, final String imageUrl) throws Throwable {
        final String path = ResHelper.getCachePath(context, "images");
        File cache = new File(path, Data.MD5(imageUrl));
        if (cache.exists()) {
            return cache.getAbsolutePath();
        }
        final HashMap<String, String> buffer = new HashMap();
        new NetworkHelper().rawGet(imageUrl, new HttpResponseCallback() {
            public void onResponse(HttpConnection conn) throws Throwable {
                int status = conn.getResponseCode();
                if (status == 200) {
                    String name = BitmapHelper.getFileName(conn, imageUrl);
                    File cache = new File(path, name);
                    if (cache.exists()) {
                        buffer.put("bitmap", cache.getAbsolutePath());
                        return;
                    }
                    if (!cache.getParentFile().exists()) {
                        cache.getParentFile().mkdirs();
                    }
                    if (cache.exists()) {
                        cache.delete();
                    }
                    try {
                        Bitmap bitmap = BitmapHelper.getBitmap(new FilterInputStream(conn.getInputStream()) {
                            public long skip(long n) throws IOException {
                                long m = 0;
                                while (m < n) {
                                    long _m = this.in.skip(n - m);
                                    if (_m == 0) {
                                        break;
                                    }
                                    m += _m;
                                }
                                return m;
                            }
                        }, 1);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            FileOutputStream fos = new FileOutputStream(cache);
                            if (name.toLowerCase().endsWith(".gif") || name.toLowerCase().endsWith(".png")) {
                                bitmap.compress(CompressFormat.PNG, 100, fos);
                            } else {
                                bitmap.compress(CompressFormat.JPEG, 80, fos);
                            }
                            fos.flush();
                            fos.close();
                            buffer.put("bitmap", cache.getAbsolutePath());
                        }
                    } catch (Throwable th) {
                        if (cache.exists()) {
                            cache.delete();
                        }
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.forName("utf-8")));
                    for (String txt = br.readLine(); txt != null; txt = br.readLine()) {
                        if (sb.length() > 0) {
                            sb.append('\n');
                        }
                        sb.append(txt);
                    }
                    br.close();
                    HashMap<String, Object> errMap = new HashMap();
                    errMap.put("error", sb.toString());
                    errMap.put("status", Integer.valueOf(status));
                    throw new Throwable(new Hashon().fromHashMap(errMap));
                }
            }
        }, null);
        return (String) buffer.get("bitmap");
    }

    private static String getFileName(HttpConnection conn, String url) throws Throwable {
        List<String> headers;
        String name = null;
        Map<String, List<String>> map = conn.getHeaderFields();
        if (map != null) {
            headers = (List) map.get("Content-Disposition");
            if (headers != null && headers.size() > 0) {
                for (String part : ((String) headers.get(0)).split(";")) {
                    if (part.trim().startsWith(MessageEncoder.ATTR_FILENAME)) {
                        name = part.split("=")[1];
                        if (name.startsWith("\"") && name.endsWith("\"")) {
                            name = name.substring(1, name.length() - 1);
                        }
                    }
                }
            }
        }
        if (name != null) {
            return name;
        }
        name = Data.MD5(url);
        if (map == null) {
            return name;
        }
        headers = (List) map.get("Content-Type");
        if (headers == null || headers.size() <= 0) {
            return name;
        }
        String value = (String) headers.get(0);
        value = value == null ? "" : value.trim();
        if (value.startsWith("image/")) {
            String type = value.substring("image/".length());
            StringBuilder append = new StringBuilder().append(name).append(FileUtil.FILE_EXTENSION_SEPARATOR);
            if ("jpeg".equals(type)) {
                type = "jpg";
            }
            return append.append(type).toString();
        }
        int index = url.lastIndexOf(47);
        String lastPart = null;
        if (index > 0) {
            lastPart = url.substring(index + 1);
        }
        if (lastPart == null || lastPart.length() <= 0) {
            return name;
        }
        int dot = lastPart.lastIndexOf(46);
        if (dot <= 0 || lastPart.length() - dot >= 10) {
            return name;
        }
        return name + lastPart.substring(dot);
    }

    public static String saveViewToImage(View view) throws Throwable {
        if (view == null) {
            return null;
        }
        int width = view.getWidth();
        int height = view.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        return saveViewToImage(view, width, height);
    }

    public static String saveViewToImage(View view, int width, int height) throws Throwable {
        Bitmap bm = captureView(view, width, height);
        if (bm == null || bm.isRecycled()) {
            return null;
        }
        File ss = new File(ResHelper.getCachePath(view.getContext(), "screenshot"), String.valueOf(System.currentTimeMillis()) + ".jpg");
        FileOutputStream fos = new FileOutputStream(ss);
        bm.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        return ss.getAbsolutePath();
    }

    public static Bitmap captureView(View view, int width, int height) throws Throwable {
        Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        view.draw(new Canvas(bm));
        return bm;
    }

    public static Bitmap blur(Bitmap bm, int radius, int scale) {
        int scaledRadius = (int) ((((float) radius) / ((float) scale)) + 0.5f);
        Bitmap overlay = Bitmap.createBitmap((int) ((((float) bm.getWidth()) / ((float) scale)) + 0.5f), (int) ((((float) bm.getHeight()) / ((float) scale)) + 0.5f), Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1.0f / ((float) scale), 1.0f / ((float) scale));
        Paint paint = new Paint();
        paint.setFlags(2);
        canvas.drawBitmap(bm, 0.0f, 0.0f, paint);
        blur(overlay, scaledRadius, true);
        return overlay;
    }

    private static Bitmap blur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (radius < 1) {
            return null;
        }
        int i;
        int y;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[(w * h)];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = (radius + radius) + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[(divsum * 256)];
        for (i = 0; i < divsum * 256; i++) {
            dv[i] = i / divsum;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(Integer.TYPE, new int[]{div, 3});
        int r1 = radius + 1;
        for (y = 0; y < h; y++) {
            int x;
            int bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            for (i = -radius; i <= radius; i++) {
                int p = pix[Math.min(wm, Math.max(i, 0)) + yi];
                int[] sir = stack[i + radius];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                int rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            int stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min((x + radius) + 1, wm);
                }
                p = pix[vmin[x] + yw];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            bsum = 0;
            gsum = 0;
            rsum = 0;
            boutsum = 0;
            goutsum = 0;
            routsum = 0;
            binsum = 0;
            ginsum = 0;
            rinsum = 0;
            int yp = (-radius) * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (((ViewCompat.MEASURED_STATE_MASK & pix[yi]) | (dv[rsum] << 16)) | (dv[gsum] << 8)) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public static Bitmap roundBitmap(Bitmap orginal, int width, int height, float leftTop, float rightTop, float rightBottom, float leftBottom) throws Throwable {
        Bitmap output;
        int oriWidth = orginal.getWidth();
        int oriHeight = orginal.getHeight();
        Rect src = new Rect(0, 0, oriWidth, oriHeight);
        if (oriWidth == width && oriHeight == height) {
            output = Bitmap.createBitmap(orginal.getWidth(), orginal.getHeight(), Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect dst = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        float[] outerRadii = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};
        ShapeDrawable draweable = new ShapeDrawable(new RoundRectShape(outerRadii, new RectF(0.0f, 0.0f, 0.0f, 0.0f), outerRadii));
        draweable.setBounds(dst);
        draweable.draw(canvas);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(orginal, src, dst, paint);
        return output;
    }

    public static int[] fixRect(int[] src, int[] target) {
        int[] dst = new int[2];
        if (((float) src[0]) / ((float) src[1]) > ((float) target[0]) / ((float) target[1])) {
            dst[0] = target[0];
            dst[1] = (int) (((((float) src[1]) * ((float) target[0])) / ((float) src[0])) + 0.5f);
        } else {
            dst[1] = target[1];
            dst[0] = (int) (((((float) src[0]) * ((float) target[1])) / ((float) src[1])) + 0.5f);
        }
        return dst;
    }

    public static int[] fixRect_2(int[] src, int[] target) {
        int[] dst = new int[2];
        if (((float) src[0]) / ((float) src[1]) > ((float) target[0]) / ((float) target[1])) {
            dst[1] = target[1];
            dst[0] = (int) (((((float) src[0]) * ((float) target[1])) / ((float) src[1])) + 0.5f);
        } else {
            dst[0] = target[0];
            dst[1] = (int) (((((float) src[1]) * ((float) target[0])) / ((float) src[0])) + 0.5f);
        }
        return dst;
    }

    public static String saveBitmap(Context context, Bitmap bm, CompressFormat format, int quality) throws Throwable {
        String path = ResHelper.getCachePath(context, "images");
        String ext = ".jpg";
        if (format == CompressFormat.PNG) {
            ext = ".png";
        }
        File ss = new File(path, String.valueOf(System.currentTimeMillis()) + ext);
        FileOutputStream fos = new FileOutputStream(ss);
        bm.compress(format, quality, fos);
        fos.flush();
        fos.close();
        return ss.getAbsolutePath();
    }

    public static String saveBitmap(Context context, Bitmap bm) throws Throwable {
        return saveBitmap(context, bm, CompressFormat.JPEG, 80);
    }

    public static CompressFormat getBmpFormat(byte[] data) {
        String mime = getMime(data);
        CompressFormat format = CompressFormat.JPEG;
        if (mime == null) {
            return format;
        }
        if (mime.endsWith("png") || mime.endsWith("gif")) {
            return CompressFormat.PNG;
        }
        return format;
    }

    public static CompressFormat getBmpFormat(String filePath) {
        String pathLower = filePath.toLowerCase();
        if (pathLower.endsWith("png") || pathLower.endsWith("gif")) {
            return CompressFormat.PNG;
        }
        if (pathLower.endsWith("jpg") || pathLower.endsWith("jpeg") || pathLower.endsWith("bmp") || pathLower.endsWith("tif")) {
            return CompressFormat.JPEG;
        }
        String mime = getMime(filePath);
        if (mime.endsWith("png") || mime.endsWith("gif")) {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }

    private static String getMime(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[8];
            fis.read(bytes);
            fis.close();
            return getMime(bytes);
        } catch (Exception e) {
            MobLog.getInstance().w(e);
            return null;
        }
    }

    private static String getMime(byte[] bytes) {
        byte[] jpeg2 = new byte[]{(byte) -1, (byte) -40, (byte) -1, (byte) -31};
        if (bytesStartWith(bytes, new byte[]{(byte) -1, (byte) -40, (byte) -1, (byte) -32}) || bytesStartWith(bytes, jpeg2)) {
            return "jpg";
        }
        if (bytesStartWith(bytes, new byte[]{(byte) -119, (byte) 80, (byte) 78, (byte) 71})) {
            return "png";
        }
        if (bytesStartWith(bytes, "GIF".getBytes())) {
            return "gif";
        }
        if (bytesStartWith(bytes, "BM".getBytes())) {
            return "bmp";
        }
        byte[] tiff2 = new byte[]{(byte) 77, (byte) 77, (byte) 42};
        if (bytesStartWith(bytes, new byte[]{(byte) 73, (byte) 73, (byte) 42}) || bytesStartWith(bytes, tiff2)) {
            return "tif";
        }
        return null;
    }

    private static boolean bytesStartWith(byte[] target, byte[] prefix) {
        if (target == prefix) {
            return true;
        }
        if (target == null || prefix == null) {
            return false;
        }
        if (target.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (target[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    public static Bitmap cropBitmap(Bitmap orginal, int left, int top, int right, int bottom) throws Throwable {
        int width = (orginal.getWidth() - left) - right;
        int height = (orginal.getHeight() - top) - bottom;
        if (width == orginal.getWidth() && height == orginal.getHeight()) {
            return orginal;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(output).drawBitmap(orginal, (float) (-left), (float) (-top), new Paint());
        return output;
    }

    public static boolean isBlackBitmap(Bitmap bm) throws Throwable {
        if (bm == null || bm.isRecycled()) {
            return true;
        }
        int[] pixels = new int[(bm.getWidth() * bm.getHeight())];
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
        boolean found = false;
        for (int i : pixels) {
            if ((i & ViewCompat.MEASURED_SIZE_MASK) != 0) {
                found = true;
                break;
            }
        }
        if (found) {
            return false;
        }
        return true;
    }

    public static int mixAlpha(int frontARGB, int backRGB) {
        int fa = frontARGB >>> 24;
        return ((ViewCompat.MEASURED_STATE_MASK | ((((fa * ((16711680 & frontARGB) >>> 16)) + ((255 - fa) * ((16711680 & backRGB) >>> 16))) / 255) << 16)) | ((((fa * ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & frontARGB) >>> 8)) + ((255 - fa) * ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & backRGB) >>> 8))) / 255) << 8)) | (((fa * (frontARGB & 255)) + ((255 - fa) * (backRGB & 255))) / 255);
    }

    public static Bitmap scaleBitmapByHeight(Context context, int resId, int height) throws Throwable {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);
        boolean rec = height != bm.getHeight();
        Bitmap dst = scaleBitmapByHeight(bm, height);
        if (rec) {
            bm.recycle();
        }
        return dst;
    }

    public static Bitmap scaleBitmapByHeight(Bitmap src, int height) throws Throwable {
        return Bitmap.createScaledBitmap(src, (src.getWidth() * height) / src.getHeight(), height, true);
    }
}
