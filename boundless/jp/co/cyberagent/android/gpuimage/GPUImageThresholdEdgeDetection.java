package jp.co.cyberagent.android.gpuimage;

public class GPUImageThresholdEdgeDetection extends GPUImageFilterGroup {
    public GPUImageThresholdEdgeDetection() {
        addFilter(new GPUImageGrayscaleFilter());
        addFilter(new GPUImageSobelThresholdFilter());
    }

    public void setLineSize(float size) {
        ((GPUImage3x3TextureSamplingFilter) getFilters().get(1)).setLineSize(size);
    }

    public void setThreshold(float threshold) {
        ((GPUImageSobelThresholdFilter) getFilters().get(1)).setThreshold(threshold);
    }
}
