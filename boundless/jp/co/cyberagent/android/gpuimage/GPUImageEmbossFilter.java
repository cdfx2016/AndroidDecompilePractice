package jp.co.cyberagent.android.gpuimage;

public class GPUImageEmbossFilter extends GPUImage3x3ConvolutionFilter {
    private float mIntensity;

    public GPUImageEmbossFilter() {
        this(1.0f);
    }

    public GPUImageEmbossFilter(float intensity) {
        this.mIntensity = intensity;
    }

    public void onInit() {
        super.onInit();
        setIntensity(this.mIntensity);
    }

    public void setIntensity(float intensity) {
        this.mIntensity = intensity;
        setConvolutionKernel(new float[]{-2.0f * intensity, -intensity, 0.0f, -intensity, 1.0f, intensity, 0.0f, intensity, 2.0f * intensity});
    }

    public float getIntensity() {
        return this.mIntensity;
    }
}
