package com.fanyu.boundless.widget.ImagPagerUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.ImagPagerUtil.LazyViewPager.SimpleOnPageChangeListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.util.ArrayList;
import java.util.List;

public class ImagPagerUtil {
    private ImageLoadingListener animateFirstListener;
    private Dialog dialog;
    private ImageLoader imageLoader;
    private Activity mActivity;
    private LinearLayout mLL_progress;
    private List<String> mPicList;
    private LazyViewPager mViewPager;
    private DisplayImageOptions options;
    private int position;
    private int screenWidth;
    private TextView tv_content;
    private TextView tv_img_count;
    private TextView tv_img_current_index;
    private TextView tv_loadingmsg;

    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        private AnimateFirstDisplayListener() {
        }

        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ImagPagerUtil.this.mLL_progress.setVisibility(8);
            ImagPagerUtil.this.tv_loadingmsg.setText("");
            if (loadedImage != null) {
                ((ImageView) view).setImageBitmap(loadedImage);
            }
        }
    }

    class MyImagPagerAdapter extends PagerAdapter {
        ArrayList<ImageView> mList;

        public MyImagPagerAdapter(ArrayList<ImageView> mList) {
            this.mList = mList;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = (ImageView) this.mList.get(position);
            ImagPagerUtil.this.showPic(imageView, (String) ImagPagerUtil.this.mPicList.get(position));
            container.addView(imageView);
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) this.mList.get(position));
        }

        public int getCount() {
            if (this.mList == null || this.mList.size() <= 0) {
                return 0;
            }
            return this.mList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public ImagPagerUtil(Activity activity, List<String> mPicList) {
        this.animateFirstListener = new AnimateFirstDisplayListener();
        this.position = 0;
        this.mPicList = mPicList;
        this.mActivity = activity;
        this.imageLoader = ImageLoader.getInstance();
        setOptions();
        init();
    }

    public ImagPagerUtil(Activity activity, String[] picarr) {
        this.animateFirstListener = new AnimateFirstDisplayListener();
        this.position = 0;
        this.mPicList = new ArrayList();
        for (Object add : picarr) {
            this.mPicList.add(add);
        }
        this.mActivity = activity;
        this.imageLoader = ImageLoader.getInstance();
        setOptions();
        init();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setContentText(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.tv_content.setText(str);
        }
    }

    public void show() {
        if (!this.dialog.isShowing() && this.dialog != null) {
            this.dialog.show();
        }
    }

    private void init() {
        this.dialog = new Dialog(this.mActivity, R.style.fullDialog);
        RelativeLayout contentView = (RelativeLayout) View.inflate(this.mActivity, R.layout.view_dialogpager_img, null);
        this.mViewPager = (LazyViewPager) getView(contentView, R.id.view_pager);
        this.mLL_progress = (LinearLayout) getView(contentView, R.id.vdi_ll_progress);
        this.tv_loadingmsg = (TextView) getView(contentView, R.id.tv_loadingmsg);
        this.tv_img_current_index = (TextView) getView(contentView, R.id.tv_img_current_index);
        this.tv_img_count = (TextView) getView(contentView, R.id.tv_img_count);
        this.tv_content = (TextView) getView(contentView, R.id.tv_content);
        this.dialog.setContentView(contentView);
        this.tv_img_count.setText(this.mPicList.size() + "");
        this.tv_img_current_index.setText("1");
        int size = this.mPicList.size();
        ArrayList<ImageView> imageViews = new ArrayList();
        for (int i = 0; i < size; i++) {
            ZoomImageView imageView = new ZoomImageView(this.mActivity);
            imageView.measure(0, 0);
            Display display = this.mActivity.getWindowManager().getDefaultDisplay();
            this.screenWidth = display.getWidth();
            imageView.setLayoutParams(new MarginLayoutParams(this.screenWidth, display.getHeight()));
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ImagPagerUtil.this.dialog.dismiss();
                }
            });
            imageViews.add(imageView);
        }
        this.dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == 4 && keyEvent.getRepeatCount() == 0) {
                    ImagPagerUtil.this.dialog.dismiss();
                }
                return false;
            }
        });
        initViewPager(imageViews);
    }

    private void initViewPager(ArrayList<ImageView> list) {
        this.mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                ImagPagerUtil.this.tv_img_current_index.setText("" + (position + 1));
            }
        });
        this.mViewPager.setAdapter(new MyImagPagerAdapter(list));
    }

    protected void setOptions() {
        this.options = new Builder().showImageForEmptyUri((int) R.mipmap.ic_launcher).showImageOnFail((int) R.mipmap.ic_launcher).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
    }

    private void showPic(ImageView imageView, String url) {
        imageView.setImageBitmap(null);
        this.mLL_progress.setVisibility(0);
        this.imageLoader.displayImage(url, imageView, this.options, this.animateFirstListener, new ImageLoadingProgressListener() {
            public void onProgressUpdate(String s, View view, int i, int i1) {
                int progress = (int) (100.0f * (((float) i) / ((float) i1)));
                if (ImagPagerUtil.this.tv_loadingmsg != null) {
                    ImagPagerUtil.this.tv_loadingmsg.setText(progress + "%");
                }
            }
        });
        this.dialog.show();
    }

    public static final <E extends View> E getView(View parent, int id) {
        try {
            return parent.findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("ImagPageUtil", "Could not cast View to concrete class \n" + ex.getMessage());
            throw ex;
        }
    }
}
