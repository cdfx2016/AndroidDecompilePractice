package cn.finalteam.toolsfinal;

import android.content.Context;

public class ResourceUtils {
    public static int getLayoutId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, TtmlNode.TAG_LAYOUT, context.getPackageName());
    }

    public static int getStringId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string", context.getPackageName());
    }

    public static int getDrawableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    public static int getMipmapId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
    }

    public static int getStyleId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, TtmlNode.TAG_STYLE, context.getPackageName());
    }

    public static Object getStyleableId(Context context, String resName) {
        return Integer.valueOf(context.getResources().getIdentifier(resName, "styleable", context.getPackageName()));
    }

    public static int getAnimId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "anim", context.getPackageName());
    }

    public static int getId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "id", context.getPackageName());
    }

    public static int getColorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, TtmlNode.ATTR_TTS_COLOR, context.getPackageName());
    }
}
