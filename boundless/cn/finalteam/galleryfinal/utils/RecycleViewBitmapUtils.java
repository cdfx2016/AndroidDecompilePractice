package cn.finalteam.galleryfinal.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RecycleViewBitmapUtils {
    public static void recycleViewGroup(ViewGroup layout) {
        if (layout != null) {
            synchronized (layout) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View subView = layout.getChildAt(i);
                    if (subView instanceof ViewGroup) {
                        recycleViewGroup((ViewGroup) subView);
                    } else if (subView instanceof ImageView) {
                        recycleImageView((ImageView) subView);
                    }
                }
            }
        }
    }

    public static void recycleImageView(View view) {
        if (view != null && (view instanceof ImageView)) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                if (bmp != null && !bmp.isRecycled()) {
                    ((ImageView) view).setImageBitmap(null);
                    bmp.recycle();
                }
            }
        }
    }
}
