package com.fanyu.boundless.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PointerIconCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryFinal.OnHanlderResultCallback;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import com.fanyu.boundless.R;
import com.fanyu.boundless.util.ImageUtils;
import com.fanyu.boundless.view.myself.IUpdateImgView;
import java.util.List;

public class NewDialog extends Dialog {
    private static Context mContext;
    private static OnHanlderResultCallback mOnHanlderResultCallback = new OnHanlderResultCallback() {
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                String pathString = ((PhotoInfo) resultList.get(0)).getPhotoPath();
                ImageUtils imageUtils = new ImageUtils();
                String mBigImageName = String.valueOf("bigmUserId" + System.currentTimeMillis() + ".jpg");
                Bitmap photo = imageUtils.getimage(pathString, mBigImageName);
                mBigImageName = Environment.getExternalStorageDirectory().getAbsolutePath() + "//wuya/" + mBigImageName;
                if (photo != null) {
                    Builder.mView.getPathImg(mBigImageName);
                    return;
                }
                return;
            }
            Toast.makeText(NewDialog.mContext, "开启摄像头权限", 1).show();
        }

        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };

    @SuppressLint({"WrongViewCast"})
    public static class Builder {
        public static IUpdateImgView mView;
        private final int REQUEST_CODE_CAMERA = 1000;
        private final int REQUEST_CODE_GALLERY = PointerIconCompat.TYPE_CONTEXT_MENU;
        private View contentView;
        private Context context;
        private String message;
        private OnClickListener negativeButtonClickListener;
        private String negativeButtonText;
        private String[] newstring;
        private OnClickListener positiveButtonClickListener;
        private String positiveButtonText;
        private String title;

        public Builder(Context context, IUpdateImgView mView) {
            this.context = context;
            mView = mView;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) this.context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) this.context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) this.context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) this.context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setItems(String[] items, OnClickListener listener) {
            this.newstring = items;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setItems(String[] items) {
            this.newstring = items;
            return this;
        }

        public NewDialog create() {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
            final NewDialog dialog = new NewDialog(this.context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.newdialog, null);
            dialog.addContentView(layout, new LayoutParams(-1, -2));
            ((TextView) layout.findViewById(R.id.title)).setText(this.title);
            if (this.newstring != null) {
                ((TextView) layout.findViewById(R.id.paizhao)).setText(this.newstring[0]);
                ((TextView) layout.findViewById(R.id.xiangce)).setText(this.newstring[1]);
                if (this.newstring.length == 3) {
                    layout.findViewById(R.id.xian).setVisibility(0);
                    ((TextView) layout.findViewById(R.id.selectone)).setVisibility(0);
                    ((TextView) layout.findViewById(R.id.selectone)).setText(this.newstring[2]);
                }
                ((TextView) layout.findViewById(R.id.paizhao)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        GalleryFinal.openGallerySingle(PointerIconCompat.TYPE_CONTEXT_MENU, NewDialog.mOnHanlderResultCallback);
                    }
                });
                ((TextView) layout.findViewById(R.id.xiangce)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        GalleryFinal.openCamera(1000, NewDialog.mOnHanlderResultCallback);
                    }
                });
                ((TextView) layout.findViewById(R.id.selectone)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        Builder.this.positiveButtonClickListener.onClick(dialog, 2);
                    }
                });
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(8);
            }
            if (this.negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton)).setText(this.negativeButtonText);
                ((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(8);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }

    public NewDialog(Context context) {
        super(context);
        mContext = context;
    }

    public NewDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }
}
