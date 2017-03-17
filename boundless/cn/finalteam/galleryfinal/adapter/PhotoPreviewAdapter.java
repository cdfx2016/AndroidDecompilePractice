package cn.finalteam.galleryfinal.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.adapter.ViewHolderRecyclingPagerAdapter.ViewHolder;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;
import cn.finalteam.toolsfinal.DeviceUtils;
import java.util.List;

public class PhotoPreviewAdapter extends ViewHolderRecyclingPagerAdapter<PreviewViewHolder, PhotoInfo> {
    private Activity mActivity;
    private DisplayMetrics mDisplayMetrics = DeviceUtils.getScreenPix(this.mActivity);

    static class PreviewViewHolder extends ViewHolder {
        PhotoView mImageView;

        public PreviewViewHolder(View view) {
            super(view);
            this.mImageView = (PhotoView) view;
        }
    }

    public PhotoPreviewAdapter(Activity activity, List<PhotoInfo> list) {
        super(activity, list);
        this.mActivity = activity;
    }

    public PreviewViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new PreviewViewHolder(getLayoutInflater().inflate(R.layout.gf_adapter_preview_viewpgaer_item, null));
    }

    public void onBindViewHolder(PreviewViewHolder holder, int position) {
        PhotoInfo photoInfo = (PhotoInfo) getDatas().get(position);
        String path = "";
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }
        holder.mImageView.setImageResource(R.drawable.ic_gf_default_photo);
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(this.mActivity, path, holder.mImageView, this.mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo), this.mDisplayMetrics.widthPixels / 2, this.mDisplayMetrics.heightPixels / 2);
    }
}
