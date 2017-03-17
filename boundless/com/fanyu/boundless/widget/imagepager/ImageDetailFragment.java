package com.fanyu.boundless.widget.imagepager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.imagepager.photoview.PhotoViewAttacher;
import com.fanyu.boundless.widget.imagepager.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageDetailFragment extends Fragment {
    private PhotoViewAttacher mAttacher;
    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar progressBar;

    public static ImageDetailFragment newInstance(String imageUrl) {
        ImageDetailFragment f = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putString(MessageEncoder.ATTR_URL, imageUrl);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mImageUrl = getArguments() != null ? getArguments().getString(MessageEncoder.ATTR_URL) : null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_page_detail_fragment, container, false);
        this.mImageView = (ImageView) v.findViewById(R.id.image);
        this.mAttacher = new PhotoViewAttacher(this.mImageView);
        this.mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                ImageDetailFragment.this.getActivity().finish();
            }
        });
        this.progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageLoader.getInstance().displayImage(this.mImageUrl, this.mImageView, new SimpleImageLoadingListener() {
            public void onLoadingStarted(String imageUri, View view) {
                ImageDetailFragment.this.progressBar.setVisibility(0);
            }

            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "下载错误";
                        break;
                    case DECODING_ERROR:
                        message = "图片无法显示";
                        break;
                    case NETWORK_DENIED:
                        message = "网络有问题，无法下载";
                        break;
                    case OUT_OF_MEMORY:
                        message = "图片太大无法显示";
                        break;
                    case UNKNOWN:
                        message = "未知的错误";
                        break;
                }
                Toast.makeText(ImageDetailFragment.this.getActivity(), message, 0).show();
                ImageDetailFragment.this.progressBar.setVisibility(8);
            }

            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ImageDetailFragment.this.progressBar.setVisibility(8);
                ImageDetailFragment.this.mAttacher.update();
            }
        });
    }
}
