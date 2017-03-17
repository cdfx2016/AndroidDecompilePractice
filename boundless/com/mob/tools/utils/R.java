package com.mob.tools.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.mob.tools.network.KVPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class R {
    public static int dipToPx(Context context, int dip) {
        return ResHelper.dipToPx(context, dip);
    }

    public static int pxToDip(Context context, int px) {
        return ResHelper.pxToDip(context, px);
    }

    public static int designToDevice(Context context, int designScreenWidth, int designPx) {
        return ResHelper.designToDevice(context, designScreenWidth, designPx);
    }

    public static int designToDevice(Context context, float designScreenDensity, int designPx) {
        return ResHelper.designToDevice(context, designScreenDensity, designPx);
    }

    public static int[] getScreenSize(Context context) {
        return ResHelper.getScreenSize(context);
    }

    public static int getScreenWidth(Context context) {
        return getScreenSize(context)[0];
    }

    public static int getScreenHeight(Context context) {
        return getScreenSize(context)[1];
    }

    public static void setResourceProvider(Object rp) {
        ResHelper.setResourceProvider(rp);
    }

    public static int getResId(Context context, String resType, String resName) {
        return ResHelper.getResId(context, resType, resName);
    }

    public static int getBitmapRes(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    public static int getStringRes(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    public static int getStringArrayRes(Context context, String resName) {
        return getResId(context, "array", resName);
    }

    public static int getLayoutRes(Context context, String resName) {
        return getResId(context, TtmlNode.TAG_LAYOUT, resName);
    }

    public static int getStyleRes(Context context, String resName) {
        return getResId(context, TtmlNode.TAG_STYLE, resName);
    }

    public static int getIdRes(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    public static int getColorRes(Context context, String resName) {
        return getResId(context, TtmlNode.ATTR_TTS_COLOR, resName);
    }

    public static int getRawRes(Context context, String resName) {
        return getResId(context, "raw", resName);
    }

    public static int getPluralsRes(Context context, String resName) {
        return getResId(context, "plurals", resName);
    }

    public static int getAnimRes(Context context, String resName) {
        return getResId(context, "anim", resName);
    }

    public static String getCacheRoot(Context context) {
        return ResHelper.getCacheRoot(context);
    }

    public static String getCachePath(Context context, String category) {
        return ResHelper.getCachePath(context, category);
    }

    public static String getImageCachePath(Context context) {
        return getCachePath(context, "images");
    }

    public static void clearCache(Context context) throws Throwable {
        ResHelper.clearCache(context);
    }

    public static void deleteFilesInFolder(File folder) throws Throwable {
        ResHelper.deleteFilesInFolder(folder);
    }

    public static void deleteFileAndFolder(File folder) throws Throwable {
        ResHelper.deleteFileAndFolder(folder);
    }

    public static String toWordText(String text, int lenInWord) {
        return ResHelper.toWordText(text, lenInWord);
    }

    public static int getTextLengthInWord(String text) {
        return ResHelper.getTextLengthInWord(text);
    }

    public static long strToDate(String strDate) {
        return ResHelper.strToDate(strDate);
    }

    public static long dateStrToLong(String strDate) {
        return ResHelper.dateStrToLong(strDate);
    }

    public static Date longToDate(long time) {
        return ResHelper.longToDate(time);
    }

    public static String longToTime(long time, int level) {
        return ResHelper.longToTime(time, level);
    }

    public static long dateToLong(String date) {
        return ResHelper.dateToLong(date);
    }

    public static int[] covertTimeInYears(long time) {
        return ResHelper.covertTimeInYears(time);
    }

    public static Uri pathToContentUri(Context context, String imagePath) {
        return ResHelper.pathToContentUri(context, imagePath);
    }

    public static String contentUriToPath(Context context, Uri uri) {
        return ResHelper.contentUriToPath(context, uri);
    }

    public static String encodeUrl(Bundle parameters) {
        return ResHelper.encodeUrl(parameters);
    }

    public static String encodeUrl(ArrayList<KVPair<String>> values) {
        return ResHelper.encodeUrl((ArrayList) values);
    }

    public static Bundle urlToBundle(String url) {
        return ResHelper.urlToBundle(url);
    }

    public static Bundle decodeUrl(String s) {
        return ResHelper.decodeUrl(s);
    }

    public static int parseInt(String string) throws Throwable {
        return parseInt(string, 10);
    }

    public static int parseInt(String string, int radix) throws Throwable {
        return ResHelper.parseInt(string, radix);
    }

    public static long parseLong(String string) throws Throwable {
        return parseLong(string, 10);
    }

    public static long parseLong(String string, int radix) throws Throwable {
        return ResHelper.parseLong(string, radix);
    }

    public static String toString(Object obj) {
        return ResHelper.toString(obj);
    }

    public static <T> T forceCast(Object obj) {
        return forceCast(obj, null);
    }

    public static <T> T forceCast(Object obj, T defValue) {
        return ResHelper.forceCast(obj, defValue);
    }

    public static boolean copyFile(String fromFilePath, String toFilePath) {
        return ResHelper.copyFile(fromFilePath, toFilePath);
    }

    public static void copyFile(FileInputStream src, FileOutputStream dst) throws Throwable {
        ResHelper.copyFile(src, dst);
    }

    public static long getFileSize(String path) throws Throwable {
        return ResHelper.getFileSize(path);
    }

    public static long getFileSize(File file) throws Throwable {
        return ResHelper.getFileSize(file);
    }

    public static boolean saveObjectToFile(String filePath, Object object) {
        return ResHelper.saveObjectToFile(filePath, object);
    }

    public static Object readObjectFromFile(String filePath) {
        return ResHelper.readObjectFromFile(filePath);
    }
}
