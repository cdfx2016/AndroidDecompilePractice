package jp.co.cyberagent.android.gpuimage;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import jp.co.cyberagent.android.gpuimage.GPUImage.ScaleType;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;

@TargetApi(11)
public class GPUImageRenderer implements Renderer, PreviewCallback {
    static final float[] CUBE = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
    public static final int NO_IMAGE = -1;
    private int mAddedPadding;
    private GPUImageFilter mFilter;
    private boolean mFlipHorizontal;
    private boolean mFlipVertical;
    private final FloatBuffer mGLCubeBuffer;
    private IntBuffer mGLRgbBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private int mGLTextureId = -1;
    private int mImageHeight;
    private int mImageWidth;
    private int mOutputHeight;
    private int mOutputWidth;
    private Rotation mRotation;
    private final Queue<Runnable> mRunOnDraw;
    private final Queue<Runnable> mRunOnDrawEnd;
    private ScaleType mScaleType = ScaleType.CENTER_CROP;
    public final Object mSurfaceChangedWaiter = new Object();
    private SurfaceTexture mSurfaceTexture = null;

    public GPUImageRenderer(GPUImageFilter filter) {
        this.mFilter = filter;
        this.mRunOnDraw = new LinkedList();
        this.mRunOnDrawEnd = new LinkedList();
        this.mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLCubeBuffer.put(CUBE).position(0);
        this.mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDisable(2929);
        this.mFilter.init();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mOutputWidth = width;
        this.mOutputHeight = height;
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(this.mFilter.getProgram());
        this.mFilter.onOutputSizeChanged(width, height);
        adjustImageScaling();
        synchronized (this.mSurfaceChangedWaiter) {
            this.mSurfaceChangedWaiter.notifyAll();
        }
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(16640);
        runAll(this.mRunOnDraw);
        this.mFilter.onDraw(this.mGLTextureId, this.mGLCubeBuffer, this.mGLTextureBuffer);
        runAll(this.mRunOnDrawEnd);
        if (this.mSurfaceTexture != null) {
            this.mSurfaceTexture.updateTexImage();
        }
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                ((Runnable) queue.poll()).run();
            }
        }
    }

    public void onPreviewFrame(final byte[] data, final Camera camera) {
        final Size previewSize = camera.getParameters().getPreviewSize();
        if (this.mGLRgbBuffer == null) {
            this.mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }
        if (this.mRunOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                public void run() {
                    GPUImageNativeLibrary.YUVtoRBGA(data, previewSize.width, previewSize.height, GPUImageRenderer.this.mGLRgbBuffer.array());
                    GPUImageRenderer.this.mGLTextureId = OpenGlUtils.loadTexture(GPUImageRenderer.this.mGLRgbBuffer, previewSize, GPUImageRenderer.this.mGLTextureId);
                    camera.addCallbackBuffer(data);
                    if (GPUImageRenderer.this.mImageWidth != previewSize.width) {
                        GPUImageRenderer.this.mImageWidth = previewSize.width;
                        GPUImageRenderer.this.mImageHeight = previewSize.height;
                        GPUImageRenderer.this.adjustImageScaling();
                    }
                }
            });
        }
    }

    public void setUpSurfaceTexture(final Camera camera) {
        runOnDraw(new Runnable() {
            public void run() {
                int[] textures = new int[1];
                GLES20.glGenTextures(1, textures, 0);
                GPUImageRenderer.this.mSurfaceTexture = new SurfaceTexture(textures[0]);
                try {
                    camera.setPreviewTexture(GPUImageRenderer.this.mSurfaceTexture);
                    camera.setPreviewCallback(GPUImageRenderer.this);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(new Runnable() {
            public void run() {
                GPUImageFilter oldFilter = GPUImageRenderer.this.mFilter;
                GPUImageRenderer.this.mFilter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                GPUImageRenderer.this.mFilter.init();
                GLES20.glUseProgram(GPUImageRenderer.this.mFilter.getProgram());
                GPUImageRenderer.this.mFilter.onOutputSizeChanged(GPUImageRenderer.this.mOutputWidth, GPUImageRenderer.this.mOutputHeight);
            }
        });
    }

    public void deleteImage() {
        runOnDraw(new Runnable() {
            public void run() {
                GLES20.glDeleteTextures(1, new int[]{GPUImageRenderer.this.mGLTextureId}, 0);
                GPUImageRenderer.this.mGLTextureId = -1;
            }
        });
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap != null) {
            runOnDraw(new Runnable() {
                public void run() {
                    Bitmap resizedBitmap = null;
                    if (bitmap.getWidth() % 2 == 1) {
                        resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(), Config.ARGB_8888);
                        Canvas can = new Canvas(resizedBitmap);
                        can.drawARGB(0, 0, 0, 0);
                        can.drawBitmap(bitmap, 0.0f, 0.0f, null);
                        GPUImageRenderer.this.mAddedPadding = 1;
                    } else {
                        GPUImageRenderer.this.mAddedPadding = 0;
                    }
                    GPUImageRenderer.this.mGLTextureId = OpenGlUtils.loadTexture(resizedBitmap != null ? resizedBitmap : bitmap, GPUImageRenderer.this.mGLTextureId, recycle);
                    if (resizedBitmap != null) {
                        resizedBitmap.recycle();
                    }
                    GPUImageRenderer.this.mImageWidth = bitmap.getWidth();
                    GPUImageRenderer.this.mImageHeight = bitmap.getHeight();
                    GPUImageRenderer.this.adjustImageScaling();
                }
            });
        }
    }

    public void setScaleType(ScaleType scaleType) {
        this.mScaleType = scaleType;
    }

    protected int getFrameWidth() {
        return this.mOutputWidth;
    }

    protected int getFrameHeight() {
        return this.mOutputHeight;
    }

    private void adjustImageScaling() {
        float outputWidth = (float) this.mOutputWidth;
        float outputHeight = (float) this.mOutputHeight;
        if (this.mRotation == Rotation.ROTATION_270 || this.mRotation == Rotation.ROTATION_90) {
            outputWidth = (float) this.mOutputHeight;
            outputHeight = (float) this.mOutputWidth;
        }
        float ratioMax = Math.max(outputWidth / ((float) this.mImageWidth), outputHeight / ((float) this.mImageHeight));
        int imageWidthNew = Math.round(((float) this.mImageWidth) * ratioMax);
        float ratioWidth = ((float) imageWidthNew) / outputWidth;
        float ratioHeight = ((float) Math.round(((float) this.mImageHeight) * ratioMax)) / outputHeight;
        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(this.mRotation, this.mFlipHorizontal, this.mFlipVertical);
        if (this.mScaleType == ScaleType.CENTER_CROP) {
            float distHorizontal = (1.0f - (1.0f / ratioWidth)) / 2.0f;
            float distVertical = (1.0f - (1.0f / ratioHeight)) / 2.0f;
            textureCords = new float[]{addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical), addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical), addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical), addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical)};
        } else {
            cube = new float[]{CUBE[0] / ratioHeight, CUBE[1] / ratioWidth, CUBE[2] / ratioHeight, CUBE[3] / ratioWidth, CUBE[4] / ratioHeight, CUBE[5] / ratioWidth, CUBE[6] / ratioHeight, CUBE[7] / ratioWidth};
        }
        this.mGLCubeBuffer.clear();
        this.mGLCubeBuffer.put(cube).position(0);
        this.mGLTextureBuffer.clear();
        this.mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1.0f - distance;
    }

    public void setRotationCamera(Rotation rotation, boolean flipHorizontal, boolean flipVertical) {
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void setRotation(Rotation rotation) {
        this.mRotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(Rotation rotation, boolean flipHorizontal, boolean flipVertical) {
        this.mFlipHorizontal = flipHorizontal;
        this.mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return this.mRotation;
    }

    public boolean isFlippedHorizontally() {
        return this.mFlipHorizontal;
    }

    public boolean isFlippedVertically() {
        return this.mFlipVertical;
    }

    protected void runOnDraw(Runnable runnable) {
        synchronized (this.mRunOnDraw) {
            this.mRunOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(Runnable runnable) {
        synchronized (this.mRunOnDrawEnd) {
            this.mRunOnDrawEnd.add(runnable);
        }
    }
}
