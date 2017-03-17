package com.fanyu.boundless.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fanyu.boundless.R;

public class ImagePathUtil {
    private static ImagePathUtil mInstance;

    public static ImagePathUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ImagePathUtil();
        }
        return mInstance;
    }

    public String getPath(String imagePath) throws Exception {
        String imgpaths = "";
        if (imagePath == null || imagePath.equals("") || imagePath.equals("null")) {
            return imgpaths;
        }
        return "http://dx.gensaint.com/" + imagePath.substring(imagePath.indexOf("upload"), imagePath.length()).replace("\\", "/");
    }

    public void setImageUrl(Context context, ImageView view, String url, Transformation transformation) {
        try {
            Glide.with(context).load(getInstance().getPath(url)).error((int) R.mipmap.jiazai_shibai).placeholder((int) R.mipmap.jiazaizhong_yuan).bitmapTransform(transformation).dontAnimate().into(view);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), 0).show();
        }
    }

    public void setYsImageUrl(Context context, final ImageView view, String url, Transformation transformation) {
        try {
            Glide.with(context).load(getInstance().getPath(url)).asBitmap().error((int) R.mipmap.jiazai_shibai).placeholder((int) R.mipmap.jiazaizhong_yuan).dontAnimate().into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    view.setImageBitmap(ImagePathUtil.getInstance().gerZoomRotateBitmap(resource));
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), 0).show();
        }
    }

    public Bitmap gerZoomRotateBitmap(Bitmap bmpOrg) {
        int width = bmpOrg.getWidth();
        int height = bmpOrg.getHeight();
        float sw = ((float) (width / 5)) / ((float) width);
        float sh = ((float) (height / 5)) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(sw, sh);
        return getRoundedCornerBitmap(Bitmap.createBitmap(bmpOrg, 0, 0, width, height, matrix, true));
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, 12.0f, 12.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
