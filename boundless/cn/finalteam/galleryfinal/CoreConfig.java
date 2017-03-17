package cn.finalteam.galleryfinal;

import android.content.Context;
import android.os.Environment;
import android.widget.AbsListView.OnScrollListener;
import java.io.File;

public class CoreConfig {
    private int animRes;
    private Context context;
    private File editPhotoCacheFolder;
    private FunctionConfig functionConfig;
    private ImageLoader imageLoader;
    private OnScrollListener onScrollListener;
    private File takePhotoFolder;
    private ThemeConfig themeConfig;

    public static class Builder {
        private int animRes = R.anim.gf_flip_horizontal_in;
        private Context context;
        private File editPhotoCacheFolder;
        private FunctionConfig functionConfig;
        private ImageLoader imageLoader;
        private boolean noAnimcation;
        private OnScrollListener onScrollListener;
        private File takePhotoFolder;
        private ThemeConfig themeConfig;

        public Builder(Context context, ImageLoader imageLoader, ThemeConfig themeConfig) {
            this.context = context;
            this.imageLoader = imageLoader;
            this.themeConfig = themeConfig;
        }

        public Builder setTakePhotoFolder(File takePhotoFolder) {
            this.takePhotoFolder = takePhotoFolder;
            return this;
        }

        public Builder setEditPhotoCacheFolder(File editPhotoCacheFolder) {
            this.editPhotoCacheFolder = editPhotoCacheFolder;
            return this;
        }

        public Builder setFunctionConfig(FunctionConfig functionConfig) {
            this.functionConfig = functionConfig;
            return this;
        }

        public Builder setAnimation(int animRes) {
            this.animRes = animRes;
            return this;
        }

        public Builder setNoAnimcation(boolean noAnimcation) {
            this.noAnimcation = noAnimcation;
            return this;
        }

        public Builder setPauseOnScrollListener(OnScrollListener listener) {
            this.onScrollListener = listener;
            return this;
        }

        public CoreConfig build() {
            return new CoreConfig();
        }
    }

    private CoreConfig(Builder builder) {
        this.context = builder.context;
        this.imageLoader = builder.imageLoader;
        this.takePhotoFolder = builder.takePhotoFolder;
        this.editPhotoCacheFolder = builder.editPhotoCacheFolder;
        this.themeConfig = builder.themeConfig;
        this.functionConfig = builder.functionConfig;
        if (builder.noAnimcation) {
            this.animRes = -1;
        } else {
            this.animRes = builder.animRes;
        }
        this.onScrollListener = builder.onScrollListener;
        if (this.takePhotoFolder == null) {
            this.takePhotoFolder = new File(Environment.getExternalStorageDirectory(), "/DCIM/GalleryFinal/");
        }
        if (!this.takePhotoFolder.exists()) {
            this.takePhotoFolder.mkdirs();
        }
        if (this.editPhotoCacheFolder == null) {
            this.editPhotoCacheFolder = new File(Environment.getExternalStorageDirectory() + "/GalleryFinal/edittemp/");
        }
        if (!this.editPhotoCacheFolder.exists()) {
            this.editPhotoCacheFolder.mkdirs();
        }
    }

    public Context getContext() {
        return this.context;
    }

    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    public File getTakePhotoFolder() {
        return this.takePhotoFolder;
    }

    public File getEditPhotoCacheFolder() {
        return this.editPhotoCacheFolder;
    }

    public int getAnimation() {
        return this.animRes;
    }

    public ThemeConfig getThemeConfig() {
        return this.themeConfig;
    }

    public FunctionConfig getFunctionConfig() {
        return this.functionConfig;
    }

    OnScrollListener getPauseOnScrollListener() {
        return this.onScrollListener;
    }
}
