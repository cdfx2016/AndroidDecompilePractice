package com.fanyu.boundless.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import com.alibaba.fastjson.asm.Opcodes;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.bither.util.NativeUtil;

public class ImageUtils {
    private String filePath = "";
    private String mBigImageName = "";
    private ArrayList<String> mbigimageList = new ArrayList();
    public Bitmap photo = null;

    public Bitmap getimage(String srcPath, String filepath) {
        return compressImage(getBitmapFromFile(srcPath), filepath);
    }

    public static int getRatioSize(int bitWidth, int bitHeight) {
        int ratio = 1;
        if (bitWidth > bitHeight && bitWidth > com.easemob.util.ImageUtils.SCALE_IMAGE_HEIGHT) {
            ratio = bitWidth / com.easemob.util.ImageUtils.SCALE_IMAGE_HEIGHT;
        } else if (bitWidth < bitHeight && bitHeight > 1280) {
            ratio = bitHeight / 1280;
        }
        if (ratio <= 0) {
            return 1;
        }
        return ratio;
    }

    public static Bitmap getBitmapFromFile(String filePath) {
        Options newOpts = new Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, newOpts);
        newOpts.inSampleSize = getRatioSize(newOpts.outWidth, newOpts.outHeight);
        newOpts.inJustDecodeBounds = false;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inTempStorage = new byte[32768];
        Bitmap bitmap = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fs != null) {
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
                int photoDegree = readPictureDegree(filePath);
                if (photoDegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) photoDegree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e222) {
                        e222.printStackTrace();
                    }
                }
            }
        }
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e2222) {
                e2222.printStackTrace();
            }
        }
        return bitmap;
    }

    public static int readPictureDegree(String path) {
        try {
            switch (new ExifInterface(path).getAttributeInt("Orientation", 1)) {
                case 3:
                    return Opcodes.GETFIELD;
                case 6:
                    return 90;
                case 8:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Bitmap compressImage(Bitmap image, String filepath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;
        System.out.println("baos.toByteArray()" + baos.toByteArray().length);
        while (baos.toByteArray().length / 1024 > 150) {
            baos.reset();
            image.compress(CompressFormat.JPEG, options, baos);
            System.out.println("baos.toByteArray()" + baos.toByteArray().length + options);
            options -= 10;
        }
        System.out.println("baos.toByteArray()" + baos.toByteArray().length + "suofang" + options);
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        NativeUtil.saveBitmap(image, options, new File(dir, filepath).getPath(), true);
        return image;
    }

    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        FileNotFoundException e;
        IOException e2;
        Throwable th;
        if (checkSDCardAvailable()) {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    try {
                        if (photoBitmap.compress(CompressFormat.PNG, 100, fileOutputStream2)) {
                            fileOutputStream2.flush();
                        }
                    } catch (FileNotFoundException e3) {
                        e = e3;
                        fileOutputStream = fileOutputStream2;
                        try {
                            photoFile.delete();
                            e.printStackTrace();
                            try {
                                fileOutputStream.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                                return;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                fileOutputStream.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                            throw th;
                        }
                    } catch (IOException e4) {
                        e222 = e4;
                        fileOutputStream = fileOutputStream2;
                        photoFile.delete();
                        e222.printStackTrace();
                        try {
                            fileOutputStream.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                            return;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileOutputStream = fileOutputStream2;
                        fileOutputStream.close();
                        throw th;
                    }
                }
                try {
                    fileOutputStream2.close();
                } catch (IOException e22222) {
                    e22222.printStackTrace();
                }
            } catch (FileNotFoundException e5) {
                e = e5;
                photoFile.delete();
                e.printStackTrace();
                fileOutputStream.close();
            } catch (IOException e6) {
                e22222 = e6;
                photoFile.delete();
                e22222.printStackTrace();
                fileOutputStream.close();
            }
        }
    }

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }
}
