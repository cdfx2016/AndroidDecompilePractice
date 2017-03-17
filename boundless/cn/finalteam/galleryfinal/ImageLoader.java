package cn.finalteam.galleryfinal;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import cn.finalteam.galleryfinal.widget.GFImageView;
import java.io.Serializable;

public interface ImageLoader extends Serializable {
    void clearMemoryCache();

    void displayImage(Activity activity, String str, GFImageView gFImageView, Drawable drawable, int i, int i2);
}
