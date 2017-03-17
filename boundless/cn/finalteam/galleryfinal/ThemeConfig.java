package cn.finalteam.galleryfinal;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import com.alibaba.fastjson.asm.Opcodes;
import java.io.Serializable;

public class ThemeConfig implements Serializable {
    public static ThemeConfig CYAN = new Builder().setTitleBarBgColor(Color.rgb(1, 131, 147)).setFabNornalColor(Color.rgb(0, 172, Opcodes.INSTANCEOF)).setFabPressedColor(Color.rgb(1, 131, 147)).setCheckSelectedColor(Color.rgb(0, 172, Opcodes.INSTANCEOF)).setCropControlColor(Color.rgb(0, 172, Opcodes.INSTANCEOF)).build();
    public static ThemeConfig DARK = new Builder().setTitleBarBgColor(Color.rgb(56, 66, 72)).setFabNornalColor(Color.rgb(56, 66, 72)).setFabPressedColor(Color.rgb(32, 37, 40)).setCheckSelectedColor(Color.rgb(56, 66, 72)).setCropControlColor(Color.rgb(56, 66, 72)).build();
    public static ThemeConfig DEFAULT = new Builder().build();
    public static ThemeConfig GREEN = new Builder().setTitleBarBgColor(Color.rgb(76, 175, 80)).setFabNornalColor(Color.rgb(76, 175, 80)).setFabPressedColor(Color.rgb(56, 142, 60)).setCheckSelectedColor(Color.rgb(76, 175, 80)).setCropControlColor(Color.rgb(76, 175, 80)).build();
    public static ThemeConfig ORANGE = new Builder().setTitleBarBgColor(Color.rgb(255, 87, 34)).setFabNornalColor(Color.rgb(255, 87, 34)).setFabPressedColor(Color.rgb(230, 74, 25)).setCheckSelectedColor(Color.rgb(255, 87, 34)).setCropControlColor(Color.rgb(255, 87, 34)).build();
    public static ThemeConfig TEAL = new Builder().setTitleBarBgColor(Color.rgb(0, 150, 136)).setFabNornalColor(Color.rgb(0, 150, 136)).setFabPressedColor(Color.rgb(0, 121, 107)).setCheckSelectedColor(Color.rgb(0, 150, 136)).setCropControlColor(Color.rgb(0, 150, 136)).build();
    private Drawable bgEditTexture;
    private Drawable bgPreveiw;
    private int checkNornalColor;
    private int checkSelectedColor;
    private int cropControlColor;
    private int fabNornalColor;
    private int fabPressedColor;
    private int iconBack;
    private int iconCamera;
    private int iconCheck;
    private int iconClear;
    private int iconCrop;
    private int iconDelete;
    private int iconFab;
    private int iconFolderArrow;
    private int iconPreview;
    private int iconRotate;
    private int titleBarBgColor;
    private int titleBarIconColor;
    private int titleBarTextColor;

    public static class Builder {
        private Drawable bgEditTexture;
        private Drawable bgPreveiw;
        private int checkNornalColor = Color.rgb(210, 210, 215);
        private int checkSelectedColor = Color.rgb(63, 81, Opcodes.PUTFIELD);
        private int cropControlColor = Color.rgb(63, 81, Opcodes.PUTFIELD);
        private int fabNornalColor = Color.rgb(63, 81, Opcodes.PUTFIELD);
        private int fabPressedColor = Color.rgb(48, 63, Opcodes.IF_ICMPEQ);
        private int iconBack = R.drawable.ic_gf_back;
        private int iconCamera = R.drawable.ic_gf_camera;
        private int iconCheck = R.drawable.ic_folder_check;
        private int iconClear = R.drawable.ic_gf_clear;
        private int iconCrop = R.drawable.ic_gf_crop;
        private int iconDelete = R.drawable.ic_delete_photo;
        private int iconFab = R.drawable.ic_folder_check;
        private int iconFolderArrow = R.drawable.ic_gf_triangle_arrow;
        private int iconPreview = R.drawable.ic_gf_preview;
        private int iconRotate = R.drawable.ic_gf_rotate;
        private int titleBarBgColor = Color.rgb(63, 81, Opcodes.PUTFIELD);
        private int titleBarIconColor = -1;
        private int titleBarTextColor = -1;

        public Builder setTitleBarTextColor(int titleBarTextColor) {
            this.titleBarTextColor = titleBarTextColor;
            return this;
        }

        public Builder setTitleBarBgColor(int titleBarBgColor) {
            this.titleBarBgColor = titleBarBgColor;
            return this;
        }

        public Builder setTitleBarIconColor(int iconColor) {
            this.titleBarIconColor = iconColor;
            return this;
        }

        public Builder setCheckNornalColor(int checkNornalColor) {
            this.checkNornalColor = checkNornalColor;
            return this;
        }

        public Builder setCheckSelectedColor(int checkSelectedColor) {
            this.checkSelectedColor = checkSelectedColor;
            return this;
        }

        public Builder setCropControlColor(int cropControlColor) {
            this.cropControlColor = cropControlColor;
            return this;
        }

        public Builder setFabNornalColor(int fabNornalColor) {
            this.fabNornalColor = fabNornalColor;
            return this;
        }

        public Builder setFabPressedColor(int fabPressedColor) {
            this.fabPressedColor = fabPressedColor;
            return this;
        }

        public Builder setIconBack(int iconBack) {
            this.iconBack = iconBack;
            return this;
        }

        public Builder setIconCamera(int iconCamera) {
            this.iconCamera = iconCamera;
            return this;
        }

        public Builder setIconCrop(int iconCrop) {
            this.iconCrop = iconCrop;
            return this;
        }

        public Builder setIconRotate(int iconRotate) {
            this.iconRotate = iconRotate;
            return this;
        }

        public Builder setIconClear(int iconClear) {
            this.iconClear = iconClear;
            return this;
        }

        public Builder setIconFolderArrow(int iconFolderArrow) {
            this.iconFolderArrow = iconFolderArrow;
            return this;
        }

        public Builder setIconDelete(int iconDelete) {
            this.iconDelete = iconDelete;
            return this;
        }

        public Builder setIconCheck(int iconCheck) {
            this.iconCheck = iconCheck;
            return this;
        }

        public Builder setIconFab(int iconFab) {
            this.iconFab = iconFab;
            return this;
        }

        public Builder setEditPhotoBgTexture(Drawable bgEditTexture) {
            this.bgEditTexture = bgEditTexture;
            return this;
        }

        public Builder setIconPreview(int iconPreview) {
            this.iconPreview = iconPreview;
            return this;
        }

        public Builder setPreviewBg(Drawable bgPreveiw) {
            this.bgPreveiw = bgPreveiw;
            return this;
        }

        public ThemeConfig build() {
            return new ThemeConfig();
        }
    }

    private ThemeConfig(Builder builder) {
        this.titleBarTextColor = builder.titleBarTextColor;
        this.titleBarBgColor = builder.titleBarBgColor;
        this.titleBarIconColor = builder.titleBarIconColor;
        this.checkNornalColor = builder.checkNornalColor;
        this.checkSelectedColor = builder.checkSelectedColor;
        this.fabNornalColor = builder.fabNornalColor;
        this.fabPressedColor = builder.fabPressedColor;
        this.cropControlColor = builder.cropControlColor;
        this.iconBack = builder.iconBack;
        this.iconCamera = builder.iconCamera;
        this.iconCrop = builder.iconCrop;
        this.iconRotate = builder.iconRotate;
        this.iconClear = builder.iconClear;
        this.iconDelete = builder.iconDelete;
        this.iconFolderArrow = builder.iconFolderArrow;
        this.iconCheck = builder.iconCheck;
        this.iconFab = builder.iconFab;
        this.bgEditTexture = builder.bgEditTexture;
        this.iconPreview = builder.iconPreview;
        this.bgPreveiw = builder.bgPreveiw;
    }

    public int getTitleBarTextColor() {
        return this.titleBarTextColor;
    }

    public int getTitleBarBgColor() {
        return this.titleBarBgColor;
    }

    public int getCheckNornalColor() {
        return this.checkNornalColor;
    }

    public int getCheckSelectedColor() {
        return this.checkSelectedColor;
    }

    public int getTitleBarIconColor() {
        return this.titleBarIconColor;
    }

    public int getFabNornalColor() {
        return this.fabNornalColor;
    }

    public int getFabPressedColor() {
        return this.fabPressedColor;
    }

    public int getCropControlColor() {
        return this.cropControlColor;
    }

    public int getIconBack() {
        return this.iconBack;
    }

    public int getIconCamera() {
        return this.iconCamera;
    }

    public int getIconCrop() {
        return this.iconCrop;
    }

    public int getIconRotate() {
        return this.iconRotate;
    }

    public int getIconClear() {
        return this.iconClear;
    }

    public int getIconFolderArrow() {
        return this.iconFolderArrow;
    }

    public int getIconDelete() {
        return this.iconDelete;
    }

    public int getIconCheck() {
        return this.iconCheck;
    }

    public int getIconFab() {
        return this.iconFab;
    }

    public int getIconPreview() {
        return this.iconPreview;
    }

    public Drawable getPreviewBg() {
        return this.bgPreveiw;
    }

    public Drawable getEditPhotoBgTexture() {
        return this.bgEditTexture;
    }
}
