package cn.finalteam.galleryfinal.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter.ViewHolder;
import java.util.List;

public class PhotoListAdapter extends ViewHolderAdapter<PhotoViewHolder, PhotoInfo> {
    private Activity mActivity;
    private int mRowWidth = (this.mScreenWidth / 3);
    private int mScreenWidth;
    private List<PhotoInfo> mSelectList;

    public static class PhotoViewHolder extends ViewHolder {
        public ImageView mIvCheck;
        public GFImageView mIvThumb;
        View mView;

        public PhotoViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mIvThumb = (GFImageView) view.findViewById(R.id.iv_thumb);
            this.mIvCheck = (ImageView) view.findViewById(R.id.iv_check);
        }
    }

    public PhotoListAdapter(Activity activity, List<PhotoInfo> list, List<PhotoInfo> selectList, int screenWidth) {
        super(activity, list);
        this.mSelectList = selectList;
        this.mScreenWidth = screenWidth;
        this.mActivity = activity;
    }

    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflate(R.layout.gf_adapter_photo_list_item, parent);
        setHeight(view);
        return new PhotoViewHolder(view);
    }

    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoInfo photoInfo = (PhotoInfo) getDatas().get(position);
        String path = "";
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }
        holder.mIvThumb.setImageResource(R.drawable.ic_gf_default_photo);
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(this.mActivity, path, holder.mIvThumb, this.mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo), this.mRowWidth, this.mRowWidth);
        holder.mView.setAnimation(null);
        if (GalleryFinal.getCoreConfig().getAnimation() > 0) {
            holder.mView.setAnimation(AnimationUtils.loadAnimation(this.mActivity, GalleryFinal.getCoreConfig().getAnimation()));
        }
        holder.mIvCheck.setImageResource(GalleryFinal.getGalleryTheme().getIconCheck());
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            holder.mIvCheck.setVisibility(0);
            if (this.mSelectList.contains(photoInfo)) {
                holder.mIvCheck.setBackgroundColor(GalleryFinal.getGalleryTheme().getCheckSelectedColor());
                return;
            } else {
                holder.mIvCheck.setBackgroundColor(GalleryFinal.getGalleryTheme().getCheckNornalColor());
                return;
            }
        }
        holder.mIvCheck.setVisibility(8);
    }

    private void setHeight(View convertView) {
        convertView.setLayoutParams(new LayoutParams(-1, (this.mScreenWidth / 3) - 8));
    }
}
