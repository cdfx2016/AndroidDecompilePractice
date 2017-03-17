package com.fanyu.boundless.config;

import android.support.annotation.IntRange;

public class Builder extends cn.finalteam.galleryfinal.FunctionConfig.Builder {
    protected cn.finalteam.galleryfinal.FunctionConfig.Builder setMutiSelect(boolean mutiSelect) {
        return super.setMutiSelect(true);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setMutiSelectMaxSize(@IntRange(from = 1, to = 2147483647L) int maxSize) {
        return super.setMutiSelectMaxSize(maxSize);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setEnableCamera(boolean enable) {
        return super.setEnableCamera(false);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setEnableEdit(boolean enable) {
        return super.setEnableEdit(false);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setEnableCrop(boolean enable) {
        return super.setEnableCrop(false);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setEnableRotate(boolean enable) {
        return super.setEnableRotate(true);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setCropSquare(boolean enable) {
        return super.setCropSquare(false);
    }

    public cn.finalteam.galleryfinal.FunctionConfig.Builder setEnablePreview(boolean preview) {
        return super.setEnablePreview(false);
    }
}
