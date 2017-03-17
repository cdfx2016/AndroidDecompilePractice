package com.fanyu.boundless.common.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fanyu.boundless.R;
import com.fanyu.boundless.util.ImagePathUtil;

public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    private boolean mChecked;
    private Context mContext;
    private View mConvertView;
    private SparseArray<View> mViews = new SparseArray();

    public ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        this.mContext = context;
        this.mConvertView = itemView;
    }

    public static ViewHolder get(Context context, ViewGroup parent, int layoutId) {
        return new ViewHolder(context, LayoutInflater.from(context).inflate(layoutId, parent, false), parent);
    }

    public <T extends View> T getView(int viewId) {
        View view = (View) this.mViews.get(viewId);
        if (view != null) {
            return view;
        }
        view = this.mConvertView.findViewById(viewId);
        this.mViews.put(viewId, view);
        return view;
    }

    public ViewHolder setText(int viewId, String text) {
        ((TextView) getView(viewId)).setText(text);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int color) {
        ((TextView) getView(viewId)).setTextColor(color);
        return this;
    }

    public ViewHolder setIschecked(int viewId, Boolean checked) {
        RelativeLayout nicaicai = (RelativeLayout) getView(viewId);
        this.mChecked = checked.booleanValue();
        if (this.mChecked) {
            nicaicai.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.mipmap.ico_xuankuang));
        } else {
            nicaicai.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.mipmap.xuesheng_bj));
        }
        return this;
    }

    public ViewHolder setNewchecked(int viewId, Boolean checked) {
        ImageView nicaicai = (ImageView) getView(viewId);
        this.mChecked = checked.booleanValue();
        if (this.mChecked) {
            nicaicai.setVisibility(0);
        } else {
            nicaicai.setVisibility(8);
        }
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ((ImageView) getView(viewId)).setImageResource(resId);
        return this;
    }

    public ViewHolder setImageUrl(int viewId, String url, Transformation transformation) {
        ImageView view = (ImageView) getView(viewId);
        try {
            Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(url)).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).bitmapTransform(transformation).dontAnimate().into(view);
        } catch (Exception e) {
            Toast.makeText(this.mContext, e.getMessage(), 0).show();
        }
        return this;
    }

    public ViewHolder setImageUrlSquare(int viewId, String url, Transformation transformation) {
        ImageView view = (ImageView) getView(viewId);
        try {
            Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(url)).error((int) R.mipmap.jiazai_shibai).placeholder((int) R.mipmap.jiazaizhong_yuan).bitmapTransform(transformation).dontAnimate().into(view);
        } catch (Exception e) {
            Toast.makeText(this.mContext, e.getMessage(), 0).show();
        }
        return this;
    }

    public ViewHolder setImageUrl(int viewId, String url) {
        try {
            Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(url)).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().into((ImageView) getView(viewId));
        } catch (Exception e) {
            Toast.makeText(this.mContext, e.getMessage(), 0).show();
        }
        return this;
    }

    public ViewHolder setBitmapImageUrl(String url, ImageView imageView) {
        final ImageView view = imageView;
        try {
            Glide.with(this.mContext).load(ImagePathUtil.getInstance().getPath(url)).asBitmap().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).error((int) R.mipmap.jiazaishibai).placeholder((int) R.drawable.empty_photo).dontAnimate().into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    view.setImageBitmap(ImagePathUtil.getInstance().gerZoomRotateBitmap(resource));
                }
            });
        } catch (Exception e) {
            Toast.makeText(this.mContext, e.getMessage(), 0).show();
        }
        return this;
    }

    public ViewHolder setloadImageUrl(int viewId, String url) {
        try {
            Glide.with(this.mContext).load(url).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).error((int) R.mipmap.jiazaishibai).dontAnimate().placeholder((int) R.drawable.empty_photo).dontAnimate().into((ImageView) getView(viewId));
        } catch (Exception e) {
            Toast.makeText(this.mContext, e.getMessage(), 0).show();
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        getView(viewId).setVisibility(visible ? 0 : 8);
        return this;
    }

    public ViewHolder setClickable(int viewId, boolean clickable) {
        getView(viewId).setClickable(clickable);
        return this;
    }

    public ViewHolder setTag(int viewId, Object tag) {
        getView(viewId).setTag(tag);
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        getView(viewId).setTag(key, tag);
        return this;
    }

    public ViewHolder setOnClickListener(int viewId, OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId, OnLongClickListener listener) {
        getView(viewId).setOnLongClickListener(listener);
        return this;
    }
}
