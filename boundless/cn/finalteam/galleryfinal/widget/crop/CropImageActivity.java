package cn.finalteam.galleryfinal.widget.crop;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.utils.ILogger;
import cn.finalteam.galleryfinal.widget.crop.ImageViewTouchBase.Recycler;
import cn.finalteam.galleryfinal.widget.crop.MonitoredActivity.LifeCycleListener;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public abstract class CropImageActivity extends MonitoredActivity {
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    private int aspectX;
    private int aspectY;
    private boolean cropEnabled;
    private HighlightView cropView;
    private int exifRotation;
    private final Handler handler = new Handler();
    private CropImageView imageView;
    private boolean isSaving;
    private int maxX;
    private int maxY;
    private RotateBitmap rotateBitmap;
    private int sampleSize;
    private Uri sourceUri;

    private class Cropper {
        private Cropper() {
        }

        private void makeDefault() {
            boolean z = false;
            if (CropImageActivity.this.rotateBitmap != null) {
                HighlightView hv = new HighlightView(CropImageActivity.this.imageView, GalleryFinal.getGalleryTheme().getCropControlColor());
                int width = CropImageActivity.this.rotateBitmap.getWidth();
                int height = CropImageActivity.this.rotateBitmap.getHeight();
                Rect imageRect = new Rect(0, 0, width, height);
                int cropWidth = (Math.min(width, height) * 4) / 5;
                int cropHeight = cropWidth;
                if (!(CropImageActivity.this.aspectX == 0 || CropImageActivity.this.aspectY == 0)) {
                    if (CropImageActivity.this.aspectX > CropImageActivity.this.aspectY) {
                        cropHeight = (CropImageActivity.this.aspectY * cropWidth) / CropImageActivity.this.aspectX;
                    } else {
                        cropWidth = (CropImageActivity.this.aspectX * cropHeight) / CropImageActivity.this.aspectY;
                    }
                }
                int x = (width - cropWidth) / 2;
                int y = (height - cropHeight) / 2;
                RectF cropRect = new RectF((float) x, (float) y, (float) (x + cropWidth), (float) (y + cropHeight));
                Matrix unrotatedMatrix = CropImageActivity.this.imageView.getUnrotatedMatrix();
                if (!(CropImageActivity.this.aspectX == 0 || CropImageActivity.this.aspectY == 0)) {
                    z = true;
                }
                hv.setup(unrotatedMatrix, imageRect, cropRect, z);
                CropImageActivity.this.imageView.add(hv);
            }
        }

        public void crop() {
            CropImageActivity.this.handler.post(new Runnable() {
                public void run() {
                    Cropper.this.makeDefault();
                    CropImageActivity.this.imageView.invalidate();
                    if (CropImageActivity.this.imageView.highlightViews.size() == 1) {
                        CropImageActivity.this.cropView = (HighlightView) CropImageActivity.this.imageView.highlightViews.get(0);
                        CropImageActivity.this.cropView.setFocus(true);
                    }
                }
            });
        }
    }

    public abstract void setCropSaveException(Throwable th);

    public abstract void setCropSaveSuccess(File file);

    public /* bridge */ /* synthetic */ void addLifeCycleListener(LifeCycleListener lifeCycleListener) {
        super.addLifeCycleListener(lifeCycleListener);
    }

    public /* bridge */ /* synthetic */ void removeLifeCycleListener(LifeCycleListener lifeCycleListener) {
        super.removeLifeCycleListener(lifeCycleListener);
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setupWindowFlags();
    }

    @TargetApi(19)
    private void setupWindowFlags() {
        requestWindowFeature(1);
        if (VERSION.SDK_INT >= 19) {
            getWindow().clearFlags(67108864);
        }
    }

    public void initCrop(CropImageView imageView, boolean square, int maxX, int maxY) {
        if (square) {
            this.aspectX = 1;
            this.aspectY = 1;
        }
        this.maxX = maxX;
        this.maxY = maxY;
        this.imageView = imageView;
        imageView.context = this;
        imageView.setRecycler(new Recycler() {
            public void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });
    }

    public void setSourceUri(Uri sourceUri) {
        if (this.rotateBitmap != null) {
            this.rotateBitmap.recycle();
            this.rotateBitmap = null;
        }
        this.sourceUri = sourceUri;
        this.isSaving = false;
        this.sampleSize = 0;
        this.rotateBitmap = null;
        this.cropView = null;
        this.imageView.clear();
        if (sourceUri != null) {
            this.exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri));
            InputStream inputStream = null;
            try {
                this.sampleSize = calculateBitmapSampleSize(sourceUri);
                inputStream = getContentResolver().openInputStream(sourceUri);
                Options option = new Options();
                option.inSampleSize = this.sampleSize;
                this.rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(inputStream, null, option), this.exifRotation);
            } catch (IOException e) {
                ILogger.e(e);
            } catch (OutOfMemoryError e2) {
                ILogger.e(e2);
            } finally {
                CropUtil.closeSilently(inputStream);
            }
        }
        if (this.rotateBitmap != null) {
            startCrop();
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options);
            int maxSize = getMaxImageSize();
            int sampleSize = 1;
            while (true) {
                if (options.outHeight / sampleSize <= maxSize && options.outWidth / sampleSize <= maxSize) {
                    return sampleSize;
                }
                sampleSize <<= 1;
            }
        } finally {
            CropUtil.closeSilently(is);
        }
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return 2048;
        }
        return Math.min(textureLimit, 4096);
    }

    private int getMaxTextureSize() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(3379, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        if (!isFinishing()) {
            this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.waiting), new Runnable() {
                public void run() {
                    final CountDownLatch latch = new CountDownLatch(1);
                    CropImageActivity.this.handler.post(new Runnable() {
                        public void run() {
                            if (CropImageActivity.this.imageView.getScale() == 1.0f) {
                                CropImageActivity.this.imageView.center();
                            }
                            latch.countDown();
                        }
                    });
                    try {
                        latch.await();
                        new Cropper().crop();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, this.handler);
        }
    }

    public void setCropEnabled(boolean enabled) {
        this.cropEnabled = enabled;
        if (enabled) {
            startCrop();
        }
    }

    public void onSaveClicked(File saveFile) {
        if (this.cropView != null && !this.isSaving) {
            this.isSaving = true;
            Rect r = this.cropView.getScaledCropRect((float) this.sampleSize);
            int width = r.width();
            int height = r.height();
            int outWidth = width;
            int outHeight = height;
            if (this.maxX > 0 && this.maxY > 0 && (width > this.maxX || height > this.maxY)) {
                float ratio = ((float) width) / ((float) height);
                if (((float) this.maxX) / ((float) this.maxY) > ratio) {
                    outHeight = this.maxY;
                    outWidth = (int) ((((float) this.maxY) * ratio) + 0.5f);
                } else {
                    outWidth = this.maxX;
                    outHeight = (int) ((((float) this.maxX) / ratio) + 0.5f);
                }
            }
            try {
                Bitmap croppedImage = decodeRegionCrop(r, outWidth, outHeight);
                if (croppedImage != null) {
                    this.imageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, this.exifRotation), true);
                    this.imageView.center();
                    this.imageView.highlightViews.clear();
                }
                saveImage(croppedImage, saveFile);
            } catch (IllegalArgumentException e) {
                setCropSaveException(e);
            }
        }
    }

    private void saveImage(Bitmap croppedImage, final File saveFile) {
        if (croppedImage != null) {
            final Bitmap b = croppedImage;
            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.saving), new Runnable() {
                public void run() {
                    CropImageActivity.this.saveOutput(b, saveFile);
                }
            }, this.handler);
        }
    }

    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
        clearImageView();
        InputStream is = null;
        Bitmap croppedImage = null;
        int width;
        int height;
        try {
            Matrix matrix;
            is = getContentResolver().openInputStream(this.sourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            width = decoder.getWidth();
            height = decoder.getHeight();
            if (this.exifRotation != 0) {
                float f;
                float f2;
                matrix = new Matrix();
                matrix.setRotate((float) (-this.exifRotation));
                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));
                if (adjusted.left < 0.0f) {
                    f = (float) width;
                } else {
                    f = 0.0f;
                }
                if (adjusted.top < 0.0f) {
                    f2 = (float) height;
                } else {
                    f2 = 0.0f;
                }
                adjusted.offset(f, f2);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }
            croppedImage = decoder.decodeRegion(rect, new Options());
            if (croppedImage != null && (rect.width() > outWidth || rect.height() > outHeight)) {
                matrix = new Matrix();
                matrix.postScale(((float) outWidth) / ((float) rect.width()), ((float) outHeight) / ((float) rect.height()));
                croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
            }
            CropUtil.closeSilently(is);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image (" + width + Constants.ACCEPT_TIME_SEPARATOR_SP + height + Constants.ACCEPT_TIME_SEPARATOR_SP + this.exifRotation + ")", e);
        } catch (IOException e2) {
            try {
                ILogger.e(e2);
                setCropSaveException(e2);
            } finally {
                CropUtil.closeSilently(is);
            }
        } catch (OutOfMemoryError e3) {
            ILogger.e(e3);
            setCropSaveException(e3);
            CropUtil.closeSilently(is);
        }
        return croppedImage;
    }

    private void clearImageView() {
        this.imageView.clear();
        if (this.rotateBitmap != null) {
            this.rotateBitmap.recycle();
        }
        System.gc();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveOutput(android.graphics.Bitmap r9, java.io.File r10) {
        /*
        r8 = this;
        if (r10 == 0) goto L_0x004f;
    L_0x0002:
        r4 = 0;
        r5 = r8.getContentResolver();	 Catch:{ IOException -> 0x0063 }
        r6 = android.net.Uri.fromFile(r10);	 Catch:{ IOException -> 0x0063 }
        r4 = r5.openOutputStream(r6);	 Catch:{ IOException -> 0x0063 }
        if (r4 == 0) goto L_0x0030;
    L_0x0011:
        r5 = r10.getAbsolutePath();	 Catch:{ IOException -> 0x0063 }
        r2 = cn.finalteam.toolsfinal.io.FilenameUtils.getExtension(r5);	 Catch:{ IOException -> 0x0063 }
        r5 = "jpg";
        r5 = r2.equalsIgnoreCase(r5);	 Catch:{ IOException -> 0x0063 }
        if (r5 != 0) goto L_0x0029;
    L_0x0021:
        r5 = "jpeg";
        r5 = r2.equalsIgnoreCase(r5);	 Catch:{ IOException -> 0x0063 }
        if (r5 == 0) goto L_0x005b;
    L_0x0029:
        r3 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ IOException -> 0x0063 }
        r5 = 90;
        r9.compress(r3, r5, r4);	 Catch:{ IOException -> 0x0063 }
    L_0x0030:
        cn.finalteam.galleryfinal.widget.crop.CropUtil.closeSilently(r4);
    L_0x0033:
        r5 = r8.getContentResolver();
        r6 = r8.sourceUri;
        r5 = cn.finalteam.galleryfinal.widget.crop.CropUtil.getFromMediaUri(r8, r5, r6);
        r6 = r8.getContentResolver();
        r7 = android.net.Uri.fromFile(r10);
        r6 = cn.finalteam.galleryfinal.widget.crop.CropUtil.getFromMediaUri(r8, r6, r7);
        cn.finalteam.galleryfinal.widget.crop.CropUtil.copyExifRotation(r5, r6);
        r8.setCropSaveSuccess(r10);
    L_0x004f:
        r0 = r9;
        r5 = r8.handler;
        r6 = new cn.finalteam.galleryfinal.widget.crop.CropImageActivity$4;
        r6.<init>(r0);
        r5.post(r6);
        return;
    L_0x005b:
        r3 = android.graphics.Bitmap.CompressFormat.PNG;	 Catch:{ IOException -> 0x0063 }
        r5 = 100;
        r9.compress(r3, r5, r4);	 Catch:{ IOException -> 0x0063 }
        goto L_0x0030;
    L_0x0063:
        r1 = move-exception;
        r8.setCropSaveException(r1);	 Catch:{ all -> 0x006e }
        cn.finalteam.galleryfinal.utils.ILogger.e(r1);	 Catch:{ all -> 0x006e }
        cn.finalteam.galleryfinal.widget.crop.CropUtil.closeSilently(r4);
        goto L_0x0033;
    L_0x006e:
        r5 = move-exception;
        cn.finalteam.galleryfinal.widget.crop.CropUtil.closeSilently(r4);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.finalteam.galleryfinal.widget.crop.CropImageActivity.saveOutput(android.graphics.Bitmap, java.io.File):void");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.rotateBitmap != null) {
            this.rotateBitmap.recycle();
        }
    }

    public boolean onSearchRequested() {
        return false;
    }

    public boolean isSaving() {
        return this.isSaving;
    }
}
