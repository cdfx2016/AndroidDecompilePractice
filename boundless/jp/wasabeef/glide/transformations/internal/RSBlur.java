package jp.wasabeef.glide.transformations.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.RSMessageHandler;
import android.renderscript.ScriptIntrinsicBlur;

public class RSBlur {
    @TargetApi(18)
    public static Bitmap blur(Context context, Bitmap bitmap, int radius) throws RSRuntimeException {
        RenderScript renderScript = null;
        try {
            renderScript = RenderScript.create(context);
            renderScript.setMessageHandler(new RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(renderScript, bitmap, MipmapControl.MIPMAP_NONE, 1);
            Allocation output = Allocation.createTyped(renderScript, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            blur.setInput(input);
            blur.setRadius((float) radius);
            blur.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        } finally {
            if (renderScript != null) {
                renderScript.destroy();
            }
        }
    }
}
