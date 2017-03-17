package cn.finalteam.galleryfinal;

import android.support.annotation.IntRange;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import java.util.ArrayList;
import java.util.Collection;

public class FunctionConfig implements Cloneable {
    private boolean camera;
    protected boolean crop;
    private int cropHeight;
    private boolean cropReplaceSource;
    private boolean cropSquare;
    private int cropWidth;
    protected boolean editPhoto;
    private ArrayList<String> filterList;
    private boolean forceCrop;
    private boolean forceCropEdit;
    protected int maxSize;
    protected boolean mutiSelect;
    private boolean preview;
    private boolean rotate;
    private boolean rotateReplaceSource;
    private ArrayList<String> selectedList;

    public static class Builder {
        private boolean camera;
        private boolean crop;
        private int cropHeight;
        private boolean cropReplaceSource;
        private boolean cropSquare;
        private int cropWidth;
        private boolean editPhoto;
        private ArrayList<String> filterList;
        private boolean forceCrop;
        private boolean forceCropEdit;
        private int maxSize;
        private boolean mutiSelect;
        private boolean preview;
        private boolean rotate;
        private boolean rotateReplaceSource;
        private ArrayList<String> selectedList;

        protected Builder setMutiSelect(boolean mutiSelect) {
            this.mutiSelect = mutiSelect;
            return this;
        }

        public Builder setMutiSelectMaxSize(@IntRange(from = 1, to = 2147483647L) int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder setEnableEdit(boolean enable) {
            this.editPhoto = enable;
            return this;
        }

        public Builder setEnableCrop(boolean enable) {
            this.crop = enable;
            return this;
        }

        public Builder setEnableRotate(boolean enable) {
            this.rotate = enable;
            return this;
        }

        public Builder setEnableCamera(boolean enable) {
            this.camera = enable;
            return this;
        }

        public Builder setCropWidth(@IntRange(from = 1, to = 2147483647L) int width) {
            this.cropWidth = width;
            return this;
        }

        public Builder setCropHeight(@IntRange(from = 1, to = 2147483647L) int height) {
            this.cropHeight = height;
            return this;
        }

        public Builder setCropSquare(boolean enable) {
            this.cropSquare = enable;
            return this;
        }

        public Builder setSelected(ArrayList<String> selectedList) {
            if (selectedList != null) {
                this.selectedList = (ArrayList) selectedList.clone();
            }
            return this;
        }

        public Builder setSelected(Collection<PhotoInfo> selectedList) {
            if (selectedList != null) {
                ArrayList<String> list = new ArrayList();
                for (PhotoInfo info : selectedList) {
                    if (info != null) {
                        list.add(info.getPhotoPath());
                    }
                }
                this.selectedList = list;
            }
            return this;
        }

        public Builder setFilter(ArrayList<String> filterList) {
            if (filterList != null) {
                this.filterList = (ArrayList) filterList.clone();
            }
            return this;
        }

        public Builder setFilter(Collection<PhotoInfo> filterList) {
            if (filterList != null) {
                ArrayList<String> list = new ArrayList();
                for (PhotoInfo info : filterList) {
                    if (info != null) {
                        list.add(info.getPhotoPath());
                    }
                }
                this.filterList = list;
            }
            return this;
        }

        public Builder setRotateReplaceSource(boolean rotateReplaceSource) {
            this.rotateReplaceSource = rotateReplaceSource;
            return this;
        }

        public Builder setCropReplaceSource(boolean cropReplaceSource) {
            this.cropReplaceSource = cropReplaceSource;
            return this;
        }

        public Builder setForceCrop(boolean forceCrop) {
            this.forceCrop = forceCrop;
            return this;
        }

        public Builder setForceCropEdit(boolean forceCropEdit) {
            this.forceCropEdit = forceCropEdit;
            return this;
        }

        public Builder setEnablePreview(boolean preview) {
            this.preview = preview;
            return this;
        }

        public FunctionConfig build() {
            return new FunctionConfig();
        }
    }

    private FunctionConfig(Builder builder) {
        this.mutiSelect = builder.mutiSelect;
        this.maxSize = builder.maxSize;
        this.editPhoto = builder.editPhoto;
        this.crop = builder.crop;
        this.rotate = builder.rotate;
        this.camera = builder.camera;
        this.cropWidth = builder.cropWidth;
        this.cropHeight = builder.cropHeight;
        this.cropSquare = builder.cropSquare;
        this.selectedList = builder.selectedList;
        this.filterList = builder.filterList;
        this.rotateReplaceSource = builder.rotateReplaceSource;
        this.cropReplaceSource = builder.cropReplaceSource;
        this.forceCrop = builder.forceCrop;
        this.forceCropEdit = builder.forceCropEdit;
        this.preview = builder.preview;
    }

    public boolean isMutiSelect() {
        return this.mutiSelect;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean isEditPhoto() {
        return this.editPhoto;
    }

    public boolean isCrop() {
        return this.crop;
    }

    public boolean isRotate() {
        return this.rotate;
    }

    public boolean isCamera() {
        return this.camera;
    }

    public int getCropWidth() {
        return this.cropWidth;
    }

    public int getCropHeight() {
        return this.cropHeight;
    }

    public boolean isCropSquare() {
        return this.cropSquare;
    }

    public boolean isRotateReplaceSource() {
        return this.rotateReplaceSource;
    }

    public boolean isCropReplaceSource() {
        return this.cropReplaceSource;
    }

    public boolean isForceCrop() {
        return this.forceCrop;
    }

    public boolean isForceCropEdit() {
        return this.forceCropEdit;
    }

    public ArrayList<String> getSelectedList() {
        return this.selectedList;
    }

    public ArrayList<String> getFilterList() {
        return this.filterList;
    }

    public boolean isEnablePreview() {
        return this.preview;
    }

    public FunctionConfig clone() {
        FunctionConfig o = null;
        try {
            return (FunctionConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return o;
        }
    }
}
