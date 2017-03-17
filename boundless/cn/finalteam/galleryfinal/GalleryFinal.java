package cn.finalteam.galleryfinal;

import android.content.Intent;
import android.widget.Toast;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.utils.ILogger;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryFinal {
    static final int PERMISSIONS_CODE_GALLERY = 2001;
    static final int TAKE_REQUEST_CODE = 1001;
    private static OnHanlderResultCallback mCallback;
    private static CoreConfig mCoreConfig;
    private static FunctionConfig mCurrentFunctionConfig;
    private static FunctionConfig mGlobalFunctionConfig;
    private static int mRequestCode;
    private static ThemeConfig mThemeConfig;

    public interface OnHanlderResultCallback {
        void onHanlderFailure(int i, String str);

        void onHanlderSuccess(int i, List<PhotoInfo> list);
    }

    public static void init(CoreConfig coreConfig) {
        mThemeConfig = coreConfig.getThemeConfig();
        mCoreConfig = coreConfig;
        mGlobalFunctionConfig = coreConfig.getFunctionConfig();
    }

    public static FunctionConfig copyGlobalFuncationConfig() {
        if (mGlobalFunctionConfig != null) {
            return mGlobalFunctionConfig.clone();
        }
        return null;
    }

    public static CoreConfig getCoreConfig() {
        return mCoreConfig;
    }

    public static FunctionConfig getFunctionConfig() {
        return mCurrentFunctionConfig;
    }

    public static ThemeConfig getGalleryTheme() {
        if (mThemeConfig == null) {
            mThemeConfig = ThemeConfig.DEFAULT;
        }
        return mThemeConfig;
    }

    public static void openGallerySingle(int requestCode, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openGallerySingle(requestCode, config, callback);
            return;
        }
        if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
        }
        ILogger.e("FunctionConfig null", new Object[0]);
    }

    public static void openGallerySingle(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if (mCoreConfig.getImageLoader() == null) {
            ILogger.e("Please init GalleryFinal.", new Object[0]);
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config == null && mGlobalFunctionConfig == null) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (DeviceUtils.existSDCard()) {
            config.mutiSelect = false;
            mRequestCode = requestCode;
            mCallback = callback;
            mCurrentFunctionConfig = config;
            Intent intent = new Intent(mCoreConfig.getContext(), PhotoSelectActivity.class);
            intent.addFlags(268435456);
            mCoreConfig.getContext().startActivity(intent);
        } else {
            Toast.makeText(mCoreConfig.getContext(), R.string.empty_sdcard, 0).show();
        }
    }

    public static void openGalleryMuti(int requestCode, int maxSize, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            config.maxSize = maxSize;
            openGalleryMuti(requestCode, config, callback);
            return;
        }
        if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
        }
        ILogger.e("Please init GalleryFinal.", new Object[0]);
    }

    public static void openGalleryMuti(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if (mCoreConfig.getImageLoader() == null) {
            ILogger.e("Please init GalleryFinal.", new Object[0]);
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config == null && mGlobalFunctionConfig == null) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config.getMaxSize() <= 0) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.maxsize_zero_tip));
            }
        } else if (config.getSelectedList() == null || config.getSelectedList().size() <= config.getMaxSize()) {
            if (DeviceUtils.existSDCard()) {
                mRequestCode = requestCode;
                mCallback = callback;
                mCurrentFunctionConfig = config;
                config.mutiSelect = true;
                Intent intent = new Intent(mCoreConfig.getContext(), PhotoSelectActivity.class);
                intent.addFlags(268435456);
                mCoreConfig.getContext().startActivity(intent);
                return;
            }
            Toast.makeText(mCoreConfig.getContext(), R.string.empty_sdcard, 0).show();
        } else if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.select_max_tips));
        }
    }

    public static void openCamera(int requestCode, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openCamera(requestCode, config, callback);
            return;
        }
        if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
        }
        ILogger.e("Please init GalleryFinal.", new Object[0]);
    }

    public static void openCamera(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if (mCoreConfig.getImageLoader() == null) {
            ILogger.e("Please init GalleryFinal.", new Object[0]);
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config == null && mGlobalFunctionConfig == null) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (DeviceUtils.existSDCard()) {
            mRequestCode = requestCode;
            mCallback = callback;
            config.mutiSelect = false;
            mCurrentFunctionConfig = config;
            Intent intent = new Intent(mCoreConfig.getContext(), PhotoEditActivity.class);
            intent.addFlags(268435456);
            intent.putExtra("take_photo_action", true);
            mCoreConfig.getContext().startActivity(intent);
        } else {
            Toast.makeText(mCoreConfig.getContext(), R.string.empty_sdcard, 0).show();
        }
    }

    public static void openCrop(int requestCode, String photoPath, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openCrop(requestCode, config, photoPath, callback);
            return;
        }
        if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
        }
        ILogger.e("Please init GalleryFinal.", new Object[0]);
    }

    public static void openCrop(int requestCode, FunctionConfig config, String photoPath, OnHanlderResultCallback callback) {
        if (mCoreConfig.getImageLoader() == null) {
            ILogger.e("Please init GalleryFinal.", new Object[0]);
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config == null && mGlobalFunctionConfig == null) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (!DeviceUtils.existSDCard()) {
            Toast.makeText(mCoreConfig.getContext(), R.string.empty_sdcard, 0).show();
        } else if (config == null || StringUtils.isEmpty(photoPath) || !new File(photoPath).exists()) {
            ILogger.d("config为空或文件不存在", new Object[0]);
        } else {
            mRequestCode = requestCode;
            mCallback = callback;
            config.mutiSelect = false;
            config.editPhoto = true;
            config.crop = true;
            mCurrentFunctionConfig = config;
            ArrayList<PhotoInfo> map = new ArrayList();
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setPhotoPath(photoPath);
            photoInfo.setPhotoId(Utils.getRandom(10000, 99999));
            map.add(photoInfo);
            Intent intent = new Intent(mCoreConfig.getContext(), PhotoEditActivity.class);
            intent.addFlags(268435456);
            intent.putExtra("crop_photo_action", true);
            intent.putExtra("select_map", map);
            mCoreConfig.getContext().startActivity(intent);
        }
    }

    public static void openEdit(int requestCode, String photoPath, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openEdit(requestCode, config, photoPath, callback);
            return;
        }
        if (callback != null) {
            callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
        }
        ILogger.e("Please init GalleryFinal.", new Object[0]);
    }

    public static void openEdit(int requestCode, FunctionConfig config, String photoPath, OnHanlderResultCallback callback) {
        if (mCoreConfig.getImageLoader() == null) {
            ILogger.e("Please init GalleryFinal.", new Object[0]);
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (config == null && mGlobalFunctionConfig == null) {
            if (callback != null) {
                callback.onHanlderFailure(requestCode, mCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
        } else if (!DeviceUtils.existSDCard()) {
            Toast.makeText(mCoreConfig.getContext(), R.string.empty_sdcard, 0).show();
        } else if (config == null || StringUtils.isEmpty(photoPath) || !new File(photoPath).exists()) {
            ILogger.d("config为空或文件不存在", new Object[0]);
        } else {
            mRequestCode = requestCode;
            mCallback = callback;
            config.mutiSelect = false;
            mCurrentFunctionConfig = config;
            ArrayList<PhotoInfo> map = new ArrayList();
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setPhotoPath(photoPath);
            photoInfo.setPhotoId(Utils.getRandom(10000, 99999));
            map.add(photoInfo);
            Intent intent = new Intent(mCoreConfig.getContext(), PhotoEditActivity.class);
            intent.addFlags(268435456);
            intent.putExtra("edit_photo_action", true);
            intent.putExtra("select_map", map);
            mCoreConfig.getContext().startActivity(intent);
        }
    }

    public static void cleanCacheFile() {
        if (mCurrentFunctionConfig != null && mCoreConfig.getEditPhotoCacheFolder() != null) {
            new Thread() {
                public void run() {
                    super.run();
                    try {
                        FileUtils.deleteDirectory(GalleryFinal.mCoreConfig.getEditPhotoCacheFolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public static int getRequestCode() {
        return mRequestCode;
    }

    public static OnHanlderResultCallback getCallback() {
        return mCallback;
    }
}
